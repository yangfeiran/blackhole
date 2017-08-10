package com.chinacloud.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinacloud.bean.ConnectorBean;
import com.chinacloud.bean.FtpConfigBean;
import com.chinacloud.bean.FtpSourceBean;
import com.chinacloud.bean.HdfsConfigBean;
import com.chinacloud.bean.HdfsSinkBean;
import com.chinacloud.bean.JdbcConfigBean;
import com.chinacloud.bean.JdbcSinkBean;
import com.chinacloud.bean.JdbcSinkConfigBean;
import com.chinacloud.bean.JdbcSourceBean;
import com.chinacloud.bean.PageResultInfo;
import com.chinacloud.bean.ResultInfo;
import com.chinacloud.config.Confluent;
import com.chinacloud.confluent.RESTClient;
import com.chinacloud.model.Connector;
import com.chinacloud.model.ConnectorSessions;
import com.chinacloud.model.ConsumerRecords;
import com.chinacloud.process.ConnectConfigProcessor;
import com.chinacloud.service.ConfluentConnectorService;
import com.chinacloud.service.ConnectorManager;
import com.chinacloud.service.ConnectorSessionsManager;
import com.chinacloud.utils.JsonUtils;
import com.chinacloud.utils.OkHttp;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


@RestController
@RequestMapping("/v1/connectors")
public class ConnectorController {
	private static final Logger log = Logger.getLogger(ConnectorController.class);

	@Autowired  
    private Environment env;
	
	@Autowired
	private ConnectorManager connectorManager;
	
	@Autowired
	private ConnectorSessionsManager connectorSessionsManager;

	@Autowired
	private RESTClient client;

	@Autowired
	private Confluent confluent;
	
	@Autowired
	private ConfluentConnectorService connectorService;
	
	@Autowired
	private KafkaController KafkaController;
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	@ResponseBody
	public ResultInfo getConnectors(
			@RequestParam(value = "parentid", required = false) String parentid,
			@RequestParam(value = "category", required = false) String category,
			@RequestParam(value = "topic", required = false) String topic,
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "size", required = false) Integer size) {

		ResultInfo info = new ResultInfo();
		if(!Strings.isNullOrEmpty(parentid)){//如果传了parentid则忽略其他参数
			List<Connector> list = connectorManager.findByParentId(parentid);
			info.setCode(HttpStatus.OK.value());
			info.setStatus("success");
			info.setResult(list);
		}else{
			info = new PageResultInfo();
			Page<Connector> list = connectorManager.query(category, topic, page-1, size);
			setPageInfo((PageResultInfo) info, list);
			info.setCode(HttpStatus.OK.value());
			info.setStatus("success");
			info.setResult(list);
		}
		
		return info;
	}

	@RequestMapping(value = "/sources", method = RequestMethod.GET)
	@ResponseBody
	public ResultInfo getSources(
			@RequestParam(value = "keyword", required = false) String queryStr,
			@RequestParam(value = "pageNum", required = false) Integer page,
			@RequestParam(value = "pageSize", required = false) Integer size) {

		PageResultInfo response = new PageResultInfo();
		Page<Connector> list = connectorManager.getSources(queryStr, page, size);
		setPageInfo(response, list);
		
		List<Map<String, Object>> results = Lists.newArrayList();
		for (Iterator<Connector> iter = list.iterator(); iter.hasNext();) {
			Connector source = (Connector) iter.next();
			Map<String, Object> map = JSON.parseObject(JSON.toJSONString(source), Map.class);
			List<Connector> sinks = connectorManager.findByParentId(source.getId());
			map.put("sinks", sinks);
			results.add(map);
		}
		response.setCode(HttpStatus.OK.value());
		response.setStatus("success");
		response.setResult(results);
		
		return response;
	}

	private void setPageInfo(PageResultInfo res, Page<Connector> page) {
		res.setPageNumber(page.getNumber());
		res.setPageSize(page.getSize()+1);
		res.setNumberOfElements(page.getNumberOfElements());
		res.setSort(page.getSort());
		res.setFirst(page.isFirst());
		res.setLast(page.isLast());
		res.setTotalPages(page.getTotalPages());
		res.setTotalElements(page.getTotalElements());
	}

	@RequestMapping(value = "/sources", method = RequestMethod.POST)
	@ResponseBody
	public ResultInfo addSource(@RequestBody ConnectorBean bean, HttpServletRequest request) {
		ResponseEntity<String> result = null;
		ResultInfo response = new ResultInfo();
		List<Connector> oldConnectors = 
				connectorManager.findConnectorsByName(bean.getName());
		if(!oldConnectors.isEmpty()&&oldConnectors.size()>0){
			response.setCode(HttpStatus.CONFLICT.value());
			response.setStatus("failure");
			response.setMsg("Job Name is exists");
			return response;
		}
		if (bean.getConnectorType().equalsIgnoreCase("jdbc")) {
			JdbcSourceBean model = new JdbcSourceBean();
			model.setName(bean.getName());
			model.setConnectorType(bean.getConnectorType());
			model.setOwner(bean.getOwner());
			model.setDescription(bean.getDescription());
			try {
				JdbcConfigBean jdbcConfigBean = JSON.parseObject(JSON.toJSONString(bean.getConfig()),
						JdbcConfigBean.class);
				jdbcConfigBean.setJobName(bean.getName());
				model.setJdbcConfigBean(jdbcConfigBean);
				jdbcConfigBean.setTopicPrefix(jdbcConfigBean.getDbName()+"_");//defalut topic prefix is db name
				
				String topicName=jdbcConfigBean.getTopicPrefix()+jdbcConfigBean.getTableName();
				List<Connector> exsitsConnectors= Lists.newArrayList();
				exsitsConnectors=connectorManager.findByTopic(topicName);
				if(!exsitsConnectors.isEmpty()&&exsitsConnectors.size()>0){
					/*response.setCode(HttpStatus.CONFLICT.value());
					response.setStatus("failure");
					response.setMsg("ingest this table job is exists");
					return response;*/
					log.info("ingest this table job is exists,and now maybe change the topic name by appending current time");
					topicName = topicName +System.currentTimeMillis();
				}
				ConnectConfigProcessor.initTypeNameTable(confluent.getConfigPath());
				Map<String, Object> configMapper = ConnectConfigProcessor
						.convertToConfig(model.getSourceKey(), jdbcConfigBean);
				configMapper.putAll(model.getConnectionUrl(jdbcConfigBean.getDbType(), jdbcConfigBean.getDatabaseIp(),
						jdbcConfigBean.getPort(), jdbcConfigBean.getDbName(), jdbcConfigBean.getUsername(),
						jdbcConfigBean.getPassword()));
				
				//set the increment mode value
				if(jdbcConfigBean.getMode().equals("incrementing")){
					configMapper.put("incrementing.column.name", jdbcConfigBean.getCheckColumn());
				}else if(jdbcConfigBean.getMode().equals("timestamp")){
					configMapper.put("timestamp.column.name", jdbcConfigBean.getCheckColumn());
				}else if(jdbcConfigBean.getMode().equals("bulk")){
					configMapper.put("mode",jdbcConfigBean.getMode());
				}
				
				configMapper.putAll(model.getConnectorClass("jdbc"));
				configMapper.put("validate.non.null", false);
				model.setConfig(configMapper);
				
				//create kafka source connector by restapi
				net.sf.json.JSONObject json = new net.sf.json.JSONObject();
				json.put("name", model.getName());
				json.put("config", JsonUtils.beanToJson(configMapper));
				log.info("add source connector params:"+json);
				result = client.addConnectors(json.toString());
				if (result.getStatusCode() == HttpStatus.CREATED) {
					// 保存source connector到数据库
					String connectorId = saveSourceConnector(model, 
							topicName,jdbcConfigBean.getDbName(),jdbcConfigBean.getTableName());
					saveConnectorSession(connectorId, model.getName(), model.getConfig());
					response.setCode(HttpStatus.CREATED.value());
					response.setStatus("success");
					response.setMsg("create source connector success");
				}
			}catch(IllegalStateException e){
				client.removeByName(model.getName());
				response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
				response.setStatus("failure");
				response.setMsg(e.getMessage());
			} catch (Exception e) {
				log.info(e.getMessage());
				response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
				response.setStatus("failure");
				response.setMsg("create source connector failed");
			}
		}
		//source is ftp
		if(bean.getConnectorType().equalsIgnoreCase("ftp")){
			return addFtpSourceConnector(bean);
		}
		return response;
	}
	
	@RequestMapping(value = "/sources/{sourceId}", method = RequestMethod.DELETE)
	@ResponseBody
	public ResultInfo deleteSource(@PathVariable("sourceId") String sourceId){
		ResultInfo info = connectorManager.deleteSource(sourceId);
		return info;
	}


	@RequestMapping(value = "/sources/{sourceId}/sinks", method = RequestMethod.POST)
	@ResponseBody
	public ResultInfo addSinkConnector(@PathVariable("sourceId") String sourceId,
			@RequestParam("topicName") String topicName, @RequestBody ConnectorBean bean) {
		ResponseEntity<String> result = null;
		ResultInfo response = new ResultInfo();
		
		List<Connector> oldConnectors = 
				connectorManager.findConnectorsByName(bean.getName());
		if(!oldConnectors.isEmpty()&&oldConnectors.size()>0){
			response.setCode(HttpStatus.CONFLICT.value());
			response.setStatus("failure");
			response.setMsg("Job Name  is exists");
			return response;
		}
		if(bean.getConnectorType().equalsIgnoreCase("pgxz")){
			return addJdbcPgxzSinkConnector(bean,topicName,sourceId);
		}
		if (bean.getConnectorType().equalsIgnoreCase("hdfs") || bean.getConnectorType().equalsIgnoreCase("hive")) {
			HdfsSinkBean model = new HdfsSinkBean();
			model.setName(bean.getName());
			model.setConnectorType(bean.getConnectorType());
			model.setOwner(bean.getOwner());
			model.setDescription(bean.getDescription());
			try {
				HdfsConfigBean hdfsConfigBean = JSON.parseObject(JSON.toJSONString(bean.getConfig()),
						HdfsConfigBean.class);
				model.setHdfsConfigBean(hdfsConfigBean);
				if(Strings.isNullOrEmpty(topicName))
					topicName = hdfsConfigBean.getTopicName();
				hdfsConfigBean.setTopicName(topicName);
				hdfsConfigBean.setJobName(bean.getName());

				ConnectConfigProcessor.initTypeNameTable(confluent.getConfigPath());
				Map<String, Object> configMapper = ConnectConfigProcessor.convertToConfig(model.getSourceKey(),
						hdfsConfigBean);

				// judge is integrate Hive
				if (hdfsConfigBean.getHiveIntegration()) {
					configMapper.put("hive.integration", hdfsConfigBean.getHiveIntegration());
					configMapper.put("hive.metastore.uris", hdfsConfigBean.getHiveMetastoreUris());
					configMapper.put("hive.conf.dir", hdfsConfigBean.getHiveConfDir());
					configMapper.put("hive.database", hdfsConfigBean.getHiveDatabase());
					configMapper.put("schema.compatibility", hdfsConfigBean.getSchemaCompatibility());
				}
				//set hadoop conf dir
				configMapper.put("hadoop.conf.dir", hdfsConfigBean.getHadoopConfDir());
				// set format class
				configMapper.putAll(model.getFormatClass(hdfsConfigBean.getFomatType()));
				// set partitioner class
				configMapper.putAll(
						model.getPartitionerClass(hdfsConfigBean.getPartitionerType(), hdfsConfigBean.getFieldName()));
				configMapper.putAll(model.getConnectorClass("hdfs"));

				model.setConfig(configMapper);

				net.sf.json.JSONObject json = new net.sf.json.JSONObject();
				json.put("name", model.getName());
				json.put("config", JsonUtils.beanToJson(configMapper));
				log.info("commit create sink connector params:"+json);
				result = client.addConnectors(json.toString());
				if(result.getStatusCode() == HttpStatus.CREATED){
					//save to db
					String connectorId = saveSinkConnector(model, topicName, 
							sourceId,	hdfsConfigBean.getHiveDatabase());
			        saveConnectorSession(connectorId, model.getName(), model.getConfig());
					response.setCode(HttpStatus.CREATED.value());
					response.setStatus("success");;
					response.setMsg("create sink connector success");
				}
			}catch(IllegalStateException e){
				client.removeByName(model.getName());
				response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
				response.setStatus("failure");
				response.setMsg(e.getMessage());
			}catch (Exception e) {
				response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
				response.setStatus("failure");
				response.setMsg("create sink connector failed");
			}
		}
		return response;
	}

	@RequestMapping(value = "/sources/{sourceId}/sinks", method = RequestMethod.GET)
	@ResponseBody
	public ResultInfo getSinks(@PathVariable(value = "sourceId") String sourceId) {
		ResultInfo info = new ResultInfo();
		List<Connector> list = connectorManager.findByParentId(sourceId);
		info.setCode(HttpStatus.OK.value());
		info.setStatus("success");
		info.setResult(list);
		
		return info;
	}
	
	@RequestMapping(value = "/sources/{sourceId}/sinks/{sinkId}", method = RequestMethod.DELETE)
	@ResponseBody
	public ResultInfo deleteSink(@PathVariable(value = "sourceId") String sourceId,
			@PathVariable("sinkId") String sinkId){
		ResultInfo info = connectorManager.deleteSink(sourceId, sinkId);
		//从confluent删除connector
		//TODO
		return info;
	}

	private String saveSourceConnector(ConnectorBean model, String topic, 
			String dbName, String tableName) {
		Connector connector = new Connector();
		connector.setId(UUID.randomUUID().toString());
		connector.setName(model.getName());
		connector.setModifedTime(new Timestamp(new Date().getTime()));
		connector.setConnectorType(model.getConnectorType());
		connector.setStatus("RUNNING");
		connector.setOwner(model.getOwner());
		connector.setTopic(topic);
		connector.setCategory("source");
		connector.setDescription(model.getDescription());
		connector.setParentId("");
		connector.setDbName(dbName);
		connector.setTableName(tableName);
		try{
			connectorManager.saveConnector(connector);
		}catch(Exception e){
			throw new IllegalStateException("save connector to myql  Error");
		}
		return connector.getId();
	}

	private String saveSinkConnector(ConnectorBean model, String topic, 
			String parentId, String dbName) {
		System.out.println("save sink***");
		Connector connector = new Connector();
		connector.setId(UUID.randomUUID().toString());
		connector.setName(model.getName());
		connector.setModifedTime(new Timestamp(new Date().getTime()));
		connector.setConnectorType(model.getConnectorType());
		connector.setStatus("RUNNING");
		connector.setOwner(model.getOwner());
		connector.setTopic(topic);
		connector.setCategory("sink");
		connector.setDescription(model.getDescription());
		connector.setParentId(parentId);
		connector.setDbName(dbName);
		connector.setTableName(topic);
		try{
			connectorManager.saveConnector(connector);
		}catch(Exception e){
			throw new IllegalStateException("save connector to myql  Error");
		}
		return connector.getId();
	}
	
	private void saveConnectorSession(String connectorId, String connectorName, Map<String, Object> map){
		List<ConnectorSessions> list = Lists.newArrayList();
		try{
				Iterator<Entry<String, Object>> iterator = map.entrySet().iterator();
				while(iterator.hasNext()){
					Entry<String, Object> obj = iterator.next();
					ConnectorSessions cs = new ConnectorSessions();
					cs.setConnectorId(connectorId);
					cs.setConnectorName(connectorName);
					cs.setPropname(obj.getKey());
					cs.setPropval(obj.getValue().toString());
					list.add(cs);
				}
				connectorSessionsManager.saveConnectorSessions(list);
			}catch(Exception e){
				throw new IllegalStateException("save connector session to myql  Error");
			}
	}

	
	@RequestMapping(value = "/{connectorName}/status", method = RequestMethod.GET)
	@ResponseBody
	public ResultInfo getConnectorStatus(@PathVariable(value = "connectorName") String connectorName) {
		ResultInfo response = new ResultInfo();
		ResponseEntity<String> result = null;
		try{
			 result = client.getConnectorStatusByName(connectorName);
			 log.info(result.getStatusCode());
			 if(result.getStatusCode() == HttpStatus.OK){
					response.setCode(HttpStatus.OK.value());
					response.setStatus("success");;
					response.setMsg("get  connector  status success");
					response.setResult(result.getBody());
			 }
		}catch(Exception e){
			response.setCode(HttpStatus.NOT_FOUND.value());
			 response.setStatus("failed");
			response.setMsg("get connector status failed");
		}
		return response;
	}
	
	@RequestMapping(value = "/{connectorName}/pause", method = RequestMethod.PUT)
	@ResponseBody
	public ResultInfo stopConnector(@PathVariable(value = "connectorName") String connectorName) {
		log.info("stop connector job...");
		ResultInfo response = new ResultInfo();
		try{
			client.stopConnectorStatusByName(connectorName);
			response.setCode(HttpStatus.ACCEPTED.value());
			response.setStatus("success");;
			response.setMsg("stop  connector  success");
		}catch(Exception e){
			response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			 response.setStatus("failed");
			response.setMsg("stop connector failed");
		}
		return response;
	}
	
	@RequestMapping(value = "/{connectorName}/resume", method = RequestMethod.PUT)
	@ResponseBody
	public ResultInfo resumeConnector(@PathVariable(value = "connectorName") String connectorName) {
		log.info("resume connector job...");
		ResultInfo response = new ResultInfo();
		try{
			client.startConnectorStatusByName(connectorName);
			
			response.setCode(HttpStatus.ACCEPTED.value());
			response.setStatus("success");
			response.setMsg("start  connector  success");
		}catch(Exception e){
			response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			 response.setStatus("failed");
			response.setMsg("start connector  failed");
		}
		return response;
	}
	
	@RequestMapping(value = "/source/{connectorName}/config", method = RequestMethod.PUT)
	@ResponseBody
	public ResultInfo updateConnector(@PathVariable
			(value = "connectorName") String connectorName,@RequestBody ConnectorBean bean) {
		
		log.info("update connector job...");
		ResultInfo response = new ResultInfo();
		try{
			Map<String, Object> config = 
					connectorService.getSourceConfigParams(bean);
			client.updateConnectorByName(connectorName,config);
			response.setCode(HttpStatus.OK.value());       
			response.setStatus("success");;
			response.setMsg("start  connector  success");
		}catch(Exception e){
			response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			 response.setStatus("failed");
			response.setMsg("start connector  failed");
		}
		return response;
	}
	
	
	@RequestMapping(value = "/{connectorName}/config", method = RequestMethod.GET)
	@ResponseBody
	public ResultInfo getConnectorConfig(@PathVariable
			(value = "connectorName") String connectorName) {
		ResponseEntity<String> result = null;
		log.info("get  connector config...");
		ResultInfo response = new ResultInfo();
		try{
			result = client.getConnectorConfigByName(connectorName);		
			response.setCode(HttpStatus.OK.value());       
			response.setStatus("success");;
			response.setMsg("start  connector  success");
			response.setResult(result.getBody());
		}catch(Exception e){
			response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			 response.setStatus("failed");
			response.setMsg("start connector  failed");
		}
		return response;
	}

	/**
	 * @param ConnectorBean
	 * create ftp source 
	 * @return ResultInfo
	 * */
	public ResultInfo  addFtpSourceConnector(ConnectorBean bean){
		ResultInfo response = new ResultInfo();
		FtpSourceBean ftpSourceBean = new FtpSourceBean();
		ftpSourceBean.setName(bean.getName());
		ftpSourceBean.setConnectorType(bean.getConnectorType());
		ftpSourceBean.setOwner(bean.getOwner());
		ftpSourceBean.setDescription(bean.getDescription());
		FtpConfigBean ftpConfigBean = JSON.parseObject(JSON.toJSONString(bean.getConfig()),
				FtpConfigBean.class);
		ftpSourceBean.setFtpConfigBean(ftpConfigBean);
		ftpConfigBean.setOfflineStorePath(bean.getName());
		ConnectConfigProcessor.initTypeNameTable(confluent.getConfigPath());
		Map<String, Object> configMapper = ConnectConfigProcessor
				.convertToConfig(ftpSourceBean.getSourceKey(), ftpConfigBean);

		configMapper.putAll(ftpSourceBean.getFtpMonTail(
				ftpConfigBean.getFtpPath(), ftpConfigBean.getTopicName()));
		configMapper.putAll(ftpSourceBean.getConnectorClass(bean.getConnectorType()));
		configMapper.putAll(ftpSourceBean.getFtpSourceConverter(ftpConfigBean.getFtpType()));
		ftpSourceBean.setConfig(configMapper);
		//the rest api need parameters is json
		net.sf.json.JSONObject json = new net.sf.json.JSONObject();
		json.put("name", ftpSourceBean.getName());
		try {
			json.put("config", JsonUtils.beanToJson(configMapper));
		} catch (JsonProcessingException e) {
			log.info(e.getMessage());
			response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setStatus("failure");
			response.setMsg("create ftp source connector failed");
		}
		log.info("add ftp source connector params:"+json);
		ResponseEntity<String> result = null;
		result = client.addConnectors(json.toString());
		if (result.getStatusCode() == HttpStatus.CREATED) {
			try{
				// 保存source connector到数据库
				String connectorId = saveSourceConnector(ftpSourceBean, 
						ftpConfigBean.getTopicName(),"","");
				saveConnectorSession(connectorId, ftpSourceBean.getName(), ftpSourceBean.getConfig());
				response.setCode(HttpStatus.CREATED.value());
				response.setStatus("success");
				response.setMsg("create ftp source connector success");
			}catch(IllegalStateException e){
				log.info(e.getMessage());
				client.removeByName(ftpSourceBean.getName());
				response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
				response.setStatus("failure");
				response.setMsg("create ftp source connector failed");
				}
		}
		return response;
	}
	
	public ResultInfo 	addJdbcPgxzSinkConnector(ConnectorBean bean, String topicName, String sourceId){
		ResultInfo response  =new ResultInfo();
		JdbcSinkBean jdbcSinkBean = new JdbcSinkBean();
		 jdbcSinkBean.setName(bean.getName());
		 jdbcSinkBean.setConnectorType(bean.getConnectorType());
		 jdbcSinkBean.setOwner(bean.getOwner());
		 jdbcSinkBean.setDescription(bean.getDescription());
		 
		 JdbcSinkConfigBean jdbcSinkConfigBean = JSON.parseObject(JSON.toJSONString(bean.getConfig()),
				 JdbcSinkConfigBean.class);
		 jdbcSinkConfigBean.setTopics(topicName);
		 jdbcSinkConfigBean.setName(bean.getName());
		 if(Strings.isNullOrEmpty(jdbcSinkConfigBean.getTableName())){
			 jdbcSinkConfigBean.setTableName(topicName);
		 }
		 jdbcSinkBean.setJdbcSinkConfigBean(jdbcSinkConfigBean);
		
		ConnectConfigProcessor.initTypeNameTable(confluent.getConfigPath());
		Map<String, Object> configMapper = ConnectConfigProcessor
				.convertToConfig(jdbcSinkBean.getSourceKey(), jdbcSinkConfigBean);
		configMapper.put("connector.class", "io.confluent.connect.jdbc.JdbcSinkConnector");
		configMapper.put("insert.mode", "insert");
		configMapper.put("auto.create", "true");
		configMapper.put("pk.mode", "none");
		configMapper.put("topics", topicName);
		jdbcSinkBean.setConfig(configMapper);
		//the rest api need parameters is json
		net.sf.json.JSONObject json = new net.sf.json.JSONObject();
		json.put("name", jdbcSinkBean.getName());
		try {
			json.put("config", JsonUtils.beanToJson(configMapper));
		} catch (JsonProcessingException e) {
			log.info(e.getMessage());
			response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setStatus("failure");
			response.setMsg("create jdbc sink connector failed");
		}
		log.info("add jdbc sink connector params:"+json);
		ResponseEntity<String> result = null;
		result = client.addConnectors(json.toString());
		if (result.getStatusCode() == HttpStatus.CREATED) {
			try{
				// 保存sin ckonnector到数据库
				String connectorId = savePgxzSinkConnector(jdbcSinkBean, topicName, sourceId, "",jdbcSinkConfigBean.getTableName());
				saveConnectorSession(connectorId, jdbcSinkBean.getName(), jdbcSinkBean.getConfig());
				response.setCode(HttpStatus.CREATED.value());
				response.setStatus("success");
				response.setMsg("create jdbc sink connector success");
			}catch(IllegalStateException e){
				log.info(e.getMessage());
				client.removeByName(jdbcSinkBean.getName());
				response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
				response.setStatus("failure");
				response.setMsg("create jdbc sink  connector failed");
				}
		}
		return response;
	}
	
	@RequestMapping(value = "/overview", method = RequestMethod.GET)
	@ResponseBody
	public ResultInfo overview(
			@RequestParam(value = "keyword", required = false) String queryStr,
			@RequestParam(value = "pageNum", required = false) Integer page,
			@RequestParam(value = "pageSize", required = false) Integer size) {

		PageResultInfo response = new PageResultInfo();
		Page<Connector> list = connectorManager.getSources(queryStr, page, size);
		setPageInfo(response, list);
		
		List<Map<String, Object>> results = Lists.newArrayList();
		/*for (Iterator<Connector> iter = list.iterator(); iter.hasNext();) {
			Connector source = (Connector) iter.next();*/
		for(Connector source:list){
			Map<String, Object> map = JSON.parseObject(JSON.toJSONString(source), Map.class);
			//查询source抽取数
			try {
				Map<String, Object> topicInfo = topicStatsUseKafkaManager(source.getTopic());
				map.put("totalSize", topicInfo.get("totalSize"));
				map.put("availableSize", topicInfo.get("availableSize"));
			} catch (Exception e) {
				e.printStackTrace();
				map.put("totalSize", 0);
				map.put("availableSize", 0);
			}
			//查询sink数据
			List<Map<String, Object>> sinksInfo = Lists.newArrayList();
			List<Connector> sinks = connectorManager.findByParentId(source.getId());
		//	for (Iterator<Connector> it = sinks.iterator(); iter.hasNext();) {
		//		Connector sink = (Connector) it.next();
			for(Connector sink:sinks){
				String group = "connect-"+sink.getName();
				Map<String, Object> sinkMap = JSON.parseObject(JSON.toJSONString(sink), Map.class);
				try {
					Map<String, Object> info = comsumerStatsUseKafkaManager(source.getTopic(), group);
					sinkMap.putAll(info);
				} catch (IOException e) {
					e.printStackTrace();
					sinkMap.put("totalLogSize", 0);
					sinkMap.put("totalOffset", 0);
					sinkMap.put("totalLag", 0);
				}
				sinksInfo.add(sinkMap);
			}
			map.put("sinks", sinksInfo);
			results.add(map);
			//设置所属部门、责任人、电话
			setResourceInfo(map, source, source.getTableName());
		}
		response.setCode(HttpStatus.OK.value());
		response.setStatus("success");
		response.setResult(results);
		
		return response;
	}
	
	private Map<String, Object> topicStatsUseKafkaManager(String topic) throws IOException {
		String url = env.getProperty("kafka.managerUrl")+"/topics/"
				+URLEncoder.encode(topic, "utf8");
		Document doc = Jsoup.connect(url).get();
		Elements rows = doc.select("div.row div.col-md-5 table>tbody>tr");
		Map<String, Object> map = Maps.newHashMap();
		long totalOffset = 0;
		for(int i=0; i<rows.size(); i++){
			Element tr = rows.get(i);
			Elements tds = tr.select("td");
			if(tds.size()==2 && tds.get(0).text().equals("Sum of partition offsets")){
				totalOffset += Long.parseLong(tds.get(1).text()); 
			}
		}
		//map.put("name", topic);
		map.put("totalSize", totalOffset);
		map.put("availableSize", totalOffset);
		
		return map;
	}

	public Map<String, Object> comsumerStatsUseKafkaManager(String topic, String group) throws IOException {
		String url = env.getProperty("kafka.managerUrl")+"/consumers/"
				+URLEncoder.encode(group, "utf8")+"/topic/"
				+URLEncoder.encode(topic, "utf8")+"/type/KF";
		Document doc = Jsoup.connect(url).get();
		Elements rows = doc.select("div.row div.col-md-12 table>tbody>tr");
		long totalLogSize = 0;
		long totalOffset = 0;
		for(int i=0; i<rows.size(); i++){
			Element tr = rows.get(i);
			Elements tds = tr.select("td");
			totalLogSize += Long.parseLong(tds.get(1).text()); 
			if(!tds.get(2).text().equals("-1"))
			    totalOffset += Long.parseLong(tds.get(2).text()); 
		}
		
		Map<String, Object> map = Maps.newHashMap();
		//map.put("topic", topic);
		//map.put("group", group);
		map.put("totalLogSize", totalLogSize);
		map.put("totalOffset", totalOffset);
		map.put("totalLag", totalLogSize - totalOffset);
		
		return map;
	}

	/**
	 * @param map
	 * @param source
	 * @param tablename 
	 */
	private void setResourceInfo(Map<String, Object> map, Connector source, String tableName) {
		ConnectorSessions session = connectorSessionsManager
				.findByConnectorIdAndPropname(source.getId(), "connection.url");
		if(session!=null && session.getPropval() != null){
			Map<String, String> body = Maps.newHashMap();
			body.put("tableName", tableName);
			String url = session.getPropval();
			try{
				if(url.startsWith("jdbc:mysql")){
					//no mysql owner info from metagrid
/*					String[] a1 = url.split("[?]");
					String[] a2 = a1[0].split("/"); //jdbc:mysql://ip:port/db
					String[] a3 = a2[2].split("[:]"); //ip:port
					body.put("dbName", a2[3]);
					body.put("ip", a3[0]);
					body.put("port", a3[1]);
					body.put("dbType", "MYSQL");
					body.put("additional", "mysql");*/
					return;
				}else if(url.startsWith("jdbc:oracle:thin")){
					String[] a1 = url.split("[:]");
					String[] a2 = a1[3].split("@");
					String[] a3 = a2[0].split("/");
					body.put("dbName", a3[0]);
					body.put("ip", a2[1]);
					body.put("port", a1[4]);
					body.put("dbType", "ORACLE");
					body.put("additional", a1[5]);
				}else {
					return;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			try {
				String  data = OkHttp.post(env.getProperty("metagrid.dsmUrl")
						+"/dataSourceManager/owner", JSON.toJSONString(body));
				JSONObject obj = JSON.parseObject(data);
				map.put("ownerDepartment", obj.get("ownerDepartment"));
				map.put("owner", obj.get("owner"));
				map.put("ownerTEL", obj.get("ownerTEL"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Get Pgxz Sink table info
	 * return tableName:lasttime,offset.
	 * */
	
	@RequestMapping(value = "/pgxztables", method = RequestMethod.GET)
	@ResponseBody
	public ResultInfo getPgxzTableInfo(){
		ResultInfo resultInfo = new ResultInfo();
		List<Connector> pgxzConnectors = connectorManager.findConnectorByCatagory("pgxz");
		List<ConsumerRecords>consumerRecords = Lists.newArrayList();
		for(Connector connector:pgxzConnectors){
			Map<String, Object> map = Maps.newHashMap();
			ConsumerRecords consumerRecord = new ConsumerRecords();
			Timestamp lastUpdateTime = new Timestamp(System.currentTimeMillis()); 
			Long totalOffset = 0l;
			try {
				KafkaController.comsumerStatsUseKafkaManager(connector.getTopic(), "connect-"+connector.getName(), map);
				if(!map.isEmpty()){
					if(map.get("lastTime")!=null) lastUpdateTime = (Timestamp)map.get("lastTime");
					if(map.get("totalOffset")!=null) totalOffset = (Long)map.get("totalOffset");
				}
				consumerRecord.setTableName(connector.getTableName());
				consumerRecord.setConsumerOffset(totalOffset);
				consumerRecord.setConsumerTime(lastUpdateTime);
				consumerRecords.add(consumerRecord);
			} catch (Exception e) {
				e.printStackTrace();
				resultInfo.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
				resultInfo.setMsg("Get table name failed");
				resultInfo.setStatus("failed");
			}
		}
		resultInfo.setCode(HttpStatus.OK.value());
		resultInfo.setMsg("Get table info success");
		resultInfo.setResult(consumerRecords);
		resultInfo.setStatus("success");
		return resultInfo;
	}
	
	
	
	private String savePgxzSinkConnector(ConnectorBean model, String topic, 
			String parentId, String dbName,String tableName) {
		System.out.println("save sink***");
		Connector connector = new Connector();
		connector.setId(UUID.randomUUID().toString());
		connector.setName(model.getName());
		connector.setModifedTime(new Timestamp(new Date().getTime()));
		connector.setConnectorType(model.getConnectorType());
		connector.setStatus("RUNNING");
		connector.setOwner(model.getOwner());
		connector.setTopic(topic);
		connector.setCategory("sink");
		connector.setDescription(model.getDescription());
		connector.setParentId(parentId);
		connector.setDbName(dbName);
		connector.setTableName(tableName);
		try{
			connectorManager.saveConnector(connector);
		}catch(Exception e){
			throw new IllegalStateException("save connector to postgresql  Error");
		}
		return connector.getId();
	}
}
package com.chinacloud.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinacloud.bean.ResultInfo;
import com.chinacloud.model.Connector;
import com.chinacloud.model.ConsumerRecords;
import com.chinacloud.service.ConnectorManager;
import com.chinacloud.utils.OkHttp;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import ch.epfl.lamp.fjbg.Main;


@RestController
@RequestMapping("/v1/connectors")
public class KafkaController {
	private static final Logger log = Logger.getLogger(KafkaController.class);
	//record the sink consumer stats
	private ConcurrentMap<String,ConsumerRecords>sinksRecords=Maps.newConcurrentMap();
	private ConcurrentMap<String,Timestamp>sinksTime = Maps.newConcurrentMap();
	@Autowired  
    private Environment env;  
	
	@Autowired
    private RestTemplate template;
	
	@Autowired
	private ConnectorManager connectorManager;
	

	@RequestMapping(value = "/topics", method = RequestMethod.GET)
	@ResponseBody
	public ResultInfo getTopics() {
		ResultInfo info = new ResultInfo();
		String kafdropUrl = env.getProperty("kafka.kafdropUrl");
		String url = kafdropUrl+"/topic/list/json";
		ResponseEntity<String> topics = template.getForEntity(url, String.class);
		info.setCode(HttpStatus.OK.value());
		info.setStatus("success");
		info.setResult(topics);
		
		return info;
	}

	@RequestMapping(value = "/sources/{topic:.+}/stats", method = RequestMethod.GET)
	@ResponseBody
	public ResultInfo getSourceInfo(@PathVariable("topic") String topic) {
		ResultInfo info = new ResultInfo();
		JSONObject topicInfo = null;
		try {
			//topicInfo = topicStatsUseKafdrop(topic);
			topicInfo = topicStatsUseKafkaManager(topic);
		} catch (Exception e) {
			System.out.println("==error@url==/sources/"+topic+"/stats");
			e.printStackTrace();
			topicInfo = new JSONObject();
			topicInfo.put("name", topic);
			topicInfo.put("totalSize", 0);
			topicInfo.put("availableSize", 0);
		}
		info.setCode(HttpStatus.OK.value());
		info.setStatus("success");
		info.setResult(topicInfo);
		
		return info;
	}

	private JSONObject topicStatsUseKafkaManager(String topic) throws IOException {
		String url = env.getProperty("kafka.managerUrl")+"/topics/"
				+URLEncoder.encode(topic, "utf8");
		Document doc = Jsoup.connect(url).get();
		Elements rows = doc.select("div.row div.col-md-5 table>tbody>tr");
		JSONObject topicInfo = new JSONObject();
		long totalOffset = 0;
		for(int i=0; i<rows.size(); i++){
			Element tr = rows.get(i);
			Elements tds = tr.select("td");
			if(tds.size()==2 && tds.get(0).text().equals("Sum of partition offsets")){
				totalOffset += Long.parseLong(tds.get(1).text()); 
			}
		}
		topicInfo.put("name", topic);
		topicInfo.put("totalSize", totalOffset);
		topicInfo.put("availableSize", totalOffset);
		return topicInfo;
	}

	public JSONObject topicStatsUseKafdrop(String topic) throws IOException {
		String kafdropUrl = env.getProperty("kafka.kafdropUrl");
		String url = kafdropUrl+"/topic/"+topic+"/json";
		JSONObject data = JSON.parseObject(OkHttp.get(url));
		JSONObject topicInfo = data.getJSONObject("topic");
		topicInfo.remove("partitions");
		topicInfo.remove("underReplicatedPartitions");
		topicInfo.remove("config");
		topicInfo.remove("preferredReplicaPercent");
		return topicInfo;
	}
	
	@RequestMapping(value = "/sinks/{topic:.+}/consumers", method = RequestMethod.GET)
	@ResponseBody
	public ResultInfo getComsumers(@PathVariable("topic") String topic) {
		ResultInfo info = new ResultInfo();
		List consumers = Lists.newArrayList();
		try {
			String paras = "[{\"name\":\"sEcho\",\"value\":1},{\"name\":\"iColumns\",\"value\":5},{\"name\":\"sColumns\",\"value\":\",,,,\"},{\"name\":\"iDisplayStart\",\"value\":0},{\"name\":\"iDisplayLength\",\"value\":10000},{\"name\":\"mDataProp_0\",\"value\":\"id\"},{\"name\":\"mDataProp_1\",\"value\":\"group\"},{\"name\":\"mDataProp_2\",\"value\":\"topic\"},{\"name\":\"mDataProp_3\",\"value\":\"consumerNumber\"},{\"name\":\"mDataProp_4\",\"value\":\"activeNumber\"}]";		
			String keUrl = env.getProperty("kafka.keUrl");
			String url = keUrl+"/ke/consumer/list/table/ajax?aoData="+URLEncoder.encode(paras, "utf8");
			JSONObject data = JSON.parseObject(OkHttp.get(url));
			JSONArray aaData = data.getJSONArray("aaData");
			String checkStr = "\""+topic+"\"";
			if(topic.length()>50)
				checkStr = "\""+topic.substring(0, 50)+"...";
			for (int i=0; i<aaData.size(); i++) {
				JSONObject item = aaData.getJSONObject(i);
				if(item.getString("topic").contains(checkStr)){
					String str = JSON.toJSONString(item).replaceAll("</?[^>]+>", "");
					consumers.add(JSON.parse(str));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		info.setCode(HttpStatus.OK.value());
		info.setStatus("success");
		info.setResult(consumers);
		
		return info;
	}
	
	@RequestMapping(value = "/sinks/{topic:.+}/{group:.+}/stats", method = RequestMethod.GET)
	@ResponseBody
	public ResultInfo getSinkInfo(@PathVariable("topic") String topic, @PathVariable("group") String group) {
		ResultInfo info = new ResultInfo();
		Map<String, Object> map = Maps.newHashMap();
		try {
			//comsumerStatsUseKe(topic, group, map);
			comsumerStatsUseKafkaManager(topic, group, map);
		} catch (Exception e) {
			System.out.println("==error@url==/sinks/"+topic+"/"+group+"/stats");
			e.printStackTrace();
			map.put("topic", topic);
			map.put("group", group);
			map.put("totalLogSize", 0);
			map.put("totalOffset", 0);
			map.put("totalLag", 0);
		}
		info.setCode(HttpStatus.OK.value());
		info.setStatus("success");
		info.setResult(map);
		
		return info;
	}

	public void comsumerStatsUseKafkaManager(String topic, String group, Map<String, Object> map) throws IOException {
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
		map.put("topic", topic);
		map.put("group", group);
		map.put("totalLogSize", totalLogSize);
		map.put("totalOffset", totalOffset);
		map.put("totalLag", totalLogSize - totalOffset);
		//for comupte the current speed
		String speed = computeSinkSpeed(group,totalOffset);
		map.put("speed", speed);
		map.put("lastTime",sinksTime.get(group));
	}

	public void comsumerStatsUseKe(String topic, String group, Map<String, Object> map)
			throws UnsupportedEncodingException, IOException {
		String paras = "[{\"name\":\"sEcho\",\"value\":1},{\"name\":\"iColumns\",\"value\":7},{\"name\":\"sColumns\",\"value\":\",,,,,,\"},{\"name\":\"iDisplayStart\",\"value\":0},{\"name\":\"iDisplayLength\",\"value\":10000},{\"name\":\"mDataProp_0\",\"value\":\"partition\"},{\"name\":\"mDataProp_1\",\"value\":\"logsize\"},{\"name\":\"mDataProp_2\",\"value\":\"offset\"},{\"name\":\"mDataProp_3\",\"value\":\"lag\"},{\"name\":\"mDataProp_4\",\"value\":\"owner\"},{\"name\":\"mDataProp_5\",\"value\":\"created\"},{\"name\":\"mDataProp_6\",\"value\":\"modify\"}]";		
		String keUrl = env.getProperty("kafka.keUrl");
		String url = keUrl+"/ke/consumer/offset/"+group+"/"+topic+"/ajax?aoData="+URLEncoder.encode(paras, "utf8");
		JSONObject data = JSON.parseObject(OkHttp.get(url));
		JSONArray aaData = data.getJSONArray("aaData");
		long totalLogSize = 0;
		long totalOffset = 0;
		for (int i=0; i<aaData.size(); i++) {
			JSONObject item = aaData.getJSONObject(i);
			totalLogSize += Long.parseLong(item.getString("logsize").replaceAll("</?[^>]+>", ""));
			String size = item.getString("offset").replaceAll("</?[^>]+>", "");
			totalOffset += Long.parseLong(size);
		}
		map.put("topic", topic);
		map.put("group", group);
		map.put("totalLogSize", totalLogSize);
		map.put("totalOffset", totalOffset);
		map.put("totalLag", totalLogSize - totalOffset);
	}

	@RequestMapping(value = "/topics/{topic:.+}/stats", method = RequestMethod.GET)
	@ResponseBody
	public ResultInfo getTopicInfo(@PathVariable("topic") String topic) {
		ResultInfo info = new ResultInfo();
		Page<Connector> sinks = connectorManager.findByCategoryAndTopic("sink", topic, null);
		String kafdropUrl = env.getProperty("kafka.kafdropUrl");
		String url = kafdropUrl+"/topic/"+topic+"/json";
		JSONObject topicInfo = null;
		try {
			JSONObject data = JSON.parseObject(OkHttp.get(url));
			topicInfo = data.getJSONObject("topic");
			topicInfo.remove("partitions");
			topicInfo.remove("underReplicatedPartitions");
			topicInfo.remove("config");
			topicInfo.remove("preferredReplicaPercent");
		} catch (Exception e) {
			e.printStackTrace();
			topicInfo = new JSONObject();
			topicInfo.put("name", topic);
			topicInfo.put("totalSize", 0);
			topicInfo.put("availableSize", 0);
		}
		
		List<Object> sinksInfo = Lists.newArrayList();
		for (Iterator<Connector> iter = sinks.iterator(); iter.hasNext();) {
			Connector sink = (Connector) iter.next();
			String group = "connect-"+sink.getName();
			Map<String, Object> map = (Map<String, Object>) getSinkInfo(topic, group).getResult();
			map.put("sinkId", sink.getId());
			map.put("sinkName", sink.getName());
			sinksInfo.add(map);
		}
		topicInfo.put("sinksInfo", sinksInfo);
		
		info.setCode(HttpStatus.OK.value());
		info.setStatus("success");
		info.setResult(topicInfo);
		
		return info;
	}
	/**
	 * compute the sink to dest speed
	 * @author xubo
	 * @return num/second 
	 * */
	public String computeSinkSpeed(String group,Long consumerOffset){
		double ingestSpeed = 0.0;
		String result ="0.0";
		DecimalFormat df = new DecimalFormat("0.0");
		if(Strings.isNullOrEmpty(group)){
			return result;
		}
		//find the connect by name
		String[] name = group.split("-");
		Connector sinkConnector = null;
		Long totalTime=0l;
		Long diffOffset =0l;
		if(name.length>1){
			sinkConnector = connectorManager.findConnectorByName(name[1]);
			if(sinkConnector == null ){
				return result;
			}
			Timestamp current = new Timestamp(System.currentTimeMillis()); 
			ConsumerRecords oldRecords =sinksRecords.get(group);
			if(oldRecords == null){
				oldRecords = new ConsumerRecords();
				totalTime=(current.getTime()-sinkConnector.getModifedTime().getTime())/1000;
				diffOffset = consumerOffset;
			}else{
				totalTime = (current.getTime() -oldRecords.getConsumerTime().getTime())/1000;
				diffOffset = consumerOffset - oldRecords.getConsumerOffset();
			}
			if(totalTime == 0) return result;
			ingestSpeed = (double)diffOffset/totalTime;
			result = df.format(ingestSpeed);
			oldRecords.setConsumerOffset(consumerOffset);
			oldRecords.setConsumerTime(current);
			if(!sinksTime.containsKey(group)){
				sinksTime.put(group, sinkConnector.getModifedTime());
				log.info("*****************sinksTime not record");
			}
			if(diffOffset>0){
				sinksTime.put(group, current);
			}
			sinksRecords.put(group, oldRecords);
		}
		return result;
	}
	/*
	public  static void main(String[] args){
		System.out.println(4/1344);
	}*/
}
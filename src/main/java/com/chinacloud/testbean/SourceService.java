package com.chinacloud.testbean;

import java.util.Map;

import com.chinacloud.bean.JdbcConfigBean;
import com.chinacloud.bean.JdbcSourceBean;
import com.chinacloud.confluent.RESTClient;
import com.chinacloud.process.ConnectConfigProcessor;
import com.chinacloud.utils.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

import net.sf.json.JSONObject;

public class SourceService {
     public static void main(String[] args) {		 
			 ConnectConfigProcessor.initTypeNameTable("/opt/server/blackhole/blackhole/src/main/resources/connector_type_name.dict");
			 JdbcSourceBean  model = new JdbcSourceBean();		 
			 model.setName("test_my1000");
			 model.setConnectorType("jdbc");
			 model.setOwner("admin");
			 JdbcConfigBean  config=new JdbcConfigBean();
			 config.setJobName("test_mysql_1228");
			 config.setDatabaseIp("172.16.50.22");;
			 config.setDbName("testimp");
			 config.setDbType("mysql");
			 config.setPort("3306");
			 config.setUsername("root");
			 config.setPassword("654321");
			 config.setTableName("t_people_1000");
			 config.setJobName(model.getName());
			 if(model.getConnectorType().equals("jdbc")){
				 config.setTopicPrefix("mysql2011_"); 
			 }else{
				 config.setTopicPrefix("oracle_");
			 }
			 config.setCheckColumn("id");	
			 config.setMode("incrementing");
			 model.setJdbcConfigBean(config);	 
			 
			 Map<String,Object> configMapper=ConnectConfigProcessor.convertToConfig(model.getSourceKey(),config);		 
			configMapper.putAll(model.getConnectionUrl(config.getDbType(), 
														config.getDatabaseIp(),
														config.getPort(), 
														config.getDbName(),
														config.getUsername(),
														config.getPassword()));
			 configMapper.putAll(model.getConnectorClass("jdbc"));		
			 model.setConfig(configMapper);	
			 
			JSONObject json = new JSONObject();
			json.put("name", model.getName());
			 try {
				 json.put("config", JsonUtils.beanToJson(configMapper));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		System.out.println(json);
		RESTClient r = new RESTClient();
		r.confluent.setUrl("http://172.16.50.23:8083");
		String result ="";
		// result = r.addConnector(json.toString());
		System.out.println(result);
	}
}

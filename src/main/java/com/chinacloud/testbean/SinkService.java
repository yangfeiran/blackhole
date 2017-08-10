package com.chinacloud.testbean;

import java.util.Map;

import com.chinacloud.bean.HdfsConfigBean;
import com.chinacloud.bean.HdfsSinkBean;
import com.chinacloud.confluent.RESTClient;
import com.chinacloud.process.ConnectConfigProcessor;
import com.chinacloud.utils.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

import net.sf.json.JSONObject;

public class SinkService {
	
	 public static void main(String[] args) {		 
		 	 ConnectConfigProcessor.initTypeNameTable("/opt/server/blackhole/blackhole/src/main/resources/connector_type_name.dict");
			 HdfsSinkBean  model = new HdfsSinkBean();		 
			 model.setName("test_hive1958");
			 model.setConnectorType("hdfs");
			 		 

			 HdfsConfigBean config = new HdfsConfigBean();
			 config.setHdfsUrl("hdfs://master");
			 config.setHiveMetastoreUris("thrift://master:9083");
			 config.setHiveDatabase("bbtest1958");
			 config.setHiveConfDir("/etc/hive/conf");
			 config.setJobName(model.getName());
			 config.setTopicName("mysql2010_t_people_1000");
			 config.setHiveIntegration(true);
			 config.setTopicsDir("/tmp/topics1228");
			 config.setLogsDir("/tmp/topics1228");
			 config.setLocale("TimeBasedPartitioner");
			 config.setTimezone("UTC");
			 model.setHdfsConfigBean(config);
			 config.setHiveIntegration(true);
			 
			 Map<String,Object> configMapper=ConnectConfigProcessor.convertToConfig(model.getSourceKey(),config);		 
			
			 if(config.getHiveIntegration()){
				 configMapper.put("hive.integration",config.getHiveIntegration());
				 configMapper.put("hive.metastore.uris",config.getHiveMetastoreUris());
				 configMapper.put("hive.conf.dir",config.getHiveConfDir());
				 configMapper.put("hive.database",config.getHiveDatabase());
				 configMapper.put("schema.compatibility",config.getSchemaCompatibility());
			 }
			 
			 configMapper.putAll(model.getFormatClass("AvroFormat"));
			 configMapper.putAll(model.getPartitionerClass("Daily",config.getFieldName()));		 
			 configMapper.putAll(model.getConnectorClass("hdfs"));
			 
			 model.setConfig(configMapper);		 
			 
			 System.err.println(model.getConfig());
			 
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
		    //  String result = r.addConnector(json.toString());
	 }
}

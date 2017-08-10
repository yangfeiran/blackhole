package com.chinacloud;

import java.util.Map;

import org.aspectj.lang.annotation.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.chinacloud.bean.JdbcConfigBean;
import com.chinacloud.bean.JdbcSourceBean;
import com.chinacloud.process.ConnectConfigProcessor;
import com.chinacloud.utils.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;


@RunWith(SpringRunner.class)
@SpringBootTest
public class HttpRestTemplateTest {
	@Autowired  
    protected RestTemplate restTemplate;  
    @BeforeClass  
    public static void setUpBeforeClass() throws Exception {  
    }  
  
    @Before(value = "")  
    public void setUp() throws Exception {  
          
    }  
   

    @Test  
    public void test() {  
    	 JdbcSourceBean  model = new JdbcSourceBean();		 
		 model.setName("test_mysql_1227");
		 model.setConnectorType("jdbc");
		 model.setOwner("admin");
		 JdbcConfigBean  config=new JdbcConfigBean();
		 config.setJobName("test_mysql_1227");
		 config.setDatabaseIp("172.16.50.22");;
		 config.setDbName("testimp");
		 config.setDbType("mysql");
		 config.setPort("3306");
		 config.setUsername("root");
		 config.setPassword("654321");
		 config.setTableName("t_people_1000");
		 config.setTableTypes("table");
		 config.setTaskNum(1);
		 if(model.getConnectorType().equals("jdbc")){
			 config.setTopicPrefix("mysql_"); 
		 }else{
			 config.setTopicPrefix("oracle_");
		 }
		 config.setCheckColumn("id");	
		 config.setMode("incrementing");
		 model.setJdbcConfigBean(config);	 
		 
		 Map<String, Object> configMapper=ConnectConfigProcessor.convertToConfig(model.getSourceKey(),config);		 
		configMapper.putAll(model.getConnectionUrl(config.getDbType(), 
													config.getDatabaseIp(),
													config.getPort(), 
													config.getDbName(),
													config.getUsername(),
													config.getPassword()));
		 configMapper.putAll(model.getConnectorClass("jdbc"));		
		 model.setConfig(configMapper);	
		 
		 String configs = null;
		try {
			configs = JsonUtils.beanToJson(configMapper);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		 
/*        String string = restTemplate.postForObject(url, request, responseType, uriVariables)
        		("http://172.16.50.23:8083/connectors/getlist.json", String.class);  */
        System.out.print(configs);  

    }  
}


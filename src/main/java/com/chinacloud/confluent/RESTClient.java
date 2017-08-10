package com.chinacloud.confluent;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.chinacloud.bean.HdfsSinkBean;
import com.chinacloud.config.Confluent;
import com.netflix.ribbon.proxy.annotation.Http.HttpMethod;
 
/**
 * function:RestTemplate调用confluent资源
 */
@Component
@ConfigurationProperties
public class RESTClient {
	@Autowired
	public Confluent confluent;
    @Autowired
    private RestTemplate template;
    
    /**
     * add connector
     * 
     * **/
    public ResponseEntity<String> addConnectors(String json) {
		HttpHeaders headers = new HttpHeaders();
		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		headers.setContentType(type);
		headers.add("Accept", MediaType.APPLICATION_JSON.toString());
		HttpEntity<String> formEntity = new HttpEntity<String>(json,headers);
		ResponseEntity<String> response = template.postForEntity(confluent.getUrl() + "/connectors", formEntity, String.class);
        return response;
    }
    
    /**
     * delete connector by name
     * @param name connecter name
     * @return 
     * */
    public void removeByName(String name) {
    	HttpHeaders headers = new HttpHeaders();
		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		headers.setContentType(type);
		headers.add("Accept", MediaType.APPLICATION_JSON.toString());
		HttpEntity<String> formEntity = new HttpEntity<String>("parameters",headers);
        template.delete(confluent.getUrl() + "/connectors/"+name, formEntity);
    }
    
    /**
     * Get status by name
     * @param name connecter name
     * @return String
     * */
    public ResponseEntity<String> getConnectorStatusByName(String name) {
    	HttpHeaders headers = new HttpHeaders();
		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		headers.setContentType(type);
		headers.add("Accept", MediaType.APPLICATION_JSON.toString());
		HttpEntity<String> formEntity = new HttpEntity<String>("parameters",headers);
		ResponseEntity<String> response = template.getForEntity
				(confluent.getUrl() + "/connectors/"+name+"/status", String.class, formEntity);
        //return template.getForObject(confluent.getUrl() + "/connectors/"+name+"/status", String.class, formEntity); 
		return response;
    }

    /**
     * Stop Connectors by name
     * @param name connecter name
     * @return String
     * */
	public void stopConnectorStatusByName(String name) {
		HttpHeaders headers = new HttpHeaders();
		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		headers.setContentType(type);
		headers.add("Accept", MediaType.APPLICATION_JSON.toString());
		HttpEntity<String> formEntity = new HttpEntity<String>("parameters",headers);
		String url = confluent.getUrl() + "/connectors/"+name+"/pause";
		template.put(url, formEntity);
	}

	 /**
     * start Connectors by name
     * @param name connecter name
     * @return String
     * */
	public void startConnectorStatusByName(String name) {
		HttpHeaders headers = new HttpHeaders();
		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		headers.setContentType(type);
		headers.add("Accept", MediaType.APPLICATION_JSON.toString());
		HttpEntity<String> formEntity = new HttpEntity<String>("parameters",headers);
		String url = confluent.getUrl() + "/connectors/"+name+"/resume";
		template.put(url, formEntity);
	}

	 /**
     * edit Connectors by name
     * @param name connecter name
     * @return String
     * */
	public void updateConnectorByName(String connectorName,Map<String, Object> config) {
		// TODO Auto-generated method stub
	}
	
	/**
     * get Connectors confluent config by name
     * @param name connecter name
     * @return String
     * */
	public ResponseEntity<String> getConnectorConfigByName(String name) {
		HttpHeaders headers = new HttpHeaders();
		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		headers.setContentType(type);
		headers.add("Accept", MediaType.APPLICATION_JSON.toString());
		HttpEntity<String> formEntity = new HttpEntity<String>("parameters",headers);
		ResponseEntity<String> response = template.getForEntity
				(confluent.getUrl() + "/connectors/"+name+"/config", String.class, formEntity);
		return response;		
	}
}
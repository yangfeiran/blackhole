package com.chinacloud.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.chinacloud.bean.ResultInfo;
import com.chinacloud.confluent.RESTClient;
import com.chinacloud.dao.ConnectorDao;
import com.chinacloud.dao.ConnectorSessionsDao;
import com.chinacloud.model.Connector;
import com.google.common.base.Strings;

@Service
public class ConnectorManager {

    @Autowired
    private ConnectorDao connectorDao;
    
    @Autowired
    private ConnectorSessionsDao connectorSessionsDao;
    
    @Autowired
	private RESTClient client;

    public void saveConnector(Connector connector) {
        connectorDao.save(connector);
    }

    public ResultInfo deleteSource(String id) {
    	ResultInfo info = new ResultInfo();
        Connector connector = connectorDao.findOne(id);
        if (connector != null) {
        	try{
	            connectorDao.delete(id);
	            //同时删除配置信息表
	            deleteConnectorSessions(id);
	            //delete confluetn connector
	            client.removeByName(connector.getName());
	            info.setCode(HttpStatus.NO_CONTENT.value());
	    		info.setStatus("success");
	    		info.setMsg("delete source connector failed");
        	}catch(Exception e){
        		info.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        		info.setStatus("failure");
        		info.setMsg("no such source connectorId: " + id);
        	}
        } else {
        	info.setCode(HttpStatus.BAD_REQUEST.value());
    		info.setStatus("failure");
    		info.setMsg("no such source connectorId: " + id);
        }
        return info;
    }

    private void deleteConnectorSessions(String id) {
		connectorSessionsDao.deleteByConnectorId(id);
	}

	public Iterable<Connector> getAllConnectors() {
        Iterable<Connector> allconnectors = connectorDao.findAll();
        return allconnectors;
    }
    
    public Page<Connector> getConnectors(Pageable pageable){
    	Page<Connector> connectors = connectorDao.findAll(pageable);
        return connectors;
    }
    
    public Connector getConnectorById(String id) {
    	Connector connector = connectorDao.findOne(id);
    	return connector;
	}

    public void updateConnector(Connector connector) {
        connectorDao.save(connector);
    }
    
    public Page<Connector> findByTopic(String topic, Pageable pageable){
    	Page<Connector> connectors = connectorDao.findByTopicOrderByModifedTimeDesc(topic, pageable);
        return connectors;
    }
    
    public Page<Connector> findByCategory(String category, Pageable pageable){
    	Page<Connector> connectors = connectorDao.findByCategoryOrderByModifedTimeDesc(category, pageable);
        return connectors;
    }

	public List<Connector> findByParentId(String parentId) {
		List<Connector> allconnectors = connectorDao.findByParentId(parentId);
        return allconnectors;
	}

	public Page<Connector> findByCategoryAndTopic(String category, String topic, Pageable pageable) {
		return connectorDao.findByCategoryAndTopicOrderByModifedTimeDesc(category, topic, pageable);
	}

	public Page<Connector> query(String category, String topic, Integer page, Integer size) {
		Pageable pageable = null;
		if(page != null && size != null){
			pageable = new PageRequest(page, size);
		}
		if(!Strings.isNullOrEmpty(category)){
			if(!Strings.isNullOrEmpty(topic)){
				return findByCategoryAndTopic(category, topic, pageable);
			}else{
				return findByCategory(category, pageable);
			}
		}else{
			if(!Strings.isNullOrEmpty(topic)){
				return findByTopic(topic, pageable);
			}else{
				return getConnectors(pageable);
			}
		}
	}

	public Page<Connector> getSources(String queryStr, Integer page, Integer size) {
		Pageable pageable = null;
		if(page != null && size != null){
			pageable = new PageRequest(page, size);
		}else{
			
		}
		//PageRequest(page, size, Direction.DESC, "id")
		if(!Strings.isNullOrEmpty(queryStr)){
			return connectorDao.findByCategoryAndNameOrderByModifedTimeDesc("source", queryStr, pageable);
		}else{
			return connectorDao.findByCategoryOrderByModifedTimeDesc("source", pageable);
		}
	}

	public ResultInfo deleteSink(String sourceId, String sinkId) {
		ResultInfo info = new ResultInfo();
        Connector connector = connectorDao.findOne(sinkId);
        if (connector != null) {
        	if(connector.getParentId().equals(sourceId)){
        		try{
	        		connectorDao.delete(sinkId);
	        		//同时删除配置信息表
	                deleteConnectorSessions(sinkId);
	
	                //delete confluetn connector
	                client.removeByName(connector.getName());
	                
	                info.setCode(HttpStatus.NO_CONTENT.value());
	        		info.setStatus("success");
	        		info.setMsg("delete sink connector success");
        		}catch(Exception e){
	                info.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	        		info.setStatus("failed");
	        		info.setMsg("delete sink connector failed");
        		}
        	}else{
        		info.setCode(HttpStatus.BAD_REQUEST.value());
        		info.setStatus("failure");
        		info.setMsg("the sink connector with sinkId: " + sinkId + " is not under source connector:"+sourceId);
        	}   
        } else {
        	info.setCode(HttpStatus.BAD_REQUEST.value());
    		info.setStatus("failure");
    		info.setMsg("no such sink connectorId: " + sinkId);
        }
        //删除与source对应的sink
        //TODO
        
        return info;
	}
	
	public List<Connector> findByTopic(String topic) {
		List<Connector> allconnectors = connectorDao.findByTopic(topic);
        return allconnectors;
	}

	public List<Connector> findConnectorsByName(String name) {
		List<Connector> allconnectors = connectorDao.findConnectorsByName(name);
        return allconnectors;
	}
	
	public Connector findConnectorByName(String name){
		return connectorDao.findConnectorByName(name);
	}
	public List<Connector> findConnectorByCatagory(String name){
		return connectorDao.findByConnectorTypeOrderByModifedTimeDesc(name);
	}
}

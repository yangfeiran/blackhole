package com.chinacloud.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.chinacloud.dao.ConnectorHistoryDao;
import com.chinacloud.model.ConnectorHistory;


@Service
public class ConnectorHistoryManager {

    @Autowired
    private ConnectorHistoryDao historyDao;

    public void saveConnectorHistory(ConnectorHistory history) {
        historyDao.save(history);
    }

    public void deleteConnectorHistory(String id) {
        ConnectorHistory history = historyDao.findOne(id);
        if (history != null) {
            historyDao.delete(id);
        } else {
            throw new IllegalStateException("no such historyId: " + id);
        }
    }

    public Iterable<ConnectorHistory> getAllConnectorHistorys() {
        Iterable<ConnectorHistory> allhistorys = historyDao.findAll();
        return allhistorys;
    }
    
    public Page<ConnectorHistory> getConnectorHistorys(Pageable pageable){
    	Page<ConnectorHistory> historys = historyDao.findAll(pageable);
        return historys;
    }
    
    public ConnectorHistory getConnectorHistoryById(String id) {
    	ConnectorHistory history = historyDao.findOne(id);
    	return history;
	}

    public void updateConnectorHistory(ConnectorHistory history) {
        historyDao.save(history);
    }
    
    public Iterable<ConnectorHistory> findBySinkId(String source, Pageable pageable){
    	Iterable<ConnectorHistory> historys = historyDao.findBySinkId(source);
        return historys;
    }
    
   
}

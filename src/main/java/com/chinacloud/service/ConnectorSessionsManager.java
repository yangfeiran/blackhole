package com.chinacloud.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chinacloud.dao.ConnectorSessionsDao;
import com.chinacloud.model.ConnectorSessions;


@Service
public class ConnectorSessionsManager {

    @Autowired
    private ConnectorSessionsDao connectorSessionsDao;

    public void saveConnectorSession(ConnectorSessions connectorSession) {
        connectorSessionsDao.save(connectorSession);
    }
    
    public void saveConnectorSessions(Iterable<ConnectorSessions> connectorSessions) {
        connectorSessionsDao.save(connectorSessions);
    }

    public void deleteConnectorSessions(String connectorId) {
    	Iterable<ConnectorSessions> connectorSessions = connectorSessionsDao.findByConnectorId(connectorId);
        if (connectorSessions != null) {
            connectorSessionsDao.delete(connectorSessions);
        } else {
            throw new IllegalStateException("no such connectorId: " + connectorId);
        }
    }

    public Iterable<ConnectorSessions> getAllConnectorSessions() {
        Iterable<ConnectorSessions> allconnectorSessionss = connectorSessionsDao.findAll();
        return allconnectorSessionss;
    }
    
    public Iterable<ConnectorSessions> getConnectorSessionsByConnectorId(String connectorId) {
    	Iterable<ConnectorSessions> connectorSessions = connectorSessionsDao.findByConnectorId(connectorId);
    	return connectorSessions;
	}
    
    public ConnectorSessions findByConnectorIdAndPropname(String connectorId, String propname) {
    	return connectorSessionsDao.findByConnectorIdAndPropname(connectorId, propname);
	}

    public void updateConnectorSession(ConnectorSessions connectorSession) {
        connectorSessionsDao.save(connectorSession);
    }
    
    public void updateConnectorSessions(Iterable<ConnectorSessions> connectorSessions) {
        connectorSessionsDao.save(connectorSessions);
    }
}

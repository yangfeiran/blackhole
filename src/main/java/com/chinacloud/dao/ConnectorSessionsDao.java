package com.chinacloud.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import com.chinacloud.model.ConnectorSessions;

@Transactional
public interface ConnectorSessionsDao extends PagingAndSortingRepository<ConnectorSessions, Integer> {
	
    Iterable<ConnectorSessions> findByConnectorId(String connectorId);
	
	void deleteByConnectorId(String connectorId);

	ConnectorSessions findByConnectorIdAndPropname(String connectorId, String propname);
}

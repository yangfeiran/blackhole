package com.chinacloud.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import com.chinacloud.model.ConnectorHistory;

@Transactional
public interface ConnectorHistoryDao extends PagingAndSortingRepository<ConnectorHistory, String> {

	Iterable<ConnectorHistory> findBySinkId(String sinkId);
}

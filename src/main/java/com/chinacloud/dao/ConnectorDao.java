package com.chinacloud.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import com.chinacloud.model.Connector;

@Transactional
public interface ConnectorDao extends PagingAndSortingRepository<Connector, String> {

	Page<Connector> findByTopicOrderByModifedTimeDesc(String topic, Pageable pageable);
	
	Page<Connector> findByCategoryOrderByModifedTimeDesc(String category, Pageable pageable);
	
	//@Query("select c from Connector c where parent_id = ?1")
	List<Connector> findByParentId(String parentId);
	
	List<Connector> findByConnectorTypeOrderByModifedTimeDesc(String cy);

	Page<Connector> findByCategoryAndTopicOrderByModifedTimeDesc(String category, String topic, Pageable pageable);

	Page<Connector> findByCategoryAndNameOrderByModifedTimeDesc(String string, String queryStr, Pageable pageable);

	List<Connector> findByTopic(String topic);

	List<Connector> findConnectorsByName(String name);
   
	Connector findConnectorByName(String name);
}

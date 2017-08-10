package com.chinacloud.dao;

import com.chinacloud.model.Job;
import com.chinacloud.model.Processor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface ProcessorDao extends PagingAndSortingRepository<Processor, String> {

    @Query("select count(1) from Processor p where p.config like %?1%")
    int findConnectionIDIfExist(String connectionID);

    @Query("select p from Processor p where p.jobId = ?1 ")
    List<Processor> findByJobId(String jobId);
}

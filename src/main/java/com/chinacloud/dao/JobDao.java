package com.chinacloud.dao;

import com.chinacloud.model.Connector;
import com.chinacloud.model.Job;
import com.chinacloud.utils.TwoTuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Transactional
public interface JobDao extends PagingAndSortingRepository<Job, String> {

    @Modifying(clearAutomatically = true)
    @Query("update Job j set j.state=?2  where j.jobId = ?1")
    void updateJobStatus(String jobId, String action);

    @Modifying(clearAutomatically = true)
    @Query("update Job j set j.cron=?2  where j.jobId = ?1")
    void updateJobScheduler(String jobId, String cron);

    @Modifying(clearAutomatically = true)
    @Query("update Job j set j.jobInfo=?2,j.lables=?3 where j.jobId = ?1")
    void updateJobInfo(String jobId, String jobInfo, String lables);

    @Modifying(clearAutomatically = true)
    @Query("update Job j set j.totalCount=?2,j.dataExtractTime=?3 where j.jobId = ?1")
    void updateJobTotalCount(String jobId, String count, Timestamp dataExtractTime);

    @Query("select count(1) from Job j where j.jobName = ?1")
    int findJobNameIfExist(String jobName);

    @Query("select j.jobName as jobName,j.output as output,j.totalCount as totalCount from Job j order by j.totalCount")
    List<Map<String,String>> getAllTotalCountByOrder();

    @Query("select j.output as output,sum(j.totalCount) as totalCount from Job j group by j.output")
    List<Map<String,String>> getAllTotalCountTogether();

    @Query("select j from Job j where j.jobId = ?1 and (j.jobName like %?2% or j.lables like '[%?2%]' or j.jobInfo like %?2%)")
    Job findByQuery(String id, String query);

    @Query("select j.state from Job j where j.jobId = ?1")
    String getJobStatus(String jobId);
}

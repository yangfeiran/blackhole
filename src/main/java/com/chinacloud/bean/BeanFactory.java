package com.chinacloud.bean;

import com.chinacloud.config.Datafeed;
import com.chinacloud.config.DatafeedRest;
import com.chinacloud.dao.JobDao;
import com.chinacloud.process.CheckTotalProcess;
import com.chinacloud.process.CheckTotalThread;
import com.chinacloud.utils.HDFSUtil;
import com.chinacloud.utils.HiveUtils;
import com.thinkbiganalytics.nifi.rest.client.NifiRestClientConfig;
import com.thinkbiganalytics.nifi.v1.rest.client.NiFiRestClientV1;
import it.sauronsoftware.cron4j.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Administrator on 2017/6/30.
 */
@Configuration
public class BeanFactory {
    @Autowired
    DatafeedRest datafeedRest;
    @Autowired
    Datafeed datafeed;
    @Autowired
    JobDao jobDao;
    @Autowired
    HiveUtils hiveUtils;
    @Autowired
    HDFSUtil hdfsUtil;

    @Bean
    public NiFiRestClientV1 NiFiRestClientV1(){
        NifiRestClientConfig nifiRestClientConfig = new NifiRestClientConfig(datafeedRest.getHost(), datafeedRest.getUsername(), datafeedRest.getPassword(), datafeedRest.getApiPath());
        nifiRestClientConfig.setPort(datafeedRest.getPort());
        return new NiFiRestClientV1(nifiRestClientConfig);
    }

    @Bean
    public CheckTotalProcess CheckTotalProcess(){
        CheckTotalProcess checkTotalProcess = new CheckTotalProcess();
        Thread thread = new CheckTotalThread(jobDao,hiveUtils,hdfsUtil);
        Scheduler scheduler = new Scheduler();
        checkTotalProcess.setScheduler(scheduler);
        checkTotalProcess.setScheduleId(scheduler.schedule(datafeed.getSchedule(), thread));
        checkTotalProcess.getScheduler().start();
        return checkTotalProcess;
    }
}

package com.chinacloud.process;

import com.chinacloud.dao.JobDao;
import com.chinacloud.model.Job;
import com.chinacloud.utils.HDFSUtil;
import com.chinacloud.utils.HiveUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Iterator;

/**
 * Created by Administrator on 2017/7/4.
 */
public class CheckTotalThread extends Thread{
    JobDao jobDao;
    HiveUtils hiveUtils;
    HDFSUtil hdfsUtil;
    public CheckTotalThread(JobDao jobDao, HiveUtils hiveUtils,HDFSUtil hdfsUtil){
        this.jobDao=jobDao;
        this.hiveUtils=hiveUtils;
        this.hdfsUtil = hdfsUtil;
    }
    @Override
    public void run() {
        super.run();
        Iterable<Job> all = jobDao.findAll();
        Iterator<Job> iterator = all.iterator();
        while(iterator.hasNext()){
            Job next = iterator.next();
            String count=null;
            if(next.getOutput().equalsIgnoreCase("hive")) {
                 count= hiveUtils.checkTotal(next.getDatabase(), next.getTable());
            }else if(next.getOutput().equalsIgnoreCase("hdfs")){
                 count = Integer.toString(hdfsUtil.countFilesInPath(next.getJobName()));
            }
            jobDao.updateJobTotalCount(next.getJobId(),count,new Timestamp(System.currentTimeMillis()));
        }
    }
}

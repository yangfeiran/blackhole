package com.chinacloud.process;


import com.chinacloud.config.Datafeed;
import com.chinacloud.dao.JobDao;
import com.chinacloud.model.Job;
import com.chinacloud.utils.HiveUtils;
import it.sauronsoftware.cron4j.Scheduler;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.util.Iterator;

/**
 * Created by Administrator on 2017/7/4.
 */
public class CheckTotalProcess  {

    private Scheduler scheduler;
    private String scheduleId;


    public void updateCron(String pattern){
        scheduler.reschedule(scheduleId,pattern);
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }
}

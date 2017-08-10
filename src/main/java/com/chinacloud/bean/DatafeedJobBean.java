package com.chinacloud.bean;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/30.
 */
public class DatafeedJobBean {
    String jobName;
    String templateId;
    String jobInfo;
    List<String> labels;
    String cron;
    Map<String, Object> config;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getJobInfo() {
        return jobInfo;
    }

    public void setJobInfo(String jobInfo) {
        this.jobInfo = jobInfo;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public Map<String, Object>  getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object>  config) {
        this.config = config;
    }
}

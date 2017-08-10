package com.chinacloud.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;

@Entity
@Table(name = "job")
public class Job implements Serializable {

	private static final long serialVersionUID = 1L;

    @Id
	private String jobId;
	@Column(name="jobname")
	private String jobName;
	@Column(name="templateid")
	private String templateId;
	@Column(name="cron")
	private String cron;
	@Column(name="runcount")
	private int runCount;
	@Column(name="failcount")
	private int failCount;
	@Column(name="totalcount")
	private String totalCount;
	@Column(name="state")
	private String state;
	@Column(name="dataextracttime")
	private Timestamp dataExtractTime;
	@Column(name="createtime")
	private Timestamp createTime;
	@Column(name="lables")
	private String lables;
	@Column(name="jobinfo")
	private String jobInfo;
	@Column(name="data_base")
	private String database;
	@Column(name="tablename")
	private String table;
	@Column(name="input")
	private String input;
	@Column(name="output")
	private String output;


	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

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

	public int getRunCount() {
		return runCount;
	}

	public void setRunCount(int runCount) {
		this.runCount = runCount;
	}

	public int getFailCount() {
		return failCount;
	}

	public void setFailCount(int failCount) {
		this.failCount = failCount;
	}

	public String getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Timestamp getDataExtractTime() {
		return dataExtractTime;
	}

	public void setDataExtractTime(Timestamp dataExtractTime) {
		this.dataExtractTime = dataExtractTime;
	}

	public String getLables() {
		return lables;
	}

	public void setLables(String lables) {
		this.lables = lables;
	}

	public String getJobInfo() {
		return jobInfo;
	}

	public void setJobInfo(String jobInfo) {
		this.jobInfo = jobInfo;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

    @Override
    public String toString() {
        return "Job{" +
                "jobId='" + jobId + '\'' +
                ", jobName='" + jobName + '\'' +
                ", templateId='" + templateId + '\'' +
                ", cron='" + cron + '\'' +
                ", runCount=" + runCount +
                ", failCount=" + failCount +
                ", totalCount='" + totalCount + '\'' +
                ", state='" + state + '\'' +
                ", dataExtractTime=" + dataExtractTime +
                ", createTime=" + createTime +
                ", lables='" + lables + '\'' +
                ", jobInfo='" + jobInfo + '\'' +
                ", database='" + database + '\'' +
                ", table='" + table + '\'' +
                '}';
    }
}

package com.chinacloud.bean;

public class HdfsConfigBean {

	private int taskNum = 1;
	private String topicName;
	private String jobName;
	private String hdfsUrl;
	private boolean hiveIntegration;
	private String hiveMetastoreUris;
	private String hiveConfDir="/etc/hive/conf";
	private String hadoopConfDir ="/etc/hadoop/conf";
	private String hiveDatabase="default";
	private String flushSize = "10000";
	private String partitionerType="Daily";
	private String fieldName;
	private String locale="TimeBasedPartitioner";
	private String timezone="UTC";
	private String fomatType="AvroFormat";
	private String schemaCompatibility = "BACKWARD";
	private String logsDir="/tmp";
	private String topicsDir="/tmp";

	public String getHadoopConfDir() {
		return hadoopConfDir;
	}

	public void setHadoopConfDir(String hadoopConfDir) {
		this.hadoopConfDir = hadoopConfDir;
	}

	public String getSchemaCompatibility() {
		return schemaCompatibility;
	}

	public void setSchemaCompatibility(String schemaCompatibility) {
		this.schemaCompatibility = schemaCompatibility;
	}

	public String getLogsDir() {
		return logsDir;
	}

	public void setLogsDir(String logsDir) {
		this.logsDir = logsDir;
	}

	public String getTopicsDir() {
		return topicsDir;
	}

	public void setTopicsDir(String topicsDir) {
		this.topicsDir = topicsDir;
	}

	public int getTaskNum() {
		return taskNum;
	}

	public void setTaskNum(int taskNum) {
		this.taskNum = taskNum;
	}

	public String getHdfsUrl() {
		return hdfsUrl;
	}

	public void setHdfsUrl(String hdfsUrl) {
		this.hdfsUrl = hdfsUrl;
	}

	public boolean getHiveIntegration() {
		return hiveIntegration;
	}

	public void setHiveIntegration(boolean hiveIntegration) {
		this.hiveIntegration = hiveIntegration;
	}

	public String getHiveMetastoreUris() {
		return hiveMetastoreUris;
	}

	public void setHiveMetastoreUris(String hiveMetastoreUris) {
		this.hiveMetastoreUris = hiveMetastoreUris;
	}

	public String getHiveConfDir() {
		return hiveConfDir;
	}

	public void setHiveConfDir(String hiveConfDir) {
		this.hiveConfDir = hiveConfDir;
	}

	public String getHiveDatabase() {
		return hiveDatabase;
	}

	public void setHiveDatabase(String hiveDatabase) {
		this.hiveDatabase = hiveDatabase;
	}

	public String getFlushSize() {
		return flushSize;
	}

	public void setFlushSize(String flushSize) {
		this.flushSize = flushSize;
	}

	public String getPartitionerType() {
		return partitionerType;
	}

	public void setPartitionerType(String partitionerType) {
		this.partitionerType = partitionerType;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getFomatType() {
		return fomatType;
	}

	public void setFomatType(String fomatType) {
		this.fomatType = fomatType;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	@Override
	public String toString() {
		return "Config [taskNum=" + taskNum + ", topicName=" + topicName + ", hdfsUrl=" + hdfsUrl + ", hiveIntegration="
				+ hiveIntegration + ", hiveMetastoreUris=" + hiveMetastoreUris + ", hiveConfDir=" + hiveConfDir
				+ ", hiveDatabase=" + hiveDatabase + ", flushSize=" + flushSize + ", partitionerType=" + partitionerType
				+ ", fieldName=" + fieldName + ", locale=" + locale + ", timezone=" + timezone + ", fomatType="
				+ fomatType + "]";
	}

}

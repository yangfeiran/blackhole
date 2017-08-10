package com.chinacloud.bean;

public class JdbcConfigBean {
	private String databaseIp;
	private String dbName;
	private String dbType;
	private String username;
	private String password;
	private int taskNum = 1;
	private String port;
	private String tableName;
	private String tableTypes;
	private String checkColumn;
	private String topicPrefix="topic_";
	private String mode;
	private int pollInterval = 5000;
	private String maxPollRows = "10000";
	private int tablePollInterval = 60000;
	private String jobName;

	public int getTaskNum() {
		return taskNum;
	}

	public void setTaskNum(int taskNum) {
		this.taskNum = taskNum;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public int getPollInterval() {
		return pollInterval;
	}

	public void setPollInterval(int pollInterval) {
		this.pollInterval = pollInterval;
	}

	public String getMaxPollRows() {
		return maxPollRows;
	}

	public void setMaxPollRows(String maxPollRows) {
		this.maxPollRows = maxPollRows;
	}

	public int getTablePollInterval() {
		return tablePollInterval;
	}

	public void setTablePollInterval(int tablePollInterval) {
		this.tablePollInterval = tablePollInterval;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTopicPrefix() {
		return topicPrefix;
	}

	public void setTopicPrefix(String topicPrefix) {
		this.topicPrefix = topicPrefix;
	}

	public String getDatabaseIp() {
		return databaseIp;
	}

	public void setDatabaseIp(String databaseIp) {
		this.databaseIp = databaseIp;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getTableTypes() {
		return tableTypes;
	}

	public void setTableTypes(String tableTypes) {
		this.tableTypes = tableTypes;
	}

	public String getCheckColumn() {
		return checkColumn;
	}

	public void setCheckColumn(String checkColumn) {
		this.checkColumn = checkColumn;
	}

	@Override
	public String toString() {
		return "Config [databaseIp=" + databaseIp + ", dbName=" + dbName + ", dbType=" + dbType + ", password="
				+ password + ", taskNum=" + taskNum + ", port=" + port + ", tableName=" + tableName + ", tableTypes="
				+ tableTypes + ", jobName=" + jobName + ", checkColumn=" + checkColumn + "]";
	}
}

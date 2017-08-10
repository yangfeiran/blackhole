package com.chinacloud.bean;
/** * @author  xubo@chinacloud.com.cn
* @date create time：Feb 24, 2017 8:33:40 AM 
* @version 1.0 
 */
public class FtpConfigBean {
	private String ftpUrl="172.16.50.21:21";
	private String ftpUser="t1";
	private String ftpPwd="123";
	private String ftpRefresh="PT5S";
	private String fileMax="P14D";
	private String ftpKeyStyle="struct";
	private String ftpPath="/tmp/horizontal/*.csv";
	private String topicName="testCSV1025";
//	private String ftpSourceConverter="com.landoop.HorizontalMonthlyCSV";
	private String ftpType = "json";
	private String sourceSchema="";
	private int taskNum = 1;
	private String filePrefix = "";
	private String offlineStorePath;
	private String fileNameFrom="";
	private String fileNameTo="";
	private int maxBatchSize=100;
	private String extractMethod;
	
	public String getExtractMethod() {
		return extractMethod;
	}
	public void setExtractMethod(String extractMethod) {
		this.extractMethod = extractMethod;
	}
	public String getFtpType() {
		return ftpType;
	}
	public String getFilePrefix() {
		return filePrefix;
	}
	public void setFilePrefix(String filePrefix) {
		this.filePrefix = filePrefix;
	}
	public void setFtpType(String ftpType) {
		this.ftpType = ftpType;
	}
	public int getTaskNum() {
		return taskNum;
	}
	public void setTaskNum(int taskNum) {
		this.taskNum = taskNum;
	}
	public String getFtpUrl() {
		return ftpUrl;
	}
	public void setFtpUrl(String ftpUrl) {
		this.ftpUrl = ftpUrl;
	}
	public String getFtpUser() {
		return ftpUser;
	}
	public void setFtpUser(String ftpUser) {
		this.ftpUser = ftpUser;
	}
	public String getFtpPwd() {
		return ftpPwd;
	}
	public void setFtpPwd(String ftpPwd) {
		this.ftpPwd = ftpPwd;
	}
	public String getFtpRefresh() {
		return ftpRefresh;
	}
	public void setFtpRefresh(String ftpRefresh) {
		this.ftpRefresh = ftpRefresh;
	}
	public String getFileMax() {
		return fileMax;
	}
	public void setFileMax(String fileMax) {
		this.fileMax = fileMax;
	}
	public String getFtpKeyStyle() {
		return ftpKeyStyle;
	}
	public void setFtpKeyStyle(String ftpKeyStyle) {
		this.ftpKeyStyle = ftpKeyStyle;
	}
	public String getSourceSchema() {
		return sourceSchema;
	}
	public void setSourceSchema(String sourceSchema) {
		this.sourceSchema = sourceSchema;
	}
	public String getFtpPath() {
		return ftpPath;
	}
	public void setFtpPath(String ftpPath) {
		this.ftpPath = ftpPath;
	}
	public String getTopicName() {
		return topicName;
	}
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	public String getOfflineStorePath() {
		return offlineStorePath;
	}
	public void setOfflineStorePath(String offlineStorePath) {
		this.offlineStorePath = offlineStorePath;
	}
	public String getFileNameFrom() {
		return fileNameFrom;
	}
	public void setFileNameFrom(String fileNameFrom) {
		this.fileNameFrom = fileNameFrom;
	}
	public String getFileNameTo() {
		return fileNameTo;
	}
	public void setFileNameTo(String fileNameTo) {
		this.fileNameTo = fileNameTo;
	}
	public int getMaxBatchSize() {
		return maxBatchSize;
	}
	public void setMaxBatchSize(int maxBatchSize) {
		this.maxBatchSize = maxBatchSize;
	}
}

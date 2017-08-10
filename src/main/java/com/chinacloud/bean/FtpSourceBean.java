package com.chinacloud.bean;

import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

/** * @author  xubo@chinacloud.com.cn
* @date create time：Feb 24, 2017 8:31:06 AM 
* @version 1.0 
 */
public class FtpSourceBean extends ConnectorBean {
	private String sourceKey = "ftpSource";
	private FtpConfigBean ftpConfigBean = null;
	public String getSourceKey() {
		return sourceKey;
	}
	public void setSourceKey(String sourceKey) {
		this.sourceKey = sourceKey;
	}
	public FtpConfigBean getFtpConfigBean() {
		return ftpConfigBean;
	}
	public void setFtpConfigBean(FtpConfigBean ftpConfigBean) {
		this.ftpConfigBean = ftpConfigBean;
	}
	public  Map<String,String> getConnectorClass(String connectorType) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(connectorType));
        Map<String,String> rMap = Maps.newHashMap();
        if (connectorType.equalsIgnoreCase("ftp")) {
        //	rMap.put("connector.class","com.datamountaineer.streamreactor.connect.ftp.FtpSourceConnector");
        	rMap.put("connector.class","com.cetc.bigdata.bobo.ftp.FtpSourceConnector");
       }else{
    	   
       }        
       return rMap;
    }
	
	 public  Map<String,String> getFtpMonTail(String ftpFilePath,String topicName){
		 Preconditions.checkArgument(!Strings.isNullOrEmpty(ftpFilePath));
		 Preconditions.checkArgument(!Strings.isNullOrEmpty(topicName));
		 Map<String,String> rMap = Maps.newHashMap();
		 String ftpMonTail = ftpFilePath+":"+topicName;
		 rMap.put("connect.ftp.monitor.tail", ftpMonTail);
	     return rMap;
	 }
	 public  Map<String,String> getFtpSourceConverter(String fileType){
		 Preconditions.checkArgument(!Strings.isNullOrEmpty(fileType));
		 Map<String,String> rMap = Maps.newHashMap();
		 if(fileType.equalsIgnoreCase("csv")){
			 rMap.put("connect.ftp.sourcerecordconverter", "com.landoop.HorizontalMonthlyCSV");
		 }else if(fileType.equalsIgnoreCase("json")){
			 rMap.put("connect.ftp.sourcerecordconverter", "com.cetc.bigdata.connector.plugin.ZipJsonConverter");
		 }
		 return rMap;
	 }
	 
	 
}

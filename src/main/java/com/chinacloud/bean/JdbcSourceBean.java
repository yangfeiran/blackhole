package com.chinacloud.bean;

import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

public class JdbcSourceBean extends ConnectorBean {
	private String sourceKey = "jdbcSource";
	private JdbcConfigBean jdbcConfigBean = null;

	public String getSourceKey() {
   		return sourceKey;
	}

	public void setSourceKey(String sourceKey) {
		this.sourceKey = sourceKey;
	}

	public JdbcConfigBean getJdbcConfigBean() {
		return jdbcConfigBean;
	}

	public void setJdbcConfigBean(JdbcConfigBean jdbcConfigBean) {
		this.jdbcConfigBean = jdbcConfigBean;
	}

	public  Map<String,String> getConnectorClass(String connectorType) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(connectorType));
        Map<String,String> rMap = Maps.newHashMap();
        if (connectorType.equalsIgnoreCase("jdbc")) {
        	rMap.put("connector.class","io.confluent.connect.jdbc.JdbcSourceConnector");
       }else{
    	   
       }        
       return rMap;
    }

	
    public  Map<String,String> getConnectionUrl(String dbtype, String ip, String port, String dbname,String user,String pass) {
    	final String MYSQLJDBCURL = "jdbc:mysql://";
    	final String ORACLEJDBCURL = "jdbc:oracle:thin:";
    	final String POSTGRESQLURL = "jdbc:postgresql://";
        Preconditions.checkArgument(!Strings.isNullOrEmpty(dbtype));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(ip));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(port));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(dbname));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(user));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(pass));
        String url = null;
        Map<String,String> rMap = Maps.newHashMap();
        if (dbtype.equalsIgnoreCase("mysql")) {
        	//String database = dbname.toLowerCase();
            url = MYSQLJDBCURL + ip + ":" + port + "/" + dbname+"?user="+user+"&password="+pass;            
        }else if (dbtype.equalsIgnoreCase("oracle")) {
            url = ORACLEJDBCURL +user+"/"+pass+"@"+ip+":"+port+":"+dbname;
        }else if(dbtype.equalsIgnoreCase("postgresql")){
        	url = POSTGRESQLURL+ip+ ":" + port + "/" + dbname+"?user="+user+"&password="+pass;
        }        
        rMap.put("connection.url", url);
        return rMap;
    }

}

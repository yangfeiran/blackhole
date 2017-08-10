package com.chinacloud.bean;
/** * @author  xubo@chinacloud.com.cn
* @date create time：Mar 8, 2017 2:18:09 PM 
* @version 1.0 
 */
public class JdbcSinkConfigBean {
	private  String name ="";
	private String topics = "";
	private String jdbcUrl = "jdbc:postgresql://10.166.113.76:15432/big_data";
	private String user="chinacloud";
	private String password ="chinacloud";
	private String tableName;
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTopics() {
		return topics;
	}
	public void setTopics(String topics) {
		this.topics = topics;
	}
	public String getJdbcUrl() {
		return jdbcUrl;
	}
	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
}

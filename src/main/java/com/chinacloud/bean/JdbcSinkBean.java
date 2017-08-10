package com.chinacloud.bean;
/** * @author  xubo@chinacloud.com.cn
* @date create time：Mar 8, 2017 2:17:40 PM 
* @version 1.0 
 */
public class JdbcSinkBean extends ConnectorBean{
	private String sourceKey = "jdbcSink";
	private JdbcSinkConfigBean jdbcSinkConfigBean=null;
	public String getSourceKey() {
		return sourceKey;
	}
	public void setSourceKey(String sourceKey) {
		this.sourceKey = sourceKey;
	}
	public JdbcSinkConfigBean getJdbcSinkConfigBean() {
		return jdbcSinkConfigBean;
	}
	public void setJdbcSinkConfigBean(JdbcSinkConfigBean jdbcSinkConfigBean) {
		this.jdbcSinkConfigBean = jdbcSinkConfigBean;
	}
}

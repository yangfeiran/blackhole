package com.chinacloud.service;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.chinacloud.bean.ConnectorBean;
import com.chinacloud.bean.JdbcConfigBean;
import com.chinacloud.bean.JdbcSourceBean;
import com.chinacloud.config.Confluent;
import com.chinacloud.controller.ConnectorController;
import com.chinacloud.process.ConnectConfigProcessor;
import com.google.common.collect.Maps;

@Service
public class ConfluentConnectorService {
	private static final Logger log = Logger.getLogger(ConfluentConnectorService.class);
	@Autowired
	private Confluent confluent;
	
	/**
	 * get jdbc source config params
	 * 
	 * **/
	public Map<String, Object> getSourceConfigParams(ConnectorBean bean){
			Map<String, Object> configMapper = Maps.newHashMap();
			if (bean.getConnectorType().equalsIgnoreCase("jdbc")) {
				JdbcSourceBean model = new JdbcSourceBean();
				model.setName(bean.getName());
				model.setConnectorType(bean.getConnectorType());
				model.setOwner(bean.getOwner());
				model.setDescription(bean.getDescription());
				try {
					JdbcConfigBean jdbcConfigBean = JSON.parseObject(JSON.toJSONString(bean.getConfig()),
							JdbcConfigBean.class);
					jdbcConfigBean.setJobName(bean.getName());
					model.setJdbcConfigBean(jdbcConfigBean);
	
					ConnectConfigProcessor.initTypeNameTable(confluent.getConfigPath());
					configMapper = ConnectConfigProcessor
							.convertToConfig(model.getSourceKey(), jdbcConfigBean);
					configMapper.putAll(model.getConnectionUrl(jdbcConfigBean.getDbType(), jdbcConfigBean.getDatabaseIp(),
							jdbcConfigBean.getPort(), jdbcConfigBean.getDbName(), jdbcConfigBean.getUsername(),
							jdbcConfigBean.getPassword()));
					
					//set the increment mode value
					if(jdbcConfigBean.getMode().equals("incrementing")){
						configMapper.put("incrementing.column.name", jdbcConfigBean.getCheckColumn());
					}else{
						configMapper.put("timestamp.column.name", jdbcConfigBean.getCheckColumn());
					}
					configMapper.putAll(model.getConnectorClass("jdbc"));
					configMapper.put("validate.non.null", false);
					model.setConfig(configMapper);
				} catch (Exception e) {
					log.error(e.getMessage());
					throw new IllegalStateException("params transform exeception");
				}
			} 
			return configMapper;
	}
	
}

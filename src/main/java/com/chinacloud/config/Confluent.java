package com.chinacloud.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component("confluent")//声明这是一个Bean
@ConfigurationProperties(prefix="confluent")//指定配置文件中的前缀
public class Confluent {
	
	private String url;

	private String configPath="	/opt/server/blackhole/blackhole/src/main/resources/connector_type_name.dict";

	public String getConfigPath() {
		return configPath;
	}

	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
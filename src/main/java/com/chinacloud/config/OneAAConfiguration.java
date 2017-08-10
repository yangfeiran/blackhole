package com.chinacloud.config;

import com.chinacloud.mir.common.audit.AuditLogger;
import com.chinacloud.mir.common.service.AuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.sql.DataSource;

@ComponentScan(basePackages = {"com.chinacloud.mir.common"})
@Configuration
@ImportResource("classpath*:one-auth-beans.xml")
public class OneAAConfiguration implements ServletContextInitializer{

    @Autowired
	private DataSource dataSource;

	@Bean
	public AuditLogger auditLogger() {
		return new AuditLogger();
	}

	public FilterRegistrationBean delegateFilter(ServletContext servletContext) throws ServletException{
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();  
        filterRegistrationBean.setFilter(new AuthFilter());  
        filterRegistrationBean.setEnabled(true);  
        filterRegistrationBean.addUrlPatterns("/*");  
        return filterRegistrationBean; 
	}

	public void onStartup(ServletContext servletContext) throws ServletException {
		delegateFilter(servletContext);
	}
	
}

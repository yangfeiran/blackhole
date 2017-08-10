package com.chinacloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.RoutesEndpoint;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;
import org.springframework.cloud.netflix.zuul.filters.discovery.DiscoveryClientRouteLocator;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.chinacloud.filters.SimpleFilter;


@EnableZuulProxy
@SpringBootApplication
public class BlackholeApplication {

  public static void main(String[] args) {
	ConfigurableApplicationContext context = SpringApplication.run(BlackholeApplication.class, args);
	/*RoutesEndpoint endpoint = context.getBean(RoutesEndpoint.class);
	DiscoveryClientRouteLocator routes = context.getBean(DiscoveryClientRouteLocator.class);
	ZuulRoute zuulRoute = new ZuulRoute("/service4/**");
	zuulRoute.setUrl("http://localhost:8034");
	routes.addRoute(zuulRoute);
	endpoint.reset();
	System.out.println(routes.getRoutes());*/
  }
  
  @Bean
  public SimpleFilter simpleFilter() {
    return new SimpleFilter();
  }
  
  @Bean
  public RestTemplate restTemplate() {
      return new RestTemplate();
  }

}

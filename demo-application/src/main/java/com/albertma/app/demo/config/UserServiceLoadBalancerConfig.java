package com.albertma.app.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import loadbalancer.gray.GrayRandomLoadBalancer;

@Configuration
@LoadBalancerClient(name = "user-service", configuration = UserServiceLoadBalancerConfig.class)
public class UserServiceLoadBalancerConfig {
	
	Logger logger = LoggerFactory.getLogger(UserServiceLoadBalancerConfig.class);
	
	@Bean
    public ReactorLoadBalancer<ServiceInstance> reactorServiceInstanceLoadBalancer(
            Environment env, LoadBalancerClientFactory factory) {
		logger.info("UserServiceLoadBalancerConfig.reactorServiceInstanceLoadBalancer");
		
		String serviceId = factory.getName(env);
		logger.info("reactorServiceInstanceLoadBalancer: {}", serviceId);
//		return new GrayVersionLoadBalancer(
//				factory.getLazyProvider(serviceId, ServiceInstanceListSupplier.class), serviceId);
		return new GrayRandomLoadBalancer(  // 随机策略
	            factory.getLazyProvider(serviceId, ServiceInstanceListSupplier.class),
	            serviceId
	        );
	}

	
}

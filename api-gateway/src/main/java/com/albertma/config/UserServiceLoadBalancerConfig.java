package com.albertma.config;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import loadbalancer.gray.GrayRandomLoadBalancer;

@Configuration
@LoadBalancerClients({
	@LoadBalancerClient(name = "user-service", configuration = UserServiceLoadBalancerConfig.class),
	@LoadBalancerClient(name = "demo-application", configuration = UserServiceLoadBalancerConfig.class)
})
public class UserServiceLoadBalancerConfig {
	
	@Bean
    public ReactorLoadBalancer<ServiceInstance> reactorServiceInstanceLoadBalancer(
            Environment env, LoadBalancerClientFactory factory) {
        String serviceId = env.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new GrayRandomLoadBalancer(
				factory.getLazyProvider(serviceId, ServiceInstanceListSupplier.class),
				serviceId
			);
            
    }
}

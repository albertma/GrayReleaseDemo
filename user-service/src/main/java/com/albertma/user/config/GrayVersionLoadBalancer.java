/**
 * 
 */
package com.albertma.user.config;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.apache.commons.fileupload.RequestContext;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.RequestDataContext;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.http.HttpHeaders;

import reactor.core.publisher.Mono;

/**
 * 
 */
public class GrayVersionLoadBalancer implements ReactorLoadBalancer<ServiceInstance> {

	private String serviceId;
	private final ObjectProvider<ServiceInstanceListSupplier> supplierProvider;

	public GrayVersionLoadBalancer(String serviceId, ObjectProvider<ServiceInstanceListSupplier> supplierProvider) {
		this.serviceId = serviceId;
		this.supplierProvider = supplierProvider;
	}

	@Override
	public Mono choose(Request request) {
		return supplierProvider.getIfAvailable().get(request).next()
				.map(instances -> filterInstances(instances, request));
	}

	private Object filterInstances(List<ServiceInstance> instances, Request request) {
		
		HttpHeaders headers = ((RequestDataContext) request.getContext()).getClientRequest().getHeaders();
		boolean isGrayRequest = "true".equals(headers.getFirst("X-Gray"));
		// Filter instances based on the "version" metadata
		List<ServiceInstance> filteredInstances = instances.stream().filter(instance -> {
			String version = instance.getMetadata().get("version");
			return isGrayRequest ? "gray".equals(version) : !"gray".equals(version);
		}).collect(Collectors.toList());
		// If no instances match the criteria, return an empty response
		if (filteredInstances.isEmpty()) {
			return new EmptyResponse();
		}
		// Randomly select an instance from the filtered list
		int index = ThreadLocalRandom.current().nextInt(filteredInstances.size());
		return new DefaultResponse(filteredInstances.get(index));
	}

	

}

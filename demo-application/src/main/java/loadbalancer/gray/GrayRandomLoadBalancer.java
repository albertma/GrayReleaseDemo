package loadbalancer.gray;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import reactor.core.publisher.Mono;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.RequestDataContext;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.SelectedInstanceCallback;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.http.HttpHeaders;
import org.springframework.util.function.SingletonSupplier;

/**
 * A random-based implementation of {@link ReactorServiceInstanceLoadBalancer}.
 * 
 */
public class GrayRandomLoadBalancer implements ReactorServiceInstanceLoadBalancer {

	private static final Log log = LogFactory.getLog(GrayRandomLoadBalancer.class);

	private final String serviceId;

	private final SingletonSupplier<ServiceInstanceListSupplier> serviceInstanceListSingletonSupplier;

	/**
	 * @param serviceInstanceListSupplierProvider a provider of
	 * {@link ServiceInstanceListSupplier} that will be used to get available instances
	 * @param serviceId id of the service for which to choose an instance
	 */
	public GrayRandomLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
			String serviceId) {
		this.serviceId = serviceId;
		this.serviceInstanceListSingletonSupplier = SingletonSupplier.of(() -> serviceInstanceListSupplierProvider.getIfAvailable(NoopServiceInstanceListSupplier::new));
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Mono<Response<ServiceInstance>> choose(Request request) {
		ServiceInstanceListSupplier supplier = serviceInstanceListSingletonSupplier.obtain();
		return supplier.get(request)
			.next()
			.map(serviceInstances -> processInstanceResponse(supplier, serviceInstances, request));
	}

	private Response<ServiceInstance> processInstanceResponse(ServiceInstanceListSupplier supplier,
			List<ServiceInstance> serviceInstances, Request request) {
		Response<ServiceInstance> serviceInstanceResponse = getInstanceResponse(serviceInstances, request);
		if (supplier instanceof SelectedInstanceCallback && serviceInstanceResponse.hasServer()) {
			((SelectedInstanceCallback) supplier).selectedServiceInstance(serviceInstanceResponse.getServer());
		}
		return serviceInstanceResponse;
	}

	private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances, Request request) {
		if (instances.isEmpty()) {
			if (log.isWarnEnabled()) {
				log.warn("No servers available for service: " + serviceId);
			}
			return new EmptyResponse();
		}
		List<ServiceInstance> filteredInstances = instances;
		HttpHeaders headers = ((RequestDataContext) request.getContext()).getClientRequest().getHeaders();
		log.info("########Get request Headers: " + headers);
		boolean isGrayRequest = "true".equals(headers.getFirst("X-Gray"));
		if (isGrayRequest) {
			// 处理灰度请求
			log.info("Gray request detected, filtering instances");
			List<ServiceInstance> grayInstances = instances.stream()
					.filter(instance -> {
						log.info("Instance metadata: " + instance.getMetadata());
						String version = instance.getMetadata().get("version");
						return version != null && version.equalsIgnoreCase("gray");
					})
					.collect(Collectors.toList());
				
			if (grayInstances.isEmpty()) {
				log.warn("No gray instances available for service: " + serviceId);
				return new EmptyResponse();
			} else {
				filteredInstances = grayInstances;
			} 
		}
		int index = ThreadLocalRandom.current().nextInt(filteredInstances.size());

		ServiceInstance instance = filteredInstances.get(index);

		return new DefaultResponse(instance);
	}

}


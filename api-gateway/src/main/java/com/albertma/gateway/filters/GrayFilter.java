/**
 * 
 */
package com.albertma.gateway.filters;


import java.util.Collections;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.env.Environment;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequest.Builder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * @created by albertma on 2025-04-01 17:00:00
 */
@Component
public class GrayFilter implements GlobalFilter {
	
	Logger logger = LoggerFactory.getLogger(GrayFilter.class);

	@Autowired
	private Environment env;
	
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		
		// Implement your gray filter logic here
		//read gray.enabled and gray.userIds from application.properties
		String grayEnabledString = env.getProperty("gray.enabled");
		String grayUserIds = env.getProperty("gray.userIds");
		logger.info("Gray filter executing, enabled: {}, Gray userIds:{}", grayEnabledString, grayUserIds);
		
		boolean needGrayHeaders = false;
		if ("true".equalsIgnoreCase(grayEnabledString)) {
			logger.info("Gray filter is enabled");
		   	if (grayUserIds != null && grayUserIds.length() > 0) {
			    String[] userIds = grayUserIds.split(",");
			    // 获取请求头中的 userId
			    ServerHttpRequest request =  exchange.getRequest();
			    logger.info("Config grayUserIds: {}", userIds);
			    logger.info("Request Headers: {}", request.getHeaders());
			    if (request.getHeaders().containsKey("X-User-Id")) {
			    	// Check if the userId in the request header matches any of the userIds in the config	
			    	logger.info("Request Headers contains userId");
			    	String currentUserId = request.getHeaders().get("X-User-Id").get(0);
			    	if(ArrayUtils.contains(userIds, currentUserId)) {
			    		needGrayHeaders = true;
			    		logger.info("UserId {} is in gray list", currentUserId);
			    	} else {
			    		logger.info("UserId {} is not in gray list", currentUserId);
			    	}
			    	
			    }
			   
		    }
		}
		logger.info("needGrayHeaders: {}", needGrayHeaders);
		if (needGrayHeaders) {
			// Add gray headers to the request
			Builder builder = exchange.getRequest().mutate();
			builder.header("X-Gray", "true");
			ServerHttpRequest request = builder.build();
			exchange = exchange.mutate().request(request).build();
			
		}
		logger.info("After processing Request Headers: {}", exchange.getRequest().getHeaders());
		return chain.filter(exchange);
	}

}

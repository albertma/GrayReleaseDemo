package com.albertma.app.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import feign.RequestInterceptor;
import feign.RequestTemplate;


public class GrayFeignInterceptor implements RequestInterceptor {
	Logger logger = LoggerFactory.getLogger(GrayFeignInterceptor.class);

	@Override
	public void apply(RequestTemplate template) {
		// Get the current request attributes
		logger.info("GrayFeignInterceptor is executing..., forward gray tag to Feign client");
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (attributes != null) {
			String grayTag = attributes.getRequest().getHeader("X-Gray");
			if (grayTag != null) {
				template.header("X-Gray", grayTag);
			}
			// 传递用户ID（需与Feign接口匹配）
            String userId = attributes.getRequest().getHeader("userId");
            if (userId != null) {
                template.header("userId", userId);
            }
		}

	}
}

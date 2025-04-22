package com.albertma.app.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {


    @Bean
    GrayFeignInterceptor grayFeignInterceptor() {
        return new GrayFeignInterceptor();
    }
}

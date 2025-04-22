package com.albertma.app.demo.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;



@FeignClient(name = "user-service")
public interface UserServiceClient {

	
	@GetMapping("/profile")
	Object getUserProfile(@RequestHeader("X-User-Id") String userId);
}

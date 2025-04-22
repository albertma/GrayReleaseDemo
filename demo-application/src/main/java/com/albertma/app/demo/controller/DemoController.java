/**
 * Create Date:Apr 21, 2025
 * Author: albertma
 */
package com.albertma.app.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.albertma.app.demo.service.UserServiceClient;

/**
 * 
 */
@RestController
public class DemoController {
	
	
	@Autowired
	private UserServiceClient userServiceClient;

	@GetMapping("/getDemo")
	public Object getDemo(@RequestHeader("X-User-Id") String userId) {
		return  userServiceClient.getUserProfile(userId);
		
	}
	
	
}

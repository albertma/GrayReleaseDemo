package com.albertma.app.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DiscoveryController {

	  @Autowired
	    private DiscoveryClient discoveryClient;

	    @GetMapping("/services")
	    public List<String> listServices() {
	        return discoveryClient.getServices(); // 应包含user-service
	    }
}

/**
 * 
 */
package com.albertma.userservice.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.albertma.common.BaseResponse;
import com.albertma.common.ResponseStatus;
import com.albertma.user.UserProfileResponse;

/**
 * 
 */
@RestController
//@RequestMapping("/user")
public class UserController {

	@GetMapping("/profile")
	public BaseResponse<UserProfileResponse> getUserProfile(@RequestHeader("X-User-Id") String userId) {
		// Simulate a user profile response
		UserProfileResponse profile = new UserProfileResponse(userId, "zhangsan", "zhangsan@abc.com","1234567890");
		BaseResponse<UserProfileResponse> resp = new BaseResponse<UserProfileResponse>(ResponseStatus.SUCCESS.getStatus(), "", profile);
				
		return resp;
	}
}

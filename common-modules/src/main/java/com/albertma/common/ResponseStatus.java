/**
 * 
 */
package com.albertma.common;

/**
 * 
 */
public enum ResponseStatus {
	SUCCESS("success"),
	FAILURE("failure"),
	ERROR("error");

	private String status;

	ResponseStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}

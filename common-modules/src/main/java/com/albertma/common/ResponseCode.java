package com.albertma.common;

public enum ResponseCode {
	SUCCESS("200", "Success"),
	ERROR("500", "Error"),
	USER_NOT_FOUND("404", "Data Not Found"),
	INVALID_INPUT("400", "Invalid Input");

	private String code;
	private String message;

	ResponseCode(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}

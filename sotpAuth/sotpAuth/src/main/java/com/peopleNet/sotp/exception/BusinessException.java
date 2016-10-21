package com.peopleNet.sotp.exception;

@SuppressWarnings("serial")
public class BusinessException extends Exception {
	private int errorCode;

	public BusinessException() {
	};

	public BusinessException(int code, String msg) {
		super(msg);
		this.errorCode = code;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

}

package com.peopleNet.sotp.vo;

import java.io.Serializable;

import com.peopleNet.sotp.constant.ErrorConstant;

public class ResultVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private int code;
	private String msg;

	public ResultVO() {
		code = ErrorConstant.RET_OK;
		msg = "";
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}

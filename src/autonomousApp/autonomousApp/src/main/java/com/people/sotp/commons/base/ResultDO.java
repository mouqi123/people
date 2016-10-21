package com.people.sotp.commons.base;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 返回值对象
 * 
 * @author bailt
 */
public class ResultDO implements Serializable {

	private static final long serialVersionUID = 4661096805690919752L;

	private boolean success = true; // 执行是否成功
	private String errCode;// 错误代码
	private Map<String, Object> model = new HashMap<String, Object>(4);// 数据模型

	private String errMsg;// 错误详细描述

	public ResultDO() {
	}

	public ResultDO(boolean success) {
		this.success = success;
	}

	public Object getModel(String key) {
		return model.get(key);
	}

	public void setModel(String key, Object model) {
		this.model.put(key, model);
	}

	public boolean isSuccess() {
		return success;
	}

	public boolean isFailure() {
		return !success;
	}

	public void setSuccess(final boolean success) {
		this.success = success;
	}

	public void setErrMsg(String errMsg) {
		this.success = false;
		this.errMsg = errMsg;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

}

package com.sotp.service;

public interface IParamProduceService {
	public String genHeader(String major, String minor, String op, String appId);

	public String genUserInfo(String phoneNum, String userId);

	public String genDevInfo();

	public String genAppInfo();

	public String genAttachedInfo();

	public String genPluginSign();
}

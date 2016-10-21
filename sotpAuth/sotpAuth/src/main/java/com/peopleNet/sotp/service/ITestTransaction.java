package com.peopleNet.sotp.service;

/**
 * 
 * @描述 缓存接口
 * @author wangxin
 * @created_at 2015年10月14日
 */
public interface ITestTransaction {

	public void insertDataForTestTransaction() throws Exception;

	public void updateStatistic(int plugintype, int totalnum);
}

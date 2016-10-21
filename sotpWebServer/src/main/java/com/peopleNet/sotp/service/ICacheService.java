package com.peopleNet.sotp.service;


/**
 * 
 * @描述 缓存接口
 * @author wangxin
 * @created_at 2015年10月14日
 */
public interface ICacheService {


	// 设置<key, value ,timeout>
	public int set(String myKey, Object value, long expire);

	// 根据Key获取value
	public Object get(String myKey);

}

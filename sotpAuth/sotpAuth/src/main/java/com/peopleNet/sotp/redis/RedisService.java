package com.peopleNet.sotp.redis;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.serializer.RedisSerializer;

public interface RedisService {
	/**
	 * 清空redis 所有数据
	 * 
	 * @return
	 */
	public abstract String flushDB() throws Exception;

	/**
	 * 查看redis里有多少数据
	 */
	public abstract long dbSize() throws Exception;

	/**
	 * 检查是否连接成功
	 * 
	 * @return
	 */
	public abstract String ping() throws Exception;

	/**
	 * 通过key删除
	 * 
	 * @param key
	 */
	public abstract void del(String key) throws Exception;

	/**
	 * 添加key value 并且设置存活时间(byte)
	 * 
	 * @param key
	 * @param value
	 * @param expire
	 */
	public abstract void set(String key, Object obj, long expire, TimeUnit timeUnit) throws Exception;

	/**
	 * 添加key value
	 * 
	 * @param key
	 * @param obj
	 */
	public abstract void set(String key, Object obj) throws Exception;

	/**
	 * 获取redis value (String)
	 * 
	 * @param key
	 * @return
	 */
	public abstract Object get(String key) throws Exception;

	/**
	 * 获取redis value (String)
	 * 
	 * @param key
	 * @param name
	 *            map key
	 * @return
	 */
	public abstract Object get(String key, Object name) throws Exception;

	/**
	 * 通过正则匹配keys
	 * 
	 * @param pattern
	 * @return
	 * @return
	 */
	public abstract void Setkeys(String pattern) throws Exception;

	/**
	 * 检查key是否已经存在
	 * 
	 * @param key
	 * @return
	 */
	public abstract boolean exists(String key) throws Exception;

	/**
	 * 栈/队列长
	 * 
	 * @return
	 */
	public abstract long getOpsForListSize(String key) throws Exception;

	/**
	 * 栈/队 范围检索
	 * 
	 * @return
	 */
	public abstract List<Serializable> getOpsForList(String key, long start, long end) throws Exception;

	/**
	 * 压栈
	 * 
	 * @param key
	 * @param obj
	 * @param type
	 *            left:向列表左边添加元素 right:向列表右边添加元素
	 * @return
	 */
	public abstract void push(String key, Serializable obj, String type) throws Exception;

	/**
	 * 出栈
	 * 
	 * @param key
	 * @param obj
	 * @param type
	 */
	public abstract Object pop(String key, String type) throws Exception;

	/**
	 * 删除指定的list所有entry
	 * 
	 * @param key
	 * @throws Exception
	 */
	public abstract void deleteList(String key) throws Exception;

	/**
	 * 删除指定的map所有entry
	 * 
	 * @param key
	 * @throws Exception
	 */
	public abstract void deleteMap(String key) throws Exception;

	/**
	 * map中的所有key
	 * 
	 * @return
	 */
	public abstract Set<Object> getOpsForHashKeys(String key) throws Exception;

	/**
	 * map中数据
	 * 
	 * @return
	 */
	public abstract Map<Object, Object> getOpsForHashData(String key) throws Exception;

	/**
	 * 删除指定的map中的entry
	 * 
	 * @return
	 */
	public abstract void deleteHashValue(String key, Object mapKey) throws Exception;

	/**
	 * 将map中name对应的long值加1
	 * 
	 * @param key
	 *            reids key值
	 * @param name
	 *            key值对应的map中的key
	 * @param value
	 *            map中的key对应value增量
	 * @param expireTime
	 *            失效时间
	 * @param timeUnit
	 *            时间时间类型
	 * @throws Exception
	 */
	public abstract void setHashLong(String key, String name, long value, long expireTime, TimeUnit timeUnit)
	        throws Exception;

	/**
	 * 获取map中name的value值
	 * 
	 * @param key
	 *            redis key
	 * @param name
	 *            map key
	 * @throws Exception
	 */
	public abstract Long getHashLong(String key, String name) throws Exception;

	/**
	 * redis 增加map数据
	 * 
	 * @return
	 */
	public abstract void setHashValue(String key, String name, Object value) throws Exception;

	/**
	 * redis中的map中存放entity
	 * 
	 * @param key
	 *            redis key
	 * @param name
	 *            map key
	 * @param obj
	 *            map value
	 * @param hashValueSerializer
	 *            redis序列化类型
	 * @return
	 * @throws Exception
	 */
	public abstract void setHashObject(String key, String name, Object obj, RedisSerializer<?> hashValueSerializer)
	        throws Exception;

	/**
	 * redis中的map中获取entity
	 * 
	 * @param key
	 *            redis key
	 * @param name
	 *            map key
	 * @param hashValueSerializer
	 *            redis序列化类型
	 * @return
	 * @throws Exception
	 */
	public abstract Object getHashObject(String key, String name, RedisSerializer<?> hashValueSerializer)
	        throws Exception;

	/**
	 * redis中的map的keys
	 * 
	 * @param key
	 *            redis key
	 * @param hashValueSerializer
	 *            redis序列化类型
	 * @return
	 * @throws Exception
	 */
	public abstract Set<String> getHashObjectKeys(String key, RedisSerializer<?> hashValueSerializer) throws Exception;

	/**
	 * 检查map中的key是否已经存在
	 * 
	 * @param key
	 * @param name
	 *            map key
	 * @return
	 * @throws Exception
	 */
	public abstract boolean exists(String key, String name) throws Exception;

}

package com.peopleNet.sotp.service.impl;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.peopleNet.sotp.log.LogUtil;
import com.peopleNet.sotp.service.ICacheService;
import com.peopleNet.sotp.util.CommonConfig;

@Service
public class CacheServiceImpl implements ICacheService {
	private static LogUtil log = LogUtil.getLogger(CacheServiceImpl.class);

	private static String cachePrefix = CommonConfig.get("redis.cache.prefix");

 

	@Autowired
	private RedisTemplate<String, java.io.Serializable> redisTemplate;


	private String useRedis = CommonConfig.get("USE_REDIS");

	// 设置<key, value ,timeout>
	public int set(String myKey, Object value, long expire)  {
		try {
			String key = cachePrefix + myKey;
			if (expire <= 0) {
				redisTemplate.opsForValue().set(key, (Serializable) value);
			} else {
				redisTemplate.opsForValue().set(key, (Serializable) value, expire, TimeUnit.SECONDS);
			}
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
		}
		return 0;
	}

	// 根据Key获取value
	public Object get(String myKey) {
		try {
			String key = cachePrefix + myKey;
			Object c = redisTemplate.opsForValue().get(key);
			return c;
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
		}
		return null;
	}

}

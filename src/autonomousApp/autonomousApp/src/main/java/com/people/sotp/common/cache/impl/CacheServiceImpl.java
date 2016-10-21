package com.people.sotp.common.cache.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.people.sotp.common.cache.ICacheService;
import com.people.sotp.commons.base.CommonConfig;
import com.people.sotp.commons.base.GlobalParam;

@Service
public class CacheServiceImpl implements ICacheService {

	private static Log log = LogFactory.getLog(CacheServiceImpl.class);

	private static String cachePrefix = CommonConfig.get("redis.cache.prefix");
	@Autowired
	private RedisTemplate<String, java.io.Serializable> redisTemplate;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void updateNoteRedis(String type,String phoneNum,String code) throws Exception {
			String key=cachePrefix+ type+"."+phoneNum;
			log.info(key);
			redisTemplate.opsForValue().set(key, code,GlobalParam.NodeFailuresTime,TimeUnit.MINUTES);
	}

	public String getNoteCode(String type,String phoneNum){
		String key=cachePrefix+ type+"."+phoneNum;
		log.info(key);
		return (String) redisTemplate.opsForValue().get(key);
		
	}
	

}

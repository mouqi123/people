package com.peopleNet.sotp.redis;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

@Service
public class RedisServiceImpl implements RedisService {

	@Autowired
	private RedisTemplate<String, java.io.Serializable> redisTemplate;

	@Autowired
	private RedisTemplate<String, java.io.Serializable> longTemplate;

	@Autowired
	private RedisTemplate<String, java.io.Serializable> visitorInfoTemplate;

	@Override
	public void del(String keys) throws Exception {
		redisTemplate.delete(keys);
	}

	@Override
	public void set(String key, Object obj, long expire, TimeUnit timeUnit) throws Exception {
		redisTemplate.opsForValue().set(key, (Serializable) obj, expire, timeUnit);
	}

	@Override
	public void set(String key, Object obj) throws Exception {
		redisTemplate.opsForValue().set(key, (Serializable) obj);
	}

	@Override
	public Object get(String key) throws Exception {
		return redisTemplate.opsForValue().get(key);
	}

	@Override
	public Object get(String key, Object name) throws Exception {
		return redisTemplate.opsForHash().get(key, name);
	}

	@Override
	public void Setkeys(String pattern) throws Exception {
		redisTemplate.keys(pattern);
	}

	@Override
	public boolean exists(String key) throws Exception {
		return redisTemplate.hasKey(key);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String flushDB() throws Exception {
		return (String) redisTemplate.execute(new RedisCallback() {
			public String doInRedis(RedisConnection connection) throws DataAccessException {
				connection.flushDb();
				return "ok";
			}
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public long dbSize() throws Exception {
		return (long) redisTemplate.execute(new RedisCallback() {
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.dbSize();
			}
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public String ping() throws Exception {
		return (String) redisTemplate.execute(new RedisCallback() {
			public String doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.ping();
			}
		});
	}

	@Override
	public long getOpsForListSize(String key) throws Exception {
		return redisTemplate.opsForList().size(key);
	}

	@Override
	public List<Serializable> getOpsForList(String key, long start, long end) throws Exception {
		Long size = this.getOpsForListSize(key);
		if (start < 0)
			start = 0;
		if (size >= end)
			return redisTemplate.opsForList().range(key, start, end);
		return redisTemplate.opsForList().range(key, start, size);
	}

	@Override
	public void push(String key, Serializable obj, String type) throws Exception {
		if ("right".equals(type))
			redisTemplate.opsForList().rightPush(key, obj);
		redisTemplate.opsForList().leftPush(key, obj);
	}

	@Override
	public Object pop(String key, String type) throws Exception {
		if ("right".equals(type))
			return redisTemplate.opsForList().rightPop(key);
		return redisTemplate.opsForList().leftPop(key);
	}

	@Override
	public void deleteList(String key) throws Exception {
		Long size = this.getOpsForListSize(key);
		for (int i = 0; i < size; i++) {
			this.pop(key, "left");
		}
	}

	@Override
	public void deleteMap(String key) throws Exception {
		Set<Object> keys = this.getOpsForHashKeys(key);
		for (Object inKey : keys) {
			this.deleteHashValue(key, inKey);
		}
	}

	@Override
	public Set<Object> getOpsForHashKeys(String key) throws Exception {
		return redisTemplate.opsForHash().keys(key + "");
	}

	@Override
	public Map<Object, Object> getOpsForHashData(String key) throws Exception {
		return redisTemplate.opsForHash().entries(key);
	}

	@Override
	public void deleteHashValue(String key, Object mapKey) throws Exception {
		redisTemplate.opsForHash().delete(key, mapKey);
	}

	@Override
	public void setHashLong(String key, String name, long value, long expireTime, TimeUnit timeUnit) throws Exception {
		this.longTemplate.setHashValueSerializer(new GenericToStringSerializer<Long>(Long.class));
		boolean setExpiredTime = false;
		if (0 == this.longTemplate.opsForHash().size(key))
			setExpiredTime = true;
		this.longTemplate.opsForHash().increment(key, name, value);
		if (setExpiredTime) {
			this.longTemplate.expire(key, expireTime, timeUnit);
		}
	}

	@Override
	public Long getHashLong(String key, String name) throws Exception {
		this.longTemplate.setHashValueSerializer(new GenericToStringSerializer<Long>(Long.class));
		return (Long) longTemplate.opsForHash().get(key, name);
	}

	@Override
	public void setHashValue(String key, String name, Object value) throws Exception {
		redisTemplate.opsForHash().put(key, name, value);
	}

	@Override
	public void setHashObject(String key, String name, Object obj, RedisSerializer<?> hashValueSerializer)
	        throws Exception {
		visitorInfoTemplate.setHashValueSerializer(hashValueSerializer);
		if (!this.visitorInfoTemplate.opsForHash().hasKey(key, name)) {
			this.visitorInfoTemplate.opsForHash().put(key, name, obj);
		}
	}

	@Override
	public Object getHashObject(String key, String name, RedisSerializer<?> hashValueSerializer) throws Exception {
		visitorInfoTemplate.setHashValueSerializer(hashValueSerializer);
		return visitorInfoTemplate.opsForHash().get(key, name);
	}

	@Override
	public Set<String> getHashObjectKeys(String key, RedisSerializer<?> hashValueSerializer) throws Exception {
		visitorInfoTemplate.setHashValueSerializer(hashValueSerializer);
		return visitorInfoTemplate.keys(key);
	}

	@Override
	public boolean exists(String key, String name) throws Exception {
		return visitorInfoTemplate.opsForHash().hasKey(key, name);
	}

}

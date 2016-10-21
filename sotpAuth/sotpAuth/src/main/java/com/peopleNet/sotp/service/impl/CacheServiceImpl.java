package com.peopleNet.sotp.service.impl;

import com.peopleNet.sotp.constant.Constant.REDIS_CONSTANT;
import com.peopleNet.sotp.constant.ErrorConstant;
import com.peopleNet.sotp.dal.dao.*;
import com.peopleNet.sotp.dal.model.*;
import com.peopleNet.sotp.exception.BusinessException;
import com.peopleNet.sotp.log.LogUtil;
import com.peopleNet.sotp.service.ICacheService;
import com.peopleNet.sotp.service.IEncryptPluginInfoService;
import com.peopleNet.sotp.service.IPolicyService;
import com.peopleNet.sotp.util.CommonConfig;
import com.peopleNet.sotp.util.DateUtil;
import com.peopleNet.sotp.vo.AutoUnlockPlugin;
import com.peopleNet.sotp.vo.ExhibitionLog;
import com.peopleNet.sotp.vo.InterfaceVisitStatistic;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import com.peopleNet.sotp.dal.dao.verifyDataDtoMapper;

@Service
public class CacheServiceImpl implements ICacheService {
	private static LogUtil log = LogUtil.getLogger(CacheServiceImpl.class);

	private static String cachePrefix = CommonConfig.get("redis.cache.prefix");

	@Autowired
	JedisCluster jedisCluster;

	@Autowired
	private RedisTemplate<String, String> ruleTemplate;

	@Autowired
	private RedisTemplate<String, java.io.Serializable> redisTemplate;

	@Autowired
	private RedisTemplate<String, java.io.Serializable> longTemplate;

	@Autowired
	private RedisTemplate<String, java.io.Serializable> visitorInfoTemplate;

	@Autowired
	private IEncryptPluginInfoService pluginInfoMapper;
	@Autowired
	private pluginUpdatePolicyDtoMapper pluginUpdatePolicyMapper;
	@Autowired
	private sotpVerifyPolicyDtoMapper sotpVerifyPolicyMapper;
	@Autowired
	private userInfoDtoMapper userInfoMapper;
	@Autowired
	private RulePolicyDtoMapper rulePolicyInfoMapper;
	@Autowired
	private appInfoDtoMapper appInfoMapper;
	@Autowired
	private appVersionInfoDtoMapper appVersionInfoMapper;
	@Autowired
	private authPolicyDtoMapper authPolicyMapper;
	@Autowired
	private verifyDataDtoMapper verifyDataMapper;
	@Autowired
	private IPolicyService policyService;

	private String useRedis = CommonConfig.get("USE_REDIS");

	/**
	 * 获取 RedisSerializer
	 *
	 */
	protected RedisSerializer<String> getRedisSerializer() {
		return redisTemplate.getStringSerializer();
	}

	protected <T> T execute(RedisCallback<T> callback) {
		try {
			return redisTemplate.execute(callback);
		} catch (JedisConnectionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 测试集群操作
	 */

	public Boolean testSet(String key, String value) {
		jedisCluster.set(key, value);
		return true;
	}

	public Object testGet(String key) {
		Object obj = jedisCluster.get(key);
		return obj;
	}

	// 根据Key获取value
	public String getRule(final String myKey) {
		if (redisTemplate != null) {
			return this.execute(new RedisCallback<String>() {
				public String doInRedis(RedisConnection connection) throws DataAccessException {
					RedisSerializer<String> serializer = getRedisSerializer();
					byte[] keys = serializer.serialize(myKey);
					byte[] values = connection.get(keys);
					if (values == null) {
						return null;
					}
					String value = serializer.deserialize(values);
					return value;
				}
			});
		}
		return null;
	}

	/**
	 * 设置key
	 */
	public Boolean set(final String key, final String value) {
		if (redisTemplate != null) {
			this.execute(new RedisCallback<Boolean>() {
				public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
					RedisSerializer<String> serializer = getRedisSerializer();
					byte[] keys = serializer.serialize(key);
					byte[] values = serializer.serialize(value);
					connection.set(keys, values);
					return true;
				}
			});
		}
		return false;
	}

	public userInfoDto getUserInfoByUid(String uid) throws BusinessException {
		if (!StringUtils.isEmpty(useRedis) && "false".equals(useRedis)) {
			log.debug("do not use redis!--------------");
			userInfoDto userInfo = null;
			try {
				userInfo = this.userInfoMapper.selectByUserId(uid);
			} catch (SQLException e) {
				log.error("selectByUserId sql error. msg:%s", e.toString());
				return userInfo;
			}
			return userInfo;
		}

		try {
			String key = cachePrefix + REDIS_CONSTANT.USER_KEY_PREFIX + uid;
			userInfoDto c = (userInfoDto) redisTemplate.opsForValue().get(key);
			// 没取到，读库取
			if (c == null) {
				synchronized (this) {
					if (!redisTemplate.hasKey(key)) {
						userInfoDto userInfo = this.userInfoMapper.selectByUserId(uid);
						if (userInfo != null) {
							redisTemplate.opsForValue().set(key, userInfo);
						}
					}
				}
				c = (userInfoDto) redisTemplate.opsForValue().get(key);
			}
			return c;
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
		}
		// 异常之后，直接读库返回结果
		userInfoDto userInfo = null;
		try {
			userInfo = this.userInfoMapper.selectByUserId(uid);
		} catch (SQLException e) {
			log.error("selectByUserId sql error. msg:%s", e.toString());
			return userInfo;
		}
		return userInfo;
	}

	public List<pluginInfoDto> getPluginInfoByUserId(String uid) throws BusinessException {
		if (!StringUtils.isEmpty(useRedis) && "false".equals(useRedis)) {
			log.debug("do not use redis!--------------");
			List<pluginInfoDto> pluginInfoList = this.pluginInfoMapper.selectByUid(uid);
			return pluginInfoList;
		}

		try {
			String key = cachePrefix + REDIS_CONSTANT.PLUGIN_KEY_PREFIX + uid;
			List<pluginInfoDto> list = getPluginInfoListByKey(key);
			if (CollectionUtils.isEmpty(list)) {
				synchronized (this) {
					if (!redisTemplate.hasKey(key)) {
						this.reloadPluginInfoList(uid);
					}
				}
				list = getPluginInfoListByKey(key);
			}
			return list;
		} catch (RedisConnectionFailureException e) {

			log.error("redis op error. msg:%s", e.toString());
		} catch (Exception e) {

			log.error("redis op error. msg:%s", e.toString());
		}
		// 异常之后，直接读库返回结果
		return this.pluginInfoMapper.selectByUid(uid);
	}

	private List<pluginInfoDto> getPluginInfoListByKey(String key) {
		List<pluginInfoDto> resultList = new ArrayList<pluginInfoDto>();
		List<Serializable> list = redisTemplate.opsForList().range(key, 0, redisTemplate.opsForList().size(key));

		for (Serializable s : list) {
			resultList.add((pluginInfoDto) s);
		}
		return resultList;
	}

	private void reloadPluginInfoList(String uid) throws BusinessException {
		String key = cachePrefix + REDIS_CONSTANT.PLUGIN_KEY_PREFIX + uid;
		if (this.redisTemplate.hasKey(key)) {
			this.redisTemplate.delete(key);
		}
		List<pluginInfoDto> pluginInfoList = this.pluginInfoMapper.selectByUid(uid);
		for (pluginInfoDto a : pluginInfoList) {
			this.redisTemplate.opsForList().rightPush(key, a);
		}

		log.info("[reloadPluginInfoList][uid:%s][plugins:%d]", uid, pluginInfoList.size());
	}

	public pluginInfoDto getPluginInfoById(String pluginId) throws BusinessException {
		if (!StringUtils.isEmpty(useRedis) && "false".equals(useRedis)) {
			log.debug("do not use redis!--------------");
			pluginInfoDto pluginInfoNew = this.pluginInfoMapper.selectByPluginId(pluginId);
			return pluginInfoNew;
		}

		try {
			log.info("use redis!--------------");
			String key = cachePrefix + REDIS_CONSTANT.PLUGIN_KEY_PREFIX + pluginId;
			pluginInfoDto c = (pluginInfoDto) redisTemplate.opsForValue().get(key);
			// 没取到，读库取
			if (c == null) {
				synchronized (this) {
					if (!redisTemplate.hasKey(key)) {
						pluginInfoDto pluginInfo = this.pluginInfoMapper.selectByPluginId(pluginId);
						if (pluginInfo != null) {
							redisTemplate.opsForValue().set(key, pluginInfo);
						}
					}
				}
				c = (pluginInfoDto) redisTemplate.opsForValue().get(key);
			}
			return c;
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
		}
		// 异常之后，直接读库返回结果
		return this.pluginInfoMapper.selectByPluginId(pluginId);
	}

	public List<sotpVerifyPolicyDto> getSotpVerifyPolicyListByStatus(Integer status) throws BusinessException {
		try {
			String key = cachePrefix + REDIS_CONSTANT.POLICY_STATUS_KEY_PREFIX + status;
			List<sotpVerifyPolicyDto> list = getPolicyListByStatus(key);
			if (CollectionUtils.isEmpty(list)) {
				synchronized (this) {
					if (!redisTemplate.hasKey(key)) {
						this.reloadPolicyListByStatus(status);
					}
				}
				list = getPolicyListByStatus(key);
			}
			return list;
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
		}
		// 异常之后，直接读库返回结果
		List<sotpVerifyPolicyDto> ret = null;
		try {
			ret = this.sotpVerifyPolicyMapper.selectByStatus(status);
		} catch (SQLException e) {
			log.error("selectByStatus sql error. msg:%s", e.toString());
			return ret;
		}
		return ret;
	}

	private List<sotpVerifyPolicyDto> getPolicyListByStatus(String key) {
		List<sotpVerifyPolicyDto> resultList = new ArrayList<sotpVerifyPolicyDto>();
		List<Serializable> list = redisTemplate.opsForList().range(key, 0, redisTemplate.opsForList().size(key));

		for (Serializable s : list) {
			resultList.add((sotpVerifyPolicyDto) s);
		}
		return resultList;
	}

	private void reloadPolicyListByStatus(Integer status) {
		String key = cachePrefix + REDIS_CONSTANT.POLICY_STATUS_KEY_PREFIX + status;
		if (this.redisTemplate.hasKey(key)) {
			this.redisTemplate.delete(key);
		}
		List<sotpVerifyPolicyDto> policyList = null;
		try {
			policyList = this.sotpVerifyPolicyMapper.selectByStatus(status);
		} catch (SQLException e) {
			log.error("selectByStatus sql error. msg:%s", e.toString());
		}
		for (sotpVerifyPolicyDto a : policyList) {
			this.redisTemplate.opsForList().rightPush(key, a);
		}
		log.info("[reloadSotpVerifyPolicyListByStatus][status:%d][policyNums:%d]", status, policyList.size());
	}

	public sotpVerifyPolicyDto getPolicyById(int id) throws BusinessException {
		try {
			String key = cachePrefix + REDIS_CONSTANT.POLICY_ID_KEY_PREFIX + id;
			sotpVerifyPolicyDto c = (sotpVerifyPolicyDto) redisTemplate.opsForValue().get(key);
			// 没取到，读库取
			if (c == null) {
				synchronized (this) {
					if (!redisTemplate.hasKey(key)) {
						sotpVerifyPolicyDto policyInfo = this.sotpVerifyPolicyMapper.selectByPrimaryKey(id);
						if (policyInfo != null) {
							redisTemplate.opsForValue().set(key, policyInfo);
						}
					}
				}
				c = (sotpVerifyPolicyDto) redisTemplate.opsForValue().get(key);
			}
			return c;
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
		}
		// 异常之后，直接读库返回结果
		sotpVerifyPolicyDto ret = null;
		try {
			ret = this.sotpVerifyPolicyMapper.selectByPrimaryKey(id);
		} catch (SQLException e) {
			log.error("selectByPrimaryKey sql error. msg:%s", e.toString());
			return ret;
		}
		return ret;
	}

	public int setPluginContent(String pluginId, String plugin) throws BusinessException {
		try {
			String key = cachePrefix + REDIS_CONSTANT.PLUGINCONTENT_KEY_PREFIX + pluginId;
			String c = (String) redisTemplate.opsForValue().get(key);
			// 没冲突，正确，写
			if (c == null) {
				synchronized (this) {
					if (!redisTemplate.hasKey(key)) {
						if (plugin != null && plugin.length() > 0) {
							redisTemplate.opsForValue().set(key, plugin);
						}
					}
				}
			} else {
				return -1;
			}
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
			throw new BusinessException(ErrorConstant.REDIS_CONNECT_ERROR, "redis connect error!");
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
			throw new BusinessException(ErrorConstant.REDIS_OP_ERROR, "redis operation error!");
		}

		return 0;
	}

	public String getPluginContent(String pluginId) throws BusinessException {
		try {
			String key = cachePrefix + REDIS_CONSTANT.PLUGINCONTENT_KEY_PREFIX + pluginId;
			String c = (String) redisTemplate.opsForValue().get(key);
			return c;
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
		}

		return null;
	}

	public int setStatisticNum(String statisticName, Long value, long expire) throws BusinessException {
		try {
			String key = cachePrefix + REDIS_CONSTANT.STATISTIC_NUMBER_KEY_PREFIX + statisticName;
			Long c = (Long) redisTemplate.opsForValue().get(key);
			// 没冲突，正确，写
			if (c == null) {
				synchronized (this) {
					if (!redisTemplate.hasKey(key)) {
						if (expire <= 0) {
							redisTemplate.opsForValue().set(key, value);
						} else {
							redisTemplate.opsForValue().set(key, value, expire, TimeUnit.SECONDS);
						}
					}
				}
			} else {
				this.redisTemplate.delete(key);
				if (expire <= 0) {
					redisTemplate.opsForValue().set(key, value);
				} else {
					redisTemplate.opsForValue().set(key, value, expire, TimeUnit.SECONDS);
				}
			}
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
			throw new BusinessException(ErrorConstant.REDIS_CONNECT_ERROR, "redis connect error!");
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
			throw new BusinessException(ErrorConstant.REDIS_OP_ERROR, "redis operation error!");
		}
		return 0;
	}

	public Long getStatisticNum(String statisticName) {
		try {
			String key = cachePrefix + REDIS_CONSTANT.STATISTIC_NUMBER_KEY_PREFIX + statisticName;
			Long c = (Long) redisTemplate.opsForValue().get(key);
			return c;
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
		}
		return null;
	}

	public int setRealTimeMap(String statisticName, String name, Long value) throws BusinessException {
		try {
			String key = cachePrefix + REDIS_CONSTANT.STATISTIC_NUMBER_KEY_PREFIX + statisticName;
			Long c = (Long) redisTemplate.opsForHash().get(key, name);
			// 没冲突，正确，写
			if (c == null) {
				synchronized (this) {
					if (!this.redisTemplate.opsForHash().hasKey(key, name)) {
						this.redisTemplate.opsForHash().put(key, name, value);
					}
				}
			} else {
				// this.redisTemplate.opsForHash().delete(key, name);
				this.redisTemplate.opsForHash().put(key, name, value);
			}
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
			throw new BusinessException(ErrorConstant.REDIS_CONNECT_ERROR, "redis connect error!");
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
			throw new BusinessException(ErrorConstant.REDIS_OP_ERROR, "redis operation error!");
		}

		return 0;
	}

	public Map<Object, Object> getRealTimeMap(String statisticName) {
		try {
			String key = cachePrefix + REDIS_CONSTANT.STATISTIC_NUMBER_KEY_PREFIX + statisticName;
			Map<Object, Object> c = redisTemplate.opsForHash().entries(key);
			return c;
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
		}
		return null;
	}

	// 实时交易日志
	public int setRealTimeLog(String statisticName, ExhibitionLog tradeLog) throws BusinessException {
		try {
			String key = cachePrefix + REDIS_CONSTANT.STATISTIC_NUMBER_KEY_PREFIX + statisticName;
			this.redisTemplate.opsForList().leftPush(key, tradeLog);
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
			throw new BusinessException(ErrorConstant.REDIS_CONNECT_ERROR, "redis connect error!");
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
			throw new BusinessException(ErrorConstant.REDIS_OP_ERROR, "redis operation error!");
		}

		return 0;
	}

	public List<ExhibitionLog> getRealTimeLog(String statisticName) {
		String key = cachePrefix + REDIS_CONSTANT.STATISTIC_NUMBER_KEY_PREFIX + statisticName;
		List<ExhibitionLog> resultList = new ArrayList<ExhibitionLog>();
		try {
			Long length = redisTemplate.opsForList().size(key);
			List<Serializable> list = null;
			if (length > 100) {
				list = redisTemplate.opsForList().range(key, 0, 100);
			} else {
				list = redisTemplate.opsForList().range(key, 0, length);
			}

			for (Serializable s : list) {
				resultList.add((ExhibitionLog) s);
			}
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
		}
		return resultList;
	}

	// 各省市认证用户数，放在map中
	public int setAmountMap(String statisticName, String name, Long amount) throws BusinessException {
		try {
			String key = cachePrefix + REDIS_CONSTANT.STATISTIC_NUMBER_KEY_PREFIX + statisticName;
			Long c = (Long) redisTemplate.opsForHash().get(key, name);
			// 没冲突，正确，写
			if (c == null) {
				synchronized (this) {
					if (!this.redisTemplate.opsForHash().hasKey(key, name)) {
						this.redisTemplate.opsForHash().put(key, name, amount);
					}
				}
			} else {
				// this.redisTemplate.opsForHash().delete(key, name);
				this.redisTemplate.opsForHash().put(key, name, amount);
			}
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
			throw new BusinessException(ErrorConstant.REDIS_CONNECT_ERROR, "redis connect error!");
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
			throw new BusinessException(ErrorConstant.REDIS_OP_ERROR, "redis operation error!");
		}

		return 0;
	}

	// 将各接口统计信息放到map中
	public int setAmountMapStr(String statisticName, String name, String info) throws BusinessException {
		try {
			String key = cachePrefix + REDIS_CONSTANT.STATISTIC_NUMBER_KEY_PREFIX + statisticName;
			String c = (String) redisTemplate.opsForHash().get(key, name);
			// 没冲突，正确，写
			if (c == null) {
				synchronized (this) {
					if (!this.redisTemplate.opsForHash().hasKey(key, name)) {
						this.redisTemplate.opsForHash().put(key, name, info);
					}
				}
			} else {
				// this.redisTemplate.opsForHash().delete(key, name);
				this.redisTemplate.opsForHash().put(key, name, info);
			}
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
			throw new BusinessException(ErrorConstant.REDIS_CONNECT_ERROR, "redis connect error!");
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
			throw new BusinessException(ErrorConstant.REDIS_OP_ERROR, "redis operation error!");
		}

		return 0;
	}

	// 将各接口统计信息放到map中
	public int setVisitorAmountMap(String statisticName, String name, InterfaceVisitStatistic info)
	        throws BusinessException {
		try {
			String key = cachePrefix + REDIS_CONSTANT.STATISTIC_NUMBER_KEY_PREFIX + statisticName;

			visitorInfoTemplate.setHashValueSerializer(
			        new Jackson2JsonRedisSerializer<InterfaceVisitStatistic>(InterfaceVisitStatistic.class));
			InterfaceVisitStatistic c = (InterfaceVisitStatistic) visitorInfoTemplate.opsForHash().get(key, name);
			// 没冲突，正确，写
			if (c == null) {
				synchronized (this) {
					if (!this.visitorInfoTemplate.opsForHash().hasKey(key, name)) {
						this.visitorInfoTemplate.opsForHash().put(key, name, info);
					}
				}
			} else {
				// this.redisTemplate.opsForHash().delete(key, name);
				this.visitorInfoTemplate.opsForHash().put(key, name, info);
			}
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
			throw new BusinessException(ErrorConstant.REDIS_CONNECT_ERROR, "redis connect error!");
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
			throw new BusinessException(ErrorConstant.REDIS_OP_ERROR, "redis operation error!");
		}

		return 0;
	}

	public Map<Object, Object> getAmountMap(String statisticName) {
		try {
			String key = cachePrefix + REDIS_CONSTANT.STATISTIC_NUMBER_KEY_PREFIX + statisticName;
			Map<Object, Object> c = redisTemplate.opsForHash().entries(key);
			return c;
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
		}
		return null;
	}

	// 获取各接口访问信息
	public Map<String, InterfaceVisitStatistic> getVisitorAmountMap(String statisticName) {
		try {
			String key = cachePrefix + REDIS_CONSTANT.STATISTIC_NUMBER_KEY_PREFIX + statisticName;

			visitorInfoTemplate.setHashValueSerializer(
			        new Jackson2JsonRedisSerializer<InterfaceVisitStatistic>(InterfaceVisitStatistic.class));
			// 得到所有服务器对应的访问信息
			Set<String> keys = this.visitorInfoTemplate.keys(key + "*");

			Map<String, InterfaceVisitStatistic> result = new HashMap<String, InterfaceVisitStatistic>();
			for (String keyEntry : keys) {
				Map<Object, Object> tmpResult = visitorInfoTemplate.opsForHash().entries(keyEntry);
				Iterator<Map.Entry<Object, Object>> entries = tmpResult.entrySet().iterator();
				while (entries.hasNext()) {
					Map.Entry<Object, Object> entry = entries.next();
					String keyName = entry.getKey().toString();
					if (result.containsKey(keyName)) {
						result.get(keyName).add((InterfaceVisitStatistic) entry.getValue());
					} else {
						InterfaceVisitStatistic valueInterface = (InterfaceVisitStatistic) entry.getValue();
						result.put(keyName, valueInterface);
					}
				}
			}
			return result;
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
		}
		return null;
	}

	// 获取插件预生成策略
	public RulePolicyDto getPregenPolicy(String PregenPolicyId) throws BusinessException {
		RulePolicyDto result = null;
		RulePolicyDto c = null;
		String key = cachePrefix + REDIS_CONSTANT.POLICY_CODE_KEY_PREFIX + PregenPolicyId;
		try {
			c = (RulePolicyDto) redisTemplate.opsForValue().get(key);
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
		}

		if (c == null) {
			// 读数据库
			synchronized (this) {
				boolean ret = false;
				try {
					ret = redisTemplate.hasKey(key);
				} catch (RedisConnectionFailureException e) {
					log.error("redis op error. msg:%s", e.toString());
				}
				if (!ret) {
					try {
						int policyId = this.rulePolicyInfoMapper.selectPolicyId(PregenPolicyId, "");
						List<policyDetailDto> pList = this.rulePolicyInfoMapper.selectByPolicyId(policyId);
						result = this.policyService.dbPolicyCovert(pList);
					} catch (SQLException e) {
						log.error("rulePolicyInfoMapper selectByPolicyId sql error " + e.toString());
					}

					if (result != null) {
						try {
							redisTemplate.opsForValue().set(key, result);
						} catch (RedisConnectionFailureException e) {
							log.error("redis op error. msg:%s", e.toString());
						}
						return result;
					}
				}
			}
		}
		return c;
	}

	// 获取插件 验证策略
	public RulePolicyDto getVerifyPolicy(String VerifyPolicyId, String appId) throws BusinessException {
		RulePolicyDto result = null;
		RulePolicyDto c = null;
		String key = cachePrefix + REDIS_CONSTANT.POLICY_CODE_KEY_PREFIX + VerifyPolicyId + "." + appId;
		try {
			c = (RulePolicyDto) redisTemplate.opsForValue().get(key);
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
		}

		if (c == null) {
			// 读数据库
			synchronized (this) {
				boolean ret = false;
				try {
					ret = redisTemplate.hasKey(key);
				} catch (RedisConnectionFailureException e) {
					log.error("redis op error. msg:%s", e.toString());
				}
				if (!ret) {
					try {
						int policyId = this.rulePolicyInfoMapper.selectPolicyId(VerifyPolicyId, appId);
						List<policyDetailDto> pList = this.rulePolicyInfoMapper.selectByPolicyId(policyId);
						result = this.policyService.dbPolicyCovert(pList);
					} catch (SQLException e) {
						log.error("rulePolicyInfoMapper selectByPolicyId sql error " + e.toString());
					}
					if (result != null) {
						try {
							redisTemplate.opsForValue().set(key, result);
						} catch (RedisConnectionFailureException e) {
							log.error("redis op error. msg:%s", e.toString());
						}
						return result;
					}
				}
			}
		}
		return c;
	}

	// 获取插件更新策略
	public RulePolicyDto getUpdatePolicy(String UpdatePolicyId, String appId) throws BusinessException {
		RulePolicyDto result = null;
		RulePolicyDto c = null;
		String key = cachePrefix + REDIS_CONSTANT.POLICY_CODE_KEY_PREFIX + UpdatePolicyId + "." + appId;
		try {
			c = (RulePolicyDto) redisTemplate.opsForValue().get(key);
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
		}
		if (c == null) {
			// 读数据库
			synchronized (this) {
				boolean ret = false;
				try {
					ret = redisTemplate.hasKey(key);
				} catch (RedisConnectionFailureException e) {
					log.error("redis op error. msg:%s", e.toString());
				}
				if (!ret) {
					try {
						int policyId = this.rulePolicyInfoMapper.selectPolicyId(UpdatePolicyId, appId);
						List<policyDetailDto> pList = this.rulePolicyInfoMapper.selectByPolicyId(policyId);
						result = this.policyService.dbPolicyCovert(pList);
					} catch (SQLException e) {
						log.error("rulePolicyInfoMapper selectByPolicyId sql error " + e.toString());
					}
					if (result != null) {
						try {
							redisTemplate.opsForValue().set(key, result);
						} catch (RedisConnectionFailureException e) {
							log.error("redis op error. msg:%s", e.toString());
						}
						return result;
					}
				}
			}
		}
		return c;
	}
	
	
	// 获取检查devInfo的策略
		public Map<Object, Object> getCheckDevInfoPolicy(String appId){
			Map<Object, Object> resultMap = null;
			Map<Object, Object> resultMapDb = new HashMap<Object, Object>();
			String key = cachePrefix + REDIS_CONSTANT.VERIFYDATAITEM_KEY_PREFIX + appId;
			// TODO
			try {
				resultMap = (Map<Object, Object>) redisTemplate.opsForValue().get(key);
			} catch (RedisConnectionFailureException e) {
				log.error("redis op error. msg:%s", e.toString());
			} catch (Exception e) {
				log.error("redis op error. msg:%s", e.toString());
			}
			if (resultMap == null || resultMap.size() == 0) {
				// 读数据库
				synchronized (this) {
					boolean ret = false;
					try{
						ret = redisTemplate.hasKey(key);
					}catch (RedisConnectionFailureException e) {
						log.error("redis op error. msg:%s", e.toString());
					}
					if (!ret) {
						try{
							// 根据appId查找申请策略
							
							List<verifyDataDto> list = verifyDataMapper.getVerifyDataList(appId);
							if (list != null && !list.isEmpty()){
								log.debug("get verifyDataList success");
								for (verifyDataDto info : list) {
							    	resultMapDb.put(info.getCode(), "1");
							    }
							    // 获取匹配率
							    if (!list.isEmpty()) {
							    	resultMapDb.put("matchingRate", list.get(0).getMatchingRate());
							    }
							}
						} catch (SQLException e) {
							log.error("rulePolicyInfoMapper selectByPolicyId sql error " + e.toString());
						}
						if (resultMapDb != null) {
							try{
								// 写redis
								redisTemplate.opsForValue().set(key, (Serializable) resultMapDb);
							}catch (RedisConnectionFailureException e) {
								log.error("redis op error. msg:%s", e.toString());
							} 
							return resultMapDb;
						}
					}
				}
			}
			return resultMap;
		}
	
	
	
	
	
	
	
	
	

	// 获取插件申请策略
	public RulePolicyDto getApplyPolicy(String ApplyPolicyId, String appId) throws BusinessException {
		RulePolicyDto result = null;
		RulePolicyDto c = null;
		String key = cachePrefix + REDIS_CONSTANT.POLICY_CODE_KEY_PREFIX + ApplyPolicyId + "." + appId;
		try {
			c = (RulePolicyDto) redisTemplate.opsForValue().get(key);
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
		}
		if (c == null) {
			// 读数据库
			synchronized (this) {
				boolean ret = false;
				try {
					ret = redisTemplate.hasKey(key);
				} catch (RedisConnectionFailureException e) {
					log.error("redis op error. msg:%s", e.toString());
				}
				if (!ret) {
					try {
						// 根据appId查找申请策略
						int policyId = this.rulePolicyInfoMapper.selectPolicyId(ApplyPolicyId, appId);
						List<policyDetailDto> pList = this.rulePolicyInfoMapper.selectByPolicyId(policyId);
						result = this.policyService.dbPolicyCovert(pList);
					} catch (SQLException e) {
						log.error("rulePolicyInfoMapper selectByPolicyId sql error " + e.toString());
					}
					if (result != null) {
						try {
							redisTemplate.opsForValue().set(key, result);
						} catch (RedisConnectionFailureException e) {
							log.error("redis op error. msg:%s", e.toString());
						}
						return result;
					}
				}
			}
		}
		return c;
	}

	// 获取插件申请策略
	public RulePolicyDto getPolicyCommon(String policyType, String appId) {
		RulePolicyDto result = null;
		RulePolicyDto c = null;
		String key = cachePrefix + REDIS_CONSTANT.POLICY_CODE_KEY_PREFIX + policyType + "." + appId;
		try {
			c = (RulePolicyDto) redisTemplate.opsForValue().get(key);
		} catch (RedisConnectionFailureException e) {
			log.error("redis op getPolicyCommon key %s error. msg:%s", key, e.toString());
		}
		if (c == null) {
			// 读数据库
			synchronized (this) {
				boolean ret = false;
				try {
					ret = redisTemplate.hasKey(key);
				} catch (RedisConnectionFailureException e) {
					log.error("redis op searchKey %s in getPolicyCommon error. msg:%s", key, e.toString());
				}
				if (!ret) {
					try {
						// 根据appId查找申请策略
						int policyId = this.rulePolicyInfoMapper.selectPolicyId(policyType, appId);
						List<policyDetailDto> pList = this.rulePolicyInfoMapper.selectByPolicyId(policyId);
						result = this.policyService.dbPolicyCovert(pList);
					} catch (SQLException e) {
						log.error("rulePolicyInfoMapper selectByPolicyId sql error " + e.toString());
					}
					if (result != null) {
						try {
							redisTemplate.opsForValue().set(key, result);
						} catch (RedisConnectionFailureException e) {
							log.error("redis op set key %s in getPolicyCommon error. msg:%s", key, e.toString());
						}
						return result;
					}
				}
			}
		}
		return c;
	}

	// 获取插件激活策略
	public RulePolicyDto getPluginActivePolicy(String ActivePolicyId, String appId) throws BusinessException {
		RulePolicyDto result = null;
		RulePolicyDto c = null;
		String key = cachePrefix + REDIS_CONSTANT.POLICY_CODE_KEY_PREFIX + ActivePolicyId + "." + appId;
		try {
			c = (RulePolicyDto) redisTemplate.opsForValue().get(key);
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
		}
		if (c == null) {
			// 读数据库
			synchronized (this) {
				boolean ret = false;
				try {
					ret = redisTemplate.hasKey(key);
				} catch (RedisConnectionFailureException e) {
					log.error("redis op error. msg:%s", e.toString());
				}
				if (!ret) {
					try {
						// 根据appId查找激活策略
						int policyId = this.rulePolicyInfoMapper.selectPolicyId(ActivePolicyId, appId);
						List<policyDetailDto> pList = this.rulePolicyInfoMapper.selectByPolicyId(policyId);
						result = this.policyService.dbPolicyCovert(pList);
					} catch (SQLException e) {
						log.error("rulePolicyInfoMapper selectByPolicyId sql error " + e.toString());
					}
					if (result != null) {
						try {
							redisTemplate.opsForValue().set(key, result);
						} catch (RedisConnectionFailureException e) {
							log.error("redis op error. msg:%s", e.toString());
						}
						return result;
					}
				}
			}
		}
		return c;
	}

	// 删除指定的map所有entry
	public void deleteMap(String statisticName) throws BusinessException {
		try {
			String key = cachePrefix + REDIS_CONSTANT.STATISTIC_NUMBER_KEY_PREFIX + statisticName;
			Set<Object> keys = redisTemplate.opsForHash().keys(key + "");
			for (Object inKey : keys) {
				redisTemplate.opsForHash().delete(key, inKey);
			}
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
			throw new BusinessException(ErrorConstant.REDIS_CONNECT_ERROR, "redis connect error!");
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
			throw new BusinessException(ErrorConstant.REDIS_OP_ERROR, "redis operation error!");
		}
	}

	// 删除指定的list所有entry
	public void deleteList(String statisticName) throws BusinessException {
		try {
			String key = cachePrefix + REDIS_CONSTANT.STATISTIC_NUMBER_KEY_PREFIX + statisticName;
			Long size = redisTemplate.opsForList().size(key);
			for (int i = 0; i < size; i++) {
				redisTemplate.opsForList().leftPop(key);
			}
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
			throw new BusinessException(ErrorConstant.REDIS_CONNECT_ERROR, "redis connect error!");
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
			throw new BusinessException(ErrorConstant.REDIS_OP_ERROR, "redis operation error!");
		}
	}

	// 添加插件id到redis list中 leftPush
	public int setPluginIdIntoList(String pluginIdListName, String pluginId) throws BusinessException {
		try {
			String key = cachePrefix + REDIS_CONSTANT.PLUGIN_KEY_PREFIX + pluginIdListName;
			this.redisTemplate.opsForList().leftPush(key, pluginId);
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
			throw new BusinessException(ErrorConstant.REDIS_CONNECT_ERROR, "redis connect error!");
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
			throw new BusinessException(ErrorConstant.REDIS_OP_ERROR, "redis operation error!");
		}
		return 0;
	}

	// 添加锁定插件对象到redis list中 leftPush
	public int setPluginObjIntoList(String pluginIdListName, AutoUnlockPlugin value) throws BusinessException {
		try {
			String key = cachePrefix + REDIS_CONSTANT.PLUGIN_KEY_PREFIX + pluginIdListName;
			this.redisTemplate.opsForList().leftPush(key, (Serializable) value);
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
			throw new BusinessException(ErrorConstant.REDIS_CONNECT_ERROR, "redis connect error!");
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
			throw new BusinessException(ErrorConstant.REDIS_OP_ERROR, "redis operation error!");
		}
		return 0;
	}

	// 添加插件id到redis list中 rightPush
	public int pushbackPluginIdIntoList(String pluginIdListName, String pluginId) throws BusinessException {
		try {
			String key = cachePrefix + REDIS_CONSTANT.PLUGIN_KEY_PREFIX + pluginIdListName;
			this.redisTemplate.opsForList().rightPush(key, pluginId);
		} catch (RedisConnectionFailureException e) {

			log.error("redis op error. msg:%s", e.toString());
			throw new BusinessException(ErrorConstant.REDIS_CONNECT_ERROR, "redis connect error!");
		} catch (Exception e) {

			log.error("redis op error. msg:%s", e.toString());
			throw new BusinessException(ErrorConstant.REDIS_OP_ERROR, "redis operation error!");
		}

		return 0;
	}

	// 添加插件id到redis list中 rightPush
	public int pushbackPluginObjIntoList(String pluginIdListName, AutoUnlockPlugin value) throws BusinessException {
		try {
			String key = cachePrefix + REDIS_CONSTANT.PLUGIN_KEY_PREFIX + pluginIdListName;
			this.redisTemplate.opsForList().rightPush(key, (Serializable) value);
		} catch (RedisConnectionFailureException e) {

			log.error("redis op error. msg:%s", e.toString());
			throw new BusinessException(ErrorConstant.REDIS_CONNECT_ERROR, "redis connect error!");
		} catch (Exception e) {

			log.error("redis op error. msg:%s", e.toString());
			throw new BusinessException(ErrorConstant.REDIS_OP_ERROR, "redis operation error!");
		}

		return 0;
	}

	// 获取redis list中的id
	public String getPluginIdFromList(String pluginIdListName) {
		try {
			String id = null;
			String key = cachePrefix + REDIS_CONSTANT.PLUGIN_KEY_PREFIX + pluginIdListName;
			synchronized (this) {
				id = (String) redisTemplate.opsForList().rightPop(key);
			}
			return id;
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
		}
		// 异常之后，直接读库返回结果
		return null;
	}

	public AutoUnlockPlugin getPluginObjFromList(String pluginIdListName) {
		try {
			AutoUnlockPlugin unlockPlugin = new AutoUnlockPlugin();
			String key = cachePrefix + REDIS_CONSTANT.PLUGIN_KEY_PREFIX + pluginIdListName;
			synchronized (this) {
				unlockPlugin = (AutoUnlockPlugin) redisTemplate.opsForList().rightPop(key);
			}
			return unlockPlugin;
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
		}
		// 异常之后，直接读库返回结果
		return null;
	}

	// 返回最大长度为指定数值的list
	public List<String> getPluginList(String pluginIdListName, int maxLength) {
		String key = cachePrefix + REDIS_CONSTANT.PLUGIN_KEY_PREFIX + pluginIdListName;
		List<String> resultList = new ArrayList<String>();
		try {
			Long length = redisTemplate.opsForList().size(key);
			List<Serializable> list = null;
			if (length > maxLength) {
				list = redisTemplate.opsForList().range(key, 0, maxLength);
			} else {
				list = redisTemplate.opsForList().range(key, 0, length);
			}

			for (Serializable s : list) {
				resultList.add((String) s);
			}
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
		}
		return resultList;
	}

    // 获取链表intervalListName中某用户有效访问次数
    // 比如保存某用户过去15分钟访问次数的链表中,要去掉距离当前时间超过15分钟的元素
    public long fetchUserVisitorNumber(String intervalListName, String userName, String appId, long intervalTime) throws BusinessException {
        try {
            String key = cachePrefix + REDIS_CONSTANT.USER_REQUEST_FREQUENCY_KEY_PREFIX + intervalListName + userName + "." + appId;
            long listSize = this.redisTemplate.opsForList().size(key);
            if (0 == listSize) {
                // 此list尚且没有记录
                //this.redisTemplate.opsForList().leftPush(key, new Date());
                return 0;
            }
            //this.redisTemplate.opsForList().leftPush(key, new Date());
            // 获取该list最末尾的元素,距离当前时间最远的元素
            Date longestVisitTime = (Date)this.redisTemplate.opsForList().index(key, -1);
            while (null != longestVisitTime) {
                // 与当前时间比较,若超过了间隔时间,则从list中去除该元素
                if (DateUtil.compareDate(longestVisitTime, intervalTime)) {
                    this.redisTemplate.opsForList().rightPop(key);
                } else {
                    break;
                }
                longestVisitTime = (Date)this.redisTemplate.opsForList().index(key, -1);
            }
            return this.redisTemplate.opsForList().size(key);
        } catch (RedisConnectionFailureException e) {
            log.error("redis op error. msg:%s", e.toString());
            throw new BusinessException(ErrorConstant.REDIS_CONNECT_ERROR, "redis connect error!");
        } catch (Exception e) {
            log.error("redis op error. msg:%s", e.toString());
            throw new BusinessException(ErrorConstant.REDIS_OP_ERROR, "redis operation error!");
        }
    }

    // 更新历史记录表
    public void setUserVisitorNumber(String intervalListName, String userName, String appId) throws BusinessException {
        try {
            String key = cachePrefix + REDIS_CONSTANT.USER_REQUEST_FREQUENCY_KEY_PREFIX + intervalListName + userName + "." + appId;
            this.redisTemplate.opsForList().leftPush(key, new Date());
        } catch (RedisConnectionFailureException e) {
            log.error("redis op error. msg:%s", e.toString());
            throw new BusinessException(ErrorConstant.REDIS_CONNECT_ERROR, "redis connect error!");
        } catch (Exception e) {
            log.error("redis op error. msg:%s", e.toString());
            throw new BusinessException(ErrorConstant.REDIS_OP_ERROR, "redis operation error!");
        }
    }

	// 用户每天的下载插件次数，放在map中
	public int incUserReqeustPluginNum(String statisticName, String name, String appId) throws BusinessException {
		try {
			String key = cachePrefix + REDIS_CONSTANT.USER_REQUEST_KEY_PREFIX + statisticName + "." + appId;
			longTemplate.setHashValueSerializer(new GenericToStringSerializer<Long>(Long.class));
			boolean setExpiredTime = false;
			if (0 == this.longTemplate.opsForHash().size(key))
				setExpiredTime = true;
			this.longTemplate.opsForHash().increment(key, name, 1L);
			if (setExpiredTime) {
				this.longTemplate.expire(key, 1L, TimeUnit.DAYS);
			}
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
			throw new BusinessException(ErrorConstant.REDIS_CONNECT_ERROR, "redis connect error!");
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
			throw new BusinessException(ErrorConstant.REDIS_OP_ERROR, "redis operation error!");
		}

		return 0;
	}

	// 获取用户当天的下载插件次数
	// 单独使用longTemplate的原因：http://www.21d.cn/detail-id-109024.html
	public Long getUserReqeustPluginNum(String statisticName, String name, String appId) {
		try {
			String key = cachePrefix + REDIS_CONSTANT.USER_REQUEST_KEY_PREFIX + statisticName + "." + appId;
			longTemplate.setHashValueSerializer(new GenericToStringSerializer<Long>(Long.class));
			Long c = (Long) longTemplate.opsForHash().get(key, name);
			return c;
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
		}

		return -1L;
	}

	// 设置<key, value ,timeout>
	public int set(String myKey, Object value, long expire) throws BusinessException {
		try {
			String key = cachePrefix + myKey;
			if (expire <= 0) {
				redisTemplate.opsForValue().set(key, (Serializable) value);
			} else {
				redisTemplate.opsForValue().set(key, (Serializable) value, expire, TimeUnit.SECONDS);
			}
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
			throw new BusinessException(ErrorConstant.REDIS_CONNECT_ERROR, "redis connect error!");
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
			throw new BusinessException(ErrorConstant.REDIS_OP_ERROR, "redis operation error!");
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

	@Override
	public void delete(String myKey) {
		try {
			String key = cachePrefix + myKey;
			redisTemplate.delete(key);
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
		}
	}

	/*
	 * @Override public void clrAppInfoDto(appInfoDto app) { String appCode =
	 * app.getAppCode(); Integer devType = app.getDevType();
	 * app.setAppCode(org.apache.commons.lang.StringUtils.isBlank(appCode) ?
	 * "devType" : appCode); String key = cachePrefix +
	 * REDIS_CONSTANT.APPLY_CODE_KEY_PREFIX + app.getAppCode() + ":" + devType;
	 * appInfoDto dto = (appInfoDto) redisTemplate.opsForValue().get(key); if
	 * (dto == null) { dto = new appInfoDto(devType); } String appHashValue =
	 * app.getAppHashValue(); String appSignature = app.getAppSignature(); if
	 * (org.apache.commons.lang.StringUtils.isNotBlank(appHashValue)) {
	 * dto.setAppHashValue(appHashValue); } if
	 * (org.apache.commons.lang.StringUtils.isNotBlank(appSignature)) {
	 * dto.setAppSignature(appSignature); } redisTemplate.opsForValue().set(key,
	 * dto); }
	 */

	// 获取app信息
	public appVersionInfoDto getAPPVersionInfoByCode(String appId) {
		try {
			String key = cachePrefix + REDIS_CONSTANT.APPLY_CODE_KEY_PREFIX + appId;
			appVersionInfoDto c = (appVersionInfoDto) redisTemplate.opsForValue().get(key);
			// 没取到，读库取
			if (c == null) {
				synchronized (this) {
					if (!redisTemplate.hasKey(key)) {
						appVersionInfoDto appVersionInfo = null;
						try {
							appVersionInfo = this.appVersionInfoMapper.selectAppVersionInfoByAppId(appId);
						} catch (SQLException e) {
							log.error("appVersionInfoMapper selectAppVersionInfoByAppId sql error.msg:%s",
							        e.toString());
							return appVersionInfo;
						}
						if (appVersionInfo != null) {
							redisTemplate.opsForValue().set(key, appVersionInfo);
						}
					}
				}
				c = (appVersionInfoDto) redisTemplate.opsForValue().get(key);
			}
			return c;
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
		}
		// 异常之后，直接读库返回结果
		appVersionInfoDto appVersionInfo = null;
		try {
			appVersionInfo = this.appVersionInfoMapper.selectAppVersionInfoByAppId(appId);
		} catch (SQLException e) {
			log.error("appVersionInfoMapper selectAppVersionInfoByAppId sql error.msg:%s", e.toString());
			return appVersionInfo;
		}
		return appVersionInfo;
	}

	@Override
	public Integer getAuthPolicy(String appId, String businessName) {
		try {
			String key = cachePrefix + REDIS_CONSTANT.AUTH_POLICY_CODE_KEY_PREFIX + appId;
			Map<Object, Object> c = redisTemplate.opsForHash().entries(key);
			if (!c.isEmpty()) {
				if (c.get(businessName) != null) {
					Integer authPolicy = Integer.parseInt(c.get(businessName).toString());
					return authPolicy;
				}
			}
		} catch (RedisConnectionFailureException e) {
			log.error("redis op getAuthPolicy error. appId:%s , businessName:%s, msg:%s", appId, businessName,
			        e.toString());
		} catch (Exception e) {
			log.error("redis op getAuthPolicy error. appId:%s , businessName:%s,  msg:%s", appId, businessName,
			        e.toString());
		}
		Integer authNum = null;
		try {
			authNum = this.rulePolicyInfoMapper.selectAuthPolicyByAppId(appId, businessName);
		} catch (SQLException e) {
			log.error("selectAuthPolicyByAppId sql error.appId:%s, businessName:%s, msg:%s", appId, businessName,
			        e.toString());
			return authNum;
		}
		Map<Object, Object> cMap = new HashMap<Object, Object>();
		cMap.put(businessName, authNum);
		try {
			redisTemplate.opsForHash().putAll(cachePrefix + REDIS_CONSTANT.AUTH_POLICY_CODE_KEY_PREFIX + appId, cMap);
		} catch (Exception e) {
			log.error("redis op setAuthPolicy error. appId:%s , businessName:%s, msg:%s", appId, businessName,
			        e.toString());
		}
		return authNum;
	}

	@Override
	public authPolicyDto getAuthPolicyDto(String appId, String businessName, String grade) {
		authPolicyDto result = null;
		authPolicyDto c = null;
		List<authPolicyDto> authPolicyDtoList = null;
		String key = cachePrefix + REDIS_CONSTANT.AUTH_POLICY_CODE_KEY_PREFIX + appId;
		try {
			Map<Object, authPolicyDto> authMap = (Map<Object, authPolicyDto>) redisTemplate.opsForValue().get(key);
			if (authMap != null) {
				if (authMap.get(businessName) != null) {
					c = (authPolicyDto) authMap.get(businessName);
				}
			}
		} catch (RedisConnectionFailureException e) {
			log.error("redis op getAuthPolicyDto error. appId:%s , businessName:%s, msg:%s", appId, businessName,
			        e.toString());
		}

		if (c == null) {
			// 读数据库
			synchronized (this) {
				boolean ret = false;
				try {
					ret = redisTemplate.hasKey(key);
				} catch (RedisConnectionFailureException e) {
					log.error("redis op error. msg:%s", e.toString());
				}
				if (!ret) {
					try {
						// 读库获取认证策略列表
						authPolicyDtoList = this.authPolicyMapper.selectAuthPolicyDtoListByAppId(appId);
						// result =
						// this.authPolicyMapper.selectAuthPolicyDtoByAppId(appId,
						// businessName);
					} catch (SQLException e) {
						log.error("authPolicyMapper selectAuthPolicyDtoByAppId sql error " + e.toString());
					}
					if (authPolicyDtoList != null) {
						try {
							Map<Object, authPolicyDto> cMap = new HashMap<Object, authPolicyDto>();
							for (authPolicyDto authPolicy : authPolicyDtoList) {
								cMap.put(authPolicy.getServiceCode(), authPolicy);
							}
							result = cMap.get(businessName);
							// cMap.put(businessName, result);
							redisTemplate.opsForValue().set(key, (Serializable) cMap);
						} catch (RedisConnectionFailureException e) {
							log.error("redis op error. msg:%s", e.toString());
						}
						return result;
					}
				}
			}
		}
		return c;
	}
}

package com.peopleNet.sotp.service.impl;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.peopleNet.sotp.constant.Constant.REDIS_CONSTANT;
import com.peopleNet.sotp.constant.ErrorConstant;
import com.peopleNet.sotp.dal.dao.RulePolicyDtoMapper;
import com.peopleNet.sotp.dal.dao.pluginUpdatePolicyDtoMapper;
import com.peopleNet.sotp.dal.dao.sotpVerifyPolicyDtoMapper;
import com.peopleNet.sotp.dal.dao.userInfoDtoMapper;
import com.peopleNet.sotp.dal.model.*;
import com.peopleNet.sotp.exception.BusinessException;
import com.peopleNet.sotp.log.LogUtil;
import com.peopleNet.sotp.redis.RedisService;
import com.peopleNet.sotp.service.IEncryptPluginInfoService;
import com.peopleNet.sotp.service.IPolicyService;
import com.peopleNet.sotp.util.CommonConfig;
import com.peopleNet.sotp.vo.ExhibitionLog;
import com.peopleNet.sotp.vo.InterfaceVisitStatistic;

@Service
public class CacheServiceImpl_new {
	private static LogUtil log = LogUtil.getLogger(CacheServiceImpl_new.class);

	private static String cachePrefix = CommonConfig.get("redis.cache.prefix");

	@Autowired
	private RedisService redisService;
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
	private IPolicyService policyService;

	private String useRedis = CommonConfig.get("USE_REDIS");

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
			// userInfoDto c = (userInfoDto)
			// redisTemplate.opsForValue().get(key);
			userInfoDto c = (userInfoDto) redisService.get(key);
			// 没取到，读库取
			if (c == null) {
				synchronized (this) {
					// if (!redisTemplate.hasKey(key)) {
					if (!redisService.exists(key)) {
						userInfoDto userInfo = this.userInfoMapper.selectByUserId(uid);
						if (userInfo != null) {
							// redisTemplate.opsForValue().set(key, userInfo);
							redisService.set(key, userInfo);
						}
					}
				}
				// c = (userInfoDto) redisTemplate.opsForValue().get(key);
				c = (userInfoDto) redisService.get(key);
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
					if (!redisService.exists(key)) {
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
		List<Serializable> list;
		try {
			list = redisService.getOpsForList(key, 0, redisService.getOpsForListSize(key));
			for (Serializable s : list) {
				resultList.add((pluginInfoDto) s);
			}
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
		}

		return resultList;
	}

	private void reloadPluginInfoList(String uid) throws BusinessException {
		String key = cachePrefix + REDIS_CONSTANT.PLUGIN_KEY_PREFIX + uid;
		// if (this.redisTemplate.hasKey(key)) {
		try {
			if (redisService.exists(key)) {
				// this.redisTemplate.delete(key);
				redisService.del(key);
			}
			List<pluginInfoDto> pluginInfoList = this.pluginInfoMapper.selectByUid(uid);
			for (pluginInfoDto a : pluginInfoList) {
				// this.redisTemplate.opsForList().rightPush(key, a);
				redisService.push(key, a, "right");
			}
			log.info("[reloadPluginInfoList][uid:%s][plugins:%d]", uid, pluginInfoList.size());
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
		}
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
			// pluginInfoDto c = (pluginInfoDto)
			// redisTemplate.opsForValue().get(key);
			pluginInfoDto c = (pluginInfoDto) redisService.get(key);

			// 没取到，读库取
			if (c == null) {
				synchronized (this) {
					if (!redisService.exists(key)) {
						pluginInfoDto pluginInfo = this.pluginInfoMapper.selectByPluginId(pluginId);
						if (pluginInfo != null) {
							// redisTemplate.opsForValue().set(key, pluginInfo);
							redisService.set(key, pluginInfo);
						}
					}
				}
				c = (pluginInfoDto) redisService.get(key);
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
					// if (!redisTemplate.hasKey(key)) {
					if (!redisService.exists(key)) {
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
		// List<Serializable> list = redisTemplate.opsForList().range(key, 0,
		// redisTemplate.opsForList().size(key));
		try {
			List<Serializable> list = redisService.getOpsForList(key, 0, redisService.getOpsForListSize(key));
			for (Serializable s : list) {
				resultList.add((sotpVerifyPolicyDto) s);
			}
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
		}
		return resultList;
	}

	private void reloadPolicyListByStatus(Integer status) {
		String key = cachePrefix + REDIS_CONSTANT.POLICY_STATUS_KEY_PREFIX + status;
		// if (this.redisTemplate.hasKey(key)) {
		// this.redisTemplate.delete(key);
		// }
		try {
			if (redisService.exists(key)) {
				redisService.del(key);
			}
			List<sotpVerifyPolicyDto> policyList = this.sotpVerifyPolicyMapper.selectByStatus(status);
			for (sotpVerifyPolicyDto a : policyList) {
				// this.redisTemplate.opsForList().rightPush(key, a);
				redisService.push(key, a, "right");
			}
			log.info("[reloadSotpVerifyPolicyListByStatus][status:%d][policyNums:%d]", status, policyList.size());

		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
		}
	}

	public sotpVerifyPolicyDto getPolicyById(int id) throws BusinessException {
		try {
			String key = cachePrefix + REDIS_CONSTANT.POLICY_ID_KEY_PREFIX + id;
			// sotpVerifyPolicyDto c = (sotpVerifyPolicyDto)
			// redisTemplate.opsForValue().get(key);
			sotpVerifyPolicyDto c = (sotpVerifyPolicyDto) redisService.get(key);
			// 没取到，读库取
			if (c == null) {
				synchronized (this) {
					// if (!redisTemplate.hasKey(key)) {
					if (!redisService.exists(key)) {
						sotpVerifyPolicyDto policyInfo = this.sotpVerifyPolicyMapper.selectByPrimaryKey(id);
						if (policyInfo != null) {
							// redisTemplate.opsForValue().set(key, policyInfo);
							redisService.set(key, policyInfo);
						}
					}
				}
				c = (sotpVerifyPolicyDto) redisService.get(key);
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
			String c = (String) redisService.get(key);
			// 没冲突，正确，写
			if (c == null) {
				synchronized (this) {
					if (!redisService.exists(key)) {
						if (plugin != null && plugin.length() > 0) {
							redisService.set(key, plugin);
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
			String c = (String) redisService.get(key);
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
			// Long c = (Long) redisTemplate.opsForValue().get(key);
			Long c = (Long) redisService.get(key);
			// 没冲突，正确，写
			if (c == null) {
				synchronized (this) {
					//
					if (!redisService.exists(key)) {
						if (expire <= 0) {
							// redisTemplate.opsForValue().set(key, value);
							redisService.set(key, value);
						} else {
							// redisTemplate.opsForValue().set(key, value,
							// expire, TimeUnit.SECONDS);
							redisService.set(key, value, expire, TimeUnit.SECONDS);
						}
					}
				}
			} else {
				// this.redisTemplate.delete(key);
				redisService.del(key);
				if (expire <= 0) {
					// redisTemplate.opsForValue().set(key, value);
					redisService.set(key, value);
				} else {
					// redisTemplate.opsForValue().set(key, value, expire,
					// TimeUnit.SECONDS);
					redisService.set(key, value, expire, TimeUnit.SECONDS);
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
			Long c = (Long) redisService.get(key);
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
			// Long c = (Long) redisTemplate.opsForHash().get(key, name);
			Long c = (Long) redisService.getHashLong(key, name);
			// 没冲突，正确，写
			if (c == null) {
				synchronized (this) {
					// if (!this.redisTemplate.opsForHash().hasKey(key, name)) {
					if (!redisService.exists(key, name)) {
						// this.redisTemplate.opsForHash().put(key, name,
						// value);
						redisService.setHashValue(key, name, value);
					}
				}
			} else {
				// this.redisTemplate.opsForHash().delete(key, name);
				redisService.setHashValue(key, name, value);
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
			// Map<Object, Object> c = redisTemplate.opsForHash().entries(key);
			Map<Object, Object> c = redisService.getOpsForHashData(key);
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
			// this.redisTemplate.opsForList().leftPush(key, tradeLog);
			redisService.push(key, tradeLog, "left");
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
			List<Serializable> list = redisService.getOpsForList(key, 0, 100);
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
			// Long c = (Long) redisTemplate.opsForHash().get(key, name);
			Long c = (Long) redisService.getHashLong(key, name);
			// 没冲突，正确，写
			if (c == null) {
				synchronized (this) {
					if (!redisService.exists(key, name)) {
						// this.redisTemplate.opsForHash().put(key, name,
						// amount);
						redisService.setHashValue(key, name, amount);
					}
				}
			} else {
				// this.redisTemplate.opsForHash().delete(key, name);
				// this.redisTemplate.opsForHash().put(key, name, amount);
				redisService.setHashValue(key, name, amount);
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
			String c = (String) redisService.get(key, name);
			// 没冲突，正确，写
			if (c == null) {
				synchronized (this) {
					if (!redisService.exists(key, name)) {
						redisService.setHashValue(key, name, info);
					}
				}
			} else {
				// this.redisTemplate.opsForHash().delete(key, name);
				redisService.setHashValue(key, name, info);
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

			/*
			 * visitorInfoTemplate.setHashValueSerializer(new
			 * Jackson2JsonRedisSerializer<InterfaceVisitStatistic>(
			 * InterfaceVisitStatistic.class)); InterfaceVisitStatistic c =
			 * (InterfaceVisitStatistic)
			 * visitorInfoTemplate.opsForHash().get(key, name);
			 */
			InterfaceVisitStatistic c = (InterfaceVisitStatistic) redisService.getHashObject(key, name,
			        new Jackson2JsonRedisSerializer<InterfaceVisitStatistic>(InterfaceVisitStatistic.class));
			// 没冲突，正确，写
			if (c == null) {
				synchronized (this) {
					if (!redisService.exists(key, name)) {
						redisService.setHashObject(key, name, info,
						        new Jackson2JsonRedisSerializer<InterfaceVisitStatistic>(
						                InterfaceVisitStatistic.class));
					}
				}
			} else {
				// this.redisTemplate.opsForHash().delete(key, name);
				redisService.setHashObject(key, name, info,
				        new Jackson2JsonRedisSerializer<InterfaceVisitStatistic>(InterfaceVisitStatistic.class));

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
			// Map<Object, Object> c = redisTemplate.opsForHash().entries(key);
			Map<Object, Object> c = redisService.getOpsForHashData(key);
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

			// 得到所有服务器对应的访问信息
			Set<String> keys = redisService.getHashObjectKeys(key + "*",
			        new Jackson2JsonRedisSerializer<InterfaceVisitStatistic>(InterfaceVisitStatistic.class));

			Map<String, InterfaceVisitStatistic> result = new HashMap<String, InterfaceVisitStatistic>();
			for (String keyEntry : keys) {
				Map<Object, Object> tmpResult = redisService.getOpsForHashData(keyEntry);
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
		try {
			String key = cachePrefix + REDIS_CONSTANT.POLICY_CODE_KEY_PREFIX + PregenPolicyId;
			RulePolicyDto c = (RulePolicyDto) redisService.get(key);

			if (c == null) {
				// 读数据库
				synchronized (this) {
					if (!redisService.exists(key)) {
						// TODO
						int policyId = this.rulePolicyInfoMapper.selectPolicyId(PregenPolicyId, "");
						List<policyDetailDto> pList = this.rulePolicyInfoMapper.selectByPolicyId(policyId);
						RulePolicyDto tmp = this.policyService.dbPolicyCovert(pList);

						if (tmp != null) {
							redisService.set(key, tmp);
						}
					}
				}
				c = (RulePolicyDto) redisService.get(key);
			}
			return c;
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
		}

		return null;
	}

	// 获取插件 验证策略
	public RulePolicyDto getVerifyPolicy(String VerifyPolicyId) throws BusinessException {
		try {
			String key = cachePrefix + REDIS_CONSTANT.POLICY_CODE_KEY_PREFIX + VerifyPolicyId;
			RulePolicyDto c = (RulePolicyDto) redisService.get(key);

			if (c == null) {
				// 读数据库
				synchronized (this) {
					if (!redisService.exists(key)) {
						// TODO 第二个参数要加appId
						int policyId = this.rulePolicyInfoMapper.selectPolicyId(VerifyPolicyId, "");
						List<policyDetailDto> pList = this.rulePolicyInfoMapper.selectByPolicyId(policyId);
						RulePolicyDto tmp = this.policyService.dbPolicyCovert(pList);

						if (tmp != null) {
							redisService.set(key, tmp);
						}
					}
				}
				c = (RulePolicyDto) redisService.get(key);
			}
			return c;
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
		}

		return null;
	}

	// 获取插件更新策略
	public RulePolicyDto getUpdatePolicy(String UpdatePolicyId) throws BusinessException {
		try {
			String key = cachePrefix + REDIS_CONSTANT.POLICY_CODE_KEY_PREFIX + UpdatePolicyId;
			RulePolicyDto c = (RulePolicyDto) redisService.get(key);

			if (c == null) {
				// 读数据库
				synchronized (this) {
					if (!redisService.exists(key)) {
						// TODO 第二个参数要加appId
						int policyId = this.rulePolicyInfoMapper.selectPolicyId(UpdatePolicyId, "");
						List<policyDetailDto> pList = this.rulePolicyInfoMapper.selectByPolicyId(policyId);
						RulePolicyDto tmp = this.policyService.dbPolicyCovert(pList);

						if (tmp != null) {
							redisService.set(key, tmp);
						}
					}
				}
				c = (RulePolicyDto) redisService.get(key);
			}
			return c;
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
		}

		return null;
	}

	// 获取插件申请策略
	public RulePolicyDto getApplyPolicy(String ApplyPolicyId) throws BusinessException {
		try {
			String key = cachePrefix + REDIS_CONSTANT.POLICY_CODE_KEY_PREFIX + ApplyPolicyId;
			RulePolicyDto c = (RulePolicyDto) redisService.get(key);
			if (c == null) {
				// 读数据库
				synchronized (this) {
					if (!redisService.exists(key)) {
						// TODO 第二个参数要加appId
						int policyId = this.rulePolicyInfoMapper.selectPolicyId(ApplyPolicyId, "");
						List<policyDetailDto> pList = this.rulePolicyInfoMapper.selectByPolicyId(policyId);
						RulePolicyDto tmp = this.policyService.dbPolicyCovert(pList);

						if (tmp != null) {
							redisService.set(key, tmp);
						}
					}
				}
				c = (RulePolicyDto) redisService.get(key);
			}
			return c;
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
		}

		return null;
	}

	// 删除指定的map所有entry
	public void deleteMap(String statisticName) throws BusinessException {
		try {
			String key = cachePrefix + REDIS_CONSTANT.STATISTIC_NUMBER_KEY_PREFIX + statisticName;
			Set<Object> keys = redisService.getOpsForHashKeys(key);
			for (Object inKey : keys) {
				// redisTemplate.opsForHash().delete(key, inKey);
				redisService.deleteHashValue(key, inKey);
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
			// Long size = redisTemplate.opsForList().size(key);
			Long size = redisService.getOpsForListSize(key);
			for (int i = 0; i < size; i++) {
				// redisTemplate.opsForList().leftPop(key);
				redisService.pop(key, "left");
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
			redisService.push(key, pluginId, "left");
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
			redisService.push(key, pluginId, "right");
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
				id = (String) redisService.pop(key, "right");
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

	// 返回最大长度为指定数值的list
	public List<String> getPluginList(String pluginIdListName, int maxLength) {
		String key = cachePrefix + REDIS_CONSTANT.PLUGIN_KEY_PREFIX + pluginIdListName;
		List<String> resultList = new ArrayList<String>();
		try {
			List<Serializable> list = redisService.getOpsForList(key, 0, maxLength);

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

	// 用户每天的下载插件次数，放在map中
	public int incUserReqeustPluginNum(String statisticName, String name) throws BusinessException {
		try {
			String key = cachePrefix + REDIS_CONSTANT.USER_REQUEST_KEY_PREFIX + statisticName;
			/*
			 * longTemplate.setHashValueSerializer(new
			 * GenericToStringSerializer<Long>(Long.class)); boolean
			 * setExpiredTime = false; if (0 ==
			 * this.longTemplate.opsForHash().size(key)) setExpiredTime = true;
			 * this.longTemplate.opsForHash().increment(key, name, 1L); if
			 * (setExpiredTime) { this.longTemplate.expire(key, 1L,
			 * TimeUnit.DAYS); }
			 */
			redisService.setHashLong(key, name, 1L, 1L, TimeUnit.DAYS);
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
	public Long getUserReqeustPluginNum(String statisticName, String name) {
		try {
			String key = cachePrefix + REDIS_CONSTANT.USER_REQUEST_KEY_PREFIX + statisticName;
			/*
			 * longTemplate.setHashValueSerializer(new
			 * GenericToStringSerializer<Long>(Long.class)); Long c = (Long)
			 * longTemplate.opsForHash().get(key, name);
			 */
			Long c = (Long) redisService.getHashLong(key, name);
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
				redisService.set(myKey, value);
			} else {
				redisService.set(key, (Serializable) value, expire, TimeUnit.SECONDS);
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
			Object c = redisService.get(key);
			return c;
		} catch (RedisConnectionFailureException e) {
			log.error("redis op error. msg:%s", e.toString());
		} catch (Exception e) {
			log.error("redis op error. msg:%s", e.toString());
		}
		return null;
	}
}

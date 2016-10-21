package com.peopleNet.sotp.controller;

import com.peopleNet.sotp.exception.BusinessException;
import com.peopleNet.sotp.log.LogUtil;
import com.peopleNet.sotp.service.ICacheService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/config")
public class ConfigController {
	private static LogUtil logger = LogUtil.getLogger(ConfigController.class);
	private static String CODE = "people2000";

	@Autowired
	private RedisTemplate<String, java.io.Serializable> redisTemplate;

	@Autowired
	private ICacheService cacheService;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = { "/execute" }, method = { RequestMethod.POST, RequestMethod.GET, RequestMethod.HEAD })
	@ResponseBody
	public String execute(String action, String code, HttpServletResponse response) {
		if (!StringUtils.isEmpty(action) && !StringUtils.isEmpty(code)) {
			if (CODE.equals(code)) {
				if ("clearcache".equals(action)) {
					String res = "";
					try {
						res = (String) redisTemplate.execute(new RedisCallback() {
							public String doInRedis(RedisConnection connection) throws DataAccessException {
								connection.flushDb();
								return "ok";
							}
						});
					} catch (Exception e) {
						logger.error("flush redis cache fail! msg:%s", e.toString());
					}
					String msg = null;
					if (!StringUtils.isEmpty(res) && "ok".equals(res)) {
						msg = "flush redis cache succeed!";
						logger.info(msg);
					} else {
						msg = "flush redis cache fail!";
						logger.error(msg);
					}
					return msg;
				}
			}
		}
		return "wrong request param";
	}

	@RequestMapping(value = { "/clearRedisMap" }, method = { RequestMethod.POST, RequestMethod.GET,
	        RequestMethod.HEAD })
	@ResponseBody
	public String clearRedisMap(String mapKey, String code, HttpServletResponse response) {
		if (!StringUtils.isEmpty(mapKey) && !StringUtils.isEmpty(code)) {
			if (CODE.equals(code)) {
				try {
					this.cacheService.deleteMap(mapKey);
				} catch (BusinessException e) {
					return "failed! msg:" + e.toString();
				}
			}
		}
		return "succeed!";
	}

	@RequestMapping(value = { "/clearRedisList" }, method = { RequestMethod.POST, RequestMethod.GET,
	        RequestMethod.HEAD })
	@ResponseBody
	public String clearRedisList(String listKey, String code, HttpServletResponse response) {
		if (!StringUtils.isEmpty(listKey) && !StringUtils.isEmpty(code)) {
			if (CODE.equals(code)) {
				try {
					this.cacheService.deleteList(listKey);
				} catch (BusinessException e) {
					return "failed! msg:" + e.toString();
				}
			}
		}
		return "succeed!";
	}
}

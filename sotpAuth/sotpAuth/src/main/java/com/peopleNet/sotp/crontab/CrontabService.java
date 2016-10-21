package com.peopleNet.sotp.crontab;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peopleNet.sotp.constant.Constant;
import com.peopleNet.sotp.dal.dao.pluginStatisticDtoMapper;
import com.peopleNet.sotp.dal.model.pluginInfoDto;
import com.peopleNet.sotp.exception.BusinessException;
import com.peopleNet.sotp.log.LogUtil;
import com.peopleNet.sotp.service.*;
import com.peopleNet.sotp.util.CommonConfig;
import com.peopleNet.sotp.util.DateUtil;
import com.peopleNet.sotp.util.UpdateUtil;
import com.peopleNet.sotp.vo.AutoUnlockPlugin;

/**
 * spring 定时任务service，此service由spring 来触发运行一些定时任务
 * 
 * @描述
 * @author wangxin
 * @created_at 2015年10月22日
 */
@Service
public class CrontabService {
	static final int THREAD_NUM = 4;
	private static LogUtil logger = LogUtil.getLogger(CrontabService.class);
	@Autowired
	private pluginStatisticDtoMapper pluginStatisticMapper;
	@Autowired
	private IEncryptPluginInfoService pluginInfoMapper;
	@Autowired
	private IThriftInvokeService thriftInvokeService;
	@Autowired
	private ICacheService cacheService;
	@Autowired
	private IDBOperationService dbOperationService;
	@Autowired
	private IPluginService pluginService;
	@Autowired
	private ITestTransaction dbService;
	private String SERVER_NAME = CommonConfig.get("SERVER_NAME");
	private int MAX_LOCKED_PLUGIN_LIST_LENGTH = Integer
	        .parseInt(CommonConfig.get("MAX_LOCKED_PLUGIN_LIST_LENGTH").trim());

	public void sayHello() {

		logger.info("Hello,sotp!");
	}

	public void autoUnlockPlugin() {
		Date now = new Date();
		logger.debug("auto unlock plugin---------! time:%s", now.toString());
		String errMsg = "";

		for (int i = 0; i < MAX_LOCKED_PLUGIN_LIST_LENGTH; i++) {
			AutoUnlockPlugin unlockPlugin = this.cacheService
			        .getPluginObjFromList(Constant.REDIS_PLUGIN_ID_SAVE_LIST_NAME.HAS_LOCKED_LIST);
			if (null == unlockPlugin) {
				// 若为空，则下一个
				continue;
			}
			String pluginId = unlockPlugin.getPluginId();

			int isUnlock = Constant.AUTOUNLOCK.DENY;
			if (unlockPlugin.getIsUnlock() != null && unlockPlugin.getIsUnlock() >= 0)
				isUnlock = unlockPlugin.getIsUnlock();

			if (isUnlock != Constant.AUTOUNLOCK.ALLOW) {
				logger.info("do not allow auto unlock plugin!");
				return;
			}
			int autoUnlockTime = 0;
			if (unlockPlugin.getAutoUnlockTime() != null && unlockPlugin.getAutoUnlockTime() >= 0)
				autoUnlockTime = unlockPlugin.getAutoUnlockTime();
			pluginInfoDto plInfo = this.pluginInfoMapper.selectByPluginId(pluginId);
			// 若允许自动解锁，先解锁
			if (null != plInfo && plInfo.getStatus() == Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_LOCKED) {
				logger.debug("unlock plugin id:%s, errDay:%s", pluginId, plInfo.getErrDay().toString());
				if (DateUtil.compareDate(plInfo.getErrDay(), autoUnlockTime * 3600000L)) {
					UpdateUtil updateInfo = new UpdateUtil();
					updateInfo.addField(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.STATUS,
					        Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_READY);
					updateInfo.addField(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.VERIFY_ERRCNT, 0);
					updateInfo.addAbsoluteKey(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.PLUGIN_ID, plInfo.getPluginId());
					Map<String, Object> updateInfoMap = updateInfo.getUpdateInfomap();
					this.pluginInfoMapper.updateHashMapByPluginId(updateInfoMap);
					logger.debug("unlock plugin id:%s, succeed", pluginId);
				} else {
					try {
						this.cacheService.pushbackPluginObjIntoList(
						        Constant.REDIS_PLUGIN_ID_SAVE_LIST_NAME.HAS_LOCKED_LIST, unlockPlugin);
					} catch (BusinessException e) {
						logger.error("pushback unlockPlugin into list error!");
					}
					break;
				}
			}
		}
	}
}

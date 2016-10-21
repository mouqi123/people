package com.peopleNet.sotp.service.impl;

import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peopleNet.sotp.constant.Constant;
import com.peopleNet.sotp.constant.Constant.REQUEST_FLAG;
import com.peopleNet.sotp.dal.dao.authFeatureDtoMapper;
import com.peopleNet.sotp.dal.dao.pluginStatisticDtoMapper;
import com.peopleNet.sotp.dal.dao.userInfoDtoMapper;
import com.peopleNet.sotp.dal.dao.userVerifyInfoDtoMapper;
import com.peopleNet.sotp.dal.model.authFeatureDto;
import com.peopleNet.sotp.dal.model.pluginInfoDto;
import com.peopleNet.sotp.dal.model.userInfoDto;
import com.peopleNet.sotp.dal.model.userVerifyInfoDto;
import com.peopleNet.sotp.log.LogUtil;
import com.peopleNet.sotp.service.ICacheService;
import com.peopleNet.sotp.service.IDBOperationService;
import com.peopleNet.sotp.service.IEncryptPluginInfoService;
import com.peopleNet.sotp.util.UpdateUtil;
import com.peopleNet.sotp.vo.UserRequestMsgFido;

@Service
public class DBOperationServiceImpl implements IDBOperationService {

	private static LogUtil logger = LogUtil.getLogger(EncryptPluginInfoServiceImpl.class);
	@Autowired
	private userInfoDtoMapper userInfoMapper;
	@Autowired
	private IEncryptPluginInfoService pluginInfoMapper;
	@Autowired
	private authFeatureDtoMapper authFeatureMapper;
	@Autowired
	private pluginStatisticDtoMapper pluginStatisticMapper;
	@Autowired
	private ICacheService cacheService;
	@Autowired
	private userVerifyInfoDtoMapper userVerifyInfoDtoMapper;
	@Autowired
	private userInfoDtoMapper userInfoDtoMapper;

	// 申请插件时，各参数（插件信息、硬件信息、用户信息）入库
	public void insertRegPluginIntoDB(authFeatureDto authFeaInfo, pluginInfoDto pluginInfo, String phoneNum,
	        String userId, String appId) throws Exception {
		// cacheService 内存中记录 phoneNum 当天申请次数
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String today = dateFormat.format(new Date(System.currentTimeMillis()));
		this.cacheService.incUserReqeustPluginNum(today, phoneNum, appId);

		// 更新表 t_plugin_info ，若存在 相同（userId，phoneNum， dev(IMEI/UUID)），则置为作废;
		UpdateUtil updateInfo = new UpdateUtil();
		updateInfo.addAbsoluteKey(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.STATUS,
		        Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_USELESS);

		Integer devTpye = authFeaInfo.getdevType();
		updateInfo.addAbsoluteKey("devTpye", devTpye);
		if (devTpye == Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_IOS) {
			updateInfo.addAbsoluteKey("uuid", authFeaInfo.getuuid());
			// updateInfo.addAbsoluteKey("imei", "");
		} else if (devTpye == Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_ANDROID_ARM
		        || devTpye == Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_ANDROID_MIPS
		        || devTpye == Constant.PLUGIN_TYPE.SOTP_PLUGIN_TYPE_ANDROID_X86) {
			// updateInfo.addAbsoluteKey("uuid", "");
			updateInfo.addAbsoluteKey("imei", authFeaInfo.getimei());
		}
		updateInfo.addAbsoluteKey("phoneNum", pluginInfo.getBindUserphone());
		updateInfo.addAbsoluteKey("appCode", appId);

		Map<String, Object> updateMap = updateInfo.getUpdateInfomap();
		
		List<String> hardwares = null;
		try {
	        hardwares = authFeatureMapper.selectByUuidAndAppCode(updateMap);
	        if(!CollectionUtils.isEmpty(hardwares)){
	        	  pluginInfoMapper.batchUpdatePluginInfo(hardwares);
	        }
 
        } catch (SQLException e1) {
        	logger.error("update PluginInfo error.msg:%s", e1.toString());
        }
//	      int i = this.pluginInfoMapper.updatePluginInfoByHashMap(updateMap);       
		// System.out.println("########################## 更新记录条数
		// ############################### " + i);

		// 插入硬件信息表
		authFeaInfo.setPluginId(pluginInfo.getPluginId());
		try {
			authFeatureMapper.insert(authFeaInfo);
		} catch (SQLException e) {
			logger.error("authFeatureMapper insert error.msg:%s", e.toString());
		}

		// 插入pluginfo
		if (pluginInfo.getPluginId() != null) {
			pluginInfo.setAppCode(appId);
			pluginInfoMapper.insert(pluginInfo);
		}

		// 表 t_user_info , 若存在不处理， 否则插入
		this.userInfoMapper.insertIfNotExist(userId, phoneNum, appId);

		// 更新插件统计的计数
//		this.pluginStatisticMapper.update_IncreTotalAndUsedNumByType(authFeaInfo.getdevType());
 	}

	// 更新插件时，各参数（插件信息、硬件信息）入库
	public void insertUpdatePluginIntoDB(authFeatureDto authFeaInfo, pluginInfoDto oldpluginInfo,
	        pluginInfoDto newpluginInfo, String appId) throws Exception {

		// cacheService 内存中记录 phoneNum 当天申请次数
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String today = dateFormat.format(new Date(System.currentTimeMillis()));
		this.cacheService.incUserReqeustPluginNum(today, newpluginInfo.getBindUserphone(), appId);

		// 插入newpluginInfo
		if (newpluginInfo.getPluginId() != null) {
			pluginInfoMapper.insert(newpluginInfo);
		}

		// 插入硬件信息表
		if (authFeaInfo != null) {
			authFeaInfo.setPluginId(newpluginInfo.getPluginId());
			authFeaInfo.setId(null);
			try {
				authFeatureMapper.insert(authFeaInfo);
			} catch (SQLException e) {
				logger.error("authFeatureMapper insert error.msg:%s", e.toString());
			}
		}

		// 原插件作废
		UpdateUtil updateInfo = new UpdateUtil();
		updateInfo.addField(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.STATUS,
		        Constant.PLUGIN_STATUS.SOTP_PLUGIN_STATUS_USELESS);
		updateInfo.addAbsoluteKey(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.PLUGIN_ID, oldpluginInfo.getPluginId());
		updateInfo.addField(Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.APP_CODE, appId);

		Map<String, Object> updateMap = updateInfo.getUpdateInfomap();
		this.pluginInfoMapper.updateHashMapByPluginId(updateMap);

		// 更新插件统计的计数
	//	this.pluginStatisticMapper.update_IncreTotalAndUsedNumByType(authFeaInfo.getdevType());
	}

	/*
	 * 更新统计表
	 */
	public synchronized void updateStatistic(int plugintype, int totalnum) {
		try {
			this.pluginStatisticMapper.update_AddTotalNumByType(plugintype, totalnum);
		} catch (SQLException e) {
			logger.error("update_AddTotalNumByType sql error.msg:%s", e.toString());
		}
	}

	// 将采集到auth信息入库
	@Override
	public void insertuserVerifyInfotoDB(UserRequestMsgFido newAuth, userVerifyInfoDto newUser) {

		Date invokeTime = new Date(System.currentTimeMillis());
		// newUser.setUserId(newAuth.getUserId());
		// newUser.setPluginId(newAuth.getPluginId());
		newUser.setLocation(newAuth.getLocation());
		newUser.setIp(newAuth.getIp());
		newUser.setWifi(newAuth.getWifiInfo());
		newUser.setInvokeTime(invokeTime);

		// 首次添加时为失败， 当认证成功或者注册下载插件成功后更新为成功
		newUser.setStatus(REQUEST_FLAG.FAILURE);
		if (newAuth != null) {
			try {
				userVerifyInfoDtoMapper.insert(newUser);
			} catch (SQLException e) {
				logger.error("insert sql error. msg:%s", e.toString());
			}
		}
	}

	// 根据auth信息更新userinfo
	@Override
	public void updateUserInfo(userInfoDto newUser) {

		if (newUser != null) {
			try {
				userInfoDtoMapper.updateByPrimaryKey(newUser);
			} catch (SQLException e) {
				logger.error("updateByPrimaryKey sql error. msg:%s", e.toString());
			}
		}
	}

	// 更新状态为成功
	@Override
	public void updateStatus(userVerifyInfoDto userInfo) {

		userInfo.setStatus(REQUEST_FLAG.SUCCESS);
		if (userInfo != null) {
			try {
				userVerifyInfoDtoMapper.updateStatus(userInfo);
			} catch (SQLException e) {
				logger.error("updateStatus sql error. msg:%s", e.toString());
			}
		}
	}
}

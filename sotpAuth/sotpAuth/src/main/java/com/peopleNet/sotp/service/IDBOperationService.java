package com.peopleNet.sotp.service;

import com.peopleNet.sotp.dal.model.authFeatureDto;
import com.peopleNet.sotp.dal.model.pluginInfoDto;
import com.peopleNet.sotp.dal.model.userInfoDto;
import com.peopleNet.sotp.dal.model.userVerifyInfoDto;
import com.peopleNet.sotp.vo.UserRequestMsgFido;

public interface IDBOperationService {

	// 申请插件时，各参数（插件信息、硬件信息、用户信息）入库
	public void insertRegPluginIntoDB(authFeatureDto authFeaInfo, pluginInfoDto pluginInfo, String phoneNum,
	        String userId, String appId) throws Exception;

	// 更新插件时，各参数（插件信息、硬件信息）入库
	public void insertUpdatePluginIntoDB(authFeatureDto authFeaInfo, pluginInfoDto oldpluginInfo,
	        pluginInfoDto newpluginInfo, String appId) throws Exception;

	// 更新统计表
	public void updateStatistic(int plugintype, int totalnum);

	// 将采集到的认证信息入库
	public void insertuserVerifyInfotoDB(UserRequestMsgFido newAuth, userVerifyInfoDto newUser);

	// 根据auth信息更新userinfo
	public void updateUserInfo(userInfoDto newUser);

	// 更新状态
	public void updateStatus(userVerifyInfoDto newAuth);

}

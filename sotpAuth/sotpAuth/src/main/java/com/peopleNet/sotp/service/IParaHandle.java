package com.peopleNet.sotp.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.peopleNet.sotp.dal.model.*;
import com.peopleNet.sotp.vo.ResultVO;
import com.peopleNet.sotp.vo.UserRequestMsg;
import com.peopleNet.sotp.vo.UserRequestMsgFido;
import com.peopleNet.sotp.vo.UserRequestMsgV2;

/**
 * 
 * @描述 业务参数判断;参数解析; 内容判断; 日志接口
 * @author hejh
 * @created_at 2015年11月30日
 */
public interface IParaHandle {

	// 参数格式判断
	public ResultVO paraFormat(String reqestMsg, int interfaceId);

	public ResultVO paraFormat(UserRequestMsg reqestMsg);

	// V2版本
	public ResultVO paraFormatV2(UserRequestMsgV2 reqestMsg);

	// fido版本
	public ResultVO paraFormatFido(UserRequestMsgFido reqestMsg);

	// 获取header、userInfo、attachedInfo信息
	public UserRequestMsgFido getNewHeaderAndUserInfo(String header, String userInfo);

	// 获取header、userInfo、attachedInfo信息
	public UserRequestMsgFido getattachedInfo(String attachedInfo);

	public UserRequestMsgFido getNewPluginSign(String pluginSign);
	
	//解析tradeInfo
	public UserRequestMsgFido getNewTradeInfo(String tradeInfo);

	// 解析auth
	public userInfoDto getAuth(String auth, int policy);

	public pluginInfoDto getPluginfo(String sotpId);

	public authFeatureDto getAuthFeature(String sotpId);

	public pluginInfoDto getOldPluginInfo(String devId, String phoneNum);

	public pluginInfoDto getNewPluginfo(String uid, String phonenum, String sotpPayInfo);

	public pluginInfoDto getNewPluginfoV2(String uid, String phonenum, String pin);

	public authFeatureDto getNewAuthFeature(String devInfo);

	public String getIsRootFromDevInfo(String devInfo);

	public appInfoDto getAppInfo(String businessCode);

	public int updAppInfo(appInfoDto appInfoDto);

	public appInfoDto getAppInfoByAppId(String appId);

	public String getBusinessCodeByAppInfoId(int appInfoId);

	public String getAppInfoCodeByAppInfoId(int appInfoId);

	public appVersionInfoDto getAppVersionInfoByAppId(String appId);

	// 验证请求信息的签名
	public ResultVO checkSignature(UserRequestMsgV2 reqestMsg);

	// Fido版本验证请求信息签名
	public ResultVO checkSignature(UserRequestMsgFido reqestMsg);

	public String getDevInfo(authFeatureDto authFeaInfo);

	public String getDevInfoJson(authFeatureDto authFeaInfo, String phoneNum, String appId,
	        RulePolicyDto applyPolicyInfo);

	public String responseHandle(String appId, String service, int status, String pluginContent, String hwInfo,
	        String errMsg, String reqmsg, HttpServletRequest request, String sotpId);

	public String responseHandleWithResult(String appId, String service, int status, String pluginContent,
	        String hwInfo, String errMsg, String reqmsg, HttpServletRequest request, Map<String, Object> resultMap);

	public String responseHandleWithSotpInfo(String appId, String service, int status, String pluginContent,
	        String hwInfo, String errMsg, String reqmsg, HttpServletRequest request, String devId, String sotpId,
	        String sotpStatus, String devName, Object devlist);
	//08-05  LXX 过滤掉map里面为null和无用的属性
	public String filterRequestMap(Map<String, Object>map);
}

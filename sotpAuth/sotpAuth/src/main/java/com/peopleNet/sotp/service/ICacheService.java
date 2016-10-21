package com.peopleNet.sotp.service;

import com.peopleNet.sotp.dal.model.*;
import com.peopleNet.sotp.exception.BusinessException;
import com.peopleNet.sotp.vo.AutoUnlockPlugin;
import com.peopleNet.sotp.vo.ExhibitionLog;
import com.peopleNet.sotp.vo.InterfaceVisitStatistic;

import java.util.List;
import java.util.Map;

/**
 * 
 * @描述 缓存接口
 * @author wangxin
 * @created_at 2015年10月14日
 */
public interface ICacheService {

	public userInfoDto getUserInfoByUid(String uid) throws BusinessException;

	public List<pluginInfoDto> getPluginInfoByUserId(String uid) throws BusinessException;

	public pluginInfoDto getPluginInfoById(String id) throws BusinessException;

	public List<sotpVerifyPolicyDto> getSotpVerifyPolicyListByStatus(Integer status) throws BusinessException;

	public sotpVerifyPolicyDto getPolicyById(int id) throws BusinessException;

	public int setPluginContent(String pluginId, String plugin) throws BusinessException;

	public String getPluginContent(String pluginId) throws BusinessException;
	public Map<Object, Object> getCheckDevInfoPolicy(String appId) throws BusinessException;
	// expire以秒为单位, <=0 表示永不超时
	public int setStatisticNum(String statisticName, Long value, long expire) throws BusinessException;

	public Long getStatisticNum(String statisticName);

	// 获取插件预生成策略
	public RulePolicyDto getPregenPolicy(String PregenPolicyId) throws BusinessException;

	// 获取插件 验证策略
	public RulePolicyDto getVerifyPolicy(String VerifyPolicyId, String appId) throws BusinessException;

	// 获取插件更新策略
	public RulePolicyDto getUpdatePolicy(String UpdatePolicyId, String appId) throws BusinessException;

	// 获取认证因素策略值
	public Integer getAuthPolicy(String appId, String businessName);

	// 获取认证因素策略值
	public authPolicyDto getAuthPolicyDto(String appId, String businessName, String grade);

	// 获取插件激活策略
	public RulePolicyDto getPluginActivePolicy(String ActivePolicyId, String appId) throws BusinessException;

	// 获取插件申请策略(通过appId区分各商户策略)
	public RulePolicyDto getApplyPolicy(String ApplyPolicyId, String appId) throws BusinessException;

	// 获取策略统一版
	public RulePolicyDto getPolicyCommon(String policyType, String appId);

	public int setRealTimeMap(String statisticName, String name, Long value) throws BusinessException;

	public Map<Object, Object> getRealTimeMap(String centerCity);

	public int setRealTimeLog(String statisticName, ExhibitionLog tradeLog) throws BusinessException;

	public List<ExhibitionLog> getRealTimeLog(String statisticName);

	public int setAmountMap(String statisticName, String name, Long amount) throws BusinessException;

	public int setAmountMapStr(String statisticName, String name, String info) throws BusinessException;

	public int setVisitorAmountMap(String statisticName, String name, InterfaceVisitStatistic info)
	        throws BusinessException;

	public Map<String, InterfaceVisitStatistic> getVisitorAmountMap(String statisticName);

	public Map<Object, Object> getAmountMap(String statisticName);

	// 删除指定的list所有entry
	public void deleteList(String statisticName) throws BusinessException;

	// 删除指定的map所有entry
	public void deleteMap(String statisticName) throws BusinessException;

	// 添加插件id到redis list中
	public int setPluginIdIntoList(String pluginIdListName, String pluginId) throws BusinessException;

	// 添加锁定插件对象到redis list中
	public int setPluginObjIntoList(String pluginIdListName, AutoUnlockPlugin value) throws BusinessException;

	// 添加插件id到redis list中 rightPush
	public int pushbackPluginIdIntoList(String pluginIdListName, String pluginId) throws BusinessException;

	public int pushbackPluginObjIntoList(String pluginIdListName, AutoUnlockPlugin value) throws BusinessException;

	// 获取redis list中的插件id
	public String getPluginIdFromList(String pluginIdListName);

	// 获取redis list中的插件id
	public AutoUnlockPlugin getPluginObjFromList(String pluginIdListName);

	// 用户每天的下载插件次数，放在map中
	public int incUserReqeustPluginNum(String statisticName, String name, String appId) throws BusinessException;

	// 获取用户当天的下载插件次数
	public Long getUserReqeustPluginNum(String statisticName, String name, String appId);

	// 返回最大长度为指定数值的list
	public List<String> getPluginList(String pluginIdListName, int maxLength);

	// 设置<key, value ,timeout>
	public int set(String myKey, Object value, long expire) throws BusinessException;

	// 根据Key获取value
	public Object get(String myKey);

	// public void clrAppInfoDto(appInfoDto app);

	public appVersionInfoDto getAPPVersionInfoByCode(String appId);

	public Boolean set(final String key, final String value);

	public void delete(String string);

	public Boolean testSet(String key, String value);

	public Object testGet(String key);

	public String getRule(String myKey);

    // 获取链表intervalListName中某用户有效访问次数
    // 比如保存某用户过去15分钟访问次数的链表中,要去掉距离当前时间超过15分钟的元素
    public long fetchUserVisitorNumber(String intervalListName, String userName, String appId, long intervalTime) throws BusinessException;

    // 更新历史记录表
    public void setUserVisitorNumber(String intervalListName, String userName, String appId) throws BusinessException;
}

package com.peopleNet.sotp.constant;

public class ServiceConstant {

	// 版本信息
	public static final String AUTH_VERSION = "2.0";
	public static final String AUTH_VERSION_3_0 = "3.0";
	// 申请插件
	public static final String PLUGIN_REG = "zrauth.plugin.apply";
	public static final String PLUGIN_REG_AUTH = "zrauth.plugin.apply.auth";
	// 更新插件
	public static final String PLUGIN_UPDATE = "zrauth.plugin.update";
	// 解绑插件
	public static final String PLUGIN_UNWRAP = "zrauth.plugin.unwrap";
	// 获取用户插件信息
	public static final String PLUGIN_LISTALL = "zrauth.plugin.listall";
	// 获取用户插件信息xian
	public static final String PLUGIN_GETDEVLIST = "zrauth.plugin.getdevlist";
	// 激活插件
	public static final String PLUGIN_ACTIVATE = "zrauth.plugin.activate";
	// 获取插件状态
	public static final String PLUGIN_GETSTATUS = "zrauth.plugin.getstatus";
	// 请求挑战码
	public static final String BUSINESS_GETCHALLENGE = "zrauth.business.getchallenge";
	// 时间校准
	public static final String BUSINESS_TIME_SYNCHR = "zrauth.business.time.synchr";
	// 安全认证申请
	public static final String BUSINESS_AUTH = "zrauth.business.auth";
	// 安全认证响应
	public static final String BUSINESS_AUTH_RESPONSE = "zrauth.business.auth.response";
	// 协商会话秘钥第一步
	public static final String BUSINESS_NEGOTIATESESSIONKEYS1 = "zrauth.business.negotiatesessionkeys1";
	// 协商会话秘钥第二步
	public static final String BUSINESS_NEGOTIATESESSIONKEYS2 = "zrauth.business.negotiatesessionkeys2";
	// 会话秘钥加密数据
	public static final String BUSINESS_SESSIONKEYENCRYPTION = "zrauth.business.sessionkeyencryption";
	// 会话秘钥解密数据
	public static final String BUSINESS_SESSIONKEYDECRYPTION = "zrauth.business.sessionkeydecryption";
	// 本地数据加密
	public static final String BUSINESS_DATAENCRYPTION = "zrauth.business.dataencryption";
	// 本地数据解密
	public static final String BUSINESS_DATADECRYPTION = "zrauth.business.datadecryption";
	// 密码键盘解密
	public static final String BUSINESS_KEYBOARDDECRYPTION = "zrauth.business.keyboard.decryption";
	// 生成会话密钥
	public static final String BUSINESS_GENSESSKEY = "zrauth.business.gensesskey";
	// 修改插件保护码
	public static final String BUSINESS_MODSOTPIN = "zrauth.plugin.modifypin";
	// 修改预留信息
	public static final String BUSINESS_MODHOLDINFO = "zrauth.business.modholdinfo";
	// 修改设备名称
	public static final String PLUGIN_SETDEVALIAS = "zrauth.plugin.setdevalias";
	// 同步共享密钥
	public static final String PLUGIN_SYNSHAREKEY = "zrauth.plugin.synsharekey";
	// 转加密
	public static final String PLUGIN_CRYPTCONVERT = "zrauth.plugin.turncrypto";
	// 验证APP完整性
	public static final String PLUGIN_VERIFYAPPINFO = "zrauth.business.verifyappinfo";
	//验证APP合法性
	public static final String PLUGIN_VERIFYAPPLEGITIMACY = "zrauth.business.verifyapplegitimacy";
	// 补发插件
	public static final String PLUGIN_RESISSUE = "zrauth.plugin.reissue";
	// 解锁插件
	public static final String PLUGIN_UNLOCK = "zrauth.plugin.unlock";
	// 解挂插件
	public static final String PLUGIN_UNHANG = "zrauth.plugin.unhang";
	// 挂起插件
	public static final String PLUGIN_HANGUP = "zrauth.plugin.hangup";
	// 已激活插件激活未激活插件
	public static final String PLUGIN_OTHERACTIVE = "zrauth.plugin.otheractive";
	// 申请插件2
	public static final String PLUGIN_APPLY2 = "zrauth.plugin.apply2";
	// 更新插件2
	public static final String PLUGIN_UPDATE2 = "zrauth.plugin.update2";
	// 检测app签名和hash
	public static final String BUSINESS_CHECKAPPSIGNHASH = "zrauth.business.checkappsignhash";
	// 检测设备id和插件签名
	public static final String PLUGIN_CHECKDEVPLUGINHASH = "zrauth.plugin.checkdevpluginhash";

	/*
	 * for 模拟系统
	 */
	public static final String PLUGIN_LISTALL_WEB = "zrauth.plugin.listallweb";

	public static final String PLUGIN_ACTIVE_WEB = "zrauth.plugin.activeweb";
}

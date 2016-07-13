package com.people.sotp.commons.base;

import java.util.HashMap;

import net.sf.json.JSONObject;

public class GlobalCacheInitData {
	// 初始化系统错误码map
	@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
	public static final HashMap errMap = new HashMap() {
		{
			put("0", "成功");
			put("100", "参数长度有误");
			put("101", "参数中含有非法字符");
			put("102", "参数缺少某项");
			put("103", "业务类型未知");
			put("104", "sotp身份码格式有误");
			put("105", "硬件信息格式有误");
			put("106", "身份码类型有误");
			put("107", "SOTP预留信息格式有误");
			put("200", "插件ID不存在");
			put("201", "用户ID不存在 ");
			put("202", "手机号不存在");
			put("203", "硬件设备不存在");
			put("204", "用户ID与插件ID不匹配");
			put("205", "手机号与插件ID不匹配");
			put("206", "硬件信息与插件ID不匹配");
			put("207", "移动终端当前时间可能有误，重新校时");
			put("300", "插件为未激活状态");
			put("301", "插件为待下载状态");
			put("302", "插件为就绪状态");
			put("303", "插件为锁定状态");
			put("304", "插件为挂起状态");
			put("305", "插件为作废状态");
			put("306", "插件未知状态");
			put("307", "插件更新周期已到，待更新");
			put("308", "插件总验证使用次数已到，待更新");
			put("309", "插件总验证错误次数已到，待更新");
			put("310", "插件申请时，数据库中预生成插件数量不足");
			put("311", "插件下载时，已超时，不是待下载状态");
			put("312", "申请的插件类型未知");
			put("313", "解绑插件时，用户ID或插件ID不对");
			put("314", "解绑插件的设备标识不存在");
			put("315", "当天插件申请次数已达上限");
			put("400", "密码机连接超时");
			put("401", "密码机返回数据格式有误");
			put("402", "密码机响应超时");
			put("403", "SOTP认证码验证错误");
			put("404", "密码机内部错误-1（参数格式有误）");
			put("405", "密码机内部错误-2（系统程序有误）");
			put("406", "密码机内部错误-3（base64编码有误）");
			put("407", "密码机内部错误-4（内存分配不足）");
			put("408", "连接密码机协议有误");
			put("409", "密码机返回的未知错误");
			put("410", "服务端解密结果比对失败");
			put("500", "redis连接出错");
			put("501", "mysql连接出错");
			put("502", "写redis出错");
			put("503", "读redis出错");
			put("504", "写数据库出错");
			put("505", "读数据库出错");
			put("600", "未知错误");
			put("601", "内存分配出错");
			put("602", "系统调用出错");
		}
	};

	/**
	 * 根据错误码获取错误详情
	 * 
	 * @param code
	 * @return
	 */
	public static final String getErrMsg(String code) {
		Object obj = errMap.get(code);
		if (obj == null) {
			return code;
		}
		return obj.toString();
	}

	/**
	 * 初始化业务码map
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
	public static final HashMap bussinessMap = new HashMap() {
		{
			put("1010", "插件状态判断");
			put("1020", "申请SOTP安全插件");
			put("1030", "更新SOTP安全插件");
			put("1040", "解绑SOTP安全插件");
			put("1050", "请求绑定插件的设备列表");
			put("1061", "数据加密");
			put("1062", "数据解密");
			put("1063", "获取验证码");
			put("1064", "获取预留信息");
			put("1070", "验证SOTP认证码 ");
			put("1080", "请求短信验证码");
			put("1090", "请求验证码(挑战码)");
			put("1091", "修改SOTP保护码 ");
			put("1092", "获取（或设置）插件预留信息");
		}
	};

	/**
	 * 根据业务接口编号获取业务名称
	 * 
	 * @param code
	 * @return
	 */
	public static final String getBussinessMsg(String code) {
		Object obj = bussinessMap.get(code);
		if (obj == null) {
			return code;
		}
		return obj.toString();
	}

	public static String getBussinessJSON() {
		JSONObject jsonObj = JSONObject.fromObject(bussinessMap);
		return jsonObj.toString();
	}

	/**
	 * 初始化插件类型map
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
	public static final HashMap pluginTypeMap = new HashMap() {
		{
			put("1", "test");
			put("2", "苹果");
			put("3", "安卓arm");
			put("4", "安卓x86");
			put("5", "安卓mips");
		}
	};

	/**
	 * 获取插件类型名称
	 * 
	 * @param code
	 * @return
	 */
	public static final String getPluginTypeMsg(String code) {
		Object obj = pluginTypeMap.get(code);
		if (obj == null) {
			return code;
		}
		return obj.toString();
	}

}

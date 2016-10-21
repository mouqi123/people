package com.peopleNet.sotp.serviceFido;

import javax.servlet.http.HttpServletRequest;

import com.peopleNet.sotp.vo.UserRequestMsgFido;

public interface IPluginMagServiceFido {

	// 插件下载
	public String pluginReg(UserRequestMsgFido requestMsg, HttpServletRequest request);

	public String pluginRegAuth(UserRequestMsgFido requestMsg, HttpServletRequest request);

	// 插件更新
	public String pluginupdate(UserRequestMsgFido requestMsg, HttpServletRequest request);

	// 解绑插件
	public String plugindel(UserRequestMsgFido requestMsg, HttpServletRequest request);

	// 获取用户插件信息
	public String getdevlist(UserRequestMsgFido requestMsg, HttpServletRequest request);

	// 激活插件
	public String activePlugin(UserRequestMsgFido requestMsg, HttpServletRequest request);

	// 插件状态
	public String getstatus(UserRequestMsgFido requestMsg, HttpServletRequest request);

	// 设置设备名称
	public String setDevAlias(UserRequestMsgFido requestMsg, HttpServletRequest request);

	// 同步共享密钥
	public String synShareKey(UserRequestMsgFido requestMsg, HttpServletRequest request);

	// 转加密
	public String cryptConvert(UserRequestMsgFido requestMsg, HttpServletRequest request);

	// 检测设备Id和插件签名
	public String checkDevPluginhash(UserRequestMsgFido requestMsg, HttpServletRequest request);

	/*
	 * for 模拟系统
	 */
	public String getPluginListWeb(UserRequestMsgFido requestMsg, HttpServletRequest request);

	public String activePluginWeb(UserRequestMsgFido requestMsg, HttpServletRequest request);
}

package com.peopleNet.sotp.serviceV2;

import javax.servlet.http.HttpServletRequest;

import com.peopleNet.sotp.vo.UserRequestMsgV2;

public interface IPluginMagService {

	// 插件下载
	public String pluginReg(UserRequestMsgV2 requestMsg, HttpServletRequest request);

	// 插件更新
	public String pluginupdate(UserRequestMsgV2 requestMsg, HttpServletRequest request);

	// 解绑插件
	public String plugindel(UserRequestMsgV2 requestMsg, HttpServletRequest request);

	// 获取用户插件信息
	public String getdevlist(UserRequestMsgV2 requestMsg, HttpServletRequest request);

	// 获取用户插件信息
	public String getPluginDevList(UserRequestMsgV2 requestMsg, HttpServletRequest request);

	// 激活插件
	public String activePlugin(UserRequestMsgV2 requestMsg, HttpServletRequest request);

	// 插件状态
	public String getstatus(UserRequestMsgV2 requestMsg, HttpServletRequest request);

	// 设置设备名称
	public String setDevAlias(UserRequestMsgV2 requestMsg, HttpServletRequest request);

	// 同步共享密钥
	public String synShareKey(UserRequestMsgV2 requestMsg, HttpServletRequest request);

	// 转加密
	public String cryptConvert(UserRequestMsgV2 requestMsg, HttpServletRequest request);

	// 补发插件
	public String pluginResissue(UserRequestMsgV2 requestMsg, HttpServletRequest request);

	// 获取插件状态
	public String pluginGetStatus(UserRequestMsgV2 requestMsg, HttpServletRequest request);

	// 验证APP完整性
	public String verifyAppInfo(UserRequestMsgV2 requestMsg, HttpServletRequest request);

	// 解锁插件
	public String pluginUnlock(UserRequestMsgV2 requestMsg, HttpServletRequest request);

	// 解挂插件
	public String pluginUnhang(UserRequestMsgV2 requestMsg, HttpServletRequest request);

	// 挂起插件
	public String pluginHangup(UserRequestMsgV2 requestMsg, HttpServletRequest request);

	// 申请插件2
	public String pluginApply2(UserRequestMsgV2 requestMsg, HttpServletRequest request);

	// 更新插件2
	public String pluginUpdate2(UserRequestMsgV2 requestMsg, HttpServletRequest request);

	// 激活插件2
	public String pluginActive2(UserRequestMsgV2 requestMsg, HttpServletRequest request);
}

package com.peopleNet.sotp.serviceV2;

import javax.servlet.http.HttpServletRequest;

import com.peopleNet.sotp.vo.UserRequestMsgV2;

public interface IGeneralService {
	// 请求挑战码
	public String getchallenge(UserRequestMsgV2 requestMsg, HttpServletRequest request);

	// 安全认证
	public String verify(UserRequestMsgV2 requestMsg, HttpServletRequest request);

	// 协商会话秘钥第一步
	public String sotpServerAuth(UserRequestMsgV2 requestMsg, HttpServletRequest request);

	// 协商会话秘钥第二步
	public String sotpVerifyClient(UserRequestMsgV2 requestMsg, HttpServletRequest request);

	// 时间校准
	public String timeSynchr(UserRequestMsgV2 requestMsg, HttpServletRequest request);

	// 会话秘钥加密数据
	// public String verify(UserRequestMsgV2 requestMsg, HttpServletRequest
	// request);
	// 会话秘钥解密数据
	// public String verify(UserRequestMsgV2 requestMsg, HttpServletRequest
	// request);
	// 本地数据加解密
	public String crypto(UserRequestMsgV2 requestMsg, HttpServletRequest request);

	// 密码键盘解密
	public String decrypt(UserRequestMsgV2 requestMsg, HttpServletRequest request);
	
	//验证APP合法性
	public String verifyapplegitimacy(UserRequestMsgV2 requestMsg,HttpServletRequest request);

	// 本地数据加解密
	public String gensesskey(UserRequestMsgV2 requestMsg, HttpServletRequest request);

	// 修改插件保护码
	public String modsotpin(UserRequestMsgV2 requestMsg, HttpServletRequest request);

	// 修改预留信息
	public String modholdinfo(UserRequestMsgV2 requestMsg, HttpServletRequest request);

}

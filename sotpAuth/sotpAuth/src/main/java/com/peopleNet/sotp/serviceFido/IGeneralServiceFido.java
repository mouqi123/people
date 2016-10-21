package com.peopleNet.sotp.serviceFido;

import javax.servlet.http.HttpServletRequest;

import com.peopleNet.sotp.vo.UserRequestMsgFido;
import com.peopleNet.sotp.vo.UserRequestMsgV2;

public interface IGeneralServiceFido {
	// 请求挑战码
	public String getchallenge(UserRequestMsgFido requestMsg, HttpServletRequest request);

	// 安全认证
	public String verify(UserRequestMsgFido requestMsg, HttpServletRequest request);

	// 安全认证
	public String verifyResponse(UserRequestMsgFido requestMsg, HttpServletRequest request);

	// 时间校准
	public String timeSynchr(UserRequestMsgFido requestMsg, HttpServletRequest request);

	// 检测app签名值和hash值
	public String checkAppSignHash(UserRequestMsgFido requestMsg, HttpServletRequest request);
	
	// 生成会话密钥
	public String gensesskey(UserRequestMsgFido requestMsg, HttpServletRequest request);
	
	// 本地数据加解密
	public String crypto(UserRequestMsgFido requestMsg, HttpServletRequest request);

}

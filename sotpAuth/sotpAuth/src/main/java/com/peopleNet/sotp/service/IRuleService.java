package com.peopleNet.sotp.service;

import com.peopleNet.sotp.vo.RiskControlResult;
import com.peopleNet.sotp.vo.UserRequestMsgFido;

/**
 * Created by wangxin on 16/7/21.
 */
public interface IRuleService {

	public RiskControlResult verifyRule(UserRequestMsgFido requestMsg);
}

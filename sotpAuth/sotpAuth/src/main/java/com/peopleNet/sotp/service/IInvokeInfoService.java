package com.peopleNet.sotp.service;

import com.peopleNet.sotp.dal.model.invokeInfoDto;

/**
 * 
 * @描述 在线日志处理
 * @author wangxin
 * @created_at 2015年12月07日
 */
public interface IInvokeInfoService {

	public int insertBatch(invokeInfoDto info);

}

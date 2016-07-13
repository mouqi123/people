package com.people.sotp.payment.service;


import com.people.sotp.commons.base.ResultDO;
import com.people.sotp.dataobject.AuditLogDO;
import com.people.sotp.dataobject.DealLogDO;

public interface LogService {

	/**
	 * 获取列表
	 * 
	 * @param model
	 *            实体类
	 * @return
	 */
	public ResultDO queryLogList(AuditLogDO model);

	public ResultDO queryDealLogList(DealLogDO model);
	
	public void insertDealLog(String phone, String dealInfo, String pcInfo, int status,
			String data);

}

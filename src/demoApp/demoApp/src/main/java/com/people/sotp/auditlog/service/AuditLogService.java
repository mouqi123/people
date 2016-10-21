package com.people.sotp.auditlog.service;

import javax.servlet.http.HttpServletRequest;

import com.people.sotp.commons.base.ResultDO;
import com.people.sotp.dataobject.AuditLogDO;

public interface AuditLogService {
	/**
	 * 
	 * @param request
	 * @param model
	 *            模块名称
	 * @param masterId
	 *            操作人编号
	 * @param operation
	 *            操作类型
	 * @param level
	 *            日志级别
	 * @param status
	 *            操作结果
	 * @param description
	 *            操作描述
	 */
	public void insertAuditLog(HttpServletRequest request, String model, long masterId, String operation, int level,
			int status, String description);

	public ResultDO queryAuditLogList(AuditLogDO log);

	public ResultDO getAuditLogInfo(AuditLogDO log);

}

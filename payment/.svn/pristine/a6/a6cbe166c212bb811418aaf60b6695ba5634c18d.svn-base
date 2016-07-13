package com.people.sotp.auditlog.service.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.people.sotp.auditlog.dao.AuditLogDAO;
import com.people.sotp.auditlog.service.AuditLogService;
import com.people.sotp.commons.base.DataGrid;
import com.people.sotp.commons.base.ResultDO;
import com.people.sotp.dataobject.AuditLogDO;

/**
 * 审计日志
 * 
 * @author tianchk
 */
@Service
public class AuditLogServiceImpl implements AuditLogService {

	private static Log log = LogFactory.getLog(AuditLogServiceImpl.class);
	@Resource
	private AuditLogDAO auditLogDAO;

	@Override
	public void insertAuditLog(HttpServletRequest request, String model, long masterId, String operation, int level,
			int status, String description) {
		AuditLogDO logDO = new AuditLogDO();
		logDO.setModelname(model);
		logDO.setMasterid(masterId);
		String srcip = getClientIP(request);
		String serverip = getServerIP();
		int serverport = getServerPort(request);
		logDO.setSrcip(srcip);
		logDO.setServerip(serverip);
		logDO.setServerport(serverport);
		logDO.setOperation(operation);
		logDO.setLevel(level + "");
		logDO.setStatus(status + "");
		logDO.setDescription(description);
		try {
			auditLogDAO.insertAuditLogo(logDO);
		} catch (SQLException e) {
			log.error("日志入库失败", e);
			e.printStackTrace();
		}
	}

	/**
	 * 获取服务器IP
	 * 
	 * @return
	 */
	public String getServerIP() {
		String ip = "127.0.0.1";
		try {
			InetAddress addr;
			addr = InetAddress.getLocalHost();
			ip = addr.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return ip;
	}

	/**
	 * 获取服务器端口
	 * 
	 * @return
	 */
	public int getServerPort(HttpServletRequest request) {
		int port = 0;
		port = request.getServerPort();
		return port;
	}

	/**
	 * 获取客户端IP
	 * 
	 * @return
	 */
	public String getClientIP(HttpServletRequest request) {
		String address = request.getHeader("X-Forwarded-For");
		if (address == null) {
			address = request.getRemoteAddr();
		}
		return address;
	}

	@Override
	public ResultDO queryAuditLogList(AuditLogDO logDO) {
		ResultDO result = new ResultDO();
		try {
			DataGrid grid = new DataGrid();
			grid.setTotal(auditLogDAO.queryAuditLogListCount(logDO));
			grid.setRows(auditLogDAO.queryAuditLogList(logDO));
			result.setModel("plugindata", grid);
		} catch (Exception e) {
			result.setErrMsg("获取帐号列表失败！");
			log.error("获取帐号列表失败", e);
		}
		return result;
	}

	@Override
	public ResultDO getAuditLogInfo(AuditLogDO logDO) {
		ResultDO result = new ResultDO();
		try {
			logDO = auditLogDAO.getAuditLogInfo(logDO);
			result.setModel("auditlogInfo", logDO);
		} catch (Exception e) {
			result.setErrMsg("获取日志信息失败！");
			log.error("获取日志信息失败", e);
		}
		return result;
	}

}

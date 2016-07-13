package com.people.sotp.payment.service.impl;

import java.sql.SQLException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.people.sotp.commons.base.DataGrid;
import com.people.sotp.commons.base.ResultDO;
import com.people.sotp.dataobject.AuditLogDO;
import com.people.sotp.dataobject.DealLogDO;
import com.people.sotp.payment.dao.LogDAO;
import com.people.sotp.payment.service.LogService;

@Service
public class LogServiceImpl implements LogService {
	@Resource
	private LogDAO logDAO;

	private static Log log = LogFactory.getLog(LogServiceImpl.class);

	public ResultDO queryLogList(AuditLogDO model) {
		ResultDO result = new ResultDO();
		try {
			DataGrid grid = new DataGrid();
			grid.setTotal(logDAO.queryLogListCount(model));
			grid.setRows(logDAO.queryLogList(model));
			result.setModel("masterdata", grid);
		} catch (Exception e) {
			result.setErrMsg("获取失败！");
			log.error("获取列表失败");
			log.error(e);
		}
		return result;
	}
	public ResultDO queryDealLogList(DealLogDO model) {
		ResultDO result = new ResultDO();
		try {
			DataGrid grid = new DataGrid();
			grid.setTotal(logDAO.queryDealLogListCount(model));
			grid.setRows(logDAO.queryDealLogList(model));
			result.setModel("masterdata", grid);
		} catch (Exception e) {
			result.setErrMsg("获取失败！");
			log.error("获取列表失败");
			log.error(e);
		}
		return result;
	}
	
	
	
	
	public void insertDealLog(String phone, String dealInfo, String pcInfo, int status,
			String data){
		DealLogDO logDO = new DealLogDO();
		logDO.setPhone(phone);
		logDO.setDealInfo(dealInfo);
		logDO.setPcInfo(pcInfo);
		logDO.setStatus(status);
		logDO.setData(data);
		try {
			logDAO.insertDealLog(logDO);
		} catch (SQLException e) {
			log.error("日志入库失败", e);
			e.printStackTrace();
		}
	}

}

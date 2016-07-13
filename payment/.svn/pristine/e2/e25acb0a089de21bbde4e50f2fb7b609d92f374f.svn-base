package com.people.sotp.payment.service.impl;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.people.sotp.commons.base.DataGrid;
import com.people.sotp.commons.base.ResultDO;
import com.people.sotp.dataobject.AuthDO;
import com.people.sotp.payment.dao.AuthDAO;
import com.people.sotp.payment.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {
	@Resource
	private AuthDAO AuthDAO;

	private static Log log = LogFactory.getLog(AuthServiceImpl.class);

	public ResultDO queryAuthList(AuthDO userDO) {
		ResultDO result = new ResultDO();
		try {
			DataGrid grid = new DataGrid();
			grid.setTotal(AuthDAO.queryAuthListCount(userDO));
			grid.setRows(AuthDAO.queryAuthList(userDO));
			result.setModel("masterdata", grid);
		} catch (Exception e) {
			result.setErrMsg("获取帐号列表失败！");
			log.error("获取帐号列表失败");
			log.error(e);
		}
		return result;
	}

	public ResultDO queryAuthOne(AuthDO userDO) {
		ResultDO result = new ResultDO();
		try {
			DataGrid grid = new DataGrid();
			grid.setRows(AuthDAO.queryAuthOne(userDO));
			result.setModel("masterdata", grid);
		} catch (Exception e) {
			result.setErrMsg("获取帐号列表失败！");
			log.error("获取帐号列表失败");
			log.error(e);
		}
		return result;
	}

	/**
	 * 添加
	 * 
	 * @param AuthDO
	 */
	public ResultDO addAuth(AuthDO userDO) {
		ResultDO result = new ResultDO();
		try {
			AuthDAO.addAuth(userDO);
		} catch (Exception e) {
			result.setErrMsg("添加用户失败！");
			log.error("添加用户失败");
			log.error(e);
		}
		return result;
	}

	/**
	 * 修改
	 */
	public ResultDO updateAuth(AuthDO userDO) {
		ResultDO result = new ResultDO();
		try {
			AuthDAO.updateAuth(userDO);
		} catch (Exception e) {
			result.setErrMsg("修改用户失败！");
			log.error("修改用户失败");
			log.error(e);
		}
		return result;
	}

	/**
	 * 删除
	 */
	public void deleteAuth(long[] ids) {
		try {
			for (int i = 0; i < ids.length; i++) {
				AuthDO userDO = new AuthDO();
				userDO.setId(ids[i]);
				AuthDAO.deleteAuth(userDO);
			}
		} catch (Exception e) {
			log.error("修改用户失败");
			log.error(e);
		}
	}

}

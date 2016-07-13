package com.people.sotp.payment.service.impl;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.people.sotp.commons.base.DataGrid;
import com.people.sotp.commons.base.ResultDO;
import com.people.sotp.dataobject.AccountManageDO;
import com.people.sotp.payment.dao.AccountManageDAO;
import com.people.sotp.payment.service.AccountManageService;

@Service
public class AccountManageServiceImpl implements AccountManageService {
	@Resource
	private AccountManageDAO AccountManageDAO;

	private static Log log = LogFactory.getLog(AccountManageServiceImpl.class);

	public ResultDO queryAccountManageList(AccountManageDO model) {
		ResultDO result = new ResultDO();
		try {
			DataGrid grid = new DataGrid();
			grid.setTotal(AccountManageDAO.queryAccountManageListCount(model));
			grid.setRows(AccountManageDAO.queryAccountManageList(model));
			result.setModel("masterdata", grid);
		} catch (Exception e) {
			result.setErrMsg("获取帐号列表失败！");
			log.error("获取帐号列表失败");
			log.error(e);
		}
		return result;
	}

	public ResultDO queryAccountManageOne(AccountManageDO model) {
		ResultDO result = new ResultDO();
		try {
			DataGrid grid = new DataGrid();
			grid.setRows(AccountManageDAO.queryAccountManageOne(model));
			result.setModel("masterdata", grid);
		} catch (Exception e) {
			result.setErrMsg("获取帐号列表失败！");
			log.error("获取帐号列表失败");
			log.error(e);
		}
		return result;
	}

	/**
	 * 添加账户
	 * 
	 * @param AccountManageDO
	 */
	public ResultDO addAccountManage(AccountManageDO model) {
		ResultDO result = new ResultDO();
		try {
			AccountManageDAO.addAccountManage(model);
		} catch (Exception e) {
			result.setErrMsg("添加账户失败！");
			log.error("添加账户失败");
			log.error(e);
		}
		return result;
	}

	/**
	 * 修改账户
	 */
	public ResultDO updateAccountManage(AccountManageDO model) {
		ResultDO result = new ResultDO();
		try {
			AccountManageDAO.updateAccountManage(model);
		} catch (Exception e) {
			result.setErrMsg("修改账户失败！");
			log.error("修改账户失败");
			log.error(e);
		}
		return result;
	}

	/**
	 * 删除账户
	 */
	public void deleteAccountManage(long[] ids) {
		try {
			for (int i = 0; i < ids.length; i++) {
				AccountManageDO model = new AccountManageDO();
				model.setId(ids[i]);
				AccountManageDAO.deleteAccountManage(model);
			}
		} catch (Exception e) {
			log.error("修改账户失败");
			log.error(e);
		}
	}

}

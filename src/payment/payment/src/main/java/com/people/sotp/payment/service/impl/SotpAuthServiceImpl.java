package com.people.sotp.payment.service.impl;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.people.sotp.commons.base.DataGrid;
import com.people.sotp.commons.base.ResultDO;
import com.people.sotp.dataobject.SotpAuthDO;
import com.people.sotp.payment.dao.SotpAuthDAO;
import com.people.sotp.payment.service.SotpAuthService;

@Service
public class SotpAuthServiceImpl implements SotpAuthService {
	@Resource
	private SotpAuthDAO SotpAuthDAO;

	private static Log log = LogFactory.getLog(SotpAuthServiceImpl.class);

	public ResultDO querySotpAuthList(SotpAuthDO model) {
		ResultDO result = new ResultDO();
		try {
			DataGrid grid = new DataGrid();
			grid.setTotal(SotpAuthDAO.querySotpAuthListCount(model));
			grid.setRows(SotpAuthDAO.querySotpAuthList(model));
			result.setModel("masterdata", grid);
		} catch (Exception e) {
			result.setErrMsg("获取列表失败！");
			log.error("获取列表失败");
			log.error(e);
		}
		return result;
	}

	/**
	 * 添加
	 * 
	 * @param SotpAuthDO
	 */
	public ResultDO addSotpAuth(SotpAuthDO model) {
		ResultDO result = new ResultDO();
		try {
			SotpAuthDAO.addSotpAuth(model);
		} catch (Exception e) {
			result.setErrMsg("添加失败！");
			log.error("添加失败");
			log.error(e);
		}
		return result;
	}

	/**
	 * 修改
	 */
	public ResultDO updateSotpAuth(SotpAuthDO model) {
		ResultDO result = new ResultDO();
		try {
			SotpAuthDAO.updateSotpAuth(model);
		} catch (Exception e) {
			result.setErrMsg("修改失败！");
			log.error("修改失败");
			log.error(e);
		}
		return result;
	}

	/**
	 * 删除
	 */
	public void deleteSotpAuth(long[] ids) {
		try {
			for (int i = 0; i < ids.length; i++) {
				SotpAuthDO model = new SotpAuthDO();
				model.setId(ids[i]);
				SotpAuthDAO.deleteSotpAuth(model);
			}
		} catch (Exception e) {
			log.error("删除失败");
			log.error(e);
		}
	}

}

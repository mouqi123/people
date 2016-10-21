package com.people.sotp.payment.service.impl;

import java.sql.SQLException;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.people.sotp.commons.base.DataGrid;
import com.people.sotp.commons.base.ResultDO;
import com.people.sotp.dataobject.ApplyDO;
import com.people.sotp.payment.dao.ApplyDAO;
import com.people.sotp.payment.service.ApplyService;

@Service
public class ApplyServiceImpl implements ApplyService {
	@Resource
	private ApplyDAO ApplyDAO;

	private static Log log = LogFactory.getLog(ApplyServiceImpl.class);

	public ResultDO queryApplyList(ApplyDO model) {
		ResultDO result = new ResultDO();
		try {
			DataGrid grid = new DataGrid();
			grid.setTotal(ApplyDAO.queryApplyListCount(model));
			grid.setRows(ApplyDAO.queryApplyList(model));
			result.setModel("masterdata", grid);
		} catch (Exception e) {
			result.setErrMsg("获取列表失败！");
			log.error("获取列表失败");
			log.error(e);
		}
		return result;
	}

	public ApplyDO queryApplyOne(ApplyDO model) {
		try {
			return ApplyDAO.queryApplyOne(model);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 添加账户
	 * 
	 * @param ApplyDO
	 */
	public ResultDO addApply(ApplyDO model) {
		ResultDO result = new ResultDO();
		try {
			ApplyDAO.addApply(model);
		} catch (Exception e) {
			result.setErrMsg("添加失败！");
			log.error("添加失败");
			log.error(e);
		}
		return result;
	}

	/**
	 * 修改账户
	 */
	public ResultDO updateApply(ApplyDO model) {
		ResultDO result = new ResultDO();
		try {
			ApplyDAO.updateApply(model);
		} catch (Exception e) {
			result.setErrMsg("修改失败！");
			log.error("修改失败");
			log.error(e);
		}
		return result;
	}

	/**
	 * 删除账户
	 */
	public void deleteApply(long[] ids) {
		try {
			for (int i = 0; i < ids.length; i++) {
				ApplyDO model = new ApplyDO();
				model.setId(ids[i]);
				ApplyDAO.deleteApply(model);
			}
		} catch (Exception e) {
			log.error("修改失败");
			log.error(e);
		}
	}

}

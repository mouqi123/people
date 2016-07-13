package com.people.sotp.payment.service.impl;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.people.sotp.commons.base.DataGrid;
import com.people.sotp.commons.base.ResultDO;
import com.people.sotp.dataobject.DealTacticsDO;
import com.people.sotp.payment.dao.DealTacticsDAO;
import com.people.sotp.payment.service.DealTacticsService;

@Service
public class DealTacticsServiceImpl implements DealTacticsService {
	@Resource
	private DealTacticsDAO DealTacticsDAO;

	private static Log log = LogFactory.getLog(DealTacticsServiceImpl.class);

	public ResultDO queryDealTacticsList(DealTacticsDO model) {
		ResultDO result = new ResultDO();
		try {
			DataGrid grid = new DataGrid();
			grid.setTotal(DealTacticsDAO.queryDealTacticsListCount(model));
			grid.setRows(DealTacticsDAO.queryDealTacticsList(model));
			result.setModel("masterdata", grid);
		} catch (Exception e) {
			result.setErrMsg("获取失败！");
			log.error("获取列表失败");
			log.error(e);
		}
		return result;
	}

	/**
	 * 添加账户
	 * 
	 * @param DealTacticsDO
	 */
	public ResultDO addDealTactics(DealTacticsDO model) {
		ResultDO result = new ResultDO();
		try {
			DealTacticsDAO.addDealTactics(model);
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
	public ResultDO updateDealTactics(DealTacticsDO model) {
		ResultDO result = new ResultDO();
		try {
			DealTacticsDAO.updateDealTactics(model);
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
	public void deleteDealTactics(long[] ids) {
		try {
			for (int i = 0; i < ids.length; i++) {
				DealTacticsDO model = new DealTacticsDO();
				model.setId(ids[i]);
				DealTacticsDAO.deleteDealTactics(model);
			}
		} catch (Exception e) {
			log.error("删除失败");
			log.error(e);
		}
	}

}

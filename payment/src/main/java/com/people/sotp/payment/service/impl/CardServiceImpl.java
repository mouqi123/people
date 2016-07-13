package com.people.sotp.payment.service.impl;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.people.sotp.commons.base.DataGrid;
import com.people.sotp.commons.base.ResultDO;
import com.people.sotp.dataobject.CardDO;
import com.people.sotp.payment.dao.CardDAO;
import com.people.sotp.payment.service.CardService;

@Service
public class CardServiceImpl implements CardService {
	@Resource
	private CardDAO CardDAO;

	private static Log log = LogFactory.getLog(CardServiceImpl.class);

	public ResultDO queryCardList(CardDO userDO) {
		ResultDO result = new ResultDO();
		try {
			DataGrid grid = new DataGrid();
			grid.setTotal(CardDAO.queryCardListCount(userDO));
			grid.setRows(CardDAO.queryCardList(userDO));
			result.setModel("masterdata", grid);
		} catch (Exception e) {
			result.setErrMsg("获取列表失败！");
			log.error("获取列表失败");
			log.error(e);
		}
		return result;
	}

	public ResultDO queryCardOne(CardDO userDO) {
		ResultDO result = new ResultDO();
		try {
			DataGrid grid = new DataGrid();
			grid.setRows(CardDAO.queryCardOne(userDO));
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
	 * @param CardDO
	 */
	public ResultDO addCard(CardDO userDO) {
		ResultDO result = new ResultDO();
		try {
			CardDAO.addCard(userDO);
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
	public ResultDO updateCard(CardDO userDO) {
		ResultDO result = new ResultDO();
		try {
			CardDAO.updateCard(userDO);
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
	public void deleteCard(long[] ids) {
		try {
			for (int i = 0; i < ids.length; i++) {
				CardDO userDO = new CardDO();
				userDO.setId(ids[i]);
				CardDAO.deleteCard(userDO);
			}
		} catch (Exception e) {
			log.error("修改用户失败");
			log.error(e);
		}
	}

}

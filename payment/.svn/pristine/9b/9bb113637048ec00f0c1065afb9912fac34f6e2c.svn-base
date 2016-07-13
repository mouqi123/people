package com.people.sotp.payment.dao.impl;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.people.sotp.common.dao.BaseDao;
import com.people.sotp.dataobject.CardDO;
import com.people.sotp.payment.dao.CardDAO;

@Repository
public class CardDAOImpl implements CardDAO {
	@Resource
	private BaseDao baseDao;

	public BaseDao getBaseDao() {
		return baseDao;
	}

	public void setBaseDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}

	/**
	 * 获取用户总数
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	public Integer queryCardListCount(CardDO model) throws SQLException {
		return baseDao.queryTotal("Card.selectCardCount", model);
	}

	/**
	 * 获取用户列表,分页
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public List<CardDO> queryCardList(CardDO model) throws SQLException {
		return (List<CardDO>) baseDao.query("Card.selectCardList", model);
	}

	/**
	 * 获取用户列表
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public List<CardDO> queryCardOne(CardDO model) throws SQLException {
		return (List<CardDO>) baseDao.query("Card.selectOneCard", model);
	}

	/**
	 * 新增用户
	 */
	public int addCard(CardDO model) throws SQLException {

		return baseDao.insert("Card.addCard", model);
	}

	/**
	 * 更改用户
	 */
	public int updateCard(CardDO model) throws SQLException {
		return baseDao.update("Card.updateCard", model);
	}

	/**
	 * 删除用户
	 */
	public int deleteCard(CardDO model) throws SQLException {
		return baseDao.delete("Card.deleteCard", model);
	}

}

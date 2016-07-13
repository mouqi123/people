package com.people.sotp.payment.dao;

import java.sql.SQLException;
import java.util.List;

import com.people.sotp.dataobject.CardDO;

public interface CardDAO {

	/**
	 * 获取
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	public Integer queryCardListCount(CardDO model) throws SQLException;

	/**
	 * 获取列表，分页
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	public List<CardDO> queryCardList(CardDO model) throws SQLException;

	/**
	 * 获取列表
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	public List<CardDO> queryCardOne(CardDO model) throws SQLException;

	/**
	 * 新增
	 * 
	 * @param model
	 * @return
	 * @throws SQLException
	 */
	public int addCard(CardDO model) throws SQLException;

	/**
	 * 修改
	 * 
	 * @param paramCardDO
	 * @return
	 * @throws SQLException
	 */
	public int updateCard(CardDO model) throws SQLException;

	/**
	 * 删除
	 * 
	 * @param paramCardDO
	 * @return
	 * @throws SQLException
	 */
	public int deleteCard(CardDO model) throws SQLException;

}

package com.people.sotp.payment.dao.impl;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.people.sotp.common.dao.BaseDao;
import com.people.sotp.dataobject.Card;

@Repository
public class CardDAO {

	@Resource
	private BaseDao baseDao;

	public BaseDao getBaseDao() {
		return baseDao;
	}

	public void setBaseDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}

	@SuppressWarnings("unchecked")
	public List<Card> queryCardList(long id) throws SQLException {
		return (List<Card>) baseDao.query("Card.selectCardList", id);
	}

	public Card queryCardOne(Card card) throws SQLException {
		return (Card) baseDao.queryOne("Card.selectOneCard", card);
	}
	
	public int addCard(Card card) throws SQLException {
		return baseDao.insert("Card.addCard", card);
	}
	
	public int deleteCard(Card card) throws SQLException {
		return baseDao.delete("Card.deleteCard", card);
	}
	
	public int updateCard(Card card) throws SQLException {
		return baseDao.update("Card.updateCard", card);
	}
}

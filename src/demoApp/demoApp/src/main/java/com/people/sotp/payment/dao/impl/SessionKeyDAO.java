package com.people.sotp.payment.dao.impl;

import java.sql.SQLException;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.people.sotp.common.dao.BaseDao;
import com.people.sotp.dataobject.SessionKeyDO;

@Repository
public class SessionKeyDAO {

	@Resource
	private BaseDao baseDao;

	public BaseDao getBaseDao() {
		return baseDao;
	}

	public void setBaseDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}

	public int addSessionKey(SessionKeyDO session)throws SQLException {
		return baseDao.insert("Session.addSessionKey", session);
	}

	public int editSessionKey(SessionKeyDO session)throws SQLException {
		return baseDao.update("Session.editSessionKey", session);
	}
	
	public SessionKeyDO selectSessionKeyOne(SessionKeyDO session) throws SQLException {
		return (SessionKeyDO) baseDao.queryOne("Session.selectSessionKeyOne", session);
	}
	
}

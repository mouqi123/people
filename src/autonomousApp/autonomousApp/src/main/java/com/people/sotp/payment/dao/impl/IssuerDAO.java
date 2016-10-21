package com.people.sotp.payment.dao.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.people.sotp.common.dao.BaseDao;
import com.people.sotp.dataobject.IssuerDO;

@Repository
public class IssuerDAO {
	
	@Resource
	private BaseDao baseDao;

	public BaseDao getBaseDao() {
		return baseDao;
	}

	public void setBaseDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}
	
	public IssuerDO queryIssuerOne(IssuerDO issuerDO) {
		return (IssuerDO) baseDao.queryOne("Issuer.selectOne", issuerDO);
	}
	
	public int addIssuer(IssuerDO issuerDO) {
		return baseDao.insert("Issuer.addIssuer", issuerDO);
	}
	
	public int updateIssuer(IssuerDO issuerDO) {
		return baseDao.update("Issuer.updateIssuer", issuerDO);
	}
	
	@SuppressWarnings("unchecked")
	public List<IssuerDO> queryIssuerList(int userId) {
		return (List<IssuerDO>) baseDao.query("Issuer.selectAccountsList", userId);
	}
	
	public int deleteIssuer(IssuerDO issuerDO) {
		return baseDao.delete("Issuer.deleteIssuer", issuerDO);
	}
}

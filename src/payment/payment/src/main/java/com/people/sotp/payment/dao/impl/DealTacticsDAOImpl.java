package com.people.sotp.payment.dao.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.people.sotp.common.dao.BaseDao;
import com.people.sotp.dataobject.DealTacticsDO;
import com.people.sotp.payment.dao.DealTacticsDAO;

@Repository
public class DealTacticsDAOImpl implements DealTacticsDAO {
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
	public Integer queryDealTacticsListCount(DealTacticsDO userDO) throws SQLException {
		return baseDao.queryTotal("DealTactics.selectDealTacticsCount", userDO);
	}

	/**
	 * 获取用户列表
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public List<DealTacticsDO> queryDealTacticsList(DealTacticsDO userDO) throws SQLException {
		return (List<DealTacticsDO>) baseDao.query("DealTactics.selectDealTacticsList", userDO);
	}
	
	
	public DealTacticsDO queryDealTacticsOne(Map<String, Object> map)  throws SQLException{
		return  (DealTacticsDO) baseDao.queryOne("DealTactics.selectDealTacticsOne", map);
	}
	
	

	/**
	 * 新增用户
	 */
	public int addDealTactics(DealTacticsDO userDO) throws SQLException {

		return baseDao.insert("DealTactics.addDealTactics", userDO);
	}

	/**
	 * 更改用户
	 */
	public int updateDealTactics(DealTacticsDO userDO) throws SQLException {
		return baseDao.update("DealTactics.updateDealTactics", userDO);
	}

	/**
	 * 删除用户
	 */
	public int deleteDealTactics(DealTacticsDO userDO) throws SQLException {
		return baseDao.delete("DealTactics.deleteDealTactics", userDO);
	}

}

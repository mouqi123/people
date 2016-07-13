package com.people.sotp.urlcontrollers;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.people.sotp.common.dao.BaseDao;
import com.people.sotp.dataobject.AuthDO;
import com.people.sotp.dataobject.CardDO;
import com.people.sotp.dataobject.MemberDO;
import com.people.sotp.dataobject.SessionKeyDO;
import com.people.sotp.dataobject.SotpAuthDO;
import com.people.sotp.dataobject.TransactionTacticsDO;

@Repository
public class SotpDao {

	@Resource
	private BaseDao baseDao;

	public BaseDao getBaseDao() {
		return baseDao;
	}

	public void setBaseDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}

	/**
	 * 开通或者关闭一键支付
	 * 
	 * @param map
	 * @return
	 * @throws SQLException
	 */
	public int openOnePay(Map<String, Object> map) throws SQLException {
		return baseDao.update("Sotp.openOnePay", map);
	}

	/**
	 * 业务的插件认证开关（开通、关闭）
	 * 
	 * @param map
	 * @return
	 * @throws SQLException
	 */
	public int businessOperate(Map<String, Object> map) throws SQLException {
		return baseDao.update("Sotp.businessOperate", map);
	}

	@SuppressWarnings("unchecked")
	public List<AuthDO> getBindlist(Map<String, Object> map) throws SQLException {
		return (List<AuthDO>) baseDao.query("Auth.getBindlist", map);
	}

	/**
	 * 条件单条查询银行卡数据
	 * 
	 * @param map
	 * @return
	 * @throws SQLException
	 */
	public CardDO onepayConsume(Map<String, Object> map) throws SQLException {
		return (CardDO) baseDao.queryOne("Card.selectOneCard", map);
	}

	/**
	 * 设置（或获取）支付限额
	 * 
	 * @param map
	 * @return
	 * @throws SQLException
	 */
	public String epayPolicy(Map<String, Object> map) throws SQLException {
		return (String) baseDao.queryOne("Sotp.epayPolicy", map);
	}

	public MemberDO loginMember(MemberDO userDO) throws SQLException {
		return (MemberDO) baseDao.queryOne("Member.selectOneMember", userDO);
	}

	public SotpAuthDO selectService() throws SQLException {
		return (SotpAuthDO) baseDao.queryOne("Sotp.selectService", null);
	}

	public TransactionTacticsDO operateLimit() throws SQLException {
		return (TransactionTacticsDO) baseDao.queryOne("Sotp.operateLimit", null);
	}

	public int updateoperateLimit(Map<String, Object> map) throws SQLException {
		return baseDao.update("Sotp.updateoperateLimit", map);
	}

	/**
	 * 获取银行卡总数
	 * 
	 * @param model
	 * @return
	 * @throws SQLException
	 */
	public Integer queryCardtCount(Map<String, Object> map) throws SQLException {
		return baseDao.queryTotal("Card.selectCardCount", map);
	}
	/**
	 * 新增预留信息
	 * @param map
	 * @return
	 * @throws SQLException
	 */
	public int addHoldInfo(Map<String, Object>map)throws SQLException {
		return baseDao.insert("Sotp.addHoldInfo", map);
	}
	/**
	 * 修改预留信息
	 * @param map
	 * @return
	 * @throws SQLException
	 */
	public int editHoldInfo(Map<String, Object>map)throws SQLException {
		return baseDao.update("Sotp.editHoldInfo", map);
	}
	/**
	 * 查询预留信息
	 * 
	 * @param model
	 * @return
	 * @throws SQLException
	 */
	public String queryHoldInfoOne(Map<String, Object> map) throws SQLException {
		return (String) baseDao.queryOne("Sotp.queryHoldInfoOne", map);
	}
	
	/**
	 * 新增会话
	 * @param map
	 * @return
	 * @throws SQLException
	 */
	public int addSessionKey(SessionKeyDO session)throws SQLException {
		return baseDao.insert("Sotp.addSessionKey", session);
	}
	
	/**
	 * 修改会话
	 * @param map
	 * @return
	 * @throws SQLException
	 */
	public int editSessionKey(SessionKeyDO session)throws SQLException {
		return baseDao.update("Sotp.editSessionKey", session);
	}
	
	public SessionKeyDO selectSessionKeyOne(SessionKeyDO session) throws SQLException {
		return (SessionKeyDO) baseDao.queryOne("Sotp.selectSessionKeyOne", session);
	}
	
}

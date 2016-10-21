package com.people.sotp.payment.dao.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.people.sotp.common.dao.BaseDao;
import com.people.sotp.dataobject.OrderDO;
import com.people.sotp.payment.dao.OrderDAO;

@Repository
public class OrderDAOImpl implements OrderDAO {
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
	public Integer queryOrderListCount(OrderDO userDO) throws SQLException {
		return baseDao.queryTotal("Order.selectOrderCount", userDO);
	}

	/**
	 * 获取用户列表
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public List<OrderDO> queryOrderList(OrderDO userDO) throws SQLException {
		return (List<OrderDO>) baseDao.query("Order.selectOrderList", userDO);
	}
	
	public OrderDO queryOrderOne(Map<String, Object> map){
		return (OrderDO) baseDao.queryOne("Order.selectOrderOne", map);
	}
	
	
	
	

	/**
	 * 新增用户
	 */
	public int addOrder(OrderDO userDO) throws SQLException {

		return baseDao.insert("Order.addOrder", userDO);
	}

	/**
	 * 更改用户
	 */
	public int updateOrder(OrderDO userDO) throws SQLException {
		return baseDao.update("Order.updateOrder", userDO);
	}

	/**
	 * 删除用户
	 */
	public int deleteOrder(OrderDO userDO) throws SQLException {
		return baseDao.delete("Order.deleteOrder", userDO);
	}

}

package com.people.sotp.payment.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.people.sotp.dataobject.OrderDO;

public interface OrderDAO {

	/**
	 * 获取列表总数总数
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	public Integer queryOrderListCount(OrderDO model) throws SQLException;

	/**
	 * 获取列表
	 * 
	 * @param masterDO
	 * @return
	 * @throws SQLException
	 */
	public List<OrderDO> queryOrderList(OrderDO model) throws SQLException;
	
	
	
	
	public OrderDO queryOrderOne(Map<String, Object> map);
	
	
	/**
	 * 新增
	 * 
	 * @param model
	 * @return
	 * @throws SQLException
	 */
	public int addOrder(OrderDO model) throws SQLException;

	/**
	 * 修改
	 * 
	 * @param paramOrderDO
	 * @return
	 * @throws SQLException
	 */
	public int updateOrder(OrderDO model) throws SQLException;

	/**
	 * 删除
	 * 
	 * @param paramOrderDO
	 * @return
	 * @throws SQLException
	 */
	public int deleteOrder(OrderDO model) throws SQLException;

}

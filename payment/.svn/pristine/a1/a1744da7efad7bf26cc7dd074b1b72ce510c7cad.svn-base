package com.people.sotp.payment.service;

import java.util.Map;

import com.people.sotp.commons.base.ResultDO;
import com.people.sotp.dataobject.OrderDO;

public interface OrderService {

	/**
	 * 获取列表
	 * 
	 * @param model
	 *            实体类
	 * @return
	 */
	public ResultDO queryOrderList(OrderDO model);
	
	
	
	public OrderDO queryOrderOne(Map<String, Object> map);
	
	/**
	 * 新增
	 * 
	 * @param model
	 * @return
	 */
	public ResultDO addOrder(OrderDO model);

	/**
	 * 修改
	 * 
	 * @param model
	 * @return
	 */
	public ResultDO updateOrder(OrderDO model);

	/**
	 * 删除
	 * 
	 * @param ids
	 */
	public void deleteOrder(long[] ids);

}

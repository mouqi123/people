package com.people.sotp.payment.service.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.people.sotp.commons.base.DataGrid;
import com.people.sotp.commons.base.ResultDO;
import com.people.sotp.dataobject.OrderDO;
import com.people.sotp.payment.dao.OrderDAO;
import com.people.sotp.payment.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {
	@Resource
	private OrderDAO OrderDAO;

	private static Log log = LogFactory.getLog(OrderServiceImpl.class);

	public ResultDO queryOrderList(OrderDO model) {
		ResultDO result = new ResultDO();
		try {
			DataGrid grid = new DataGrid();
			grid.setTotal(OrderDAO.queryOrderListCount(model));
			grid.setRows(OrderDAO.queryOrderList(model));
			result.setModel("masterdata", grid);
		} catch (Exception e) {
			result.setErrMsg("获取失败！");
			log.error("获取列表失败");
			log.error(e);
		}
		return result;
	}

	
	
	public OrderDO queryOrderOne(Map<String, Object> map){
		OrderDO order = new OrderDO();
		try {
			order =OrderDAO.queryOrderOne(map);
		} catch (Exception e) {
			log.error("添加失败");
			log.error(e);
		}
		return order;
	}
	
	
	
	/**
	 * 添加账户
	 * 
	 * @param OrderDO
	 */
	public ResultDO addOrder(OrderDO model) {
		ResultDO result = new ResultDO();
		try {
			OrderDAO.addOrder(model);
		} catch (Exception e) {
			result.setErrMsg("添加失败！");
			log.error("添加失败");
			log.error(e);
		}
		return result;
	}

	/**
	 * 修改账户
	 */
	public ResultDO updateOrder(OrderDO model) {
		ResultDO result = new ResultDO();
		try {
			OrderDAO.updateOrder(model);
		} catch (Exception e) {
			result.setErrMsg("修改失败！");
			log.error("修改失败");
			log.error(e);
		}
		return result;
	}

	/**
	 * 删除账户
	 */
	public void deleteOrder(long[] ids) {
		try {
			for (int i = 0; i < ids.length; i++) {
				OrderDO model = new OrderDO();
				model.setId(ids[i]);
				OrderDAO.deleteOrder(model);
			}
		} catch (Exception e) {
			log.error("删除失败");
			log.error(e);
		}
	}

}

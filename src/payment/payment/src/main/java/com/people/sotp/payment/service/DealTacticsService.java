package com.people.sotp.payment.service;

import com.people.sotp.commons.base.ResultDO;
import com.people.sotp.dataobject.DealTacticsDO;

public interface DealTacticsService {

	/**
	 * 获取列表
	 * 
	 * @param model
	 *            实体类
	 * @return
	 */
	public ResultDO queryDealTacticsList(DealTacticsDO model);

	/**
	 * 新增
	 * 
	 * @param model
	 * @return
	 */
	public ResultDO addDealTactics(DealTacticsDO model);

	/**
	 * 修改
	 * 
	 * @param model
	 * @return
	 */
	public ResultDO updateDealTactics(DealTacticsDO model);

	/**
	 * 删除
	 * 
	 * @param ids
	 */
	public void deleteDealTactics(long[] ids);

}

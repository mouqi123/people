package com.people.sotp.payment.service;

import com.people.sotp.commons.base.ResultDO;
import com.people.sotp.dataobject.CardDO;

public interface CardService {

	/**
	 * 获取列表,分页
	 * 
	 * @param UserDO
	 *            实体类
	 * @return
	 */
	public ResultDO queryCardList(CardDO userDO);

	/**
	 * 获取列表
	 * 
	 * @param UserDO
	 *            实体类
	 * @return
	 */
	public ResultDO queryCardOne(CardDO userDO);

	/**
	 * 新增
	 * 
	 * @param userDO
	 * @return
	 */
	public ResultDO addCard(CardDO userDO);

	/**
	 * 修改
	 * 
	 * @param userDO
	 * @return
	 */
	public ResultDO updateCard(CardDO userDO);

	/**
	 * 删除
	 * 
	 * @param ids
	 */
	public void deleteCard(long[] ids);

}

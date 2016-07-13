package com.people.sotp.payment.service;

import com.people.sotp.commons.base.ResultDO;
import com.people.sotp.dataobject.MemberDO;

public interface MemberService {

	/**
	 * 获取用户列表,分页
	 * 
	 * @param UserDO
	 *            实体类
	 * @return
	 */
	public ResultDO queryMemberList(MemberDO userDO);


	/**
	 * 获取用户
	 * 
	 * @param UserDO
	 *            实体类
	 * @return
	 */
	public MemberDO queryMemberOne(MemberDO userDO);

	/**
	 * 新增用户
	 * 
	 * @param userDO
	 * @return
	 */
	public ResultDO addMember(MemberDO userDO);

	/**
	 * 修改用户
	 * 
	 * @param userDO
	 * @return
	 */
	public ResultDO updateMember(MemberDO userDO);

	/**
	 * 删除用户
	 * 
	 * @param ids
	 */
	public void deleteMember(long[] ids);


}

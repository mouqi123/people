package com.people.sotp.payment.service.impl;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.people.sotp.commons.base.DataGrid;
import com.people.sotp.commons.base.ResultDO;
import com.people.sotp.dataobject.UserDO;
import com.people.sotp.payment.dao.MemberDAO;
import com.people.sotp.payment.service.MemberService;

@Service
public class MemberServiceImpl implements MemberService {
	@Resource
	private MemberDAO memberDAO;
	private static Log log = LogFactory.getLog(MemberServiceImpl.class);

	public ResultDO queryMemberList(UserDO userDO) {
		ResultDO result = new ResultDO();
		try {
			DataGrid grid = new DataGrid();
			grid.setTotal(memberDAO.queryMemberListCount(userDO));
			grid.setRows(memberDAO.queryMemberList(userDO));
			result.setModel("masterdata", grid);
		} catch (Exception e) {
			result.setErrMsg("获取帐号列表失败！");
			log.error("获取帐号列表失败");
			log.error(e);
		}
		return result;
	}


	public UserDO queryMemberOne(UserDO userDO) {
		UserDO user = new UserDO();
		try {
			 user =(UserDO) memberDAO.queryMemberOne(userDO);
		} catch (Exception e) {
			log.error("获取帐号列表失败");
			log.error(e);
		}
		return user;
	}

	/**
	 * 添加用户
	 * 
	 * @param UserDO
	 */
	public ResultDO addMember(UserDO userDO) {
		ResultDO result = new ResultDO();
		try {
			memberDAO.addMember(userDO);
		} catch (Exception e) {
			result.setErrMsg("添加用户失败！");
			log.error("添加用户失败");
			log.error(e);
		}
		return result;
	}

	/**
	 * 修改用户
	 */
	public ResultDO updateMember(UserDO userDO) {
		ResultDO result = new ResultDO();
		try {
			memberDAO.updateMember(userDO);
		} catch (Exception e) {
			result.setErrMsg("修改用户失败！");
			log.error("修改用户失败");
			log.error(e);
		}
		return result;
	}

	/**
	 * 删除用户
	 */
	public void deleteMember(long[] ids) {
		try {
			for (int i = 0; i < ids.length; i++) {
				UserDO userDO = new UserDO();
				userDO.setId((int) ids[i]);
				memberDAO.deleteMember(userDO);
			}
		} catch (Exception e) {
			log.error("修改用户失败");
			log.error(e);
		}
	}

	

}

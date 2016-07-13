package com.people.sotp.payment.service.impl;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.people.sotp.commons.base.DataGrid;
import com.people.sotp.commons.base.ResultDO;
import com.people.sotp.dataobject.AuthDO;
import com.people.sotp.dataobject.MemberDO;
import com.people.sotp.payment.dao.AuthDAO;
import com.people.sotp.payment.dao.MemberDAO;
import com.people.sotp.payment.service.MemberService;

@Service
public class MemberServiceImpl implements MemberService {
	@Resource
	private MemberDAO memberDAO;
	@Resource
	private AuthDAO authDAOl;
	private static Log log = LogFactory.getLog(MemberServiceImpl.class);

	public ResultDO queryMemberList(MemberDO userDO) {
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

	public ResultDO queryMemberAuthList() {
		ResultDO result = new ResultDO();
		try {
			DataGrid grid = new DataGrid();
			grid.setRows(memberDAO.queryMemberAuthList());
			result.setModel("masterdata", grid);
		} catch (Exception e) {
			result.setErrMsg("获取帐号列表失败！");
			log.error("获取帐号列表失败");
			log.error(e);
		}
		return result;
	}

	public ResultDO queryMemberOne(MemberDO userDO) {
		ResultDO result = new ResultDO();
		try {
			MemberDO user =(MemberDO) memberDAO.queryMemberOne(userDO);
			result.setModel("masterdata", user);
		} catch (Exception e) {
			result.setErrMsg("获取帐号列表失败！");
			log.error("获取帐号列表失败");
			log.error(e);
		}
		return result;
	}

	/**
	 * 添加用户
	 * 
	 * @param MemberDO
	 */
	public ResultDO addMember(MemberDO userDO) {
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
	public ResultDO updateMember(MemberDO userDO) {
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
				MemberDO userDO = new MemberDO();
				userDO.setId(ids[i]);
				memberDAO.deleteMember(userDO);
			}
		} catch (Exception e) {
			log.error("修改用户失败");
			log.error(e);
		}
	}

	public ResultDO loginMember(MemberDO userDO) {
		ResultDO result = new ResultDO();
		try {
			MemberDO member = memberDAO.loginMember(userDO);
			if (member != null) {
				AuthDO authDO = new AuthDO();
				authDO.setMemberId(Long.toString(member.getId()));
				DataGrid grid = new DataGrid();
				grid.setRows(authDAOl.queryAuthOne(authDO));
				result.setModel("masterdata", grid);
			} else {
				result.setErrMsg("账户,密码错误");
				return result;
			}
		} catch (Exception e) {
			result.setErrMsg("账户,密码错误");
			log.error("登录失败");
			log.error(e);
		}
		return result;
	}

}

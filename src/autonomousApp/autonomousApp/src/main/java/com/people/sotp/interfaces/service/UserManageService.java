package com.people.sotp.interfaces.service;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.people.sotp.common.cache.ICacheService;
import com.people.sotp.commons.base.BaseController;
import com.people.sotp.commons.base.GlobalParam;
import com.people.sotp.commons.base.SingleSiteLoginFilter;
import com.people.sotp.commons.util.AlidayuNote;
import com.people.sotp.commons.util.EncryptUtil;
import com.people.sotp.dataobject.AutoAppDO;
import com.people.sotp.dataobject.LoginLogDO;
import com.people.sotp.dataobject.MemberDO;
import com.people.sotp.dataobject.UserFeedbackDO;
import com.people.sotp.payment.dao.LoginLogDAO;
import com.people.sotp.payment.dao.UserFeedbackDAO;
import com.people.sotp.payment.dao.impl.MemberDAOImpl;
import com.people.sotp.util.FileUtil;

import net.sf.json.JSONObject;

@Service
public class UserManageService extends BaseController {

	@Resource
	private MemberDAOImpl memberDAO;
	@Resource
	private LoginLogDAO loginLogDAO;
	@Resource
	private UserFeedbackDAO userFeedbackDAO;

	@Resource
	private ICacheService icaChe;

	private static Log log = LogFactory.getLog(UserManageService.class);

	public String registerNote(AutoAppDO auto, HttpServletResponse response) {
		MemberDO member = new MemberDO();
		StringBuilder jsondata = new StringBuilder();
		StringBuilder errorMsg = new StringBuilder();
		member.setPhoneNum(auto.getPhoneNum());
		try {
			member = memberDAO.queryMemberOne(member);
		} catch (Exception e) {
			log.debug(e);
			jsondata.append("{}");
			errorMsg.append("查询手机号失败");
			return utilJson(auto.getService(), GlobalParam.ParameterError, jsondata.toString(), errorMsg.toString());
		}
		if (member != null) {
			jsondata.append("{}");
			errorMsg.append("账户已存在");
			return utilJson(auto.getService(), GlobalParam.UserExusts, jsondata.toString(), errorMsg.toString());
		} else {
			String nonce_str = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));

			try {
				icaChe.updateNoteRedis(GlobalParam.NodeRegisterCode, auto.getPhoneNum(), nonce_str);
			} catch (Exception e) {
				log.debug(e);
				jsondata.append("{}");
				errorMsg.append("连接redis失败");
				return utilJson(auto.getService(), GlobalParam.ParameterError, jsondata.toString(),
						errorMsg.toString());
			}

			AlidayuNote.sendNoteCode(auto.getPhoneNum(), nonce_str, GlobalParam.registerNote);

		}

		jsondata.append("{}");
		return utilJson(auto.getService(), 0, jsondata.toString(), errorMsg.toString());
	}

	public String registerUser(AutoAppDO auto, HttpServletResponse response) {
		StringBuilder jsondata = new StringBuilder();
		StringBuilder errorMsg = new StringBuilder();

		MemberDO member = new MemberDO();
		String passwordMD5 = null;
		if (auto.getPassword() != null) {
			passwordMD5 = EncryptUtil.md5(auto.getPassword());
		}
		member.setPhoneNum(auto.getPhoneNum());
		member.setPassword(passwordMD5);
		try {
			String noteCode = icaChe.getNoteCode(GlobalParam.NodeRegisterCode, auto.getPhoneNum());
			if ("".equals(noteCode) || noteCode == null) {
				errorMsg.append("您的验证码，已经过有效期");
				return utilJson(auto.getService(), GlobalParam.FailureNoteCode, jsondata.toString(),
						errorMsg.toString());
			}
			if (!noteCode.equals(auto.getVerCode())) {
				errorMsg.append("您的验证码错误");
				return utilJson(auto.getService(), GlobalParam.NoteCodeErroe, jsondata.toString(), errorMsg.toString());
			}
			member = memberDAO.queryMemberOne(member);
			if (member != null) {
				jsondata.append("{}");
				errorMsg.append("账户已存在");
				return utilJson(auto.getService(), GlobalParam.UserExist, jsondata.toString(), errorMsg.toString());
			}
			MemberDO user = new MemberDO();
			user.setPhoneNum(auto.getPhoneNum());
			user.setPassword(passwordMD5);
			memberDAO.addMember(user);
			member = memberDAO.queryMemberOne(user); // 取出member，因为要用到member.id

			String devinfo = auto.getDevinfo();
			JSONObject json = JSONObject.fromObject(devinfo);
			LoginLogDO loginLog = new LoginLogDO();

			loginLog.setUserId(member.getId());
			loginLog.setPhoneNum(auto.getPhoneNum());
			loginLog.setAddress(json.getString("address"));
			loginLog.setLoginTime(json.getString("loginTime"));
			loginLog.setPhoneType(json.getString("phoneType"));
			loginLogDAO.addLoginLog(loginLog);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		HttpServletRequest request = getRequest();
		request.getSession(true).setAttribute("member", member);

		addUniqCookie(member, response);

		jsondata.append("{}");
		return utilJson(auto.getService(), 0, jsondata.toString(), errorMsg.toString());
	}

	public String login(AutoAppDO auto, HttpServletResponse response) {
		StringBuilder jsondata = new StringBuilder();
		StringBuilder errorMsg = new StringBuilder();
		MemberDO member = new MemberDO();
		if ("".equals(auto.getPhoneNum()) || "".equals(auto.getPassword())) {
			errorMsg.append("手机号或者密码是空！");
			return utilJson(auto.getService(), GlobalParam.paramError, jsondata.toString(), errorMsg.toString());
		}

		String passwordMD5 = null;
		if (auto.getPassword() != null) {
			passwordMD5 = EncryptUtil.md5(auto.getPassword());
		}
		member.setPhoneNum(auto.getPhoneNum());
		member.setPassword(passwordMD5);
		try {
			member = memberDAO.queryMemberOne(member);
			if (member == null) {
				jsondata.append("{}");
				errorMsg.append("账户或密码错误！");
				return utilJson(auto.getService(), GlobalParam.UserExist, jsondata.toString(), errorMsg.toString());

			}
			String[] includeProperties = { "phoneNum", "username", "picPath", "password" };
			jsondata.append(" " + writeJsonByFilter(member, includeProperties, null) + " ");

			int num = jsondata.toString().indexOf("{");
			if (num < 0) {
				jsondata.append("{}");
			}
			String devinfo = auto.getDevinfo();
			JSONObject json = JSONObject.fromObject(devinfo);
			LoginLogDO loginLog = new LoginLogDO();

			loginLog.setUserId(member.getId());
			loginLog.setPhoneNum(auto.getPhoneNum());
			loginLog.setAddress(json.getString("address"));
			loginLog.setLoginTime(json.getString("loginTime"));
			loginLog.setPhoneType(json.getString("phoneType"));
			loginLogDAO.addLoginLog(loginLog);
		} catch (Exception e) {
			log.debug(e);
			e.printStackTrace();
		}

		// 登录成功，设置一个Session,用于用户的权限控制。
		HttpServletRequest request = getRequest();
		request.getSession(true).setAttribute("member", member);

		addUniqCookie(member, response);

		return utilJson(auto.getService(), 0, jsondata.toString(), errorMsg.toString());
	}

	public String setUserInfo(AutoAppDO auto) {
		StringBuilder jsondata = new StringBuilder();
		StringBuilder errorMsg = new StringBuilder();
		MemberDO member = (MemberDO) getRequest().getSession().getAttribute("member");

		String userinfo = auto.getUserInfo();
		jsondata.append("{}");
		if (userinfo == null) {
			errorMsg.append("userinfo为空！");
			return utilJson(auto.getService(), GlobalParam.UserExist, jsondata.toString(), errorMsg.toString());
		}
		JSONObject json = JSONObject.fromObject(userinfo);
		if (json.containsKey("phoneNum")) {
			member.setPhoneNum(json.getString("phoneNum"));
		}
		if (json.containsKey("picPath")) {
			member.setPicPath(json.getString("picPath"));
		}
		if (json.containsKey("username")) {
			member.setUserName(json.getString("username"));
		}
		if (json.containsKey("email")) {
			member.setEmail(json.getString("email"));
		}
		if (json.containsKey("identityType")) {
			member.setIdentityType(json.getInt("identityType"));
		}
		if (json.containsKey("identityNumber")) {
			member.setIdentityNumber(json.getString("identityNumber"));
		}
		if (json.containsKey("password")) {
			String passwordMD5 = EncryptUtil.md5(json.getString("password"));
			member.setPassword(passwordMD5);
		}
		try {
			memberDAO.updateMember(member);
		} catch (SQLException e) {
			log.debug(e);
			e.printStackTrace();
		}
		return utilJson(auto.getService(), 0, jsondata.toString(), errorMsg.toString());
	}

	public String getUserInfo(AutoAppDO auto) {
		StringBuilder jsondata = new StringBuilder();
		StringBuilder errorMsg = new StringBuilder();
		MemberDO member = new MemberDO();
		member.setPhoneNum(auto.getPhoneNum());

		if (!checkSession(auto)) {
			jsondata.append("{}");
			errorMsg.append("您还没有登录，请登录后再尝试请求！");
			return utilJson(auto.getService(), GlobalParam.NOT_LOGIN, jsondata.toString(), errorMsg.toString());
		}

		try {

			member = memberDAO.queryMemberOne(member);
			String[] includeProperties = { "phoneNum", "userName", "picPath", "password" };
			jsondata.append(" " + writeJsonByFilter(member, includeProperties, null) + " ");
			int num = jsondata.toString().indexOf("{");
			if (num < 0) {
				jsondata.append("{}");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return utilJson(auto.getService(), 0, jsondata.toString(), errorMsg.toString());
	}

	public String setAuthentication(AutoAppDO auto) {
		StringBuilder jsondata = new StringBuilder();
		StringBuilder errorMsg = new StringBuilder();
		MemberDO member = (MemberDO) getRequest().getSession().getAttribute("member");
		String userInfo = auto.getUserInfo();

		if (userInfo == null) {
			jsondata.append("{}");
			errorMsg.append("userinfo为空！");
			return utilJson(auto.getService(), GlobalParam.UserExist, jsondata.toString(), errorMsg.toString());
		}
		JSONObject json = JSONObject.fromObject(userInfo);
		try {
			member.setPhoneNum(json.getString("phoneNum"));
			member.setIdentityType(json.getInt("identityType"));
			member.setIdentityNumber(json.getString("identityNum"));
			memberDAO.updateMember(member);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		jsondata.append("{}");
		return utilJson(auto.getService(), 0, jsondata.toString(), errorMsg.toString());
	}

	public String getAuthentication(AutoAppDO auto) {
		StringBuilder jsondata = new StringBuilder();
		StringBuilder errorMsg = new StringBuilder();
		MemberDO member = new MemberDO();

		if (!checkSession(auto)) {
			jsondata.append("{}");
			errorMsg.append("您还没有登录，请登录后再尝试请求！");
			return utilJson(auto.getService(), GlobalParam.NOT_LOGIN, jsondata.toString(), errorMsg.toString());
		}

		try {
			member.setPhoneNum(auto.getPhoneNum());
			member = memberDAO.queryMemberIdentityOne(member);

			// String[] includeProperties = { "identityType", "identityNumber"
			// };
			// jsondata.append(" " + writeJsonByFilter(member,
			// includeProperties, null) + " ");
			String s;
			try {
				s = "{\"identityNum\":\"" + member.getIdentityNumber() + "\",\"identityType\":"
						+ member.getIdentityType() + "}";
			} catch (Exception e) {
				s = "{}";
			}
			jsondata.append(s);

		} catch (SQLException e) {
			e.printStackTrace();
			jsondata.append("{}");
			return utilJson(auto.getService(), GlobalParam.ParameterError, jsondata.toString(), errorMsg.toString());
		}

		return utilJson(auto.getService(), 0, jsondata.toString(), errorMsg.toString());
	}

	public String loginLog(AutoAppDO auto) {
		StringBuilder jsondata = new StringBuilder();
		StringBuilder errorMsg = new StringBuilder();
		StringBuilder addressCount = new StringBuilder();
		MemberDO member = (MemberDO) getRequest().getSession().getAttribute("member");
		member.setPhoneNum(auto.getPhoneNum());

		try {
			member = memberDAO.queryMemberOne(member);
		} catch (Exception e) {
			e.printStackTrace();
		}

		LoginLogDO loginlog = new LoginLogDO();
		loginlog.setUserId(member.getId());

		if (!checkSession(auto)) {
			jsondata.append("{}");
			errorMsg.append("您还没有登录，请登录后再尝试请求！");
			return utilJson(auto.getService(), GlobalParam.NOT_LOGIN, jsondata.toString(), errorMsg.toString());
		}

		Map<String, Integer> times = new HashMap<>(); // times为登录地的次数统计

		try {
			List<LoginLogDO> list = loginLogDAO.queryLoginLogList(loginlog);
			for (LoginLogDO loginDo : list) {
				String address = loginDo.getAddress();
				if (!times.containsKey(address)) {
					times.put(address, 0);
				}
				times.put(address, times.get(address) + 1);
			}
			log.info("登录地和次数的统计信息：-----------" + times);

			jsondata.append(" " + writeJson(list) + " ");
			if (times.size() == 0)
				addressCount.append("[]");
			else {
				addressCount.append("[");
				for (Map.Entry<String, Integer> entry : times.entrySet()) {
					String address = entry.getKey() == null ? "" : entry.getKey();
					addressCount
							.append("{\"address\":\"" + address + "\",\"count\":\"" + entry.getValue() + "\"}");
					addressCount.append(",");
				}
				addressCount = new StringBuilder(addressCount.toString().substring(0, addressCount.length() - 1));
				addressCount.append("]");
			}
		} catch (SQLException e) {
			log.error(e.getCause());
		}
		// return utilJson(auto.getService(), 0, jsondata.toString(),
		// errorMsg.toString());
		return utilJson(auto.getService(), 0, jsondata.toString(), errorMsg.toString(), addressCount.toString());
	}

	public String userFeedback(AutoAppDO auto) {

		StringBuilder jsondata = new StringBuilder();
		StringBuilder errorMsg = new StringBuilder();
		jsondata.append("{}");

		if (!checkSession(auto)) {
			errorMsg.append("您还没有登录，请登录后再尝试请求！");
			return utilJson(auto.getService(), GlobalParam.NOT_LOGIN, jsondata.toString(), errorMsg.toString());
		}

		UserFeedbackDO feedback = new UserFeedbackDO();
		feedback.setPhoneNum(auto.getPhoneNum());
		feedback.setFeedbackInfo(auto.getFeedbackInfo());

		try {
			userFeedbackDAO.addUserFeedback(feedback);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return utilJson(auto.getService(), 0, jsondata.toString(), errorMsg.toString());
	}

	public String upPportrait(String service, HttpServletRequest request) {
		StringBuilder jsondata = new StringBuilder();
		StringBuilder errorMsg = new StringBuilder();
		MemberDO member = (MemberDO) getRequest().getSession().getAttribute("member");

		if (member == null) {
			jsondata.append("{}");
			errorMsg.append("您还没有登录，请登录后再尝试请求！");
			return utilJson(service, GlobalParam.NOT_LOGIN, jsondata.toString(), errorMsg.toString());
		}

		try {
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
			Iterator<?> iter = multiRequest.getFileNames();
			while (iter.hasNext()) {
				MultipartFile file = multiRequest.getFile((String) iter.next());
				if (file != null) {
					String fileName = file.getOriginalFilename();
					String picPath = "/download/" + member.getId() + "/" + fileName;
					String path = getWebRootPath() + picPath;
					if (!FileUtil.createFile(path))
						log.error("无法创建文件或目录: " + path);

					File localFile = new File(path);

					// 写文件到本地
					file.transferTo(localFile);

					member.setPicPath(picPath);
					memberDAO.updateMember(member);
					jsondata.append("{\"picPath\":\"" + picPath + "\"}");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return utilJson(service, GlobalParam.UpPortraitError, jsondata.toString(), errorMsg.toString());
		}
		return utilJson(service, 0, jsondata.toString(), errorMsg.toString());

	}

	public String oldPassword(AutoAppDO auto) {
		StringBuilder jsondata = new StringBuilder();
		StringBuilder errorMsg = new StringBuilder();
		jsondata.append("{}");

		if (!checkSession(auto)) {
			errorMsg.append("您还没有登录，请登录后再尝试请求！");
			return utilJson(auto.getService(), GlobalParam.NOT_LOGIN, jsondata.toString(), errorMsg.toString());
		}

		MemberDO member = new MemberDO();
		String passwordMD5 = null;
		if (auto.getPassword() != null) {
			passwordMD5 = EncryptUtil.md5(auto.getPassword());
		}
		member.setPhoneNum(auto.getPhoneNum());
		member.setPassword(passwordMD5);
		try {
			member = memberDAO.queryMemberOne(member);
			if (member == null) {
				jsondata.append("{}");
				errorMsg.append("密码错误");
				return utilJson(auto.getService(), GlobalParam.UserExist, jsondata.toString(), errorMsg.toString());
			}
		} catch (Exception e) {
		}
		return utilJson(auto.getService(), 0, jsondata.toString(), errorMsg.toString());
	}

	public String newPassword(AutoAppDO auto) {
		StringBuilder jsondata = new StringBuilder();
		StringBuilder errorMsg = new StringBuilder();
		jsondata.append("{}");
		MemberDO member = (MemberDO) getRequest().getSession().getAttribute("member");
		String passwordMD5 = null;
		if (auto.getNewPasswrd() != null) {
			passwordMD5 = EncryptUtil.md5(auto.getNewPasswrd());
		}
		member.setPhoneNum(auto.getPhoneNum());
		member.setPassword(passwordMD5);

		try {
			memberDAO.updateMember(member);
			member = memberDAO.queryMemberOne(member);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HttpServletRequest request = getRequest();
		request.getSession(true).setAttribute("member", member);

		return utilJson(auto.getService(), 0, jsondata.toString(), errorMsg.toString());
	}

	public String findPwdCode(AutoAppDO auto, HttpServletResponse response) {
		StringBuilder jsondata = new StringBuilder();
		StringBuilder errorMsg = new StringBuilder();
		jsondata.append("{}");
		String nonce_str = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));

		try {
			icaChe.updateNoteRedis(GlobalParam.FindPassword, auto.getPhoneNum(), nonce_str);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AlidayuNote.sendNoteCode(auto.getPhoneNum(), nonce_str, GlobalParam.findPasswordNote);
		return utilJson(auto.getService(), 0, jsondata.toString(), errorMsg.toString());
	}

	public String findPassword(AutoAppDO auto, HttpServletResponse response) {
		StringBuilder jsondata = new StringBuilder();
		StringBuilder errorMsg = new StringBuilder();
		jsondata.append("{}");

		String noteCode = icaChe.getNoteCode(GlobalParam.FindPassword, auto.getPhoneNum());
		if ("".equals(noteCode) || noteCode == null) {
			errorMsg.append("您的验证码，已经过有效期");
			return utilJson(auto.getService(), GlobalParam.FailureNoteCode, jsondata.toString(), errorMsg.toString());
		}
		if (!noteCode.equals(auto.getVerCode())) {
			errorMsg.append("您的验证码错误");
			return utilJson(auto.getService(), GlobalParam.NoteCodeErroe, jsondata.toString(), errorMsg.toString());
		}

		MemberDO member = new MemberDO();
		String passwordMD5 = null;
		if (auto.getPassword() != null) {
			passwordMD5 = EncryptUtil.md5(auto.getPassword());
		}
		member.setPhoneNum(auto.getPhoneNum());
		member.setPassword(passwordMD5);

		try {
			memberDAO.updateMember(member);
			member = memberDAO.queryMemberOne(member);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpServletRequest request = getRequest();
		request.getSession(true).invalidate();
		return utilJson(auto.getService(), 0, jsondata.toString(), errorMsg.toString());
	}

	public String quickNote(AutoAppDO auto, HttpServletResponse response) {
		MemberDO member = new MemberDO();
		StringBuilder jsondata = new StringBuilder();
		StringBuilder errorMsg = new StringBuilder();
		member.setPhoneNum(auto.getPhoneNum());
		try {
			member = memberDAO.queryMemberOne(member);
		} catch (Exception e) {
			log.debug(e);
			jsondata.append("{}");
			errorMsg.append("查询手机号失败");
			return utilJson(auto.getService(), GlobalParam.ParameterError, jsondata.toString(), errorMsg.toString());
		}
		if (member == null) {
			jsondata.append("{}");
			errorMsg.append("您的手机号不存在");
			return utilJson(auto.getService(), GlobalParam.paramError, jsondata.toString(), errorMsg.toString());
		} else {
			String nonce_str = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));

			try {
				icaChe.updateNoteRedis(GlobalParam.NodeLoginCode, auto.getPhoneNum(), nonce_str);
			} catch (Exception e) {
				e.printStackTrace();
				jsondata.append("{}");
				errorMsg.append("连接redis失败");
				return utilJson(auto.getService(), GlobalParam.ParameterError, jsondata.toString(),
						errorMsg.toString());
			}

			AlidayuNote.sendNoteCode(auto.getPhoneNum(), nonce_str, GlobalParam.loginNote);
		}

		jsondata.append("{}");
		return utilJson(auto.getService(), 0, jsondata.toString(), errorMsg.toString());
	}

	public String quickLogin(AutoAppDO auto, HttpServletResponse response) {
		StringBuilder jsondata = new StringBuilder();
		StringBuilder errorMsg = new StringBuilder();

		MemberDO member = new MemberDO();
		member.setPhoneNum(auto.getPhoneNum());
		try {
			String noteCode = icaChe.getNoteCode(GlobalParam.NodeLoginCode, auto.getPhoneNum());
			if ("".equals(noteCode) || noteCode == null) {
				errorMsg.append("您的验证码，已经过有效期");
				return utilJson(auto.getService(), GlobalParam.FailureNoteCode, jsondata.toString(),
						errorMsg.toString());
			}
			if (!noteCode.equals(auto.getVerCode())) {
				errorMsg.append("您的验证码错误");
				return utilJson(auto.getService(), GlobalParam.NoteCodeErroe, jsondata.toString(), errorMsg.toString());
			}
			member = memberDAO.queryMemberOne(member);
			if (member == null) {
				jsondata.append("{}");
				errorMsg.append("账户不存在");
				return utilJson(auto.getService(), GlobalParam.paramError, jsondata.toString(), errorMsg.toString());
			}
			String devinfo = auto.getDevinfo();
			JSONObject json = JSONObject.fromObject(devinfo);
			LoginLogDO loginLog = new LoginLogDO();

			loginLog.setUserId(member.getId());
			loginLog.setPhoneNum(auto.getPhoneNum());
			loginLog.setAddress(json.getString("address"));
			loginLog.setLoginTime(json.getString("loginTime"));
			loginLog.setPhoneType(json.getString("phoneType"));
			loginLogDAO.addLoginLog(loginLog);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		HttpServletRequest request = getRequest();
		request.getSession(true).setAttribute("member", member);
		addUniqCookie(member, response);

		jsondata.append("{}");
		return utilJson(auto.getService(), 0, jsondata.toString(), errorMsg.toString());
	}

	public String logout(AutoAppDO auto) {
		StringBuilder jsondata = new StringBuilder();
		StringBuilder errorMsg = new StringBuilder();

		MemberDO member = new MemberDO();
		member.setPhoneNum(auto.getPhoneNum());

		getRequest().getSession().invalidate();
		jsondata.append("{}");
		return utilJson(auto.getService(), 0, jsondata.toString(), errorMsg.toString());
	}

	public String changePhoneNum(AutoAppDO auto) {
		StringBuilder jsondata = new StringBuilder();
		StringBuilder errorMsg = new StringBuilder();

		if (!checkSession(auto)) {
			jsondata.append("{}");
			errorMsg.append("您还没有登录，请登录后再尝试请求！");
			return utilJson(auto.getService(), GlobalParam.NOT_LOGIN, jsondata.toString(), errorMsg.toString());
		}

		MemberDO member = (MemberDO) getRequest().getSession().getAttribute("member");
		member.setPhoneNum(auto.getPhoneNum());

		String userinfo = auto.getUserInfo();
		jsondata.append("{}");
		if (userinfo == null) {
			errorMsg.append("userinfo为空！");
			return utilJson(auto.getService(), GlobalParam.UserExist, jsondata.toString(), errorMsg.toString());
		}
		JSONObject json = JSONObject.fromObject(userinfo);
		try {
			member = memberDAO.queryMemberOne(member);
			member.setPhoneNum(json.getString("newPhoneNum"));
			memberDAO.updatePhoneNum(member);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return utilJson(auto.getService(), 0, jsondata.toString(), errorMsg.toString());
	}

	public String saveGesture(Map<String, String> map) {
		StringBuilder jsondata = new StringBuilder();
		StringBuilder errorMsg = new StringBuilder();
		jsondata.append("{}");
		int status = 0;
		MemberDO member = (MemberDO) getRequest().getSession().getAttribute("member");

		if (member == null) {
			errorMsg.append("您还没有登录，请登录后再尝试请求！");
			return utilJson(map.get("service"), GlobalParam.NOT_LOGIN, jsondata.toString(), errorMsg.toString());
		}

		if (!map.containsKey("gestureCode") || !map.containsKey("gestureStatus")) {
			errorMsg.append("手势码或手势码状态不能为空！");
			status = GlobalParam.paramError;
		} else {
			member.setGestureCode(map.get("gestureCode"));
			String gestureStatus = map.get("gestureStatus");

			member.setGestureStatus(gestureStatus != null && gestureStatus.equalsIgnoreCase("true"));
			try {
				memberDAO.updateMember(member);
			} catch (Exception e) {
				e.printStackTrace();
				status = GlobalParam.ParameterError;
				errorMsg.append("系统错误");
			}
		}
		return utilJson(map.get("service"), status, jsondata.toString(), errorMsg.toString());
	}

	public String getGesture(Map<String, String> map) {
		StringBuilder jsondata = new StringBuilder();
		StringBuilder errorMsg = new StringBuilder();
		MemberDO member = (MemberDO) getRequest().getSession().getAttribute("member");
		if (member == null) {
			jsondata.append("{}");
			errorMsg.append("您还没有登录，请登录后再尝试请求！");
			return utilJson(map.get("service"), GlobalParam.NOT_LOGIN, jsondata.toString(), errorMsg.toString());
		}
		errorMsg.append("{}");
		String gestureCode = member.getGestureCode();
		boolean gestureStatus = member.getGestureStatus();
		gestureCode = gestureCode == null ? "" : gestureCode;
		String data = "{\"gestureCode\":\"" + gestureCode + "\",\"gestureStatus\":\"" + gestureStatus + "\"}";
		jsondata.append(data);
		return utilJson(map.get("service"), 0, jsondata.toString(), errorMsg.toString());
	}

	/**
	 * 返回json
	 * 
	 * @param type
	 *            业务类型
	 * @param status
	 *            结果
	 * @param data
	 *            返回信息
	 * @param errorMsg
	 *            错误信息
	 * @param printWriter
	 */
	public String utilJson(String type, int status, String data, String errorMsg) {
		StringBuilder jsondata = new StringBuilder();
		jsondata.append("{\"service\":\"" + type + "\",\"status\":" + status + ",");
		jsondata.append("\"data\":" + data + "");
		jsondata.append(",\"errorMsg\":\"" + errorMsg + "\"}");
		return jsondata.toString();
	}

	public String utilJson(String type, int status, String data, String errorMsg, String addressCount) {
		StringBuilder jsondata = new StringBuilder();
		jsondata.append("{\"service\":\"" + type + "\",\"status\":" + status + ",");
		jsondata.append("\"data\":" + data + "");
		jsondata.append(",\"errorMsg\":\"" + errorMsg + "\"");
		jsondata.append(",\"addressCount\":" + addressCount + "}");
		return jsondata.toString();
	}

	/**
	 * 检查用户的Session，如果Session不匹配，说明这是一个伪造的用户。
	 * 
	 * @param auto
	 * @param request
	 * @param response
	 * @return
	 */
	public HttpServletRequest getRequest() {
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	}

	public void addUniqCookie(MemberDO member, HttpServletResponse response) {

		Cookie cookie = (Cookie) getRequest().getAttribute("cookie");
		@SuppressWarnings("unchecked")
		Map<Integer, String> sessions = (Map<Integer, String>) getRequest().getServletContext()
				.getAttribute(SingleSiteLoginFilter.SINGLE_SITE_LOGIN);
	
		sessions.put(member.getId(), cookie.getValue());
		response.addCookie(cookie);
	}

	public boolean checkSession(AutoAppDO auto) {
		HttpServletRequest request = getRequest();
		HttpSession session = request.getSession();
		MemberDO member = (MemberDO) session.getAttribute("member");
		if (member == null) {
			return false;
		} else
			return true;

	}
}

package com.people.sotp.interfaces.service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.people.sotp.commons.base.BaseController;
import com.people.sotp.commons.base.GlobalParam;
import com.people.sotp.dataobject.AutoAppDO;
import com.people.sotp.dataobject.LoginLogDO;
import com.people.sotp.dataobject.MemberDO;
import com.people.sotp.dataobject.UserFeedbackDO;
import com.people.sotp.payment.dao.LoginLogDAO;
import com.people.sotp.payment.dao.MemberDAO;
import com.people.sotp.payment.dao.UserFeedbackDAO;

import net.sf.json.JSONObject;

@Service
public class BusinessService extends BaseController {

	@Resource
	private MemberDAO memberDAO;
	@Resource
	private LoginLogDAO loginLogDAO;
	@Resource
	private UserFeedbackDAO userFeedbackDAO;

	private static Log log = LogFactory.getLog(BusinessService.class);

	public String registerNote(AutoAppDO auto) {
		MemberDO member = new MemberDO();
		StringBuffer jsondata = new StringBuffer();
		StringBuffer errorMsg = new StringBuffer();
		member.setPhoneNum(auto.getPhoneNum());
		try {
			member = memberDAO.queryMemberOne(member);
			if (member != null) {
				jsondata.append("{}");
				errorMsg.append("账户已存在");
				return utilJson(auto.getService(), GlobalParam.UserExusts, jsondata.toString(), errorMsg.toString());
			}

		} catch (Exception e) {
			log.debug(e);
			jsondata.append("{}");
			errorMsg.append("查询手机号失败");
			return utilJson(auto.getService(), GlobalParam.ParameterError, jsondata.toString(), errorMsg.toString());
		}
		jsondata.append("{}");
		return utilJson(auto.getService(), 0, jsondata.toString(), errorMsg.toString());
	}

	public String registerUser(AutoAppDO auto) {
		StringBuffer jsondata = new StringBuffer();
		StringBuffer errorMsg = new StringBuffer();

		MemberDO member = new MemberDO();
		member.setPhoneNum(auto.getPhoneNum());
		member.setPassword(auto.getPassword());
		try {
			member = memberDAO.queryMemberOne(member);
			if (member != null) {
				jsondata.append("{}");
				errorMsg.append("账户已存在");
				return utilJson(auto.getService(), GlobalParam.UserExist, jsondata.toString(), errorMsg.toString());
			}
			MemberDO user = new MemberDO();
			user.setPhoneNum(auto.getPhoneNum());
			user.setPassword(auto.getPassword());
			memberDAO.addMember(user);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		jsondata.append("{}");
		return utilJson(auto.getService(), 0, jsondata.toString(), errorMsg.toString());
	}

	public String login(AutoAppDO auto) {
		StringBuffer jsondata = new StringBuffer();
		StringBuffer errorMsg = new StringBuffer();
		MemberDO member = new MemberDO();
		if ("".equals(auto.getPhoneNum()) || "".equals(auto.getPassword())) {
			errorMsg.append("手机号或者密码是空！");
			return utilJson(auto.getService(), GlobalParam.paramError, jsondata.toString(), errorMsg.toString());
		}
		member.setPhoneNum(auto.getPhoneNum());
		member.setPassword(auto.getPassword());
		try {
			member = memberDAO.queryMemberOne(member);
			if (member == null) {
				jsondata.append("{}");
				errorMsg.append("账户密码错误！");
				return utilJson(auto.getService(), GlobalParam.UserExist, jsondata.toString(), errorMsg.toString());

			}
			jsondata.append(" " + writeJson(member) + " ");
			int num = jsondata.toString().indexOf("{");
			if (num < 0) {
				jsondata.append("{}");
			}
			String devinfo = auto.getDevinfo();
			JSONObject json = JSONObject.fromObject(devinfo);
			LoginLogDO loginLog = new LoginLogDO();
			loginLog.setPhoneNum(auto.getPhoneNum());
			loginLog.setAddress(json.getString("address"));
			loginLog.setLoginTime(json.getString("loginTime"));
			loginLog.setPhoneType(json.getString("phoneType"));
			loginLogDAO.addLoginLog(loginLog);
		} catch (Exception e) {
			log.debug(e);
			e.printStackTrace();
		}
		return utilJson(auto.getService(), 0, jsondata.toString(), errorMsg.toString());
	}

	public String setUserInfo(AutoAppDO auto) {
		StringBuffer jsondata = new StringBuffer();
		StringBuffer errorMsg = new StringBuffer();
		MemberDO member = new MemberDO();
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
			String str = json.getString("picPath");
			System.out.println(str.length());
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
			member.setPassword(json.getString("password"));
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
		StringBuffer jsondata = new StringBuffer();
		StringBuffer errorMsg = new StringBuffer();
		MemberDO member = new MemberDO();
		member.setPhoneNum(auto.getPhoneNum());

		try {
			member = memberDAO.queryMemberOne(member);

			jsondata.append(" " + writeJson(member) + " ");
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
		StringBuffer jsondata = new StringBuffer();
		StringBuffer errorMsg = new StringBuffer();
		MemberDO member = new MemberDO();
		String userInfo = auto.getUserInfo();
		jsondata.append("{}");
		if (userInfo == null) {
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

		return utilJson(auto.getService(), 0, jsondata.toString(), errorMsg.toString());
	}

	public String getAuthentication(AutoAppDO auto) {
		StringBuffer jsondata = new StringBuffer();
		StringBuffer errorMsg = new StringBuffer();
		MemberDO member = new MemberDO();
		try {
			member.setPhoneNum(auto.getPhoneNum());
			member = memberDAO.queryMemberIdentityOne(member);

			jsondata.append(" " + writeJson(member) + " ");
			int num = jsondata.toString().indexOf("{");
			if (num < 0) {
				jsondata.append("{}");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return utilJson(auto.getService(), 0, jsondata.toString(), errorMsg.toString());
	}

	public String loginLog(AutoAppDO auto) {
		StringBuffer jsondata = new StringBuffer();
		StringBuffer errorMsg = new StringBuffer();

		LoginLogDO loginlog = new LoginLogDO();
		loginlog.setPhoneNum(auto.getPhoneNum());
		try {
			List<LoginLogDO> list = loginLogDAO.queryLoginLogList(loginlog);
			jsondata.append(" " + writeJson(list) + " ");
			int num = jsondata.toString().indexOf("{");
			if (num < 0) {
				jsondata.append("{}");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			log.debug(e);
		}
		return utilJson(auto.getService(), 0, jsondata.toString(), errorMsg.toString());
	}

	public String userFeedback(AutoAppDO auto) {

		StringBuffer jsondata = new StringBuffer();
		StringBuffer errorMsg = new StringBuffer();
		jsondata.append("{}");

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

	public String upPportrait(AutoAppDO auto, HttpServletRequest request) {
		StringBuffer jsondata = new StringBuffer();
		StringBuffer errorMsg = new StringBuffer();
		MemberDO member = new MemberDO();
		member.setPhoneNum(auto.getPhoneNum());
		try {
			member = memberDAO.queryMemberOne(member);
			if (member == null) {
				jsondata.append("{}");
				errorMsg.append("用户不存在");
				return utilJson(auto.getService(), 0, jsondata.toString(), errorMsg.toString());
			}
			
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
			Iterator iter = multiRequest.getFileNames();
			while (iter.hasNext()) {
				MultipartFile file = multiRequest.getFile((String) iter.next());
				if (file != null) {
					String fileName = file.getOriginalFilename();
					String picPath ="/download/" + member.getId()+"/"+ fileName;
					String path = getWebRootPath()+picPath;
					File localFile = new File(path);
					  if(!localFile.exists()){  
						  localFile.mkdirs();  
				        }  
					// 写文件到本地 
					file.transferTo(localFile);
					
					member.setPicPath(picPath);
					memberDAO.updateMember(member);
				}
			} 
		} catch (Exception e) {
			System.out.println(e);
		}
		return utilJson(auto.getService(), 0, jsondata.toString(), errorMsg.toString());

	}

	public String oldPassword(AutoAppDO auto) {
		StringBuffer jsondata = new StringBuffer();
		StringBuffer errorMsg = new StringBuffer();
		jsondata.append("{}");
		MemberDO member = new MemberDO();
		member.setPhoneNum(auto.getPhoneNum());
		member.setPassword(auto.getOldPasswrd());
		try {
			member = memberDAO.queryMemberOne(member);
			if (member == null) {
				jsondata.append("{}");
				errorMsg.append("密码错误");
				return utilJson(auto.getService(), GlobalParam.UserExist, jsondata.toString(), errorMsg.toString());
			}
			}catch (Exception e) {
			}
		return utilJson(auto.getService(), 0, jsondata.toString(), errorMsg.toString());
	}
	public String newPassword(AutoAppDO auto){
		StringBuffer jsondata = new StringBuffer();
		StringBuffer errorMsg = new StringBuffer();
		jsondata.append("{}");
		MemberDO member = new MemberDO();
		member.setPhoneNum(auto.getPhoneNum());
		member.setPassword(auto.getNewPasswrd());
		
		try {
			memberDAO.updateMember(member);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return utilJson(auto.getService(), 0, jsondata.toString(), errorMsg.toString());
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
		StringBuffer jsondata = new StringBuffer();
		jsondata.append("{\"service\":\"" + type + "\",\"status\":" + status + ",");
		jsondata.append("\"data\":" + data + "");
		jsondata.append(",\"errorMsg\":\"" + errorMsg + "\"}");
		return jsondata.toString();
	}

}

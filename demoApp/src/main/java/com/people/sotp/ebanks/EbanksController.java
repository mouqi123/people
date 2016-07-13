package com.people.sotp.ebanks;



import static com.people.sotp.page.SimplePage.cpn;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.people.sotp.commons.base.BaseController;
import com.people.sotp.commons.base.GlobalParam;
import com.people.sotp.commons.util.Base64;
import com.people.sotp.commons.util.sendRequest;
import com.people.sotp.commons.util.sotpAuthUtil;
import com.people.sotp.dataobject.ActivationDO;
import com.people.sotp.dataobject.ApplyDO;
import com.people.sotp.dataobject.MasterDO;
import com.people.sotp.dataobject.SotpAuthDO;
import com.people.sotp.dataobject.UserDO;
import com.people.sotp.demoApp.dao.DemoAppDAO;
import com.people.sotp.page.CookieUtils;
import com.people.sotp.page.Pagination;
import com.people.sotp.payment.dao.ApplyDAO;
import com.people.sotp.payment.dao.SotpAuthDAO;
import com.people.sotp.payment.service.LoginService;
import com.people.sotp.qrcode.TwoDimensionCode;

import net.sf.json.JSONObject;



@Controller
@RequestMapping("/ebanks")
public class EbanksController extends BaseController {
	/*
	 * private static Logger logger =
	 * LogManager.getLogger(LoginController.class.getName());
	 */
	private static Log logger = LogFactory.getLog(EbanksController.class);
	@Resource
	private LoginService loginService;
	
	@Resource
	private SotpAuthDAO sotpDao;
	
	@Resource
	private ApplyDAO applyDao;
	@Resource
	private DemoAppDAO demoAppDAo;
	
	
	@RequestMapping(value = "/index.do", method = RequestMethod.GET)
	public String index(HttpServletRequest request, HttpServletResponse response) {
		UserDO member = (UserDO) request.getSession().getAttribute("ebanks");
		if(member==null){
			return "ebanks/login";
		}
		
		return "ebanks/index";
	}
	
	@RequestMapping(value = "/login.do", method = RequestMethod.GET)
	public String login(HttpServletRequest request, HttpServletResponse response) {
		return "ebanks/login";
	}
	@RequestMapping(value = "/account.do", method = RequestMethod.GET)
	public String account(HttpServletRequest request, HttpServletResponse response) {
		UserDO member = (UserDO) request.getSession().getAttribute("ebanks");
		if(member==null){
			return "ebanks/login";
		}
		request.setAttribute("user", member);
		return "ebanks/account";
	}
	
	
	@RequestMapping(value = "/transfer.do", method = RequestMethod.GET)
	public String transfer(HttpServletRequest request, HttpServletResponse response) {
		UserDO member = (UserDO) request.getSession().getAttribute("ebanks");
		if(member==null){
			return "ebanks/login";
		}
		return "ebanks/transfer";
	}

	@RequestMapping(value = "/scanPaycode.do", method = RequestMethod.GET)
	public String scanPaycode(HttpServletRequest request, HttpServletResponse response) {
		UserDO member = (UserDO) request.getSession().getAttribute("ebanks");
		if(member==null){
			return "ebanks/login";
		}
		return "ebanks/scanPaycode";
	}



	/**
	 * 获取二维码
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getQRCode.do", method = RequestMethod.GET)
	public void getQRCode(HttpServletRequest request, HttpServletResponse response) {
		TwoDimensionCode handler = new TwoDimensionCode();
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");
		// 将图像输出到Servlet输出流中。
		UserDO master =(UserDO) request.getSession().getAttribute("ebanks");
		Map<String, Object>map = new HashMap<String,Object>();
		map.put("phoneNum", master.getPhoneNum());
		try {
			ServletOutputStream sos = response.getOutputStream();
			ImageIO.write(handler.qRCodeCommon("http://192.168.1.221:8280/payment/ebanks/payEBanks.do", "png", 8), "jpeg", sos);
			sos.close();
		} catch (IOException e) {
			logger.error("验证码图片产生出现错误：" + e.toString());
		}

	}
	
	
	
	/**
	 * 用户登录
	 * 
	 * @return
	 */
	@RequestMapping(value = "/userLogin.do", method = RequestMethod.POST)
	public ModelAndView userLogin(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		ModelAndView mv = new ModelAndView();

		String loginName =request.getParameter("loginName");
		String password = request.getParameter("password");
		
		if (loginName == null || "".equals(loginName)) {
			mv.addObject("message", "用户名不能为空！");
			mv.setViewName("ebanks/login");
			return mv;
		}
		if (password == null || "".equals(password)) {
			mv.addObject("message", "用户密码不能为空！");
			mv.setViewName("ebanks/login");
			return mv;
		}
		
		UserDO member = new UserDO();
		member.setPhoneNum(loginName);
		member.setPwd(password);
		try {
			member =demoAppDAo.queryUserOne(member);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(member==null){
			mv.addObject("message", "用户名密码错误！");
			mv.setViewName("ebanks/login");
			return mv;
		}
		request.getSession().setAttribute("ebanks", member);
		mv.setViewName("redirect:/ebanks/index.do");
		return mv;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	//获取设备列表
	@RequestMapping(value = "/actPlugin.do", method = RequestMethod.GET)
	public ModelAndView actPlugin(@ModelAttribute ActivationDO model,HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		String error;
		int page = (int) model.getPage();
		StringBuffer Devlist = new StringBuffer();
		
		SotpAuthDO service = new SotpAuthDO();
		
		
		String sotpInfo=encapsulationReq("zrauth.plugin.listallweb","as","qwe");
		
		Map<String, Object> sotpMap = new HashMap<String, Object>();
		sotpMap = sotpAuthUtil.getUrlParams(sotpInfo);
		
		String appKey = getAppKey(String.valueOf(sotpMap.get("appId")));
		
		if (appKey == null) {
			error="appId不存在";
		}
		try {
			service = sotpDao.querySotpAuthOne();
			Devlist.append("http://" + service.getIp() + ":" + service.getPost() + "");
			Devlist.append("/" + GlobalParam.project + "");
			String sotpAuth=sotpAuthUtil.genRequestSotpAuthMsg(sotpMap, sotpInfo, appKey);
			String info = sendRequest.sendPost(Devlist.toString(),sotpAuth );
			JSONObject jsonsotp = JSONObject.fromObject(info);
			
			appKey = getAppKey(jsonsotp.getString("appId"));
			if (appKey == null) {
				error="appId不存在";
			}
			boolean falg = sotpAuthUtil.verifySotp(jsonsotp,jsonsotp.getString("appId"),appKey);
			if (!falg) {
				error="sign not match";
			}
			if (jsonsotp.getInt("status") == 0) {
				JSONObject sotpJson = (JSONObject) jsonsotp.get("message");
				String devlist =sotpJson.getString("data");
				List list =getDevlist(devlist);
				Pagination pagination = new Pagination(cpn(page), CookieUtils.getPageSize(request), list.size(),
						list);
				mv.addObject("pagination", pagination);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		mv.setViewName("ebanks/actPlugin");
		return mv;
		
	}
	
	
	@RequestMapping("/back_activation.do")
	public void backActivation(HttpServletRequest request,
			HttpServletResponse response) {
		String error ="";
		
		String sotpCode = request.getParameter("sotpCode");
		String sotpId = request.getParameter("sotpId");
		StringBuffer Devlist = new StringBuffer();
		
		
		SotpAuthDO service = new SotpAuthDO();
		
		
		String sotpInfo=encapsulationReq("zrauth.plugin.activeweb",sotpCode,sotpId);
		
		Map<String, Object> sotpMap = new HashMap<String, Object>();
		sotpMap = sotpAuthUtil.getUrlParams(sotpInfo);
		
		String appKey = getAppKey(String.valueOf(sotpMap.get("appId")));
		
		if (appKey == null) {
			error="appId不存在";
		}
		
		
		
		
		try {
			service = sotpDao.querySotpAuthOne();
			Devlist.append("http://" + service.getIp() + ":" + service.getPost() + "");
			Devlist.append("/" + GlobalParam.project + "");
			String sotpAuth=sotpAuthUtil.genRequestSotpAuthMsg(sotpMap, sotpInfo, appKey);
			String info = sendRequest.sendPost(Devlist.toString(),sotpAuth );
			JSONObject jsonsotp = JSONObject.fromObject(info);
			
			appKey = getAppKey(jsonsotp.getString("appId"));
			if (appKey == null) {
				error="appId不存在";
			}
			boolean falg = sotpAuthUtil.verifySotp(jsonsotp,jsonsotp.getString("appId"),appKey);
			if (!falg) {
				error="sign not match";
			}
			if (jsonsotp.getInt("status") == 0) {
				
				error ="激活成功";
				
				
			}else{
				error ="激活失败";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	
	
	
	public String encapsulationReq(String str,String sotpCode,String sotpId){
		JSONObject upv = new JSONObject();
		upv.put("major", 3);
		upv.put("minor", 0);
		JSONObject header = new JSONObject();
		header.put("upv", upv);
		header.put("op", str);
		header.put("appId", "4f94cc2901cefd1e0c9d4f58abfba22e6c6dcaad");
		
		JSONObject userInfo = new JSONObject();
		userInfo.put("phoneNum", "13051126671");
		
		StringBuffer sotpInfo = new StringBuffer();
		
		sotpInfo.append("appId=");
		sotpInfo.append("4f94cc2901cefd1e0c9d4f58abfba22e6c6dcaad&");
		sotpInfo.append("header=");
		sotpInfo.append(""+Base64.encode(header.toString())+"&");
		sotpInfo.append("userInfo=");
		sotpInfo.append(""+Base64.encode(userInfo.toString())+"&");
		sotpInfo.append("sotpCode=");
		sotpInfo.append(""+sotpCode+"&");
		sotpInfo.append("activePluginId=");
		sotpInfo.append(""+sotpId+"");
		
		return sotpInfo.toString();
	}
	
	/**
	 * 通过appid获取appkey
	 * 
	 * @param appId
	 * @return
	 */
	public String getAppKey(String appId) {
		ApplyDO apply = new ApplyDO();
		apply.setAppId(appId);
		try {
			apply = applyDao.queryApplyOne(apply);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (apply != null ) {
			return apply.getAppKey();
		} else {
			return null;
		}
	}
	public List getDevlist(String info){
		List list = new ArrayList<>();
		String [] dev = info.split("&");
		for (int i = 0; i < dev.length; i++) {
			String reg = dev[i];
			String [] ret =reg.split("@");
				ActivationDO act = new ActivationDO();
				act.setModelName(ret[0]);
				act.setModelType(ret[1]);
				act.setTime(ret[2]);
				act.setSotpId(ret[3]);
				act.setStatus(ret[4]);
				list.add(act);
		}
		return list;
	}
	

}

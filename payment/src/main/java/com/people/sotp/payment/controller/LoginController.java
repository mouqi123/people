package com.people.sotp.payment.controller;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.people.sotp.auditlog.service.AuditLogService;
import com.people.sotp.commons.base.BaseController;
import com.people.sotp.commons.base.GlobalParam;
import com.people.sotp.commons.base.ResultDO;
import com.people.sotp.commons.log.LogConfig;
import com.people.sotp.commons.util.RandomNumUtil;
import com.people.sotp.dataobject.AuditLogDO;
import com.people.sotp.dataobject.LdfDO;
import com.people.sotp.dataobject.MasterDO;
import com.people.sotp.freemarker.MessageResolver;
import com.people.sotp.payment.service.LoginService;

@Controller
@RequestMapping("/login")
public class LoginController extends BaseController {
	/*
	 * private static Logger logger =
	 * LogManager.getLogger(LoginController.class.getName());
	 */
	private static Log logger = LogFactory.getLog(LoginController.class);
	@Resource
	private LoginService loginService;
	@Autowired
	private AuditLogService auditLogService;

	@RequestMapping(value = "/login.do", method = RequestMethod.GET)
	public String submit(HttpServletRequest request, HttpServletResponse response) {

		return "login";

	}

	@RequestMapping(value = "/test.do", method = RequestMethod.GET)
	public ModelAndView test(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		AuditLogDO log = new AuditLogDO();
			ResultDO resultDO= auditLogService.queryAuditLogList(log);
			if (resultDO.isFailure()){
				mv.addObject("erroe", resultDO.getErrMsg());
			}
		mv.setViewName("test");
		return mv;

	}
	/**
	 * 获取验证码
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getRandomNum.do", method = RequestMethod.GET)
	public void getRandomNum(HttpServletRequest request, HttpServletResponse response) {
		RandomNumUtil rdnu = RandomNumUtil.Instance();
		String str = rdnu.getString().toString();
		request.getSession().setAttribute("random", str.trim());// 取得随机字符串放入HttpSession
		// ActionContext.getContext().getSession().put("random",str.trim());//取得随机字符串放入HttpSession
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		response.setContentType("image/jpeg");
		// 将图像输出到Servlet输出流中。
		try {
			ServletOutputStream sos = response.getOutputStream();
			ImageIO.write(rdnu.getImage(), "jpeg", sos);
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
		String username = request.getParameter("loginname");
		String password = request.getParameter("pwd");
		String random = request.getParameter("random");

		logger.info("登录人：" + username);
		ModelAndView mv = new ModelAndView();

		if (username == null || "".equals(username)) {
			mv.addObject("message", "用户名不能为空！");
			mv.setViewName("login");
			return mv;
		}
		if (password == null || "".equals(password)) {
			mv.addObject("message", "用户密码不能为空！");
			mv.setViewName("login");
			return mv;
		}
		if (random == null || "".equals(random)) {
			mv.addObject("message", "验证码不能为空！");
			mv.setViewName("login");
			return mv;
		}
		String session_random = (String) request.getSession().getAttribute("random");
		if (!random.equalsIgnoreCase(session_random)) {
			mv.addObject("message", "验证码错误！");
			mv.setViewName("login");
			return mv;
		}

		MasterDO manager = new MasterDO();
		manager.setUserName(username);
		manager.setPassword(password);
		// 验证帐号登录逻辑
		MasterDO masterDO = new MasterDO();
		masterDO.setUserName(username);
		masterDO.setPassword(password);
		ResultDO resultDO = loginService.userLogin(masterDO, request.getContextPath());
		if (resultDO.isFailure()) {
			auditLogService.insertAuditLog(getRequest(), LogConfig.SYS_LOGIN, 0, LogConfig.OPT_AUTHENTICATION,
					LogConfig.HIGH_LEVEL, 2, "账号[" + username + "]登录失败,失败原因:{" + resultDO.getErrMsg() + "}");
				
			mv.addObject("message", resultDO.getErrMsg());
			mv.setViewName("login");
			return mv;
		}
		MasterDO master = (MasterDO) resultDO.getModel("master");
		auditLogService.insertAuditLog(getRequest(), LogConfig.SYS_LOGIN, master.getId(), LogConfig.OPT_AUTHENTICATION,
				LogConfig.MIDDLE_LEVEL, 1, "账号[" + username + "]登录成功");

		MessageResolver.getMessage(request, "global.admin.welcome");

		request.getSession().setAttribute("user", master);
		String systemMenu = (String) resultDO.getModel("systemMenu");
		request.getSession().setAttribute(GlobalParam.SESSION_SYSTEMMUNU, systemMenu);
		mv.setViewName("redirect:/frame/index.do");
		return mv;
	}

	/**
	 * 获取客户端IP
	 * 
	 * @return
	 */
	public String getClientIP(HttpServletRequest request) {
		String address = request.getHeader("X-Forwarded-For");
		if (address == null) {
			address = request.getRemoteAddr();
		}
		return address;
	}

	/**
	 * 退出登录
	 * 
	 * @return
	 */
	@RequestMapping(value = "/logout.do", method = RequestMethod.GET)
	public ModelAndView logout(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		ModelAndView mv = new ModelAndView();
		MasterDO user = (MasterDO) session.getAttribute(GlobalParam.SESSION_USER);
		if (user != null) {
			session.invalidate();
		}
		mv.setViewName("redirect:/login/login.do");
		return mv;
	}

	/**
	 * 获取用户手册
	 * 
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/getUserManual.htm", method = RequestMethod.GET)
	public ResponseEntity<byte[]> getUserManual(HttpServletRequest request, HttpServletResponse response) {
		String path = getWebRootPath() + System.getProperty("file.separator") + "管理平台用户使用手册V1.0.doc";
		// String path =
		// "E:/sotp/apache-tomcat-7.0.57/webapps/sotpUimc/WEB-INF/classes/管理平台用户使用手册V1.0.doc";
		File file = new File(path);
		HttpHeaders headers = new HttpHeaders();
		byte[] body = null;
		String fileName;
		try {
			if (file.exists()) {
				InputStream is = new FileInputStream(file);
				body = new byte[is.available()];
				is.read(body);
				is.close();
			}
			fileName = new String("管理平台用户使用手册V1.0.doc".getBytes("UTF-8"), "iso-8859-1");// 为了解决中文名称乱码问题
			headers.setContentDispositionFormData("attachment", fileName);
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ResponseEntity<byte[]>(body, headers, HttpStatus.CREATED);
	}

	@RequestMapping("/ldf.do")
	public ModelAndView ldf(ModelMap model) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("ldf");
		return mv;
	}
	
	
	@RequestMapping("/v_ldf.do")
	public ModelAndView ldf(@ModelAttribute LdfDO ldf, HttpServletRequest request,HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		String str[] = new String[10];
		str[0]=ldf.getAa();
		str[1]=ldf.getBb();
		str[2]=ldf.getCc();
		str[3]=ldf.getDd();
		str[4]=ldf.getEe();
		str[5]=ldf.getFf();
		str[6]=ldf.getGg();
		str[7]=ldf.getHh();
		str[8]=ldf.getIi();
		str[9]=ldf.getJj();
	    try {
			FileWriter fileWriter=new FileWriter("c:\\ldf.txt", true);
			for (int i = 0; i < str.length; i++) {
				String urlone = str[i];
				int num =urlone.indexOf("?");
				String one =urlone.substring(0, num+1);
				fileWriter.write("\r\n");
				fileWriter.write("\r\n");
				fileWriter.write("\r\n");
				fileWriter.write(one);
				fileWriter.write("\r\n");
				String er =urlone.substring(num+1,urlone.length());
				Map<String,Object>map = getUrlParams(er);
				List<String> keys = new ArrayList<String>();
				keys.add("envInfo");
				keys.add("sotpCode");
				keys.add("phoneNum");
				keys.add("appId");
				keys.add("sotpCodePara");
				keys.add("service");
				keys.add("sotpId");
				keys.add("version");
				keys.add("nonce_str");
				keys.add("sign");
				for (int j = 0; j < keys.size(); j++) {
					String prestr = "";
					String key = keys.get(j);
					Object value = map.get(key);
						prestr = key + "=" + value;
						fileWriter.write(prestr);
						fileWriter.write("\r\n");
				}
			}
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		
		
		
		
		
		
		
		mv.setViewName("ldf");
		return mv;
	}
	
	

	public static Map<String, Object> getUrlParams(String param) {
		Map<String, Object> map = new HashMap<String, Object>();
		if ("".equals(param) || null == param) {
			return map;
		}
		String[] params = param.split("&");

		for (int i = 0; i < params.length; i++) {
			String[] p = null;
			if (params[i].indexOf("sotpCode") != -1) {
				p = params[i].split("=", 2);
			}else if(params[i].indexOf("devInfo") != -1){
				p = params[i].split("=", 2);
			}else {
				p = params[i].split("=");
			}
			if (p.length == 2) {
				map.put(p[0], p[1]);
			}
		}
		return map;
	}

}

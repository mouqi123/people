package com.people.sotp.Ebbanks;

import static com.people.sotp.page.SimplePage.cpn;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
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
import org.springframework.stereotype.Controller;
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
import com.people.sotp.dataobject.ActivationDO;
import com.people.sotp.dataobject.MasterDO;
import com.people.sotp.dataobject.OrderDO;
import com.people.sotp.dataobject.SotpAuthDO;
import com.people.sotp.freemarker.MessageResolver;
import com.people.sotp.page.CookieUtils;
import com.people.sotp.page.Pagination;
import com.people.sotp.payment.service.LoginService;
import com.people.sotp.payment.service.OrderService;
import com.people.sotp.qrcode.TwoDimensionCode;
import com.people.sotp.urlcontrollers.SotpDao;
import com.people.sotp.urlcontrollers.SotpService;
import com.people.sotp.urlcontrollers.sendRequest;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/ebanks")
public class EbanksLoginController extends BaseController {
	private static Log logger = LogFactory.getLog(EbanksLoginController.class);
	@Resource
	private LoginService loginService;
	@Resource
	private OrderService orderService;
	@Autowired
	private AuditLogService auditLogService;
	@Resource
	private SotpDao sotpDao;
	

	@RequestMapping(value = "/index.do", method = RequestMethod.GET)
	public String index(HttpServletRequest request, HttpServletResponse response) {
		return "ebanks/index";
	}

	@RequestMapping(value = "/top.do", method = RequestMethod.GET)
	public String top(HttpServletRequest request, HttpServletResponse response) {

		return "ebanks/top";
	}
	@RequestMapping(value = "/foot.do", method = RequestMethod.GET)
	public String foot(HttpServletRequest request, HttpServletResponse response) {

		return "ebanks/foot";
	}
	
	
	@RequestMapping(value = "/login.do", method = RequestMethod.POST)
	public String login(HttpServletRequest request, HttpServletResponse response) {
		String ebanks = request.getParameter("ebanks");
		if(ebanks!=null&&ebanks.equals("1")){
			request.getSession().setAttribute("ebanks", ebanks);
		}
		return "redirect:/frame/index.do";
	}
	@RequestMapping(value = "/payEBanks.do", method = RequestMethod.GET)
	public void payEBanks(HttpServletRequest request, HttpServletResponse response, PrintWriter printWriter) {
		String phoneNum= request.getParameter("phoneNum");
		String sotpId= request.getParameter("sotpId");
		String sotpCode= request.getParameter("sotpCode");
		String sotpCodePara= request.getParameter("sotpCodePara");
		String devInfo= request.getParameter("devInfo");
		StringBuffer sotpGet = new StringBuffer();
		StringBuffer data = new StringBuffer();
		SotpAuthDO service = new SotpAuthDO();
		String error="";
		try {
			service = sotpDao.selectService();
			sotpGet.append("http://" + service.getIp() + ":" + service.getPost() + "");
			sotpGet.append("/" + SotpService.project + "/verify?");
			sotpGet.append("type=1070&phoneNum=" + phoneNum + "&sotpId=" + sotpId
					+ "&sotpCode=" + sotpCode + "&sotpCodePara=" + sotpCodePara + "&devInfo="
					+ devInfo + "");
			String info = sendRequest.sendGet(sotpGet.toString());
			JSONObject jsonsotp = JSONObject.fromObject(info);
			JSONObject sotpJson = (JSONObject) jsonsotp.get("message");
			if (jsonsotp.getInt("status") == 0) {
				data.append("\"\"");
			}else{
				data.append("\"\"");
				 error =sotpJson.getString("errorMsg");
			}
			
			StringBuffer jsondata = new StringBuffer();
			Date now = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String serverTime = dateFormat.format(now);
			jsondata.append("{\"type\":" + 1070 + ",\"status\":" + jsonsotp.getInt("status") + ",\"message\":{");
			jsondata.append("\"data\":" + data + "");
			jsondata.append(",\"errorMsg\":\"" + error + "\"},");
			jsondata.append("\"serverTime\":\"" + serverTime + "\"}");
			
			printWriter.write(jsondata.toString());
			printWriter.flush();
			printWriter.close();
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	@RequestMapping(value = "/v_list.do", method = RequestMethod.GET)
	public String list(HttpServletRequest request, HttpServletResponse response) {
		return "ebanks/list";
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
		String ebanks = request.getParameter("ebanks");
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
			auditLogService.insertAuditLog(getRequest(), "商城板块", 0, LogConfig.OPT_AUTHENTICATION,
					LogConfig.HIGH_LEVEL, 2, "账号[" + username + "]登录失败,失败原因:{" + resultDO.getErrMsg() + "}");
				
			mv.addObject("message", resultDO.getErrMsg());
			mv.setViewName("login");
			return mv;
		}
		MasterDO master = (MasterDO) resultDO.getModel("master");
		auditLogService.insertAuditLog(getRequest(), LogConfig.SYS_LOGIN, master.getId(), LogConfig.OPT_AUTHENTICATION,
				LogConfig.MIDDLE_LEVEL, 1, "账号[" + username + "]登录成功");

		MessageResolver.getMessage(request, "global.admin.welcome");

		request.getSession().setAttribute(GlobalParam.SESSION_USER, master);
		String systemMenu = (String) resultDO.getModel("systemMenu");
		request.getSession().setAttribute(GlobalParam.SESSION_SYSTEMMUNU, systemMenu);
		if(ebanks!=null&&ebanks.equals("1")){
			mv.setViewName("redirect:/ebanks/index.do");
		}else{
			mv.setViewName("redirect:/frame/index.do");
		}
		
		return mv;
	}

	
	
	
	/**
	 * 获取客户端IP
	 * 
	 * @return
	 */
	public static boolean isWindow(){
		boolean falg = false;
		String osName = System.getProperty("os.name");
		if(osName.toLowerCase().indexOf("windows")>-1){
			falg= true;
		}
		return falg;
	}
	public static String getlocalIp(){
		String sIp ="";
		InetAddress ip=null;
		
		try {
			if (isWindow()) {
				ip = InetAddress.getLocalHost();
			}else{
				 boolean bFindIP = false;
				 Enumeration<NetworkInterface> netInterfaces = (Enumeration<NetworkInterface>) NetworkInterface.getNetworkInterfaces();
				 while (netInterfaces.hasMoreElements()) {
					 if(bFindIP){
						  break;
					 }
					 NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
					 Enumeration<InetAddress> ips = ni.getInetAddresses();
					 while (ips.hasMoreElements()) {
						 ip = (InetAddress) ips.nextElement();
						 if( ip.isSiteLocalAddress() && !ip.isLoopbackAddress()&& ip.getHostAddress().indexOf(":")==-1){
							 bFindIP = true;
							 break;  
						 }
					 }
				 }
			
			} 
		} catch (Exception e) {
		}
		 if(null != ip){
			  sIp = ip.getHostAddress();
		 }
		 return sIp;
	}
	/**
	 * 获取服务器端口
	 * 
	 * @return
	 */
	public int getServerPort(HttpServletRequest request) {
		int port = 0;
		port = request.getServerPort();
		return port;
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
		MasterDO master =(MasterDO) request.getSession().getAttribute("user");
		Map<String, Object>map = new HashMap<String,Object>();
		map.put("phoneNum", master.getMobile());
		OrderDO order = orderService.queryOrderOne(map);
		try {
			ServletOutputStream sos = response.getOutputStream();
			
			ImageIO.write(handler.qRCodeCommon("http://"+getlocalIp()+":"+getServerPort(request)+"/payment/ebanks/payEBanks.do", "png", 8), "jpeg", sos);
			sos.close();
		} catch (Exception e) {
			logger.error("验证码图片产生出现错误：" + e.toString());
		}

	}
	@RequestMapping(value = "/v_activationList.do", method = RequestMethod.GET)
	public String activation(HttpServletRequest request, HttpServletResponse response) {
		return "ebanks/activationList";
	}
	
	@RequestMapping(value = "/sotp_activation.do", method = RequestMethod.POST)
	public ModelAndView sotpActivation(@ModelAttribute ActivationDO model,HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		String phone = request.getParameter("phone");
		StringBuffer Devlist = new StringBuffer();
		SotpAuthDO service = new SotpAuthDO();
	
		int page = (int) model.getPageNo();
		try {
			service = sotpDao.selectService();
			Devlist.append("http://" + service.getIp() + ":" + service.getPost() + "");
			Devlist.append("/" + SotpService.project + "/getdevlist?");
			Devlist.append("type=1050&phoneNum=" + phone + "");
			String devinfo = sendRequest.sendGet(Devlist.toString());
			JSONObject jsondev = JSONObject.fromObject(devinfo);
			if (jsondev.getInt("status") == 0) {
				JSONObject sotpJson = (JSONObject) jsondev.get("message");
				String devlist =sotpJson.getString("data");
				List list =getDevlist(devlist);
				Pagination pagination = new Pagination(cpn(page), CookieUtils.getPageSize(request), list.size(),
						list);
				mv.addObject("pagination", pagination);
			} else {
			}
			mv.addObject("phone", phone);

			mv.setViewName("ebanks/activationList");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}
	@RequestMapping(value = "/back_activation.do")
	public ModelAndView backActivation(HttpServletRequest request, HttpServletResponse response){
		ModelAndView mv = new ModelAndView();
		String phone = request.getParameter("phone");
		String activation = request.getParameter("activation");
		String sotpId = request.getParameter("sotpId");
		StringBuffer sotpGet = new StringBuffer();
		String status="";
		SotpAuthDO service = new SotpAuthDO();
		
		try {
			service = sotpDao.selectService();
			sotpGet.append("http://" + service.getIp() + ":" + service.getPost() + "");
			sotpGet.append("/" + SotpService.project + "/activePlugin?");
			sotpGet.append("type=1100&sotpId="+sotpId+"&phoneNum=" + phone + "&activeCode=" + activation);
			String info = sendRequest.sendGet(sotpGet.toString());
			JSONObject jsonsotp = JSONObject.fromObject(info);
			if (jsonsotp.getInt("status") != 0) {
				JSONObject sotpJson = (JSONObject) jsonsotp.get("message");
				status= "激活失败："+sotpJson.getString("errorMsg");
			} else {
				status="激活成功！";
			}
			mv.addObject("phone", phone);
			mv.addObject("status", status);
			mv.setViewName("ebanks/activationList");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return mv;
	}
	
	
	
	
	
	
	
	
	
	
	public List getDevlist(String info){
		List list = new ArrayList<>();
		String [] dev = info.split("&");
		for (int i = 0; i < dev.length; i++) {
			String reg = dev[i];
			String [] ret =reg.split("@");
				ActivationDO act = new ActivationDO();
				
				act.setModel(ret[0]);
				act.setTime(ret[1]);
				act.setIMEI(ret[2]);
				act.setSotpId(ret[3]);
				act.setStatus(ret[4]);
				list.add(act);
		}
		return list;
	}
	

}

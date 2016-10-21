package com.people.sotp.urlcontrollers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.people.sotp.commons.base.BaseController;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/faceV2")
public class Sotp extends BaseController {

	@Resource
	private SotpService sotpService;

	@Resource
	private ManageSotpService manageService;
	
	private static Log log = LogFactory.getLog(Sotp.class);

	@RequestMapping(value = "/controllers.do", method = RequestMethod.POST, consumes = "application/json")
	@ResponseBody
	public void entrance(HttpServletRequest request, HttpServletResponse response, PrintWriter printWriter) {
		int type = 0;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
			String line = null;
			StringBuffer sb = new StringBuffer();
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			String appMsg = sb.toString();
			JSONObject json = JSONObject.fromObject(appMsg);
			
			log.info(json.toString());
			
			type = json.getInt("type");
			String data = null;
			switch (type) {
			case 2001: // 登录验证
				data = sotpService.login(json);
				utilJson(data, printWriter);
				break;
			case 2032: // 绑定银行卡
				String cardNumber = json.getString("cardNumber");
				String accountId = json.getString("accountId");
				sotpService.addCard(type, cardNumber, accountId); //并没有起什么作用
				break;
			case 2002: // 开通一键支付
				data = sotpService.openOnePay(json);
				utilJson(data, printWriter);
				break;
			case 2003: // 设置业务的插件认证开关
				data = sotpService.businessOperate(json);
				utilJson(data, printWriter);
				break;
			case 2004: // 获取业务的插件认证列表
				data = sotpService.getBindlist(json, response.getWriter());
				utilJson(data, printWriter);
				break;
			case 2005: // 支付请求
				data = sotpService.onepayConsume(json);
				utilJson(data, printWriter);
				break;
			case 2006: // 获取e支付策略请求
				data = sotpService.epayPolicy(json);
				utilJson(data, printWriter);
				break;
			case 2007: // 支付请求(e支付)
				data = sotpService.epayConsume(json, response.getWriter());
				utilJson(data, printWriter);
				break;
			case 2008: // 登录SOTP认证请求
				data = sotpService.loginSotpverify(json, response.getWriter());
				utilJson(data, printWriter);
				break;
			case 2009: // 设置（或获取）支付限额
				data = sotpService.operateLimit(json, response.getWriter());
				utilJson(data, printWriter);
				break;
			case 2010: // 消费订单确认
				data = sotpService.tradeDetail(json, printWriter);
				utilJson(data, printWriter);
				break;
			case 1001: // 申请插件
				data = manageService.applyPlugin(json, response.getWriter());
				utilJson(data, printWriter);
				break;
			case 1002: // 下载插件
				data = manageService.downPlugin(json, response.getWriter());
				utilJson(data, printWriter);
				break;
			case 1003: // 插件状态判断
				data = manageService.pluginStatus(json, response.getWriter());
				utilJson(data, printWriter);
				break;
			case 1004: // 更新请求
				data = manageService.pluginUpdateNew(json, response.getWriter());
				utilJson(data, printWriter);
				break;
			case 1005: // 获取设备列表
				data = manageService.getDevlist(json, response.getWriter());
				utilJson(data, printWriter);
				break;
			case 1006: // 解绑设备
				data = manageService.delBinddev(json, response.getWriter());
				utilJson(data, printWriter);
				break;
			case 1007: // 请求验证码(挑战码)
				data = manageService.reqChallengcode(json, response.getWriter());
				utilJson(data, printWriter);
				break;
			case 1008: // 修改保护码
				data = manageService.modProtect(json, response.getWriter());
				utilJson(data, printWriter);
				break;
			case 1009: // 预留信息（获取、修改）
				data = manageService.holdInfo(json, response.getWriter());
				utilJson(data, printWriter);
				break;
				
			case 2011:  //请求时间同步
				data = manageService.genSynSrvtime(json, response.getWriter());
				utilJson(data, printWriter);
				break;
				
			case 2012: //设置绑定安全插件的设备别名
				data = manageService.genSynSrvtime(json, response.getWriter());
				utilJson(data, printWriter);
				break;
			case 2013: //激活插件2
				data = manageService.genSynSrvtime(json, response.getWriter());
				utilJson(data, printWriter);
				break;
				      
			case 2014: //会话密钥1
				data = manageService.dataencryption(json, printWriter);
				utilJson(data, printWriter);
				break;
			case 2015: //会话密钥	2
				data = manageService.gensesskey(json, printWriter); //生成了一个sessionKey
				utilJson(data, printWriter);
				break;
				
			case 2016:  //验证sessionKey
				data =manageService.verifySessionKey(json, printWriter);
				utilJson(data, printWriter);
				break;
				
			case 2017:  //解密
				data = manageService.genSynSrvtime(json, response.getWriter());
				utilJson(data, printWriter);
				break;
				
			case 2018:
				data = manageService.genSynSrvtime(json, printWriter);
				utilJson(data, printWriter);
				break;
			case 2019:
				data = manageService.verifyApp(json, printWriter);
				utilJson(data, printWriter);
				break;
			default:
				StringBuffer error = new StringBuffer();
				Date now = new Date();
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String serverTime = dateFormat.format(now);
				error.append("{\"type\":" + type + ",\"status\":-1,\"message\":{");
				error.append("\"data\":\"\"");
				error.append(",\"errorMsg\":\"parameter error\"},");
				error.append("\"serverTime\":\"" + serverTime + "\"}");
				utilJson(error.toString(), printWriter);
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
			StringBuffer error = new StringBuffer();
			Date now = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String serverTime = dateFormat.format(now);
			error.append("{\"type\":" + type + ",\"status\":"+ErrorInfo.ParameterError+",\"message\":{");
			error.append("\"data\":\"\"");
			error.append(",\"errorMsg\":\"parameter error\"},");
			error.append("\"serverTime\":\"" + serverTime + "\"}");
			utilJson(error.toString(), printWriter);
		}

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
	public void utilJson(String data, PrintWriter printWriter) {
		System.out.println(data);
		JSONObject json = JSONObject.fromObject(data);
		printWriter.write(json.toString());
		printWriter.flush();
		printWriter.close();
	}

}

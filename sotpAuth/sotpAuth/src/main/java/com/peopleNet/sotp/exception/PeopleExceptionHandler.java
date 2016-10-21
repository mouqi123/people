package com.peopleNet.sotp.exception;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mybatis.spring.MyBatisSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.peopleNet.sotp.constant.Constant;
import com.peopleNet.sotp.constant.ErrorConstant;
import com.peopleNet.sotp.controller.ControllerHelper;
import com.peopleNet.sotp.log.LogUtil;
import com.peopleNet.sotp.service.IBusinessService;
import com.peopleNet.sotp.thrift.service.PluginSaveHelper;

//import org.apache.catalina.connector.ClientAbortException;

@Component
public class PeopleExceptionHandler implements HandlerExceptionResolver {
	private static LogUtil logger = LogUtil.getLogger(PeopleExceptionHandler.class);

	@Autowired
	private IBusinessService businessService;

	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
	        Exception ex) {
		// ModelAndView view = new ModelAndView("error");

		/*
		 * // 客户端超时，不返回信息 if (ex instanceof ClientAbortException) { logger.warn(
		 * "ex is instanceof ClientAbortException. return null!"); return null;
		 * }
		 */
		StringWriter errors = new StringWriter();
		ex.printStackTrace(new PrintWriter(errors));
		logger.error("<PeopleExceptionHandler> error StackTrace :" + errors.toString());

		logger.error("<PeopleExceptionHandler> error:%s.", ex.toString());
		// 释放该线程保存插件的缓存区
		PluginSaveHelper.release();

		int type = Constant.SERVICE_TYPE.SOTP_UNKOWN_TYPE;
		int status = ErrorConstant.SYSTEM_UNKOWN_ERR;
		String errMsg = "";
		String reqmsg = request.getQueryString();

		/*
		 * if (null != reqmsg) { type = Integer.valueOf(this.getReqParam(reqmsg,
		 * Constant.PARA_NAME.TYPE)); }
		 */

		if (ex instanceof MyBatisSystemException) {
			errMsg = "db ops error!";
			status = ErrorConstant.SQL_OPS_ERR;
		} else {
			errMsg = "system error!";
			status = ErrorConstant.SYSTEM_UNKOWN_ERR;
		}
		String responseMsg = this.retStr(type, status, "", errMsg, reqmsg);
		// view.addObject("errorMsg", responseMsg);

		try {
			ServletOutputStream writer = response.getOutputStream();
			writer.print(responseMsg);
			writer.flush();

			// PrintWriter writer;
			// writer = response.getWriter();
			// writer.write(responseMsg);
			// writer.flush();
		} catch (IOException e) {
			logger.error("<PeopleExceptionHandler> print exception msg error. %s", e.toString());
		}

		return null;
	}

	public String getReqParam(String requestMsg, String title) {
		if (requestMsg == null || requestMsg.length() <= 0)
			return null;

		if (title == null || title.length() <= 0)
			return null;

		String[] codesplit = requestMsg.split("&");
		for (String tmp : codesplit) {
			String[] split = tmp.split("=");
			if (split.length == 2) {
				if (split[0].equals(title))
					return split[1];
			}
		}
		return null;
	}

	/*
	 * 封装返回数据
	 */
	public String retStr(int type, int status, String data, String errMsg, String reqmsg) {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String timeinfo = dateFormat.format(System.currentTimeMillis());

		Map<String, Object> message = new HashMap<String, Object>();
		if (data != null && data.length() > 0) {
			message.put("data", data);
		}
		if (status != ErrorConstant.RET_OK) {
			if (errMsg != null && errMsg.length() > 0) {
				message.put("errorMsg", errMsg);
			}
		}
		String jsonResStr = ControllerHelper.getResultJsonString(type, status, message, timeinfo);

		// 记录业务日志
		this.businessService.setBusinessLog(reqmsg, jsonResStr, status, errMsg);

		logger.debug("sotpAuth response:" + jsonResStr);
		return jsonResStr;
	}
}

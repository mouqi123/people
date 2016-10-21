package com.people.sotp.auditlog.controller;

import java.io.PrintWriter;

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
import com.people.sotp.commons.base.DataGrid;
import com.people.sotp.commons.base.ResultDO;
import com.people.sotp.dataobject.AuditLogDO;

@Controller
@RequestMapping("/auditlog")
public class AuditLogController extends BaseController {

	private static Log log = LogFactory.getLog(AuditLogController.class);

	@Autowired
	private AuditLogService auditLogService;

	/**
	 * 日志查询
	 * 
	 * @return
	 */
	@RequestMapping(value = "/viewAuditlogList.htm", method = RequestMethod.GET)
	public ModelAndView viewAuditlog(@ModelAttribute AuditLogDO log, PrintWriter printWriter) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("auditlog/auditlogList");
		return mv;
	}

	/**
	 * 日志查询
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getAuditlogList.do", method = RequestMethod.POST)
	public void getAuditlogList(@ModelAttribute AuditLogDO logDO, PrintWriter printWriter) {
		try {
			ResultDO resultDO = auditLogService.queryAuditLogList(logDO);
			if (resultDO.isSuccess()) {
				DataGrid grid = (DataGrid) resultDO.getModel("plugindata");
				resultjson = writeJson(grid);
			}
		} catch (Exception e) {
			log.error("获取日志查询列表失败");
			log.error(e);
		}
		printWriter.write(resultjson);
		printWriter.flush();
		printWriter.close();
	}

	/**
	 * 日志详情
	 * 
	 * @return
	 */
	@RequestMapping(value = "/viewAuditlogInfo.htm", method = RequestMethod.GET)
	public ModelAndView getAuditlogInfo(@ModelAttribute AuditLogDO log, PrintWriter printWriter) {
		ModelAndView mv = new ModelAndView();
		ResultDO resultDO = auditLogService.getAuditLogInfo(log);
		if (resultDO.isSuccess()) {
			AuditLogDO logDO = (AuditLogDO) resultDO.getModel("auditlogInfo");
			if ("1".equals(logDO.getLevel())) {
				logDO.setLevel("低级");
			} else if ("2".equals(logDO.getLevel())) {
				logDO.setLevel("中级");
			} else if ("3".equals(logDO.getLevel())) {
				logDO.setLevel("高级");
			}
			if ("1".equals(logDO.getStatus())) {
				logDO.setStatus("成功");
			} else if ("2".equals(logDO.getStatus())) {
				logDO.setStatus("失败");
			}
			mv.addObject("auditlog", logDO);
		} else {
			mv.addObject("message", resultDO.getErrMsg());
		}
		mv.setViewName("auditlog/viewAuditlogInfo");
		return mv;
	}

}

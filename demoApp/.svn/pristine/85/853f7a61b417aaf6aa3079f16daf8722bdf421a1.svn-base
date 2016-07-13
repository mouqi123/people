package com.people.sotp.auditlog.controller;

import static com.people.sotp.page.SimplePage.cpn;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.people.sotp.auditlog.service.AuditLogService;
import com.people.sotp.commons.base.BaseController;
import com.people.sotp.commons.base.DataGrid;
import com.people.sotp.commons.base.ResultDO;
import com.people.sotp.dataobject.AuditLogDO;
import com.people.sotp.page.CookieUtils;
import com.people.sotp.page.Pagination;

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
	@RequestMapping(value = "/getAuditlogList.do")
	public ModelAndView getAuditlogList(@ModelAttribute AuditLogDO logDO, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		try {
			Long pagesize = (long) CookieUtils.getPageSize(request);
			logDO.setPagesize(pagesize);
			ResultDO resultDO = auditLogService.queryAuditLogList(logDO);
			CookieUtils.getPageSize(request);
			if (resultDO.isSuccess()) {
				DataGrid grid = (DataGrid) resultDO.getModel("plugindata");
				int page = (int) logDO.getPage();
				Pagination pagination = new Pagination(cpn(page), CookieUtils.getPageSize(request), grid.getTotal(),
						grid.getRows());
				mv.addObject("pagination", pagination);
			}
		} catch (Exception e) {
			log.error("获取日志查询列表失败");
			log.error(e);
		}
		mv.setViewName("log/system_list");
		return mv;
	}


}

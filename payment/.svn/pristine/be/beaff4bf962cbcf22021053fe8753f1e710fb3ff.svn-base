package com.people.sotp.payment.controller;

import static com.people.sotp.page.SimplePage.cpn;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.people.sotp.commons.base.BaseController;
import com.people.sotp.commons.base.DataGrid;
import com.people.sotp.commons.base.ResultDO;
import com.people.sotp.dataobject.AuditLogDO;
import com.people.sotp.dataobject.DealLogDO;
import com.people.sotp.page.Pagination;
import com.people.sotp.payment.service.LogService;
import com.people.sotp.page.CookieUtils;

@Controller
@RequestMapping("/log")
public class LogController extends BaseController {

	private static Log logger = LogFactory.getLog(LogController.class);
	@Resource
	private LogService logService;

	@RequestMapping("/operation_list.do")
	public ModelAndView OperationList(@ModelAttribute AuditLogDO model, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		try {
			Long pagesize = (long) CookieUtils.getPageSize(request);
			model.setPagesize(pagesize);
			ResultDO resultDO = logService.queryLogList(model);
			CookieUtils.getPageSize(request);
			if (resultDO.isSuccess()) {
				DataGrid grid = (DataGrid) resultDO.getModel("masterdata");
				int page = (int) model.getPageNo();
				Pagination pagination = new Pagination(cpn(page), CookieUtils.getPageSize(request), grid.getTotal(),
						grid.getRows());
				mv.addObject("pagination", pagination);
			}
		} catch (Exception e) {
			logger.error("获取用户管理查询列表失败");
			logger.error(e);
		}
		mv.setViewName("log/operationList");
		return mv;
	}
	
	
	
	@RequestMapping("/deal_list.do")
	public ModelAndView dealList(@ModelAttribute DealLogDO model, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		try {
			Long pagesize = (long) CookieUtils.getPageSize(request);
			model.setPagesize(pagesize);
			ResultDO resultDO = logService.queryDealLogList(model);
			CookieUtils.getPageSize(request);
			if (resultDO.isSuccess()) {
				DataGrid grid = (DataGrid) resultDO.getModel("masterdata");
				int page = (int) model.getPageNo();
				Pagination pagination = new Pagination(cpn(page), CookieUtils.getPageSize(request), grid.getTotal(),
						grid.getRows());
				mv.addObject("pagination", pagination);
			}
		} catch (Exception e) {
			logger.error("获取用户管理查询列表失败");
			logger.error(e);
		}
		mv.setViewName("log/dealList");
		return mv;
	}
	
	
	
	
	
	
	
}

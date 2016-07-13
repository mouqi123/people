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
import com.people.sotp.dataobject.ApplyDO;
import com.people.sotp.page.Pagination;
import com.people.sotp.payment.service.ApplyService;
import com.people.sotp.page.CookieUtils;

@Controller
@RequestMapping("/apply")
public class ApplyController extends BaseController {

	private static Log logger = LogFactory.getLog(ApplyController.class);
	@Resource
	private ApplyService ApplyService;

	@RequestMapping("/v_list.do")
	public ModelAndView ApplyList(@ModelAttribute ApplyDO model, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		try {
			Long pagesize = (long) CookieUtils.getPageSize(request);
			model.setPagesize(pagesize);
			ResultDO resultDO = ApplyService.queryApplyList(model);
			CookieUtils.getPageSize(request);
			if (resultDO.isSuccess()) {
				DataGrid grid = (DataGrid) resultDO.getModel("masterdata");
				int page = (int) model.getPage();
				Pagination pagination = new Pagination(cpn(page), CookieUtils.getPageSize(request), grid.getTotal(),
						grid.getRows());
				mv.addObject("pagination", pagination);
			}
		} catch (Exception e) {
			logger.error("获取列表失败");
			logger.error(e);
		}
		mv.setViewName("apply/list");
		return mv;
	}

	@RequestMapping(value = "/v_add.do")
	public ModelAndView add(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("apply/add");
		return mv;
	}

	@RequestMapping(value = "/o_save.do")
	public ModelAndView save(@ModelAttribute ApplyDO model, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		ApplyService.addApply(model);
		mv.setViewName("redirect:/apply/v_list.do");
		return mv;
	}

	@RequestMapping("/v_edit.do")
	public ModelAndView edit(@ModelAttribute ApplyDO model, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		ApplyDO apply = ApplyService.queryApplyOne(model);
		mv.addObject("ApplyDO", apply);
		mv.setViewName("apply/edit");
		return mv;
	}

	@RequestMapping("/o_update.do")
	public ModelAndView update(@ModelAttribute ApplyDO model, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		try {
			ApplyService.updateApply(model);
		} catch (Exception e) {
			logger.error("修改失败");
			logger.error(e);
		}
		mv.setViewName("redirect:/apply/v_list.do");
		return mv;
	}

	@RequestMapping("/o_delete.do")
	public ModelAndView delete(long[] ids, HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		try {
			ApplyService.deleteApply(ids);
		} catch (Exception e) {
			logger.error("删除失败");
			logger.error(e);
		}
		mv.setViewName("redirect:/apply/v_list.do");
		return mv;
	}
}

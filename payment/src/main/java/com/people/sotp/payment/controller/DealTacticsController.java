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
import com.people.sotp.dataobject.DealTacticsDO;
import com.people.sotp.page.Pagination;
import com.people.sotp.payment.service.DealTacticsService;
import com.people.sotp.page.CookieUtils;

@Controller
@RequestMapping("/dealTactics")
public class DealTacticsController extends BaseController {

	private static Log logger = LogFactory.getLog(DealTacticsController.class);
	@Resource
	private DealTacticsService DealTacticsService;

	@RequestMapping("/v_list.do")
	public ModelAndView DealTacticsList(@ModelAttribute DealTacticsDO model, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		try {
			Long pagesize = (long) CookieUtils.getPageSize(request);
			model.setPagesize(pagesize);
			ResultDO resultDO = DealTacticsService.queryDealTacticsList(model);
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
		mv.setViewName("dealTactics/list");
		return mv;
	}

	@RequestMapping(value = "/v_add.do")
	public ModelAndView add(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("dealTactics/add");
		return mv;
	}

	@RequestMapping(value = "/o_save.do")
	public ModelAndView save(@ModelAttribute DealTacticsDO model, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		DealTacticsService.addDealTactics(model);
		mv.setViewName("redirect:/dealTactics/v_list.do");
		return mv;
	}

	@RequestMapping("/v_edit.do")
	public ModelAndView edit(@ModelAttribute DealTacticsDO model, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		ResultDO resultDO = DealTacticsService.queryDealTacticsList(model);
		DataGrid grid = (DataGrid) resultDO.getModel("masterdata");
		mv.addObject("DealTacticsDO", grid.getRows().get(0));
		mv.setViewName("dealTactics/edit");
		return mv;
	}

	@RequestMapping("/o_update.do")
	public ModelAndView update(@ModelAttribute DealTacticsDO model, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		try {
			DealTacticsService.updateDealTactics(model);
		} catch (Exception e) {
			logger.error("修改账户失败");
			logger.error(e);
		}
		mv.setViewName("redirect:/dealTactics/v_list.do");
		return mv;
	}

	@RequestMapping("/o_delete.do")
	public ModelAndView delete(long[] ids, HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		try {
			DealTacticsService.deleteDealTactics(ids);
		} catch (Exception e) {
			logger.error("删除账户失败");
			logger.error(e);
		}
		mv.setViewName("redirect:/dealTactics/v_list.do");
		return mv;
	}
}

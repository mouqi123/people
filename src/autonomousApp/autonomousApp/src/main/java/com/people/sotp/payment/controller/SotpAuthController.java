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
import com.people.sotp.dataobject.SotpAuthDO;
import com.people.sotp.page.Pagination;
import com.people.sotp.payment.service.SotpAuthService;
import com.people.sotp.page.CookieUtils;

@Controller
@RequestMapping("/sotpAuth")
public class SotpAuthController extends BaseController {

	private static Log logger = LogFactory.getLog(SotpAuthController.class);
	@Resource
	private SotpAuthService sotpAuthService;

	@RequestMapping("/v_list.do")
	public ModelAndView sotpAuthList(@ModelAttribute SotpAuthDO model, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		try {
			Long pagesize = (long) CookieUtils.getPageSize(request);
			model.setPagesize(pagesize);
			ResultDO resultDO = sotpAuthService.querySotpAuthList(model);
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
		mv.setViewName("sotpAuth/list");
		return mv;
	}

	@RequestMapping(value = "/v_add.do")
	public ModelAndView add(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("sotpAuth/add");
		return mv;
	}

	@RequestMapping(value = "/o_save.do")
	public ModelAndView save(@ModelAttribute SotpAuthDO model, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		sotpAuthService.addSotpAuth(model);
		mv.setViewName("redirect:/sotpAuth/v_list.do");
		return mv;
	}

	@RequestMapping({ "/v_edit.do" })
	public ModelAndView edit(@ModelAttribute SotpAuthDO model, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		ResultDO resultDO = sotpAuthService.querySotpAuthList(model);
		DataGrid grid = (DataGrid) resultDO.getModel("masterdata");
		mv.addObject("SotpAuthDO", grid.getRows().get(0));
		mv.setViewName("sotpAuth/edit");
		return mv;
	}

	@RequestMapping({ "/o_update.do" })
	public ModelAndView update(@ModelAttribute SotpAuthDO model, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		try {
			sotpAuthService.updateSotpAuth(model);
		} catch (Exception e) {
			logger.error("修改失败");
			logger.error(e);
		}
		mv.setViewName("redirect:/sotpAuth/v_list.do");
		return mv;
	}

	@RequestMapping({ "/o_delete.do" })
	public ModelAndView delete(long[] ids, HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		try {
			sotpAuthService.deleteSotpAuth(ids);
		} catch (Exception e) {
			logger.error("删除失败");
			logger.error(e);
		}
		mv.setViewName("redirect:/sotpAuth/v_list.do");
		return mv;
	}
}

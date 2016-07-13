package com.people.sotp.payment.controller;

import static com.people.sotp.page.SimplePage.cpn;

import java.util.List;

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
import com.people.sotp.dataobject.AuthDO;
import com.people.sotp.dataobject.MemberDO;
import com.people.sotp.page.Pagination;
import com.people.sotp.payment.service.AuthService;
import com.people.sotp.payment.service.MemberService;
import com.people.sotp.page.CookieUtils;

@Controller
@RequestMapping("/auth")
public class AuthController extends BaseController {

	private static Log logger = LogFactory.getLog(AuthController.class);
	@Resource
	private AuthService AuthService;
	@Resource
	private MemberService memberService;

	@RequestMapping("/v_list.do")
	public ModelAndView AuthList(@ModelAttribute AuthDO model, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		try {
			Long pagesize = (long) CookieUtils.getPageSize(request);
			model.setPagesize(pagesize);
			ResultDO resultDO = AuthService.queryAuthList(model);
			CookieUtils.getPageSize(request);
			if (resultDO.isSuccess()) {
				DataGrid grid = (DataGrid) resultDO.getModel("masterdata");
				int page = (int) model.getPageNo();
				Pagination pagination = new Pagination(cpn(page), CookieUtils.getPageSize(request), grid.getTotal(),
						grid.getRows());
				mv.addObject("pagination", pagination);
			}
		} catch (Exception e) {
			logger.error("获取认证列表失败");
			logger.error(e);
		}
		mv.setViewName("auth/list");
		return mv;
	}

	@RequestMapping(value = "/v_add.do")
	public ModelAndView add(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		try {
			ResultDO resultDO = memberService.queryMemberAuthList();
			if (resultDO.isSuccess()) {
				DataGrid grid = (DataGrid) resultDO.getModel("masterdata");
				List<MemberDO> list = grid.getRows();
				mv.addObject("groupList", list);
			}
		} catch (Exception e) {
			logger.error("添加账户失败");
			logger.error(e);
		}

		mv.setViewName("auth/add");
		return mv;
	}

	@RequestMapping(value = "/o_save.do")
	public ModelAndView save(@ModelAttribute AuthDO model, HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		AuthService.addAuth(model);
		mv.setViewName("redirect:/auth/v_list.do");
		return mv;
	}

	@RequestMapping({ "/v_edit.do" })
	public ModelAndView edit(@ModelAttribute AuthDO model, HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		ResultDO resultDO = AuthService.queryAuthOne(model);
		DataGrid grid = (DataGrid) resultDO.getModel("masterdata");
		ResultDO result = new ResultDO();
		result = memberService.queryMemberAuthList();
		if (result.isSuccess()) {
			DataGrid gridSult = (DataGrid) result.getModel("masterdata");
			List<MemberDO> list = gridSult.getRows();
			mv.addObject("groupList", list);
		}
		mv.addObject("AuthDO", grid.getRows().get(0));
		mv.setViewName("auth/edit");
		return mv;
	}

	@RequestMapping({ "/o_update.do" })
	public ModelAndView update(@ModelAttribute AuthDO model, HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		try {
			AuthService.updateAuth(model);
		} catch (Exception e) {
			logger.error("修改认证失败");
			logger.error(e);
		}
		mv.setViewName("redirect:/auth/v_list.do");
		return mv;
	}

	@RequestMapping({ "/o_delete.do" })
	public ModelAndView delete(long[] ids, HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		try {
			AuthService.deleteAuth(ids);
		} catch (Exception e) {
			logger.error("删除认证失败");
			logger.error(e);
		}
		mv.setViewName("redirect:/auth/v_list.do");
		return mv;
	}
}

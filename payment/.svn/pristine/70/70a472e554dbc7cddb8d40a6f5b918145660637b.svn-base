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
import com.people.sotp.dataobject.AccountManageDO;
import com.people.sotp.dataobject.MemberDO;
import com.people.sotp.page.Pagination;
import com.people.sotp.payment.service.AccountManageService;
import com.people.sotp.payment.service.MemberService;
import com.people.sotp.page.CookieUtils;

@Controller
@RequestMapping("/accountManage")
public class AccountManageController extends BaseController {

	private static Log logger = LogFactory.getLog(AccountManageController.class);
	@Resource
	private AccountManageService AccountManageService;
	@Resource
	private MemberService memberService;

	@RequestMapping("/v_list.do")
	public ModelAndView AccountManageList(@ModelAttribute AccountManageDO model, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		try {
			Long pagesize = (long) CookieUtils.getPageSize(request);
			model.setPagesize(pagesize);
			ResultDO resultDO = AccountManageService.queryAccountManageList(model);
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
		mv.setViewName("accountManage/list");
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
		mv.setViewName("accountManage/add");
		return mv;
	}

	@RequestMapping(value = "/o_save.do")
	public ModelAndView save(@ModelAttribute AccountManageDO model, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		AccountManageService.addAccountManage(model);
		mv.setViewName("redirect:/accountManage/v_list.do");
		return mv;
	}

	@RequestMapping("/v_edit.do")
	public ModelAndView edit(@ModelAttribute AccountManageDO model, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		ResultDO resultDO = AccountManageService.queryAccountManageOne(model);
		DataGrid grid = (DataGrid) resultDO.getModel("masterdata");
		ResultDO result = new ResultDO();
		result = memberService.queryMemberAuthList();
		if (result.isSuccess()) {
			DataGrid gridSult = (DataGrid) result.getModel("masterdata");
			List<MemberDO> list = gridSult.getRows();
			mv.addObject("groupList", list);
		}
		mv.addObject("accountManageDO", grid.getRows().get(0));
		mv.setViewName("accountManage/edit");
		return mv;
	}

	@RequestMapping("/o_update.do")
	public ModelAndView update(@ModelAttribute AccountManageDO model, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		try {
			AccountManageService.updateAccountManage(model);
		} catch (Exception e) {
			logger.error("修改账户失败");
			logger.error(e);
		}
		mv.setViewName("redirect:/accountManage/v_list.do");
		return mv;
	}

	@RequestMapping("/o_delete.do")
	public ModelAndView delete(long[] ids, HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		try {
			AccountManageService.deleteAccountManage(ids);
		} catch (Exception e) {
			logger.error("删除账户失败");
			logger.error(e);
		}
		mv.setViewName("redirect:/accountManage/v_list.do");
		return mv;
	}
}

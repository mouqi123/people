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
import com.people.sotp.commons.util.RequestUtils;
import com.people.sotp.dataobject.UserDO;
import com.people.sotp.page.Pagination;
import com.people.sotp.payment.service.MemberService;
import com.people.sotp.page.CookieUtils;

@Controller
@RequestMapping("/member")
public class MemberController extends BaseController {

	private static Log logger = LogFactory.getLog(MemberController.class);
	@Resource
	private MemberService memberService;

	@RequestMapping("/v_list.do")
	public ModelAndView memberList(@ModelAttribute UserDO userDO, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		try {
			Long pagesize = (long) CookieUtils.getPageSize(request);
			userDO.setPagesize(pagesize);
			ResultDO resultDO = memberService.queryMemberList(userDO);
			CookieUtils.getPageSize(request);
			if (resultDO.isSuccess()) {
				DataGrid grid = (DataGrid) resultDO.getModel("masterdata");
				int page = (int) userDO.getPage();
				Pagination pagination = new Pagination(cpn(page), CookieUtils.getPageSize(request), grid.getTotal(),
						grid.getRows());
				mv.addObject("pagination", pagination);
			}
		} catch (Exception e) {
			logger.error("获取用户管理查询列表失败");
			logger.error(e);
		}
		mv.setViewName("member/list");
		return mv;
	}

	@RequestMapping(value = "/v_add.do")
	public ModelAndView add(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("member/add");
		return mv;
	}

	@RequestMapping(value = "/o_save.do")
	public ModelAndView save(@ModelAttribute UserDO userDO, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		memberService.addMember(userDO);
		mv.setViewName("redirect:/member/v_list.do");
		return mv;
	}

	@RequestMapping("/v_edit.do")
	public ModelAndView edit(@ModelAttribute UserDO userDO, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		UserDO member = memberService.queryMemberOne(userDO);
		mv.addObject("UserDO", member);
		mv.setViewName("member/edit");
		return mv;
	}

	@RequestMapping("/o_update.do")
	public ModelAndView update(@ModelAttribute UserDO userDO, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		try {
			memberService.updateMember(userDO);
		} catch (Exception e) {
			logger.error("修改账户失败");
			logger.error(e);
		}
		mv.setViewName("redirect:/member/v_list.do");
		return mv;
	}

	@RequestMapping({ "/o_delete.do" })
	public ModelAndView delete(long[] ids, HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();

		try {
			memberService.deleteMember(ids);
		} catch (Exception e) {
			logger.error("删除账户失败");
			logger.error(e);
		}
		mv.setViewName("redirect:/member/v_list.do");
		return mv;
	}
}

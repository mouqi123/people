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
import com.people.sotp.dataobject.AccountManageDO;
import com.people.sotp.dataobject.AuthDO;
import com.people.sotp.dataobject.CardDO;
import com.people.sotp.dataobject.DealTacticsDO;
import com.people.sotp.dataobject.MemberDO;
import com.people.sotp.dataobject.OrderDO;
import com.people.sotp.page.Pagination;
import com.people.sotp.payment.service.AccountManageService;
import com.people.sotp.payment.service.AuthService;
import com.people.sotp.payment.service.CardService;
import com.people.sotp.payment.service.DealTacticsService;
import com.people.sotp.payment.service.MemberService;
import com.people.sotp.payment.service.OrderService;
import com.people.sotp.page.CookieUtils;

@Controller
@RequestMapping("/member")
public class MemberController extends BaseController {

	private static Log logger = LogFactory.getLog(MemberController.class);
	@Resource
	private MemberService memberService;
	@Resource
	private AccountManageService AccountManageService;
	@Resource
	private AuthService AuthService;
	@Resource
	private CardService CardService;
	@Resource
	private DealTacticsService DealTacticsService;
	@Resource
	private OrderService OrderService;

	@RequestMapping("/v_list.do")
	public ModelAndView memberList(@ModelAttribute MemberDO userDO, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		try {
			Long pagesize = (long) CookieUtils.getPageSize(request);
			userDO.setPagesize(pagesize);
			ResultDO resultDO = memberService.queryMemberList(userDO);
			CookieUtils.getPageSize(request);
			if (resultDO.isSuccess()) {
				DataGrid grid = (DataGrid) resultDO.getModel("masterdata");
				int page = (int) userDO.getPageNo();
				Pagination pagination = new Pagination(cpn(page), CookieUtils.getPageSize(request), grid.getTotal(),
						grid.getRows());
				mv.addObject("phone", userDO.getPhone());
				mv.addObject("identityType", userDO.getIdentityType());
				mv.addObject("identityNumber", userDO.getIdentityNumber());
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
	public ModelAndView save(@ModelAttribute MemberDO userDO, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		memberService.addMember(userDO);
		AccountManageDO model = new AccountManageDO();
		model.setMemberId(String.valueOf(userDO.getId()));
		model.setAccountType("金融");
		model.setAccountName("理财");
		AccountManageService.addAccountManage(model);
		AuthDO auth = new AuthDO();
		auth.setMemberId(String.valueOf(userDO.getId()));
		auth.setLogin(0);
		auth.setePay(0);
		auth.setePay(0);
		AuthService.addAuth(auth);
		CardDO card = new CardDO();
		card.setCardNumber("6228486700131");
		card.setCardPwd("123456");
		card.setCardType("0");
		card.setPayPwd("147258");
		card.setMoney("1000000");
		card.setPhone(userDO.getPhone());
		card.setName(userDO.getUserName());
		card.setIdentityType("0");
		card.setIdentityNumber("31321356456");
		card.setOneKeyStatus(0);
		CardService.addCard(card);
		DealTacticsDO deal = new DealTacticsDO();
		deal.setMemberId(userDO.getId());
		deal.setLastMoney("123456");
		deal.setLastTime("123456");
		deal.setPayMoney("123456");
		deal.setLimitMoney("123456");
		DealTacticsService.addDealTactics(deal);
		OrderDO order = new OrderDO();
		order.setMemberId(userDO.getId());
		order.setOrderNumber("20123344");
		order.setOrderMoney("179.5");
		order.setOrderStatus(0);
		OrderService.addOrder(order);
		mv.setViewName("redirect:/member/v_list.do");
		return mv;
	}

	@RequestMapping("/v_edit.do")
	public ModelAndView edit(@ModelAttribute MemberDO userDO, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		String phone = RequestUtils.getQueryParam(request, "phone");
		String identityType = RequestUtils.getQueryParam(request, "identityType");
		String identityNumber = RequestUtils.getQueryParam(request, "identityNumber");
		mv.addObject("phone", phone);
		mv.addObject("identityType", identityType);
		mv.addObject("identityNumber", identityNumber);
		ResultDO resultDO = memberService.queryMemberOne(userDO);
		MemberDO grid =(MemberDO) resultDO.getModel("masterdata");
		mv.addObject("MemberDO", grid);
		mv.setViewName("member/edit");
		return mv;
	}

	@RequestMapping("/o_update.do")
	public ModelAndView update(@ModelAttribute MemberDO userDO, HttpServletRequest request,
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
		String phone = RequestUtils.getQueryParam(request, "phone");
		String identityType = RequestUtils.getQueryParam(request, "identityType");
		String identityNumber = RequestUtils.getQueryParam(request, "identityNumber");
		mv.addObject("phone", phone);
		mv.addObject("identityType", identityType);
		mv.addObject("identityNumber", identityNumber);

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

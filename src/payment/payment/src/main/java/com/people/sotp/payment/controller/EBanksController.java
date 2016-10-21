package com.people.sotp.payment.controller;


import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.people.sotp.commons.base.BaseController;
import com.people.sotp.dataobject.OrderDO;
import com.people.sotp.freemarker.UUIDDirective;
import com.people.sotp.payment.service.OrderService;

@Controller
@RequestMapping("/eBanks")
public class EBanksController extends BaseController {

	@Resource
	private OrderService orderService;
	
	private static Log logger = LogFactory.getLog(EBanksController.class);
	

	@RequestMapping("/v_list.do")
	public ModelAndView AccountManageList(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("eBanks/list");
		return mv;
	}
	
	@RequestMapping(value = "/payEBanks.do", method = RequestMethod.GET)
	public void payEBanks(HttpServletRequest request, HttpServletResponse response) {
		String uuid =request.getParameter("uuid");
		String phoneNum = request.getParameter("phoneNum");
		String tradeNum = request.getParameter("tradeNum");
		String tradeMoney = request.getParameter("tradeMoney");
		UUIDDirective.map.put(phoneNum, uuid);
	}
	
	@RequestMapping("/ajaxPolling.do")
	public void ajaxPolling(HttpServletRequest request,HttpServletResponse response,PrintWriter printWriter) {
		String phone =request.getParameter("phoneNum");
		Map<String,Object> map = new HashMap<String,Object>();
		StringBuffer payment = new StringBuffer();
		if(UUIDDirective.map.get(phone)!=null){
			payment.append("{\"status\":1}");
			map.put("phoneNum", phone);
			OrderDO order =orderService.queryOrderOne(map);
			if(order.getOrderStatus()==0){
				payment.delete(0, payment.length());
				payment.append("{\"status\":0}");
			}
		}else{
			payment.append("{\"status\":2}");
		}
		printWriter.write(payment.toString());
	}

}

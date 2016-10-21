package com.people.sotp.eshop;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.people.sotp.commons.base.BaseController;

@Controller
@RequestMapping("/eshop")
public class EshopController extends BaseController{

	
	
	private static Log logger = LogFactory.getLog(EshopController.class);
	
	
	@RequestMapping(value = "/list.do", method = RequestMethod.GET)
	public String index(HttpServletRequest request, HttpServletResponse response) {
		return "eshop/list";
	}
}

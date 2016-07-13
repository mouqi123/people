package com.people.sotp.frame;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.people.sotp.commons.base.BaseController;

@Controller
@RequestMapping("/frame")
public class frameController extends BaseController {

	@RequestMapping("/index.do")
	public ModelAndView index(ModelMap model) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("index");
		return mv;
	}

	@RequestMapping("/top.do")
	public ModelAndView top(HttpServletRequest request, ModelMap model) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("frame/top");
		return mv;
	}

	@RequestMapping("/user_main.do")
	public ModelAndView userMain(ModelMap model) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("frame/user_main");
		return mv;
	}

	@RequestMapping("/user_left.do")
	public String userLeft(ModelMap model) {
		return "frame/user_left";
	}

	@RequestMapping("/user_right.do")
	public String userRight(ModelMap model) {
		return "frame/user_right";
	}

	@RequestMapping("/config_main.do")
	public ModelAndView configMain(ModelMap model) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("frame/config_main");
		return mv;
	}

	@RequestMapping("/config_left.do")
	public String configLeft(ModelMap model) {
		return "frame/config_left";
	}

	@RequestMapping("/log_main.do")
	public ModelAndView logMain(ModelMap model) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("frame/log_main");
		return mv;
	}

	@RequestMapping("/log_left.do")
	public String logLeft(ModelMap model) {
		return "frame/log_left";
	}

	@RequestMapping("/deal_main.do")
	public ModelAndView dealMain(ModelMap model) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("frame/deal_main");
		return mv;
	}

	@RequestMapping("/deal_left.do")
	public String dealLeft(ModelMap model) {
		return "frame/deal_left";
	}

	@RequestMapping("/eBanks_main.do")
	public ModelAndView eBanksMain(ModelMap model) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("frame/eBanks_main");
		return mv;
	}

	@RequestMapping("/eBanks_left.do")
	public String eBanksLeft(ModelMap model) {
		return "frame/eBanks_left";
	}
	@RequestMapping("/apply_main.do")
	public ModelAndView applyMain(ModelMap model) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("frame/apply_main");
		return mv;
	}

	@RequestMapping("/apply_left.do")
	public String applyLeft(ModelMap model) {
		return "frame/apply_left";
	}

}

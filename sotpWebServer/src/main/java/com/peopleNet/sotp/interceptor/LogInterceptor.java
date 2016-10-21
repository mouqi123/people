package com.peopleNet.sotp.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.peopleNet.sotp.log.LogUtil;

/**
 * 
 * @描述 用户controller层的访问日志拦截器
 */
public class LogInterceptor implements HandlerInterceptor {
	private static LogUtil logger = LogUtil.getLogger(LogInterceptor.class);

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
	        throws Exception {
		request.setAttribute("_log_s_time", System.currentTimeMillis());
		return true;
	}

	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView view)
	        throws Exception {
		long time = System.currentTimeMillis() - (Long) request.getAttribute("_log_s_time");
		String controller = handler.getClass().getName();
		String viewName = "";
		if (view != null && view.hasView()) {
			viewName = view.getViewName();
		}
		request.setAttribute("_visit_long_time", time);
		logger.debug("controller:'%s', gotoView:'%s', tm:%dms", controller, viewName, time);
	}

	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
	        throws Exception {
	}

}

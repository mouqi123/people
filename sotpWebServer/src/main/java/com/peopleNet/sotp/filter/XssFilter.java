package com.peopleNet.sotp.filter;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import com.peopleNet.sotp.log.LogUtil;

public class XssFilter implements Filter {
	private static LogUtil logger = LogUtil.getLogger(XssFilter.class);

	FilterConfig filterConfig = null;

	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}

	public void destroy() {
		this.filterConfig = null;
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
	        throws IOException, ServletException {
		logger.debug("excute xss filter-----start!!");
		chain.doFilter(new XssHttpServletRequestWrapper((HttpServletRequest) request), response);
		logger.debug("use filter to avoid xss-----end!");
	}
}

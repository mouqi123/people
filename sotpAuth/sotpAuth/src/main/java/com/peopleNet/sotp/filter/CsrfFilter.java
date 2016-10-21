package com.peopleNet.sotp.filter;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import com.peopleNet.sotp.log.LogUtil;
import com.peopleNet.sotp.security.CSRFTokenManager;

public class CsrfFilter implements Filter {
	private static LogUtil logger = LogUtil.getLogger(CsrfFilter.class);
	FilterConfig filterConfig = null;

	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}

	public void destroy() {
		this.filterConfig = null;
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
	        throws IOException, ServletException {
		logger.debug("excute csrf filter-----start!!");
		String CSRFToken = request.getParameter("CSRFToken");
		if (CSRFToken == null || !CSRFToken.equals(((HttpServletRequest) request).getSession()
		        .getAttribute(CSRFTokenManager.CSRF_TOKEN_FOR_SESSION_ATTR_NAME).toString())) {
			logger.debug("CSRF attack detected. URL:" + ((HttpServletRequest) request).getRequestURL());
		}
		chain.doFilter(request, response);
		logger.debug("use csrf filter to avoid csrf-----end!");
	}
}

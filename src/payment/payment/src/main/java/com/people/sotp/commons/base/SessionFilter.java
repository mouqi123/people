package com.people.sotp.commons.base;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.people.sotp.dataobject.MasterDO;

/**
 * 会话过滤器
 */
@Component("SessionFilter")
public class SessionFilter implements Filter {

	String exclude = "login.jsp";
	String redirecturl = "login.htm";

	public void destroy() {
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		MasterDO user = (MasterDO) request.getSession().getAttribute(GlobalParam.SESSION_USER);
		String url = request.getRequestURI();
		if (user == null) {
			url = url.substring(url.lastIndexOf("/") + 1, url.length());
			String str[] = exclude.split(",");
			boolean bn = false;
			for (int i = 0; i < str.length; i++) {
				if (str[i].equalsIgnoreCase(url)) {
					bn = true;
					break;
				}
			}
			if (bn) {
				chain.doFilter(request, response);
			} else {
				String path = request.getContextPath();
				if (!path.endsWith("/")) {
					path = path + "/";
				}
				response.sendRedirect(path + redirecturl); //302重定向
				return;
			}
		} else {
			chain.doFilter(request, response);
		}
	}

	public void init(FilterConfig config) throws ServletException {
		this.exclude = config.getInitParameter("exclude");
		this.redirecturl = config.getInitParameter("redirecturl");
	}

}

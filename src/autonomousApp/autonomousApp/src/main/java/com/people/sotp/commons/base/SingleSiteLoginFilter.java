package com.people.sotp.commons.base;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.people.sotp.commons.util.RandomNumUtil;
import com.people.sotp.dataobject.MemberDO;

public class SingleSiteLoginFilter implements Filter {

	public static final String SINGLE_SITE_LOGIN = "singleSiteLogin";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		Map<Integer, String> sessions = new HashMap<Integer, String>();
		filterConfig.getServletContext().setAttribute(SINGLE_SITE_LOGIN, sessions);
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		@SuppressWarnings("unchecked")
		Map<Integer, String> sessions = (Map<Integer, String>) request.getServletContext()
				.getAttribute(SINGLE_SITE_LOGIN);
		MemberDO member = (MemberDO) request.getSession().getAttribute("member");
		Cookie[] tokens = request.getCookies();
		Cookie uniq = null; // 这个cookie存储用户的唯一标识符，用于单点登录的验证。
		if (tokens != null) {
			for (Cookie cookie : tokens) {
				if (cookie.getName().equals("uniq")) {
					uniq = cookie;
					break;
				}
			}
		}

		String url = request.getRequestURI();
		url = url.substring(url.lastIndexOf("/") + 1, url.length());

		if (member == null || (uniq != null && sessions.get(member.getId()).equals(uniq.getValue()) )
				|| url.equals("userauth.do")) {
			if (member == null) {
				String random = RandomNumUtil.getRandomString(10);
				Cookie responseCookie = new Cookie("uniq", random);
				responseCookie.setPath("/");
				request.setAttribute("cookie", responseCookie);
			}
			chain.doFilter(request, response);
		} else {
			member = null;
			String responseText = utilJson("logout", GlobalParam.ForceLogout, "{}", "该帐号已在其他地方登录！请检查帐号是否安全!");
			PrintWriter printWriter = response.getWriter();
			printWriter.write(responseText);
			System.out.println(responseText);
			request.getSession().invalidate();
			printWriter.flush();
			printWriter.close();
		}

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	public String utilJson(String type, int status, String data, String errorMsg) {
		StringBuilder jsondata = new StringBuilder();
		jsondata.append("{\"service\":\"" + type + "\",\"status\":" + status + ",");
		jsondata.append("\"data\":" + data + "");
		jsondata.append(",\"errorMsg\":\"" + errorMsg + "\"}");
		return jsondata.toString();
	}
}

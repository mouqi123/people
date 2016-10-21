package com.peopleNet.sotp.security;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 
 * A manager for the CSRF token for a given session. The
 * {@link #getTokenForSession(HttpSession)} should used to
 * 
 * obtain the token value for the current session (and this should be the only
 * way to obtain the token value).
 * 
 * referer:http://www.cnblogs.com/Mainz/archive/2012/11/01/2749874.html
 * 
 ***/

public final class CSRFTokenManager {

	/**
	 *
	 * The location on the session which stores the token
	 *
	 */

	public static final String CSRF_TOKEN_FOR_SESSION_ATTR_NAME = CSRFTokenManager.class

	.getName() + ".tokenval";
	/**
	 *
	 * The token parameter name
	 *
	 */

	static final String CSRF_PARAM_NAME = "CSRFToken";

	private CSRFTokenManager() {

	}

	public static String getTokenForSession(HttpSession session) {

		String token = null;

		// I cannot allow more than one token on a session - in the case of two

		// requests trying to

		// init the token concurrently

		synchronized (session) {

			token = (String) session

			.getAttribute(CSRF_TOKEN_FOR_SESSION_ATTR_NAME);

			if (null == token) {

				token = UUID.randomUUID().toString();

				session.setAttribute(CSRF_TOKEN_FOR_SESSION_ATTR_NAME, token);

			}

		}

		return token;

	}

	/**
	 *
	 * Extracts the token value from the session
	 *
	 *
	 *
	 * @param request
	 *
	 * @return
	 *
	 */

	public static String getTokenFromRequest(HttpServletRequest request) {

		return request.getParameter(CSRF_PARAM_NAME);

	};

}
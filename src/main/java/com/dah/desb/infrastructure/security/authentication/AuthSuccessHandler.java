package com.dah.desb.infrastructure.security.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

public class AuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	public final Integer SESSION_TIMEOUT_IN_SECONDS = 60 * 60;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
		HttpSession session = request.getSession();
		// username
		String username = authentication.getName();
		session.setAttribute("username", username);
		// sessionTimeout
		session.setMaxInactiveInterval(SESSION_TIMEOUT_IN_SECONDS);
		// savedRequest
		super.onAuthenticationSuccess(request, response, authentication);
	}

}

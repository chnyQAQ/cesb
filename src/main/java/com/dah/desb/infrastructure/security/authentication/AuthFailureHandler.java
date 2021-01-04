package com.dah.desb.infrastructure.security.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

public class AuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	public static final String FLASH_USERNAME_ATTRIBUTE = "flash_username";
	public static final String FLASH_LOGIN_ERROR_ATTRIBUTE = "flash_login_error";

	private static final Logger logger = LoggerFactory.getLogger(AuthFailureHandler.class);

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		HttpSession session = request.getSession();
		session.setAttribute(FLASH_USERNAME_ATTRIBUTE, request.getParameter("username"));
		if (exception instanceof UsernameNotFoundException) {
			session.setAttribute(FLASH_LOGIN_ERROR_ATTRIBUTE, "用户名或密码错误，请重试！");
		} else if (exception instanceof BadCredentialsException) {
			session.setAttribute(FLASH_LOGIN_ERROR_ATTRIBUTE, "用户名或密码错误，请重试！");
		} else if (exception instanceof DisabledException) {
			session.setAttribute(FLASH_LOGIN_ERROR_ATTRIBUTE, "账户已禁用，请联系管理员！");
		} else if (exception instanceof InternalAuthenticationServiceException) {
			logger.error("认证服务异常", exception);
			session.setAttribute(FLASH_LOGIN_ERROR_ATTRIBUTE, "认证服务异常，请联系管理员！");
		} else {
			logger.error("认证失败", exception);
			session.setAttribute(FLASH_LOGIN_ERROR_ATTRIBUTE, "系统异常，请联系管理员！");
		}
		super.onAuthenticationFailure(request, response, exception);
	}

}

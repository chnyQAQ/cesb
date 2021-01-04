package com.dah.desb.controller;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import com.dah.desb.infrastructure.security.authentication.AuthFailureHandler;

@Controller
public class LoginController {

	@GetMapping(value = "/login/password", produces="html/text")
	public String getLoginPasswordPage(Principal principal, HttpServletRequest request, Model model) {
		if (principal != null) {
			return "redirect:/";
		} else {
			retrieveAndUpdateFlash(request);
			return "/view/login/login-password";
		}
	}

	private void retrieveAndUpdateFlash(HttpServletRequest request) {
		HttpSession session = request.getSession();
		// flash_username
		String flash_username = (String) session.getAttribute(AuthFailureHandler.FLASH_USERNAME_ATTRIBUTE);
		if (!StringUtils.isEmpty(flash_username)) {
			request.setAttribute("username", flash_username);
			session.removeAttribute(AuthFailureHandler.FLASH_USERNAME_ATTRIBUTE);
		}
		// flash_login_error
		String flash_login_error = (String) session.getAttribute(AuthFailureHandler.FLASH_LOGIN_ERROR_ATTRIBUTE);
		if (!StringUtils.isEmpty(flash_login_error)) {
			request.setAttribute("login_error", flash_login_error);
			session.removeAttribute(AuthFailureHandler.FLASH_LOGIN_ERROR_ATTRIBUTE);
		}
	}

}

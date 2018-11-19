package org.memoryextension.java.samples.examMaker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AuthController {

	private static final Logger log = LoggerFactory.getLogger( AuthController.class);
	
	  // for auth
    @RequestMapping(value = "/login")
	public String login() {
		return "login";
	}

	@RequestMapping("/login-error")
	public String loginError(Model model) {
		model.addAttribute("loginError", true);
		return "login";
	}
	
    @RequestMapping(value = "/logout")
	public String logout(Model model) {
    	model.addAttribute("LogoutMessage", true);
		return "login";
	}

    @RequestMapping(value = "/403")
	public String accessDenied(Model model) {
    	model.addAttribute("noAccess", true);
    	return "login";
    }
	
}

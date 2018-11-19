package org.memoryextension.java.samples.examMaker;

import org.memoryextension.java.samples.examMaker.security.FirstUrlOnSuccessfullAuthHandler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class RootController {

	@RequestMapping("/")
	public String defaultRoute(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return "redirect:"+FirstUrlOnSuccessfullAuthHandler.determineTargetUrl(auth);

	}
}

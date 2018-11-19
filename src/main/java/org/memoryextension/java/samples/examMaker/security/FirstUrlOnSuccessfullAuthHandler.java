package org.memoryextension.java.samples.examMaker.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


// taken from http://www.baeldung.com/spring_redirect_after_login
public class FirstUrlOnSuccessfullAuthHandler implements AuthenticationSuccessHandler {

	private static final Logger log = LoggerFactory.getLogger(FirstUrlOnSuccessfullAuthHandler.class);

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication auth) throws IOException,
			ServletException {
		  
        handle(request, response, auth);
        clearAuthenticationAttributes(request);
		
	}

	protected void handle(HttpServletRequest request, 
		      HttpServletResponse response, Authentication authentication)
		      throws IOException {
		  
		        String targetUrl = determineTargetUrl(authentication);
		 
		        if (response.isCommitted()) {
		            log.debug(
		              "Response has already been committed. Unable to redirect to "
		              + targetUrl);
		            return;
		        }
		 
		        redirectStrategy.sendRedirect(request, response, targetUrl);
		    }
	
	public static String determineTargetUrl(Authentication auth) {
		log.error(auth.getName());
		for(String s:AuthCheck.getRoles(auth) ) {
			log.error(s);
		}
		if(AuthCheck.userIsUser(auth)) {
			log.error("It is a user --> exam");
			return "/exam/";
		}
		// default to admin
    	return "/admin/";
    }
	
	 protected void clearAuthenticationAttributes(HttpServletRequest request) {
	        HttpSession session = request.getSession(false);
	        if (session == null) {
	            return;
	        }
	        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
	    }
	 
	    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
	        this.redirectStrategy = redirectStrategy;
	    }
	    protected RedirectStrategy getRedirectStrategy() {
	        return redirectStrategy;
	    }
	
}
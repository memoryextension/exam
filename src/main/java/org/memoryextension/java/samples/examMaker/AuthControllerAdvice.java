package org.memoryextension.java.samples.examMaker;

/* this exposes various info to the templates
 *  e.g. name, role, cluster
 */

import java.util.Collection;

import org.memoryextension.java.samples.examMaker.security.AuthCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class AuthControllerAdvice {

	private static final Logger log = LoggerFactory.getLogger(AuthControllerAdvice.class);
	
	@Value("${software.version}")
    private String softwareVersion;
	
	@ModelAttribute("username")
	public String getUsername(Authentication auth) {
		if (auth == null) {	return "";}
		return auth.getName();
	}
	
	
	public String formatRoles(Collection<? extends GrantedAuthority> authorities){
		String role=null;
		for (GrantedAuthority authority : authorities) {
			if(authority.getAuthority().toString().equals("ROLE_user") && (role==null)) {role="Simple user";}
			if(authority.getAuthority().toString().equals("ROLE_admin")) {role="GLOBAL super Admin";}
		}
		return role;
	}
	
	
	@ModelAttribute("user_roles")
	public String getRoles(Authentication auth) {
		if (auth == null) {	return ""; }
		// here we format the roles a bit
		return formatRoles(auth.getAuthorities());
	}
	
	@ModelAttribute("userMode")
	public Boolean getUserMode(Authentication auth) {
		if (auth == null) {	return false; }
		if(AuthCheck.userIsUser(auth)) {
			return true;
		}
		return false;
	}
	
	@ModelAttribute("adminMode")
	public Boolean getAdminMode(Authentication auth) {
		if (auth == null) {	return false; }
		if(AuthCheck.userIsSuperAdmin(auth)) {
			return true;
		}
		return false;
	}
	

	@ModelAttribute("userMode")
	public Boolean getClientMode(Authentication auth) {
		if (auth == null) {	return false; }
		if(AuthCheck.userIsUser(auth) ) {
			return true;
		}
		return false;
	}
	

	@ModelAttribute("stagingFlag")
	public Boolean getStagingFlag() {
		if(softwareVersion.endsWith("staging")) {
			return true;
		}
		return false;
	}
	
	@ModelAttribute("softwareVersion")
	public String getSoftwareVersion() {
		return softwareVersion;
	}
	
	
	
}

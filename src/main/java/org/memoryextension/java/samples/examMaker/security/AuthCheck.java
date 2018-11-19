package org.memoryextension.java.samples.examMaker.security;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class AuthCheck {
	
	public static boolean userHasNoRole(Authentication auth){return hasRole(auth,"ROLE_NONE");}
	public static boolean userIsUser(Authentication auth){ return hasRole(auth,"ROLE_user");}
	public static boolean userIsSuperAdmin(Authentication auth){ return hasRole(auth,"ROLE_admin");}
	
	private static boolean hasRole(Authentication auth,String role) {
		  @SuppressWarnings("unchecked")
		Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) auth.getAuthorities();
		  
		  for (GrantedAuthority authority : authorities) {
		     if(authority.getAuthority().toString().equals(role)){
		    	 return true;
		    }
		  }
		  return false;
		}
 
 public static String[] getRoles(Authentication auth){
	   @SuppressWarnings("unchecked")
	Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) auth.getAuthorities();
	   String[] result = new String[authorities.size()];
	   int index=0;
	   for (GrantedAuthority authority : authorities) {
		   result[index]=authority.getAuthority().toString();
		   index++;
	   }
	   return result;
 }
 
}

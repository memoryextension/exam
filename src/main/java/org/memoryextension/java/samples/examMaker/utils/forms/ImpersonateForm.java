package org.memoryextension.java.samples.examMaker.utils.forms;

import java.util.List;

import org.memoryextension.java.samples.examMaker.pojos.UserRole;

public class ImpersonateForm {
	private String login;
	private List<UserRole> roles;
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public List<UserRole> getRoles() {
		return roles;
	}
	public void setRoles(List<UserRole> roles) {
		this.roles = roles;
	}
	
	
	
	

}

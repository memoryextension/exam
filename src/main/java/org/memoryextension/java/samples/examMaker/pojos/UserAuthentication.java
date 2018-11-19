package org.memoryextension.java.samples.examMaker.pojos;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
public class UserAuthentication {

	@Id
	@NotEmpty(message = "Need a unique login, not matching a uniteID")
	@Size(min=1,max = 255,message = "Login has wrong size")
	private String login;
	
	@NotEmpty
	@Size(min=12,max = 64,message = "password wrong size")
	private String password;

	public UserAuthentication() {}
	
	public UserAuthentication(String login,String clearPassword) {
		PasswordEncoder pwdEncoder = new BCryptPasswordEncoder(); // default is 10 rounds
		this.login = login;
		this.password = pwdEncoder.encode(clearPassword);
	}
	
	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setClearPassword(String clearPassword) {
		PasswordEncoder pwdEncoder = new BCryptPasswordEncoder(); // default is 10 rounds
		this.password = pwdEncoder.encode(clearPassword);
	}
	
	
	
}

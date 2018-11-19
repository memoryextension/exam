package org.memoryextension.java.samples.examMaker.pojos;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Table;
import org.memoryextension.java.samples.examMaker.validators.UserRoleCheck;


@Entity
public class AppUser {

	@Id
	@GeneratedValue
	private Long id;
	
	@NotEmpty(message = "Need a login")
	@Size(min=1,max = 255,message = "Login invalid")
	private String login;
	
	@NotEmpty
	@Size(min=1,max = 255,message = "Display Name invalid")
	private String displayName;
	
	@Email(message="Invalid email")
	@NotEmpty(message = "Please provide an e-mail")
	@Column(unique = true)
	private String email;
	
	// see validators.UserRoleValidator class to implement validation for userRoles (@@UserRoleCheck used below)
	@ManyToMany(fetch = FetchType.EAGER)
	private List<UserRole> userRoles;
	
	@Transient
	@Size(min=12,max = 64,message = "Minimum size is 12, max size is 64")
	private String clearPassword;
	
	public Long getId(){ return id;}
	public void setId(Long id) {this.id=id;}

	public String getLogin() {return login;}
	

	public void setLogin(String login) {
		this.login = login.toLowerCase();
	}
	
	public List<UserRole> getRoles() {
		return userRoles;
	}
	public void setRoles(List<UserRole> userRoles) {
		this.userRoles = userRoles;
	} 
	
	public void addRole(UserRole r){
		if(this.userRoles==null){
			this.userRoles=new ArrayList<UserRole>();
		}
		userRoles.add(r);
	}
	
	//
	@UserRoleCheck(clientRole="client") // custom validator
	public String getAllRoles(){
		StringBuilder result=new StringBuilder();
		for(UserRole r: this.userRoles){
			if(result.length()>0) {result.append(", ");}
			result.append(r.getName());
		}
		return result.toString();
	}
	
	public String getDisplayName() {
		if((displayName==null)|| displayName.isEmpty()) {
			return login;
		}
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public AppUser(){}
	
	public AppUser(String login){
		if(login!=null){this.setLogin(login);}
	}
	
	public AppUser(String login,String displayName){
		if(login!=null){this.setLogin(login);}
		if(displayName!=null){this.setDisplayName(displayName);}
	}
	
	public String getClearPassword() {
		return clearPassword;
	}
	public void setClearPassword(String clearPassword) {
		this.clearPassword = clearPassword;
	}


	
}
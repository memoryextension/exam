package org.memoryextension.java.samples.examMaker.validators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// custom-ish validation
// per http://codetutr.com/2013/05/29/custom-spring-mvc-validation-annotations/
// and https://docs.jboss.org/hibernate/validator/5.0/reference/en-US/html/validator-customconstraints.html
public class UserRoleValidator implements ConstraintValidator<UserRoleCheck, String> {

	//private static final Logger log = LoggerFactory.getLogger(UserRoleValidator.class);
	
	private String clientRole;
	private List<String> client_sub_roles; 
	
	@Override
	public void initialize(UserRoleCheck arg0) {
		this.clientRole = arg0.clientRole();
		
	}

	
	
	@Override
	public boolean isValid(String roles, ConstraintValidatorContext constraintContext) {
		
		if(roles==null || roles.trim().isEmpty()){
			constraintContext.disableDefaultConstraintViolation();
			constraintContext.buildConstraintViolationWithTemplate("At least one role is needed")
            .addConstraintViolation();
			return false;
		}

		
		// target is a single string, with comma separated roles.
		List<String> tentative_roles= Arrays.asList(roles.toLowerCase().trim().split("\\s*,\\s*"));
		
		// either admin OR user
		if(!tentative_roles.contains("user") &&  !tentative_roles.contains("admin") ) {
				constraintContext.disableDefaultConstraintViolation();
				constraintContext.buildConstraintViolationWithTemplate("At least 'admin' or 'user' role is needed")
				.addConstraintViolation();
				return false;
		}
		


		return true;
	}

}

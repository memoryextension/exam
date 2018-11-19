package org.memoryextension.java.samples.examMaker.security;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.memoryextension.java.samples.examMaker.pojos.AppUser;
import org.memoryextension.java.samples.examMaker.pojos.UserAuthentication;
import org.memoryextension.java.samples.examMaker.pojos.UserRole;
import org.memoryextension.java.samples.examMaker.repositories.AuthenticationRepository;
import org.memoryextension.java.samples.examMaker.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
public class DatabaseAuthoritiesProvider implements AuthenticationProvider {

	private static final Logger log = LoggerFactory.getLogger(DatabaseAuthoritiesProvider.class);
	
	@Autowired
	private AuthenticationRepository authRepo;
	
	@Autowired
	private UserRepository userRepo;
	
	@Override
	public Authentication authenticate(Authentication auth) throws AuthenticationException {
		// load user, compare password
		
		PasswordEncoder pwdEncoder = new BCryptPasswordEncoder(); // default is 10 rounds
		final String login = auth.getName();
        final String password = auth.getCredentials().toString();
		
        UserAuthentication user = authRepo.findByLogin(login);
        
        if(user!=null && pwdEncoder.matches(password, user.getPassword()) ) {
        	AppUser appUser = userRepo.findByLogin(login.toLowerCase());
        	Collection<GrantedAuthority> gas = new HashSet<GrantedAuthority>();
        	
        	if(appUser!=null){
        		// the roles
				for(UserRole r: appUser.getRoles() ){
					gas.add(new SimpleGrantedAuthority("ROLE_"+r.getName()));
				}
        	} else {
        		log.error("USER "+login+" NOT FOUND --> no auth");
				gas.add(new SimpleGrantedAuthority("NONE"));
        	}
        	
        	return new UsernamePasswordAuthenticationToken(login, password, gas);
        } else {
        	throw new BadCredentialsException("External system authentication failed");
        }
	}

	@Override
	public boolean supports(Class<?> auth) {
		return auth.equals(UsernamePasswordAuthenticationToken.class);
	}

}

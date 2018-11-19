package org.memoryextension.java.samples.examMaker.security;


import org.memoryextension.java.samples.examMaker.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

// references https://kielczewski.eu/2014/12/spring-boot-security-application/
// and https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#multiple-httpsecurity

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

		@Configuration
		@Order(1) 
		public static class H2Console extends WebSecurityConfigurerAdapter {
			
			protected void configure(HttpSecurity http) throws Exception {
	            http
	            	.antMatcher("/h2-console/**").
	            		authorizeRequests().anyRequest().hasRole("admin");
	            
	            http.csrf().disable();
	            http.headers().frameOptions().disable();
				}

		}



		@Configuration
		@Order(2)
		public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
			@Autowired
			private UserRepository repo;

			@Autowired
			DatabaseAuthoritiesProvider databaseAuthoritiesProvider;

			AuthenticationSuccessHandler successHandlerBean =  new FirstUrlOnSuccessfullAuthHandler();


			@Override
			protected void configure(HttpSecurity http) throws Exception {
				http
				.authorizeRequests()
				 .antMatchers("/css/**","/img/**","/js/**","/login*").permitAll() // everyone can access, including anonymous
				 .antMatchers("/","/logout").fullyAuthenticated() // not anonymous
				 .antMatchers("/admin/**").hasRole("admin")
				 .antMatchers("/exam/**").hasRole("user")
				 .anyRequest().authenticated()
				.and()
					.formLogin()
			          .successHandler(successHandlerBean)
					.loginPage("/login")
					.failureUrl("/login-error")
				.and()
					.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
						.deleteCookies("JSESSIONID")
				.and()
				 .exceptionHandling().accessDeniedPage("/403");
			}

			@Autowired
			public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

				// DB
				auth
				.authenticationProvider(databaseAuthoritiesProvider);

			}
		}
		
		
	}

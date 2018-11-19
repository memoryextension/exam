package org.memoryextension.java.samples.examMaker.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class HardcodedAuthorizationToken extends UsernamePasswordAuthenticationToken {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2935916051674946231L;

	public HardcodedAuthorizationToken( String username, String password) {
        super( username, password );
    }
}

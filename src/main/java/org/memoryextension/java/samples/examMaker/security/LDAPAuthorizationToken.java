package org.memoryextension.java.samples.examMaker.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;


public class LDAPAuthorizationToken extends AbstractAuthenticationToken {
	private static final long serialVersionUID = -1810592462269432266L;
	private String token;
    
	public LDAPAuthorizationToken( String token ) {
        super( null );
        this.token = token;
    }

    public Object getCredentials() {
        return token;
    }

    public Object getPrincipal() {
        return null;
    }
}

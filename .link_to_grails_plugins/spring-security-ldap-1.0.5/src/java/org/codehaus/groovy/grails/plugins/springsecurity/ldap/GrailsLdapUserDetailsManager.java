package org.codehaus.groovy.grails.plugins.springsecurity.ldap;

import org.codehaus.groovy.grails.plugins.springsecurity.GrailsUserDetailsService;
import org.springframework.dao.DataAccessException;
import org.springframework.ldap.core.ContextSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.userdetails.LdapUserDetailsManager;

/**
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
public class GrailsLdapUserDetailsManager extends LdapUserDetailsManager implements GrailsUserDetailsService {

	/**
	 * Constructor.
	 * @param contextSource the context source
	 */
	public GrailsLdapUserDetailsManager(final ContextSource contextSource) {
		super(contextSource);
	}

	/**
	 * {@inheritDoc}
	 * @see org.codehaus.groovy.grails.plugins.springsecurity.GrailsUserDetailsService#loadUserByUsername(
	 * 	java.lang.String, boolean)
	 */
	public UserDetails loadUserByUsername(final String username, final boolean loadRoles)
			throws UsernameNotFoundException, DataAccessException {
		return super.loadUserByUsername(username);
	}
}

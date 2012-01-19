package de.fhdortmund.UsrHlpDsk.core

import java.util.Collection;

import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;


class FHDOUserDetailsContextMapper implements UserDetailsContextMapper {

	@Override
	public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection authorities) {
	
		String fullname =  ctx.originalAttrs.attrs['fullname'].values[0]
		String email = ctx.originalAttrs.attrs['mail'].values[0].toString().toLowerCase()
		String commonname = ctx.originalAttrs.attrs['cn'].values[0].toString().toLowerCase()
		String matr = ctx.originalAttrs.attrs['idmedustudentnumber'].values[0]
//		def title = ctx.originalAttrs.attrs['title']

		new FHDOUserDetails(commonname, 'notused', true, true, true, true,
				authorities, fullname, email, matr) 
	}
	@Override
	public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
		throw new IllegalStateException("Only retrieving data from AD is currently supported")
	}
}

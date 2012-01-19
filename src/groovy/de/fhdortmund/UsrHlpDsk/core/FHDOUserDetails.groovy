package de.fhdortmund.UsrHlpDsk.core

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User;

class FHDOUserDetails extends User{
	// extra instance variables
	final String fullname
	final String email
	final String matr

	FHDOUserDetails(String username, 
					String password, 
					boolean enabled, 
					boolean accountNonExpired,
					boolean credentialsNonExpired, 
					boolean accountNonLocked,
					Collection<GrantedAuthority> authorities, 
					String fullname,
					String email,
					String matr) 
	{
		super(username, password, enabled, accountNonExpired, credentialsNonExpired,
		accountNonLocked, authorities)

		this.fullname = fullname
		this.email = email
		this.matr = matr
	}
}

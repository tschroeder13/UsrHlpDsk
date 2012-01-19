package de.fhdortmund.ldap.directory;

import java.util.Hashtable;

import javax.naming.Context;

public class FHDOConnector {
	
	private String bindUser;
	private String binPW;
	private String idmZvdServiceOU = "ou=service,o=FHDO";
	private String idmPersonContainer = "ou=personen,ou=Vault,o=FHDO";
	
	Hashtable<String, String> idmEnv;

	
	
	public FHDOConnector() {
		initIDMContext(bindUser, binPW, idmZvdServiceOU);
	}
	
	private void initIDMContext(String username, String passwd, String ou){
		idmEnv= new Hashtable<String, String>();
		idmEnv.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
		idmEnv.put("java.naming.ldap.factory.socket", "de.fhdortmund.ldap.directory.MySSLSocketFactory");
		idmEnv.put(Context.SECURITY_PROTOCOL,"ssl");
		idmEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
		idmEnv.put(Context.PROVIDER_URL,"ldaps://idm.fh-dortmund.de:636/");
		idmEnv.put(Context.SECURITY_PRINCIPAL, "CN=" + username + ou); 
		idmEnv.put(Context.SECURITY_CREDENTIALS,passwd);

	}
	
	

}

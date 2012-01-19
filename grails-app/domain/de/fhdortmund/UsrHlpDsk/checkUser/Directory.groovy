package de.fhdortmund.UsrHlpDsk.checkUser

import javax.naming.Context;
import javax.naming.directory.InitialDirContext;

import org.springframework.ldap.core.LdapTemplate
import org.springframework.ldap.core.support.LdapContextSource

class Directory {
	String name
	String url
	int port
	boolean ssl
	String baseDN
	int weight
	Context context
	
	def setContext(Context ctx){
		this.context = ctx
	}
	
	def getContext(){
		return this.context
	}
	
	static transients = ['context']
	
	
	static hasMany = [queries: Query]
	
    static constraints = {
		name(blank:false)
		name(unique:true)
		url(blank:false)
		url(url: true)
		port(nullable:false)
		baseDN(blank:false)
		weight(range:1..100)
		weight(unique:true)
    }
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name;
	}
	
}

package de.fhdortmund.UsrHlpDsk.core

import java.awt.image.renderable.ContextualRenderedImageFactory;
import java.util.Hashtable
import javax.naming.*
import javax.naming.event.*
import javax.naming.directory.*
class JndiLdapTest {

	public static void main(String[] args) {


		def ou = "service"

		// here, we're just reading in some parameters...
		def stdin = new BufferedReader(
				new InputStreamReader(System.in)
				)
		print "username: " // your username to access LDAP... not a real user
		String username = stdin.readLine()
		print "password: " // the password for that account...
		String passwd = stdin.readLine()

		// next we set up an environment for LDAP
		Hashtable env = new Hashtable()
		env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory")
		env.put(Context.SECURITY_PROTOCOL,"ssl")

		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put("java.naming.ldap.factory.socket", "de.fhdortmund.ldap.directory.MySSLSocketFactory");

		// You'll want to put your server's name or IP here:
		env.put(Context.PROVIDER_URL,"ldaps://idm.fh-dortmund.de:636/")

		// Here, you'll want to set up your company's parameters for OU and DC...
		env.put(Context.SECURITY_PRINCIPAL, "CN=" + username + ",OU=" + ou + ",o=FHDO") 
		env.put(Context.SECURITY_CREDENTIALS,passwd)

		// Now we'll try and connect...
		try {
			def ctx = new InitialDirContext(env)
			def attr = ctx.getAttributes("")
			def srchInfo = new SearchControls()
			// you'll want to use your own search base
			def searchBase= "OU=personen,ou=Vault,o=FHDO"
			// this is the magic search string sauce:
			def searchFilter = "(&(objectClass=inetOrgPerson))"
			// we strong type the next var because it is
			// used in a Java API that needs String[]
			String[] objAttribs=[
				"givenName",
				"cn",
				"sn",
				"mail"
			]
			srchInfo.setSearchScope(SearchControls.SUBTREE_SCOPE)
			srchInfo.setReturningAttributes(objAttribs)

			// Now we get the results and loop through them...
			NamingEnumeration dirObjects = ctx.search(searchBase,searchFilter,srchInfo)
			def nodirObjects = 0
			while (dirObjects != null && dirObjects.hasMoreElements()) {
				def dirObject = dirObjects.next()
				println("'" + dirObject.getName() + "':")
				def attrs = dirObject.getAttributes()
				for(name in objAttribs) {
					println "\t * " + attrs.get(name)
				}
				nodirObjects++
			}
			ctx.close()
			println("Number of entries identified: " + nodirObjects)
		} catch (Exception e) {
			println "Exception: " + e
		}

	}
}

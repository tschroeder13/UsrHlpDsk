package de.fhdortmund.UsrHlpDsk.core
import javax.naming.directory.InitialDirContext

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

class LogoutController {

	/**
	 * Index action. Redirects to the Spring security logout uri.
	 */
	def index = {
		// TODO put any pre-logout code here
		def directoryMap = session.getAttribute('sessionDirectories')
		directoryMap.each { key, value ->
			InitialDirContext ctx = value
			ctx.close()
			println "Closed Session for ${key}"
		}
		redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl // '/j_spring_security_logout'
	}
}

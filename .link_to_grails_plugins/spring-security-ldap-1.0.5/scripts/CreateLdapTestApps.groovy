/* Copyright 2006-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Creates test applications for functional tests.
 *
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */

includeTargets << grailsScript('_GrailsBootstrap')

functionalTestPluginVersion = '1.2.7'
appName = null
grailsHome = null
dotGrails = null
projectDir = null
pluginVersion = null
pluginZip = null
testprojectRoot = null
deleteAll = false

target(createLdapTestApps: 'Creates LDAP test apps') {

	def configFile = new File(basedir, 'testapps.config.groovy')
	if (!configFile.exists()) {
		error "$configFile.path not found"
	}

	new ConfigSlurper().parse(configFile.text).each { name, config ->
		echo "\nCreating app based on configuration $name: ${config.flatten()}\n"
		init name, config
		createApp()
		installPlugins()
		runQuickstart()
		createProjectFiles()
	}
}

private void init(String name, config) {

	pluginVersion = config.pluginVersion
	if (!pluginVersion) {
		error "pluginVersion wasn't specified for config '$name'"
	}

	pluginZip = new File(basedir, "grails-spring-security-ldap-${pluginVersion}.zip")
	if (!pluginZip.exists()) {
		error "plugin $pluginZip.absolutePath not found"
	}

	grailsHome = config.grailsHome
	if (!new File(grailsHome).exists()) {
		error "Grails home $grailsHome not found"
	}

	projectDir = config.projectDir
	appName = 'spring-security-ldap-test-' + name
	testprojectRoot = "$projectDir/$appName"
	dotGrails = config.dotGrails
}

private void createApp() {

	ant.mkdir dir: projectDir

	deleteDir testprojectRoot
	deleteDir "$dotGrails/projects/$appName"

	callGrails(grailsHome, projectDir, 'dev', 'create-app') {
		ant.arg value: appName
	}
}

private void installPlugins() {

	// install plugins in local dir to make optional STS setup easier
	// also configure the functional tests to run in order
	new File("$testprojectRoot/grails-app/conf/BuildConfig.groovy").withWriterAppend {
		it.writeLine 'grails.project.plugins.dir = "plugins"'
		it.writeLine 'grails.testing.patterns = ["Person1Functional", "Person2Functional", "Person3Functional"]'
	}

	ant.mkdir dir: "${testprojectRoot}/plugins"

	callGrails(grailsHome, testprojectRoot, 'dev', 'install-plugin') {
		ant.arg value: "functional-test ${functionalTestPluginVersion}"
	}

	callGrails(grailsHome, testprojectRoot, 'dev', 'install-plugin') {
		ant.arg value: "ldap-server"
	}

	callGrails(grailsHome, testprojectRoot, 'dev', 'install-plugin') {
		ant.arg value: pluginZip.absolutePath
	}
}

private void runQuickstart() {
	callGrails(grailsHome, testprojectRoot, 'dev', 's2-quickstart') {
		ant.arg value: 'com.testldap'
		ant.arg value: 'User'
		ant.arg value: 'Role'
	}
	callGrails(grailsHome, testprojectRoot, 'dev', 's2-create-persistent-token') {
		ant.arg value: 'com.testldap.PersistentLogin'
	}
}

private void createProjectFiles() {
	String source = "$basedir/webtest/projectfiles"

	ant.copy file: "$source/classpath", tofile: "$testprojectRoot/.classpath", overwrite: true

	ant.copy file: "$source/SecureController.groovy",
	         todir: "$testprojectRoot/grails-app/controllers", overwrite: true

	ant.copy file: "$source/BootStrap.groovy",
	         todir: "$testprojectRoot/grails-app/conf", overwrite: true

	ant.copy(todir: "$testprojectRoot/test/functional", overwrite: true) {
		fileset dir: "$basedir/webtest", includes: "*Test*.groovy"
	}

	ant.mkdir dir: "$testprojectRoot/grails-app/ldap-servers/d1/data"
	ant.copy file: "$source/users.ldif", todir: "$testprojectRoot/grails-app/ldap-servers/d1/data", overwrite: true

	new File("$testprojectRoot/grails-app/conf/Config.groovy").withWriterAppend {
		it.writeLine "grails.plugins.springsecurity.ldap.context.managerDn = 'uid=admin,ou=system'"
		it.writeLine "grails.plugins.springsecurity.ldap.context.managerPassword = 'secret'"
		it.writeLine "grails.plugins.springsecurity.ldap.context.server = 'ldap://localhost:10389'"
		it.writeLine "grails.plugins.springsecurity.ldap.authorities.groupSearchFilter = 'uniquemember={0}'"
		it.writeLine "grails.plugins.springsecurity.ldap.authorities.groupSearchBase = 'ou=groups,dc=d1,dc=example,dc=com'"
		it.writeLine "grails.plugins.springsecurity.ldap.authorities.retrieveDatabaseRoles = true"
		it.writeLine "grails.plugins.springsecurity.ldap.search.base = 'dc=d1,dc=example,dc=com'"
		it.writeLine "grails.plugins.springsecurity.ldap.search.filter = '(uid={0})'"
		it.writeLine "grails.plugins.springsecurity.password.algorithm = 'SHA-256'"
		it.writeLine "ldapServers {"
		it.writeLine "   d1 {"
		it.writeLine "      base = 'dc=d1,dc=example,dc=com'"
		it.writeLine "      port = 10389"
		it.writeLine "      indexed = ['objectClass', 'uid', 'mail', 'userPassword', 'description']"
		it.writeLine "   }"
		it.writeLine "}"
		it.writeLine "grails.plugins.springsecurity.ldap.useRememberMe = true"
		it.writeLine "grails.plugins.springsecurity.ldap.rememberMe.detailsManager.groupSearchBase = 'ou=groups,dc=d1,dc=example,dc=com'"
		it.writeLine "grails.plugins.springsecurity.ldap.rememberMe.detailsManager.groupRoleAttributeName = 'cn'"
		it.writeLine "grails.plugins.springsecurity.ldap.rememberMe.usernameMapper.userDnBase = 'dc=d1,dc=example,dc=com'"
		it.writeLine "grails.plugins.springsecurity.ldap.rememberMe.usernameMapper.usernameAttribute = 'cn'"
	}
}

private void deleteDir(String path) {
	if (new File(path).exists() && !deleteAll) {
		String code = "confirm.delete.$path"
		ant.input message: "$path exists, ok to delete?", addproperty: code, validargs: 'y,n,a'
		def result = ant.antProject.properties[code]
		if ('a'.equalsIgnoreCase(result)) {
			deleteAll = true
		}
		else if (!'y'.equalsIgnoreCase(result)) {
			ant.echo "\nNot deleting $path"
			exit 1
		}
	}

	ant.delete dir: path
}

private void error(String message) {
	ant.echo "\nERROR: $message"
	exit 1
}

private void callGrails(String grailsHome, String dir, String env, String action, extraArgs = null) {
	ant.exec(executable: "${grailsHome}/bin/grails", dir: dir, failonerror: 'true') {
		ant.env key: 'GRAILS_HOME', value: grailsHome
		ant.arg value: env
		ant.arg value: action
		extraArgs?.call()
	}
}

setDefaultTarget 'createLdapTestApps'

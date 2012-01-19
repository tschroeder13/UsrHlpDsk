/*
 * Copyright 2011 the original author or authors
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
class SvnGrailsPlugin {
    def version = "1.0.0.M1"
    def grailsVersion = "1.1 > *"
    def dependsOn = [:]

    def author = "Peter Ledbrook"
    def authorEmail = "pledbrook@vmware.com"
    def title = "Subversion Plugin"
    def description = '''\
Provides SVNKit as a dependency; an SvnClient class that makes it easier to work with Subversion; and integration with the Release plugin.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/svn"

    def license = "APACHE"
    def organization = [ name: "SpringSource", url: "http://www.springsource.org/" ]
    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GRAILSPLUGINS" ]
    def scm = [ url: "https://github.com/grails-plugins/grails-svn" ]
}

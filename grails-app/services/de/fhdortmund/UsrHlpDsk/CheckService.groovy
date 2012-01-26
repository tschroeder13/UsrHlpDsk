package de.fhdortmund.UsrHlpDsk

import groovy.text.SimpleTemplateEngine
import org.codehaus.groovy.grails.plugins.springsecurity.SecurityRequestHolder  as SRH;

import javax.naming.NamingEnumeration
import javax.naming.directory.Attribute
import javax.naming.directory.DirContext
import javax.naming.directory.InitialDirContext
import javax.naming.directory.SearchControls
import javax.naming.directory.SearchResult;

import de.fhdortmund.UsrHlpDsk.checkUser.Check
import de.fhdortmund.UsrHlpDsk.checkUser.DirectoryController;
import de.fhdortmund.UsrHlpDsk.checkUser.Query
import de.fhdortmund.UsrHlpDsk.util.CheckParserUtil

class CheckService {
	static scope = "session"

	def String searchAttribute
	def String searchValue

	/* Singleton */
	def private static INSTANCE
	def static CheckService getInstance(){
		if (INSTANCE == null) {
			INSTANCE = new CheckService()
		}
		return INSTANCE
	}
	/* Singleton */

	def runCheck(String searchAttr, String searchValue, String checkTitle){
		def check = Check.findByTitle(checkTitle)
		runCheck(searchAttr, searchValue, check, false)
	}
	
	def devCheck(String searchAttr, String searchValue, String checkTitle){
		def check = Check.findByTitle(checkTitle)
		runCheck(searchAttr, searchValue, check, true)
	}
	
	def runCheck(String searchAttr, String searchValue, Check check){
		runCheck(searchAttr, searchValue, check, false)
	}

	def runCheck(String searchAttribute, String searchValue, Check check, boolean devel) {
		this.searchAttribute = searchAttribute
		this.searchValue = searchValue

		def queryResult = []
		check.queries.each {Query qu ->
			def tmp = Query.get(qu.id)
			queryResult.add runQuery(tmp)
		}


		def Binding binding = new Binding(input: queryResult, errorMsg: check.getInstruction())
		if(devel) {
			return binding
		}else if(!devel){
			def shell = new GroovyShell(binding)
			def checkResult = shell.evaluate(check.checkBody)
			check.setResult(checkResult)
			return checkResult
		}
	}

	def runQuery(Query query){

		def Map<String, InitialDirContext> contexts = SRH.request.getSession().getAttribute(DirectoryController.SESSION_VARIABLE_NAME)
		def dirName = query.directory.name
		def ctx = contexts.get(dirName)

		def retAttr = query.getReturnAttr()
		def SearchControls sc = new SearchControls()
		sc.setReturningAttributes(retAttr)
		sc.setSearchScope(SearchControls.SUBTREE_SCOPE)

		def searchAttr = whichAttr(dirName, searchAttribute)

		def binder = ["searchAttr":searchAttr, "searchVal":this.searchValue]
		def engine = new SimpleTemplateEngine()
		def filter = engine.createTemplate(query.filter).make(binder).toString()
		def baseDN = query.getBaseDN()
		DirContext dctx = ctx.lookup("")
		def NamingEnumeration searchResult = dctx.search(baseDN, filter, sc)

		def tmp = buildSingleResult(searchResult)
		dctx.close()
		return [query.title, tmp[0], tmp[1]]
	}

	def private buildSingleResult(NamingEnumeration<SearchResult> searchResult){
		def name
		def attrMap = [:]
		// wenns jetzt keine mehr hat, dann wurde nix gefunden
		if (!searchResult.hasMore()) {
			return [
				-1,
				[errorMsg:'No Results Found!']
			]
		}

		while (searchResult.hasMore()) {
			SearchResult sr = searchResult.next();

			name = sr.getName()
			NamingEnumeration<Attribute> ae = sr.getAttributes().getAll()
			while (ae.hasMore()) {
				Attribute attribute = (Attribute) ae.next();
				attrMap.put(attribute.getID(), attribute.get())
			}
			// TODO Wenn du die ModalBox installierst, kannst du dann bei mehreren resultaten eine Auswahl machen?
			// dann ist das hier OBSOLET
			if (searchResult.hasMore()) {
				return [
					-1,
					'Not a unique search result'
				]
			}// Kein eindeutiges Ergebnis
		}
		return [name, attrMap]
	}

	def private whichAttr(String directory, String searchAttr){
		switch (directory) {
			case "IDM":
				switch (searchAttr) {
					case "FHKennung":
					return "cn"
					break;
					case "Matrikelnr.":
					return "idmEduStudentNumber"
					break;
					case "S7-Kennung":
					return "idmEduInitialIdentifier"
					break;
					default:
					break;
				}
				break;
			case "ZVD":
				switch (searchAttr) {
					case "FHKennung":
					return "cn"
					break;
					case "Matrikelnr.":
					return "employeeNumber"
					break;
					case "S7-Kennung":
					return "uid"
					break;
					default:
					break;
				}
				break
			case "CIP":
				switch (searchAttr) {
					case "FHKennung":
					return "cn"
					break;
					case "Matrikelnr.":
					return "employeeNumber"
					break;
					case "S7-Kennung":
					return "uid"
					break;
					default:
					break;
				}
			default:
				break;
		}
	}
	def help = {
		println 'Implicit variables include: \n\n ' + 
		'ctx: the Spring application context \n' +
		'grailsApplication: the Grails application \n'+
		'config: the Grails configuration \n' +
		'request: the HTTP request \n' +
		'session: the HTTP session \n' +
		'chkSrv: the CheckService. Use its help() method to repeat this message \n' 

	}
}

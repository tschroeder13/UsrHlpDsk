package de.fhdortmund.UsrHlpDsk.checkUser

import groovy.text.SimpleTemplateEngine

import javax.naming.NamingEnumeration
import javax.naming.directory.DirContext
import javax.naming.directory.InitialDirContext
import javax.naming.directory.SearchControls

import de.fhdortmund.UsrHlpDsk.util.CheckParserUtil


class ReviewController {

	static scaffold = Review
	def index = {}

	def runAllChecks = {
		def model = Review.get(1)

		
		def runResult = []

		def String searchValue = params.searchFor
		def String searchAttribute = params.searchIn
		def sessionVar = DirectoryController.SESSION_VARIABLE_NAME
		def Map<String, InitialDirContext> contexts = session.getAttribute(sessionVar)

		def res
		//		def checks = Check.findAll(sort: 'weight', order: 'desc')
		def checks = model.checks
		checks.each(){ Check check ->

			def ArrayList queryResult = []

			check.queries.each{Query query ->
				
				def dirName = query.directory.name
				def ctx = contexts.get(dirName)
				
				def retAttr = query.getReturnAttr()
				def SearchControls sc = new SearchControls()
				sc.setReturningAttributes(retAttr)
				sc.setSearchScope(SearchControls.SUBTREE_SCOPE)

				def searchAttr = CheckParserUtil.whichAttr(dirName, searchAttribute)

				def binder = ["searchAttr":searchAttr, "searchVal":searchValue]
				def engine = new SimpleTemplateEngine()
				def filter = engine.createTemplate(query.filter).make(binder).toString()
				def baseDN = query.getBaseDN()
				DirContext dctx = ctx.lookup("")
				def NamingEnumeration searchResult = dctx.search(baseDN, filter, sc)

				def tmp = CheckParserUtil.buildSingleResult(searchResult)
				if (tmp[0] == -1) {
					// TODO im Moment muss ich davon ausgehen, das ich genau ein ergebnis in der Liste "match" habe
					// wenn ModalBox installiert ist und funktioniert, könnte man da evtl eine nachauswahl machen.
					//					queryResult.add(tmp)
					flash.message = tmp[1]
//					redirect(controller: 'home', action: 'index')
					runResult.add(tmp)
					return [user: searchValue, attr: searchAttribute, result: runResult]
					
				}else{
					queryResult.add( [query.title, tmp[0], tmp[1]])
				}
				dctx.close()
			}//each query
			def Binding binding = new Binding(input: queryResult, errorMsg: check.getInstruction())
//			def Binding binding = new Binding(input: queryResult, errorMsg: check.getInstruction(), result: [])
			def shell = new GroovyShell(binding)
			def checkResult = shell.evaluate(check.checkBody)
			check.setResult(checkResult)
			runResult.add(checkResult);
		}//each check
		return [user: searchValue, attr: searchAttribute, result: runResult]
	}

}

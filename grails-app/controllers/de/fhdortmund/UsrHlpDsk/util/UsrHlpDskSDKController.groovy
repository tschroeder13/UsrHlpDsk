package de.fhdortmund.UsrHlpDsk.util

import groovy.text.SimpleTemplateEngine

import javax.naming.NamingEnumeration
import javax.naming.directory.DirContext
import javax.naming.directory.InitialDirContext
import javax.naming.directory.SearchControls

import de.fhdortmund.UsrHlpDsk.checkUser.Check
import de.fhdortmund.UsrHlpDsk.checkUser.DirectoryController
import de.fhdortmund.UsrHlpDsk.checkUser.Query

class UsrHlpDskSDKController {
	def static UsrHlpDskSDKController INSTANCE
	// Singleton
	def static getInstance = {
		if (INSTANCE == null) {
			return new UsrHlpDskSDKController()
		}
		return INSTANCE
	}
	
	def runQueriesForCheck(String searchAttr, String searchValue, String checkTitle){
		def check = Check.findByTitle(checkTitle)
		def queries = []
		check.queries.each {Query qu ->
			queries.add(Query.get(qu.id))
		}
		
		return runQueries(searchAttr, searchValue, queries)
	}
	
	def runQueries(String searchAttribute, String searchValue, ArrayList queries){
		def sessionVar = DirectoryController.SESSION_VARIABLE_NAME
		def Map<String, InitialDirContext> contexts = session.getAttribute(sessionVar)
		def queryResults = []
		queries.each{Query query ->
			
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
			queryResults.add( [query.title, tmp[0], tmp[1]])
			dctx.close()
		}//each query
		return queryResults
	}
}

package de.fhdortmund.UsrHlpDsk.checkUser

import groovy.text.SimpleTemplateEngine

import javax.naming.NamingEnumeration
import javax.naming.directory.DirContext
import javax.naming.directory.InitialDirContext
import javax.naming.directory.SearchControls

import de.fhdortmund.UsrHlpDsk.CheckService;
import de.fhdortmund.UsrHlpDsk.util.CheckParserUtil


class ReviewController {

	static scaffold = Review
	def index = {}

	
	def runAllChecks = {
		def model = Review.findByName('default')
		
		def runResult = []

		def String searchValue = params.searchFor
		def String searchAttribute = params.searchIn

		def checks = model.checks
		checks.each(){ Check check ->
			def tmp = CheckService.getInstance().runCheck(searchAttribute, searchValue, check)
			
			runResult.add(tmp)
		}//each check
		return [user: searchValue, attr: searchAttribute, result: runResult]
	}

}

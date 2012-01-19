package de.fhdortmund.UsrHlpDsk.util

import javax.naming.NamingEnumeration
import javax.naming.directory.Attribute
import javax.naming.directory.SearchResult

import de.fhdortmund.UsrHlpDsk.checkUser.Directory;


class CheckParserUtil {

	//	def static run(String searchIn, String searchVal,  Check check){
	//		def Context ctx
	//		def results = [:]
	//
	//		def result = 0
	//		def queries = check.queries
	//
	//
	//		def searchRes
	//		queries.each{query ->
	//			ctx = getContextForQuery(query)
	//
	//			def SearchControls sc = new SearchControls()
	//			sc.setReturningAttributes(query.getReturnAttr())
	//			sc.setSearchScope(SearchControls.SUBTREE_SCOPE)
	//
	//			def searchAttr = whichAttr(searchIn)
	//
	//			def binder = ["searchAttr":searchAttr, "searchVal":searchVal]
	//			def engine = new SimpleTemplateEngine()
	//			def filter = engine.createTemplate(query.filter).make(binder)
	//
	//			NamingEnumeration queryResult = ctx.search(query.getBaseDN(), filter.toString(), sc);
	//
	//			def tmp = buildSingleResult(queryResult)
	//			result = [query.title, tmp[0], tmp[1]]
	//			ctx.close()
	//
	//		}//each query
	//
	//
	//
	//		def Binding binding = new Binding(result: result, check: check)// mach mal sinnvolle Parameter draus
	//		def shell = new GroovyShell(binding)
	//		result = shell.evaluate(check.getCheckBody())
	//		return result
	//	}

	def static buildSingleResult(NamingEnumeration<SearchResult> searchResult){
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
			}
		}
		return [name, attrMap]
	}

	def static whichAttr(String directory, String searchAttr){
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

}

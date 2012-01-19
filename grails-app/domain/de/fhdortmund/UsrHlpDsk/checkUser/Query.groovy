package de.fhdortmund.UsrHlpDsk.checkUser

class Query {
	
	String title
	String baseDN
	String filter
	String[] returnAttr 
	
	static belongsTo = [directory: Directory]
	
	
	static constraints = {
		title(blank:false)
		baseDN(blank:false)
		filter(blank:false)
	}
	
	
	
	public String toString() {
		return title;
	}
	
}

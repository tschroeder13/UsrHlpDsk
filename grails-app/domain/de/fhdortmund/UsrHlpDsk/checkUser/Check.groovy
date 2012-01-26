package de.fhdortmund.UsrHlpDsk.checkUser

import javax.swing.text.html.StyleSheet.SearchBuffer;

class Check {

	String title
	String checkBody
	String instruction
	int weight
	List result
	
	static hasMany = [queries: Query]
	
	static transients = ['result']
	List<Object> getResult(){
		result.toString()
	}
	void setResult(List<Object> res){
		result = res
	}


//	static belongsTo = [review: Review]
		
	static constraints = {
		title(blank:false)
		checkBody(blank:false)
		checkBody(widget: 'textarea')
		instruction(blank:false)
		weight(range:1..100)
	}

	static mapping = { 
		table 'checking'
		checkBody type: 'text'
	}
	
	public String toString() {
		return title;
	}
}

package de.fhdortmund.UsrHlpDsk.checkUser

class Review {
	
	String name
	
	static hasMany = [checks: Check]

    static constraints = {
		name(blank:false)
    }
	
	String toString(){
		return name
	}
}

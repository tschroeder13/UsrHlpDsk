package de.fhdortmund.UsrHlpDsk.core

class DSRole {

	String authority

	static mapping = {
		cache true
	}

	static constraints = {
		authority blank: false, unique: true
	}
}

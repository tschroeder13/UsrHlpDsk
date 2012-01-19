package de.fhdortmund.UsrHlpDsk.core

import org.apache.commons.lang.builder.HashCodeBuilder

class DSUserDSRole implements Serializable {

	DSUser DSUser
	DSRole DSRole

	boolean equals(other) {
		if (!(other instanceof DSUserDSRole)) {
			return false
		}

		other.DSUser?.id == DSUser?.id &&
			other.DSRole?.id == DSRole?.id
	}

	int hashCode() {
		def builder = new HashCodeBuilder()
		if (DSUser) builder.append(DSUser.id)
		if (DSRole) builder.append(DSRole.id)
		builder.toHashCode()
	}

	static DSUserDSRole get(long DSUserId, long DSRoleId) {
		find 'from DSUserDSRole where DSUser.id=:DSUserId and DSRole.id=:DSRoleId',
			[DSUserId: DSUserId, DSRoleId: DSRoleId]
	}

	static DSUserDSRole create(DSUser DSUser, DSRole DSRole, boolean flush = false) {
		new DSUserDSRole(DSUser: DSUser, DSRole: DSRole).save(flush: flush, insert: true)
	}

	static boolean remove(DSUser DSUser, DSRole DSRole, boolean flush = false) {
		DSUserDSRole instance = DSUserDSRole.findByDSUserAndDSRole(DSUser, DSRole)
		if (!instance) {
			return false
		}

		instance.delete(flush: flush)
		true
	}

	static void removeAll(DSUser DSUser) {
		executeUpdate 'DELETE FROM DSUserDSRole WHERE DSUser=:DSUser', [DSUser: DSUser]
	}

	static void removeAll(DSRole DSRole) {
		executeUpdate 'DELETE FROM DSUserDSRole WHERE DSRole=:DSRole', [DSRole: DSRole]
	}

	static mapping = {
		id composite: ['DSRole', 'DSUser']
		version false
	}
}

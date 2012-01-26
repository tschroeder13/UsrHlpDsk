dataSource {
	pooled = true
	driverClassName = "org.h2.Driver"
	username = "sa"
	password = ""
}
hibernate {
	cache.use_second_level_cache = true
	cache.use_query_cache = true
	cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory'
}
// environment specific settings
environments {
	development {
		dataSource {
			dbCreate = "create-drop" // one of 'create', 'create-drop', 'update', 'validate', ''
			url = "jdbc:h2:mem:devDb;MVCC=TRUE"
//			dialect = org.hibernate.dialect.MySQL5Dialect.class
		}
	}
	test {
		dataSource {
			dbCreate = "update"
			url = "jdbc:h2:mem:testDb;MVCC=TRUE"
		}
	}
	production {
		dataSource {
			pooled = true
			dbCreate = "update"
			url = "jdbc:mysql://localhost/usrHlpDsk"
			driverClassName = "com.mysql.jdbc.Driver"
			dialect = org.hibernate.dialect.MySQL5InnoDBDialect.class
			username = 'root'
			password = 'o4fh-dots'
		}
	}
}

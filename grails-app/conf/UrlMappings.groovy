class UrlMappings {

	static mappings = {
		
		"/"(controller:'review',action:'index')
		
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/admin"(view:"/index")
		"500"(view:'/error')
	}
}

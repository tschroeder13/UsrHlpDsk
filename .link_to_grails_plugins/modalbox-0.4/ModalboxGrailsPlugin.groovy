
class ModalboxGrailsPlugin {
    def version = 0.4
    def dependsOn = [:]
    def author = "Alexander Koehn"
    def authorEmail = "alibasta@gmail.com"
    def title = "This plugin adds the ModalBox to your Grails applications."
    def description = '''\
    ModalBox is a JavaScript technique for creating modern (Web 2.0-style) 
    modal dialogs or even wizards (sequences of dialogs) without using 
    conventional popups and page reloads. It's inspired by Mac OS X modal 
    dialogs. And yes, it may also be useful for showing larger versions of images. :)
    http://www.wildbit.com/labs/modalbox/
    '''
	
    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }
   
    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)		
    }

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional)
    }
	                                      
    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }
	
    def onChange = { event ->
        // TODO Implement code that is executed when this class plugin class is changed  
        // the event contains: event.application and event.applicationContext objects
    }
                                                                                  
    def onApplicationChange = { event ->
        // TODO Implement code that is executed when any class in a GrailsApplication changes
        // the event contain: event.source, event.application and event.applicationContext objects
    }
}

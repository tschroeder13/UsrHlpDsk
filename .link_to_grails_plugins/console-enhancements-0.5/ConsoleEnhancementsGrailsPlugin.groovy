import uk.co.desirableobjects.console.enhancements.ConsoleEnhancer
import uk.co.desirableobjects.console.enhancements.ANSICode
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import grails.util.GrailsUtil

class ConsoleEnhancementsGrailsPlugin {
    // the plugin version
    def version = "0.5"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.3.1 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def author = "Antony Jones (Desirable Objects)"
    def authorEmail = "aj@desirableobjects.co.uk"
    def title = "Grails console enhancements"
    def description = 'Enhances the grails console output for better visibility'

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/grails-console-enhancements"

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before 
    }

    def doWithSpring = {

        def config = ConfigurationHolder.config
        def cl = new GroovyClassLoader(getClass().classLoader)
        def cs = new ConfigSlurper(GrailsUtil.environment)
        config.merge(cs.parse(cl.loadClass('ConsoleEnhancementsConfig')))

    }

    def doWithDynamicMethods = { ctx ->

        ConsoleEnhancer.enhanceConsole()

    }

    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }
}

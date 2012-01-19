package uk.co.desirableobjects.console.enhancements

import static uk.co.desirableobjects.console.enhancements.ANSICode.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH

class ConsoleEnhancer {

    static void enhanceConsole() {

        stylePrintLns()

    }

    private static void stylePrintLns() {

        Object.metaClass.println = { ANSICode first = null, ANSICode second = null, ANSICode third = null, def message ->

            List<ANSICode> sequence = [first, second, third].findAll { ANSICode code -> code != null } as List<ANSICode>
            sequence = sequence ?: (CH.config.console?.colours?.normal ?: [FOREGROUND_CYAN, BOLD_ON])
            Object.metaClass.&println new ANSISequence(sequence).format(message.toString())

        }

    }

}

package uk.co.desirableobjects.console.enhancements

import static uk.co.desirableobjects.console.enhancements.ANSIConstants.*
import static uk.co.desirableobjects.console.enhancements.ANSICode.RESET

class ANSISequence {

    private List<ANSICode> sequence

    public ANSISequence(List<ANSICode> codes) {
        sequence = codes
    }

    public String format(String string) {
        String before = ANSI_ESCAPE+sequence.join(ANSI_ESCAPE)
        String after = ANSI_ESCAPE+RESET
        return before+string+after
    }

}

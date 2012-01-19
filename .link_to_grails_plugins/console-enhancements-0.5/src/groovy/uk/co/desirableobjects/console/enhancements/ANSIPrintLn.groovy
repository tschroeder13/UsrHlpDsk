package uk.co.desirableobjects.console.enhancements

class ANSIPrintLn {

    def methodMissing(String name, args) {
        List<ANSICode> colourName = [ANSICode.valueOf(ANSICode.class, "FOREGROUND_${name.toUpperCase()}")]
        String output = new ANSISequence(colourName).format(args[0])
        return output
    }

}

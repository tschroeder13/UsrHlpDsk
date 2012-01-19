package uk.co.desirableobjects.console.enhancements
enum ANSICode {

    NONE(null),
    RESET('[0m'),
    BOLD_ON('[1m'),
    ITALICS_ON('[3m'),
    UNDERLINE_ON('[4m'),
    INVERSE_ON('[7m'),
    STRIKETHROUGH_ON('[9m'),
    BOLD_OFF('[22m'),
    ITALICS_OFF('[23m'),
    UNDERLINE_OFF('[24m'),
    INVERSE_OFF('[27m'),
    STRIKETHROUGH_OFF('[29m'),
    FOREGROUND_BLACK('[30m'),
    FOREGROUND_RED('[31m'),
    FOREGROUND_GREEN('[32m'),
    FOREGROUND_YELLOW('[33m'),
    FOREGROUND_BLUE('[34m'),
    FOREGROUND_MAGENTA('[35m'),
    FOREGROUND_CYAN('[36m'),
    FOREGROUND_WHITE('[37m'),
    FOREGROUND_DEFAULT('[39m'),
    BACKGROUND_BLACK('[40m'),
    BACKGROUND_RED('[41m'),
    BACKGROUND_GREEN('[42m'),
    BACKGROUND_YELLOW('[43m'),
    BACKGROUND_BLUE('[44m'),
    BACKGROUND_MAGENTA('[45m'),
    BACKGROUND_CYAN('[46m'),
    BACKGROUND_WHITE('[47m'),
    BACKGROUND_RESET('[49m')

    private String sequence

    private ANSICode(String sequence) {
        this.sequence = sequence
    }

    @Override
    String toString() {
        return sequence
    }

}

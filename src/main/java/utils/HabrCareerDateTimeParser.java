package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A class for a realization of DateTimeParser interface
 *
 * @author Alena Ageeva
 */
public class HabrCareerDateTimeParser implements DateTimeParser {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    /**
     * The method converts the original data, given as strings, into LocalDateTime objects
     * that a program can further work with them as a date type.
     *
     * @param parse A String for converting
     * @return LocalDateTime object
     */
    @Override
    public LocalDateTime parse(String parse) {
        return LocalDateTime.parse(parse, FORMATTER);
    }
}

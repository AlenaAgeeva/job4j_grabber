package utils;

import java.time.LocalDateTime;

/**
 * An interface with a method for converting date from a format of career.habr.com.
 *
 * @author Alena Ageeva
 */
public interface DateTimeParser {
    LocalDateTime parse(String parse);
}

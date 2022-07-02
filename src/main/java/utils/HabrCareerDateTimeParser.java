package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HabrCareerDateTimeParser implements DateTimeParser {
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public LocalDateTime parse(String parse) {
        String date = parse.split("T")[0]
                .concat(" ")
                .concat(parse.split("T")[1]
                        .split("\\+")[0]);
        return LocalDateTime.parse(date, formatter);
    }
}

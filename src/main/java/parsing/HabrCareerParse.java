package parsing;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.DateTimeParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A class represent itself a simple Html parser using Jsoup lib.
 *
 * @author Alena Ageeva
 */
public class HabrCareerParse implements Parse {

    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);
    private static final int COUNT = 5;
    private static DateTimeParser dateTimeParser;

    /**
     * A constructor with arguments
     *
     * @param dateTimeParser an argument using for initialization of dateTimeParser.
     */
    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    /**
     * A method parses a description of a vacancy on the html page.
     *
     * @param link a link for parsing
     * @return String object
     */
    private String retrieveDescription(String link) {
        Connection connection = Jsoup.connect(link);
        Element rows = null;
        try {
            rows = connection.get().selectFirst(".style-ugc");
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
        return rows.text();
    }

    /**
     * A method parses html pages and put results to a list for further work.
     * Elements of the list are Post objects that will be stored and transferred to a database.
     *
     * @param link link a link for parsing
     * @return list
     */
    @Override
    public List<Post> list(String link) {
        List<Post> list = new ArrayList<>();
        for (int i = 1; i <= COUNT; i++) {
            try {
                Connection connection = Jsoup.connect(link + i);
                Document document = connection.get();
                Elements rows = document.select(".vacancy-card__inner");
                rows.forEach(row -> list.add(getPost(row)));
            } catch (IOException e) {
                throw new IllegalArgumentException();
            }
        }
        return list;
    }

    /**
     * A method parses a single vacancy page with extraction of title, link, description and posting date.
     * In addition, it creates a Post object with all retrieved data as a result for return.
     *
     * @param row an Element for parsing data
     * @return a Post object
     */
    private Post getPost(Element row) {
        Element titleElement = row.select(".vacancy-card__title").first();
        Element linkElement = titleElement.child(0);
        String date = row.selectFirst(".basic-date").attr("datetime");
        String vacancyName = titleElement.text();
        String link = linkElement.attr("abs:href");
        return new Post(
                vacancyName,
                link,
                retrieveDescription(link),
                dateTimeParser.parse(date));
    }
}


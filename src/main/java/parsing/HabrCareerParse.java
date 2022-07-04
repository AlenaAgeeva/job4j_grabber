package parsing;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.DateTimeParser;
import utils.HabrCareerDateTimeParser;

import java.io.IOException;
import java.util.*;

public class HabrCareerParse implements Parse {

    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);
    private static final int COUNT = 5;
    private static DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    private String retrieveDescription(String link) throws IOException {
        Connection connection = Jsoup.connect(link);
        Elements rows = connection.get().select(".section-box");
        return rows.select(".collapsible-description__content").first().text();
    }

    @Override
    public List<Post> list(String l) {
        List<Post> list = new ArrayList<>();
        for (int i = 1; i <= COUNT; i++) {
            try {
                Connection connection = Jsoup.connect(PAGE_LINK + "?page=" + i);
                Document document = null;
                try {
                    document = connection.get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Elements rows = document.select(".vacancy-card__inner");
                rows.forEach(row -> {
                    Post post = null;
                    try {
                        post = getPost(row);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    list.add(post);
                });
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    private Post getPost(Element row) throws IOException {
        Element titleElement = row.select(".vacancy-card__title").first();
        Element linkElement = titleElement.child(0);
        String date = row.selectFirst(".basic-date").attr("datetime");
        String vacancyName = titleElement.text();
        String link = linkElement.attr("abs:href");
        return new Post(
                vacancyName,
                link,
                new HabrCareerParse(new HabrCareerDateTimeParser()).retrieveDescription(link),
                dateTimeParser.parse(date));
    }
}


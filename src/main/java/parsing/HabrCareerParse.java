package parsing;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class HabrCareerParse {

    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);

    private String retrieveDescription(String link) throws IOException {
        Connection connection = Jsoup.connect(link);
        Elements rows = connection.get().select(".section-box");
        return rows.select(".job_show_description__vacancy_description").first().text();
    }

    public static void main(String[] args) throws IOException {

        for (int i = 1; i <= 5; i++) {
            Connection connection = Jsoup.connect(PAGE_LINK + "?page=" + i);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                String date = row.selectFirst(".basic-date").attr("datetime");
                String vacancyName = titleElement.text();
                String link = linkElement.attr("abs:href");
                System.out.printf("%s %s %s%n", vacancyName, link, date);
            });
        }
    }
}


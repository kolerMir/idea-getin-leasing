package ai.makeitright.utilities.crawler;

import ai.makeitright.utilities.DriverConfig;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;

public class WebDriverCrawler extends DriverConfig {

    public static ArrayList<String> crawl() {
        String uri = System.getProperty("inputParameters.startPage");
        driver.navigate().to(uri);
        ArrayList<String> arrayListOfPageSources = new ArrayList<>();
        while (webElementExists(By.xpath("//div[@id='listing-desktop']//div[@class='pagination']//a[@href]//img[@src='/imgs/pag-arrow-right.png']"))) {
            arrayListOfPageSources.add(driver.getPageSource());
            driver.findElement(By.xpath("//div[@id='listing-desktop']//div[@class='pagination']//a[@href]//img[@src='/imgs/pag-arrow-right.png']")).click();
        }
        arrayListOfPageSources.add(driver.getPageSource());

        List<String> hrefsOfAllAuctions = scrapePartialLinksToAuctionsDetials(arrayListOfPageSources,
                "div.single-auction div.left.picture a:nth-child(1)");
        ArrayList<String> urlsOfSpecificAuctions = new ArrayList<>();
        for (String hrefOfAuction : hrefsOfAllAuctions) {
            urlsOfSpecificAuctions.add("https://aukcje.ideagetin.pl" + hrefOfAuction);
        }
        return urlsOfSpecificAuctions;
    }


    private static List<String> scrapePartialLinksToAuctionsDetials(final ArrayList<String> htmlPagesAsString,
                                                                    final String selectForAElement) {
        List<String> finalListOfPartialLinks = new ArrayList<>();
        for (String htmlPageAsString : htmlPagesAsString) {
            Document parsedHtmlPage = Jsoup.parse(htmlPageAsString);
            Elements aTags = parsedHtmlPage.select(selectForAElement);
            List<String> temporarylistOfPartialLinks = aTags.eachAttr("href");
            finalListOfPartialLinks.addAll(temporarylistOfPartialLinks);
        }
        return finalListOfPartialLinks;
    }

    public static boolean webElementExists(final By by) {
        return !driver.findElements(by).isEmpty();
    }

}

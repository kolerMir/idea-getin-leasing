package ai.makeitright.webdrivertests;

import ai.makeitright.utilities.DriverConfig;
import ai.makeitright.utilities.db.AuctionData;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

import static ai.makeitright.utilities.crawler.WebDriverCrawler.crawl;
import static ai.makeitright.utilities.db.DBConnector.getAuctionsToUpdate;
import static ai.makeitright.utilities.db.DBConnector.sendScrappedAuctionDatas;
import static ai.makeitright.utilities.scraper.WebDriverScrapper.scrape;
import static ai.makeitright.utilities.xlsx.XlsxCreator.convertArrayOfAuctionDatasToExcelFile;


public class GetinTest extends DriverConfig {

    @Test
    public void doTest() throws InterruptedException, IOException, SQLException, ParseException {
        boolean scrapeAllAuctions = Boolean.parseBoolean(System.getProperty("inputParameters.scrapeAllAuctions"));
        boolean scrapeFavouritedAuctions = Boolean.parseBoolean(System.getProperty("inputParameters.scrapeFavouritedAuctions"));

        long start = System.currentTimeMillis();
        ArrayList<String> urlsToScrape = new ArrayList<>();
        if (scrapeAllAuctions) {
            urlsToScrape = crawl();
        }
        if (scrapeFavouritedAuctions) {
            urlsToScrape = getAuctionsToUpdate();
        }
        ArrayList<AuctionData> auctionDatas = scrape(urlsToScrape);
        convertArrayOfAuctionDatasToExcelFile(auctionDatas, System.getProperty("inputParameters.title"));
        sendScrappedAuctionDatas(auctionDatas);
        long stop = System.currentTimeMillis();
        System.out.println("Czas:  " + (stop - start));
    }

}
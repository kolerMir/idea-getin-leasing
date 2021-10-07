package ai.makeitright.utilities.scraper;

import ai.makeitright.utilities.DriverConfig;
import ai.makeitright.utilities.db.AuctionData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;

import java.text.ParseException;
import java.util.ArrayList;

import static ai.makeitright.utilities.db.AuctionData.crateAuctionDataObjectFromJSoupDocument;

public class WebDriverScrapper extends DriverConfig {

    public static ArrayList<AuctionData> scrape(final ArrayList<String> urlsOfAllAuctions) throws InterruptedException, ParseException {
        System.out.println("------------------------------------------------------");
        System.out.println("Quantity of urls of all auctions: " + urlsOfAllAuctions.size());
        System.out.println("");
        ArrayList<AuctionData> auctionDatas = new ArrayList<>();
        for (String urlOfAuction : urlsOfAllAuctions) {
            System.out.println("Downloading url: " + urlOfAuction);
            Thread.sleep(500);

            driver.navigate().to(urlOfAuction);

            Document document = scrapeAuctionContent();
            AuctionData ad = crateAuctionDataObjectFromJSoupDocument(document, urlOfAuction);
            auctionDatas.add(ad);
        }
        return auctionDatas;
    }

    /**
     * USE WITH CAUTION!
     * don't download pdf files if not required
     */
    static void downloadPdfFile(final String aTagXpath) {
        System.setProperty("inputParameters.downloadFiles", "false");
        if (System.getProperty("inputParameters.downloadFiles").equals("true")) {
            String pdfUrl = driver.findElement(By.xpath(aTagXpath)).getAttribute("href");
            driver.navigate().to(pdfUrl);
        }
    }

    private static Document scrapeAuctionContent() throws InterruptedException {
        Thread.sleep(2000);
        String pageSource = driver.getPageSource();
        return Jsoup.parse(pageSource);
    }
}
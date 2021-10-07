package ai.makeitright.utilities.crawler;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public final class Crawler {


    public static ArrayList<String> crawl() throws IOException, InterruptedException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, URISyntaxException {
        System.setProperty("jsse.enableSNIExtension", "false");
        String uriString = System.getProperty("inputParameters.startPage");
        URI uRI = new URI(uriString);
        CloseableHttpResponse response0 = getClosableHttpClient().execute(new HttpGet(uRI.toString()));
        HttpEntity entity = response0.getEntity();
        System.out.println("Status of GET to: " + uRI.toString() + " is " + response0.getStatusLine());

        Document parse = Jsoup.parse(EntityUtils.toString(entity, "UTF-8"));
        Elements asFrom1stPage = parse.select("div.right.options > a");
        Elements maxPages = parse.select("#listing-mobile > div.pagination > a > span");
        int maxPaging = 0;
        ArrayList<String> urlsOfSpecificAuctions = new ArrayList<>();
        ArrayList<String> hrefsOfAllAuctions = new ArrayList<>();
        for (Element elem : maxPages) {
            if (!elem.text().contains("...")) {
                maxPaging = Integer.parseInt(elem.text());
            }
        }
//        System.out.println("Quantity of pages with results: " + maxPaging);
//        System.out.println("Getting href values for auction from 1st page of search results");
        for (Element a : asFrom1stPage) {
            hrefsOfAllAuctions.add(a.attr("href"));
        }
        for (int i = 2; i <= maxPaging; i++) {
            Thread.sleep(300);
//            CloseableHttpResponse response1 = getClosableHttpClient().execute(new HttpGet("https://aukcje.ideagetin.pl/aukcje/pojazdy-samochodowe-i-motocykle/widok-lista/strona-" + i + "?subcategory=6"));
            String uriForHttpGet = "https://aukcje.ideagetin.pl/aukcje/pojazdy-samochodowe-i-motocykle/widok-lista/strona-" + i + "?"+ uRI.getQuery();
            CloseableHttpResponse response1 = getClosableHttpClient().execute(new HttpGet(uriForHttpGet));
//            System.out.println("Getting href values for auctions from " + i + " page of search results");
            HttpEntity httpEntity = response1.getEntity();
            Document document = Jsoup.parse(EntityUtils.toString(httpEntity, "UTF-8"));
            Elements asFrom2ndPageAndSoOn = document.select("div.right.options > a");
            for (Element a : asFrom2ndPageAndSoOn) {
                hrefsOfAllAuctions.add(a.attr("href"));
            }
        }
        for (String hrefOfAuction : hrefsOfAllAuctions) {
            urlsOfSpecificAuctions.add("https://aukcje.ideagetin.pl" + hrefOfAuction);
        }
        return urlsOfSpecificAuctions;
    }

    private static CloseableHttpClient getClosableHttpClient() throws
            KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        BasicCookieStore cookieStore = new BasicCookieStore();
        return HttpClients.custom()
                .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build())
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .setDefaultCookieStore(cookieStore)
                .build();
    }
}
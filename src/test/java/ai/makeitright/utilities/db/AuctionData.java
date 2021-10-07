package ai.makeitright.utilities.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DatabaseTable(tableName = "auctions")
public class AuctionData {
    @DatabaseField(id = true)
    private String id;
    @DatabaseField
    private String marka;
    @DatabaseField
    private String model;
    @DatabaseField
    private String rokProdukcji;
    @DatabaseField
    private String numerRejestracyjny;
    @DatabaseField
    private String vin;
    @DatabaseField
    private String rodzajPaliwa;
    @DatabaseField
    private String klasaEuro;
    @DatabaseField
    private String kluczyki;
    @DatabaseField
    private String dowodRejestracyjny;
    @DatabaseField
    private String kartaPojazdu;
    @DatabaseField
    private Long przebieg;
    @DatabaseField
    private Long cena;
    @DatabaseField
    private String pdfUrl;
    @DatabaseField
    private String wyposazenie;
    @DatabaseField
    private Timestamp dataWyszukania;
    @DatabaseField
    private Timestamp doKoncaAukcji;
    @DatabaseField
    private String zrodlo;
    @DatabaseField
    private String typAukcji;

    public static AuctionData crateAuctionDataObjectFromJSoupDocument(Document document, String urlOfAuction) throws ParseException {
        Element divAC = document.selectFirst("div.auction-content");
        AuctionData ad = new AuctionData();
        ad.setId(urlOfAuction);
        ad.setMarka(divAC.selectFirst(".single-box div.data-inner img[src='/imgs/extra-icons/marka.png']").parent().ownText());
        ad.setModel(divAC.selectFirst(".single-box div.data-inner img[src='/imgs/extra-icons/model.png']").parent().ownText());
        ad.setRokProdukcji(divAC.selectFirst(".single-box div.data-inner img[src='/imgs/extra-icons/data-produkcji.png']").parent().ownText());
        ad.setNumerRejestracyjny(divAC.selectFirst("div.left.boxes").getElementsContainingOwnText("Nr rejestracyjny:").first().parent().ownText());

        String vin;
        Elements spanWithVin = divAC.selectFirst("div.left.boxes").getElementsContainingOwnText("VIN:");
        if (!spanWithVin.isEmpty()) {
            vin = divAC.selectFirst("div.left.boxes").getElementsContainingOwnText("VIN:").first().parent().ownText();
        } else {
            vin = "";
        }
        ad.setVin(vin);

        if (divAC.selectFirst("div.left.boxes").getElementsContainingOwnText("Paliwo:").first() != null) {
            ad.setRodzajPaliwa(divAC.selectFirst("div.left.boxes").getElementsContainingOwnText("Paliwo:").first().parent().ownText());
        } else {
            ad.setRodzajPaliwa("");
        }
        String klasaEuro;
        Elements spanWithKlasaEuro = divAC.selectFirst("div.left.boxes").getElementsContainingOwnText("Klasa EURO:");
        if (!spanWithKlasaEuro.isEmpty()) {
            klasaEuro = divAC.selectFirst("div.left.boxes").getElementsContainingOwnText("Klasa EURO:").first().parent().ownText();
        } else {
            klasaEuro = "";
        }
        ad.setKlasaEuro(klasaEuro);

        String kluczyki;
        Elements spanWithKluczyki = divAC.selectFirst("div.left.boxes").getElementsContainingOwnText("Kluczyki:");
        if (!spanWithKluczyki.isEmpty()) {
            kluczyki = divAC.selectFirst("div.left.boxes").getElementsContainingOwnText("Kluczyki:").first().parent().ownText();
        } else {
            kluczyki = "";
        }
        ad.setKluczyki(kluczyki);

        String dowodRejestracyjny;
        Elements spanWithDowodRejestracyjny = divAC.selectFirst("div.left.boxes").getElementsContainingOwnText("Dowód rejestracyjny:");
        if (!spanWithDowodRejestracyjny.isEmpty()) {
            dowodRejestracyjny = divAC.selectFirst("div.left.boxes").getElementsContainingOwnText("Dowód rejestracyjny:").first().parent().ownText();
        } else {
            dowodRejestracyjny = "";
        }
        ad.setDowodRejestracyjny(dowodRejestracyjny);

        String kartaPojazdu;
        Elements spanKartaPojazdu = divAC.selectFirst("div.left.boxes").getElementsContainingOwnText("Karta pojazdu:");
        if (!spanKartaPojazdu.isEmpty()) {
            kartaPojazdu = divAC.selectFirst("div.left.boxes").getElementsContainingOwnText("Karta pojazdu:").first().parent().ownText();
        } else {
            kartaPojazdu = "";
        }
        ad.setKartaPojazdu(kartaPojazdu);

        String przebieg;
        Elements spanWithPrzebieg = divAC.selectFirst("div.left.boxes").getElementsContainingOwnText("Przebieg:");
        if (!spanWithPrzebieg.isEmpty()) {
            String przebiegWithKm = divAC.selectFirst("div.left.boxes").getElementsContainingOwnText("Przebieg:").first().parent().ownText();
            przebieg = przebiegWithKm.replace(" km", "");
        } else {
            przebieg = "0";
        }
        ad.setPrzebieg(Long.valueOf(przebieg));

        if (divAC.selectFirst("div.sliding-box div.price span.numbers.curr_price") != null) {
            ad.setCena(Long.valueOf(divAC.selectFirst("div.sliding-box div.price span.numbers.curr_price").ownText().replaceAll("\\s+", "")));
        } else {
            ad.setCena(0L);
        }

        String pdfUrl;
        if (divAC.selectFirst("div.auction-story-cnt.documents-cnt a") != null) {
            //todo wyciac potrzebna czesc adresu
            pdfUrl = "https://aukcje.ideagetin.pl" + divAC.selectFirst("div.auction-story-cnt.documents-cnt a").attr("href");
        } else {
            pdfUrl = "";
        }
        ad.setPdfUrl(pdfUrl);

        StringBuilder wyposazenie = new StringBuilder();

        Element someTagWithWyposazenie = divAC.selectFirst("div.single-box.quartet");
        if (someTagWithWyposazenie != null) {
            Element parentOfH2WithWyposazenie = divAC.selectFirst("div.single-box.quartet h2").getElementsContainingOwnText("Wyposa").first().parent();
            if (parentOfH2WithWyposazenie != null) {
                Elements wyposazenieList = parentOfH2WithWyposazenie.select("div.data-inner");
                if (wyposazenieList != null) {
                    for (Element item : wyposazenieList) {
                        wyposazenie.append(item.ownText());
                        wyposazenie.append(" ");
                    }
                }
                ad.setWyposazenie(wyposazenie.toString());
            }
        } else {
            ad.setWyposazenie("");
        }
        Timestamp dataWyszukaniaTimestamp = new Timestamp(System.currentTimeMillis());
        ad.setDataWyszukania(dataWyszukaniaTimestamp);
        if (divAC.selectFirst("div.auction-information p span") != null) {
            String doKoncaAukcji = divAC.selectFirst("div.auction-information p span").ownText();
            if (!doKoncaAukcji.equals("Trwa weryfikacja")) {
                Pattern pattern = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2}):(\\d{2})");
                Matcher matcher = pattern.matcher(doKoncaAukcji);
                String koniecAukcji = "";
                if (matcher.find()) {
                    koniecAukcji = matcher.group();
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date parsedDate = dateFormat.parse(koniecAukcji);
                Timestamp koniecAukcjiTimestamp = new Timestamp(parsedDate.getTime());
                ad.setDoKoncaAukcji(koniecAukcjiTimestamp);
            } else {
                ad.setDoKoncaAukcji(new Timestamp(0L));
            }
        }
        ad.setZrodlo(System.getProperty("inputParameters.title"));
        if (!document.select("#kup-teraz").isEmpty()) {
            ad.setTypAukcji("kup teraz");
        } else if (!document.select("#licytuj").isEmpty()) {
            ad.setTypAukcji("licytacja");
        }
        return ad;
    }

}
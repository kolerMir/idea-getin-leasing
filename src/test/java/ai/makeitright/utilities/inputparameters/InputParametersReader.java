package ai.makeitright.utilities.inputparameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InputParametersReader {

    public static ArrayList<String> getAuctionsPartialLinksFromJsonArrayOfInputParameter() {
        ArrayList<String> urlsOfFavouritedAuctions = new ArrayList<>();

        String auctions = System.getProperty("inputParameters.endingAuctions");
        String str[] = auctions.split(";");
        List<String> al = new ArrayList<>();
        al = Arrays.asList(str);
        for (String s : al) {
            urlsOfFavouritedAuctions.add("https://aukcje.ideagetin.pl/" + s);
        }
        return urlsOfFavouritedAuctions;
    }
}
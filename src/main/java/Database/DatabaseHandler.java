package Database;

import DataClasses.*;
import Stemmer.*;

import java.sql.ResultSet;
import java.util.*;

public class DatabaseHandler {

    public static double minIdentityScore = 0.5;
    public static double minReliabilityScore = 0.5;

    public static void insertTitle(String title, String subTitle, String Url) {
        HashMap<String, String> values = new HashMap<>();
        String headlineValue = "";
        String[] headlineArray = title.split(" ");
        List<String> test = new ArrayList<>();


        values.put("headline", title);
        values.put("sub_headline", subTitle);
        // add to db
        SqlHandler.insert("headlines", values);

        values.clear();
        // TODO : get headline id
        values.put("headline_id", String.valueOf(1));
        values.put("domain", Url);
        // add to db
        SqlHandler.insert("headlines_sites", values);
    }

    public static int getTitleId(String title) {
        // TODO : return the actual id
        SqlHandler.select("Titles", new String[] {"id"}, "");

        return 1;
    }

    public static HeadlineSites getHeadlineSiteDetails(int id) {
        // TODO : do...
        return new HeadlineSites() {};
    }

    public static void updateTitle(int id, int reportersCount) {
        String condition = "id=" + id;
        HashMap<String, String> values = new HashMap<>();
        values.put("reporters_count", String.valueOf(reportersCount));
        SqlHandler.upadte("Titles", values, condition);
    }

    // will search the db for titles with the same words in the same order
    // will return <found title length> / <title length>
    public static double[] searchTitle(String[] title) {
        ResultSet rs = SqlHandler.select("Titles", new String [] {"headline"}, "headline LIKE");

        return null;
    }

    public static double[] searchTitle(String[] title, boolean maxOnly) {
        return new double[] { 3 };
    }

    public static String processCrawlRequest(Map<Integer, Article> request, String domain) {
        ArrayList<Integer> suspicious = new ArrayList<>();

        for (Integer key:
             request.keySet()){
            Article article = request.get((key));

            article.headline = StemmerAPI.cleanHeadline(article.headline);
            article.sub_headline = StemmerAPI.cleanHeadline(article.sub_headline);

            double[] similarSearch = searchTitle(article.headline.split(" "), true);
            if (similarSearch[0] < minReliabilityScore) {
                suspicious.add(key);
            }
            else {
                insertTitle(article.headline, article.sub_headline, domain);
            }
        }
        
        String output = "";
        for (Integer i :
                suspicious) {
            output += String.valueOf(i);
        }

        return output;
    }

    public static String processMarkRequest(Article request, String domain) {
        request.headline = StemmerAPI.cleanHeadline(request.headline);
        request.sub_headline = StemmerAPI.cleanHeadline(request.sub_headline);

        double[] similarSearch = searchTitle(request.headline.split(" "), true);
        if (similarSearch[0] > minIdentityScore) {
            int similarHeadline = (int)similarSearch[1];
            int reporters = getHeadlineSiteDetails(similarHeadline).reportersCount + 1;
            updateTitle(similarHeadline, reporters);
        }
        else {
            insertTitle(request.headline, request.sub_headline, domain);
        }

        return "";
    }

}

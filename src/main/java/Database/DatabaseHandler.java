package Database;

import DataClasses.*;
import Stemmer.*;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DatabaseHandler {

    public static double minIdentityScore = 0.5;
    public static double minReliabilityScore = 0.5;

    public static void insertTitle(String title, String Url) {
        System.out.println("inserting " + title + " : " + Url);
        HashMap<String, String> values = new HashMap<>();
        String headlineValue = "{";
        String[] headlineArray = title.split(" ");
        for (String s :
                headlineArray) {
            headlineValue += "\"" + s + "\"";
        }
        headlineValue += "}";
        values.put("headline", headlineValue);

        // add to db
        SqlHandler.insert("headlines", values);

        values.clear();
        // TODO : get headline id
        values.put("headline_id", String.valueOf(1));
        values.put("domain", Url);

        // add to db
        SqlHandler.insert("headlines_sites", values);
    }

    public static int getTitleId(String title) throws SQLException {
        ResultSet rs = SqlHandler.select("Titles", new String[] {"id"}, "headline = " + title);
        rs.next();
        return rs.getInt("id");
    }

    public static HeadlineSites getHeadlineSiteDetails(int id) throws SQLException{
        // TODO : do...
        ResultSet rs = SqlHandler.select("headlineTitles", new String[] {"id","url","reportersCount"}, "id = " + id);
        rs.next();
        HeadlineSites headline = new HeadlineSites(rs.getInt("id"),rs.getString("url"),rs.getInt("reportersCount"));

        return headline;
    }

    public static void updateTitle(int id, int reportersCount) {
        String condition = "id=" + id;
        HashMap<String, String> values = new HashMap<>();
        values.put("reporters_count", String.valueOf(reportersCount));
        SqlHandler.upadte("Titles", values, condition);
    }

    // will search the db for titles with the same words in the same order
    // will return <found title length> / <title length>
    public static Map<Integer,Double> searchTitle(String[] title) {
        try {
            HashMap<Integer, Double> values = new HashMap<>();
            // all headlines... why? change later
            ResultSet rs = SqlHandler.select("headlines", new String[]{"id", "headline"}, ">@");
            double max = 0;
            int id;
            while (rs.next()) {
                String[] dbTitle = rs.getString("headline").split(" ");
                int index = 0;
                for (int i = 0; i < title.length; i++) {
                    if (dbTitle[i].equals(title[index]))
                        index++;
                }
                double reliability = index / title.length;
                if (max < reliability) {
                    max = reliability;
                    id = rs.getInt("id");
                }
                values.put(rs.getInt("id"), reliability);
            }
            return values;
        }
        catch (Exception e) {
            return null;
        }
    }

    public static Map<Integer,Double> searchTitle(String[] title, boolean maxOnly) {
        try {
            HashMap<Integer, Double> values = new HashMap<>();
            // all headlines... why? change later
            ResultSet rs = SqlHandler.select("headlines", new String[]{"id", "headline"}, ">@");
            double max = 0;
            int id = 0;
            while (rs.next()) {
                String[] dbTitle = rs.getString("headline").split(" ");
                int index = 0;
                for (int i = 0; i < title.length; i++) {
                    if (dbTitle[i].equals(title[index]))
                        index++;
                }
                double reliability = index / title.length;
                if (max < reliability) {
                    max = reliability;
                    id = rs.getInt("id");
                }
                values.put(rs.getInt("id"), reliability);
            }
            values.clear();
            values.put(0, max);
            values.put(1, Double.valueOf(id));
            return values;
        }
        catch (Exception e) {
            return null;
        }
    }

    public static String processCrawlRequest(Map<Integer, Article> request, String domain) {
        ArrayList<Integer> suspicious = new ArrayList<>();

        for (Integer key:
             request.keySet()){
            Article article = request.get((key));

            article.headline = StemmerAPI.cleanHeadline(article.headline);

            Map<Integer,Double> similarSearch = searchTitle(article.headline.split(" "), true);
            if (similarSearch != null && similarSearch.size() > 0 && similarSearch.get(0) < minReliabilityScore) {
                System.out.println(article.headline + " marked as suspicious");
                suspicious.add(key);
            }
            else {
                System.out.println(article.headline + " added to db");
                insertTitle(article.headline, domain);
            }
        }
        
        String output = "";
        for (Integer i :
                suspicious) {
            output += String.valueOf(i);
        }

        return output;
    }

    public static String processMarkRequest(Article request, String domain) throws SQLException{
        try {
            request.headline = StemmerAPI.cleanHeadline(request.headline);

            Map<Integer, Double> similarSearch = searchTitle(request.headline.split(" "), true);
            if (similarSearch != null && similarSearch.size() > 0 && similarSearch.get(0) > minIdentityScore) {
                int similarHeadline = Integer.valueOf(similarSearch.get(1).toString());
                int reporters = getHeadlineSiteDetails(similarHeadline).getReportersCount() + 1;
                updateTitle(similarHeadline, reporters);
            } else {
                insertTitle(request.headline, domain);
            }
        }
        catch (Exception e) {

        }
        return "";
    }

}

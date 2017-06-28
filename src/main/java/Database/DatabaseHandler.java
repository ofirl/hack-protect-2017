package Database;

import DataClasses.*;

import java.sql.ResultSet;
import java.util.*;

public class DatabaseHandler {

    public static void insertTitle(String title, String subTitle, String Url) {
        HashMap<String, String> values = new HashMap<>();
        values.put("title", title);
        values.put("subtitle", subTitle);
        values.put("url", Url);

        SqlHandler.insert("Titles", values);
    }

    public static int getTitleId(String title) {
        // TODO : return the actual id
        SqlHandler.select("Titles", new String[] {"id"}, "");

        return 1;
    }

    public static void updateTitle(int id) {
        String condition = "id=" + id;
        HashMap<String, String> values = new HashMap<>();
        SqlHandler.upadte("Titles", values, condition);
    }

    // will search the db for titles with the same words in the same order
    // will return <found title length> / <title length>
    public static double[] searchTitle(String[] title) {
        ResultSet rs = SqlHandler.select("Titles", new String [] {"headline"}, "headline LIKE");

        return null;
    }

    public static String processCrawlRequest(Map<Integer, Article> request, String domain) {

        return "";
    }

    public static String processMarkRequest(Map<Integer, Article> request, String domain) {

        return "";
    }

}

package Database;

import DataClasses.*;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    public static Map<String,Double> searchTitle(String[] title) throws SQLException {
        HashMap<String, Double> values = new HashMap<>();
        ResultSet rs = SqlHandler.select("Titles", new String[]{"id", "headline"}, "headline LIKE");
        double max = 0;
        String id = "";
        while(rs.next()) {
            String[] dbTitle = (String[]) rs.getArray("headline").getArray();
            int index = 0;
            for (int i = 0; i < title.length; i++) {
                if (dbTitle[i].equals(title[index]))
                    index++;
            }
            double reliability = index / title.length;
            if (max < reliability) {
                max = reliability;
                id = rs.getString("id");
            }
            values.put(rs.getString("id"), reliability);
        }
        return values;
    }

    public static String processCrawlRequest(Map<Integer, Article> request, String domain) {

        return "";
    }

    public static String processMarkRequest(Map<Integer, Article> request, String domain) {

        return "";
    }

}

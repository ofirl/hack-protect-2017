package Database;

import java.util.*;

public class DatabaseHandler {

    public void insertTitle(String title, String subTitle, String Url) {
        HashMap<String, String> values = new HashMap<>();
        values.put("title", title);
        values.put("subtitle", subTitle);
        values.put("url", Url);

        SqlHandler.insert("Titles", values);
    }

    public int getTitleId(String title) {
        // TODO : return the actual id
        SqlHandler.select("Titles", new String[] {"id"}, "");
    }

    public void updateTitle(int id) {
        String condition = "WHERE id=" + id;
        HashMap<String, String> values = new HashMap<>();
        SqlHandler.upadte("Titles", values, condition);
    }

}

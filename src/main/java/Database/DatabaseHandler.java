package Database;

import DataClasses.*;

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


        return null;
    }
    private List<String[]> getSubsets(int k, String [] array ) {
        List<String[]> subsets = new ArrayList<>();
        int[] s = new int[k];
        if (k <= array.length) {
            for (int i = 0; (s[i] = i) < k - 1; i++) ;
            subsets.add(getSubset(array, s));
            for (; ; ) {
                int i;
                for (i = k - 1; i >= 0 && s[i] == array.length - k + i; i--) ;
                if (i < 0) {
                    break;
                }
                s[i]++;                    // increment this item
                for (++i; i < k; i++) {    // fill up remaining items
                    s[i] = s[i - 1] + 1;
                }
                subsets.add(getSubset(array, s));
            }
        }
        return subsets;
    }
// generate actual subset by index sequence
    private String[] getSubset(String[] input, int[] subset) {
            String[] result = new String[subset.length];
            for (int i = 0; i < subset.length; i++)
                result[i] = input[subset[i]];
            return result;
        }


    public static String processCrawlRequest(Map<Integer, Article> request, String domain) {

        return "";
    }

    public static String processMarkRequest(Map<Integer, Article> request, String domain) {

        return "";
    }

}

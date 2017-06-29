package Database;

import com.example.Main;

import java.util.*;
import java.sql.*;

public class SqlHandler {
    public static void upadte(String table, Map<String, String> values, String condition) {
    try (Connection connection = Main.getDataSource().getConnection()) {
        Statement stmt = connection.createStatement();
        String setValues = "";
        for(String key: values.keySet())
        	setValues = setValues + key + "=" + values.get(key) + ",";
        setValues = setValues.substring(0,setValues.length()-1);
        ResultSet rs = stmt.executeQuery("UPDATE" + table + "SET" + setValues + "WHERE" + condition);
      } catch (Exception e) {

      }

    }

    public static void insert(String table, Map<String, String> values) {
        try (Connection connection = Main.getDataSource().getConnection()) {
            System.out.println("insert started");
            Statement stmt = connection.createStatement();
            String tcolumns = "(";
            String tvalues = "(";
            for(String key: values.keySet()) {
                tcolumns += key + ",";
                tvalues += values.get(key) + ",";
            }
            System.out.println("test print " + tcolumns + " " + tvalues);
            tcolumns = tcolumns.substring(0,tcolumns.length()-1) + ")";
            tvalues = tvalues.substring(0,tvalues.length()-1) + ")";
            System.out.println("INSERT INTO" + table + tcolumns + "VALUES" + tvalues);
            ResultSet rs = stmt.executeQuery("INSERT INTO" + table + tcolumns + "VALUES" + tvalues);
          } catch (Exception e) {
                System.out.println(e.getMessage());
          }

    }

    public static void delete(String table, String condition) {
        try (Connection connection = Main.getDataSource().getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("DELETE FROM" + table + "WHERE" + condition);
          } catch (Exception e) {

          }

    }

    public static ResultSet select(String table, String[] fields, String condition) {
        try (Connection connection = Main.getDataSource().getConnection()) {
            Statement stmt = connection.createStatement();
            String columns = "";
            for (String field : fields)
            	columns = columns + field + ",";
            columns = columns.substring(0,columns.length()-1);
            ResultSet rs = stmt.executeQuery("SELECT" + columns + "FROM" + table + "WHERE" + condition);
            return rs;
          } catch (Exception e) {
               return null;
          }
    }
}

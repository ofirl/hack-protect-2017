package Database;

import java.util.*;
import java.sql.*;

public class SqlHandler {
    public static void upadte(String table, Map<String, String> values, String condition) {
    DataSource db = dataSource();
    try (Connection connection = dataSource.getConnection()) {
        Statement stmt = connection.createStatement();
        String setValues = "";
        for(String key: values.keySet())
        	setValues = setValues + key + "=" + values.get(key) + ",";
        setValues = setValue.substring(0,setValues.length()-1);
        ResultSet rs = stmt.executeQuery("UPDATE" + table + "SET" + setValues + "WHERE" + condition);
      } catch (Exception e) {
        
      }
    }

    public static void insert(String table, Map<String, String> values) {
        DataSource db = dataSource();
        try (Connection connection = dataSource.getConnection()) {
            Statement stmt = connection.createStatement();
            String tcolumns = "(";
            String tvalues = "(";
            for(String key: values.keySet())
            	tcolumns = tcolumns + key + ",";
            tcolumns = tcolumns.substring(0,tcolumns.length()-1) + ")";
            for(String key: values.keySet())
            	tvalues = tvalues + values.get(key) + ",";
            tvalues = tvalues.substring(0,tvalues.length()-1) + ")";
            ResultSet rs = stmt.executeQuery("INSERT INTO" + table + tcolumns + "VALUES" + tvalues);
          } catch (Exception e) {
            
          }

    }

    public static void delete(String table, String condition) {
        DataSource db = dataSource();
        try (Connection connection = dataSource.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("DELETE FROM" + table + "WHERE" + condition);
          } catch (Exception e) {
            
          }

    }

    public static void select(String table, String[] fields, String condition) {
    	DataSource db = dataSource();
        try (Connection connection = dataSource.getConnection()) {
            Statement stmt = connection.createStatement();
            String columns = "";
            for (String field : fields)
            	columns = columns + field + ",";
            columns = columns.substring(0,columns.length()-1);
            ResultSet rs = stmt.executeQuery("SELECT" + columns + "FROM" + table + "WHERE" + condition);
          } catch (Exception e) {
            
          }

    }
}

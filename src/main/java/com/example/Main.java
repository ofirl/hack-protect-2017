/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import DataClasses.Article;
import Database.DatabaseHandler;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.io.Console;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@SpringBootApplication
public class Main {

  @Value("${spring.datasource.url}")
  private static String dbUrl;

  @Autowired
  public static DataSource dataSource;

  public static void main(String[] args) throws Exception {
    SpringApplication.run(Main.class, args);
  }

  @RequestMapping("/")
  String index() {
    return "index";
  }

  @RequestMapping("/db")
  String db(Map<String, Object> model) {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
      stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
      ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");

      ArrayList<String> output = new ArrayList<String>();
      while (rs.next()) {
        output.add("Read from DB: " + rs.getTimestamp("tick"));
      }

      model.put("records", output);
      return "db";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    String test(Map<String, Object> model, @RequestHeader(value="HOST") String host) {
        System.out.println("Received GET request from:" + host);
        model.put("message", "test");
        return "test";
    }

    @RequestMapping(value = "/test2", method = RequestMethod.POST, headers = )
    ResponseEntity<String> test2(Map<String, Object> model, @RequestHeader(value="HOST") String host) {
        //System.out.println("Received POST request:" + json);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Access-Control-Allow-Origin", "*");
        System.out.println("Received POST request from:" + host);

        model.put("message", "test");
        return new ResponseEntity<String>("Hello World", responseHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/crawl", method = RequestMethod.POST)
    public String crawl(@RequestParam("json") String json, @RequestHeader(value="HOST") String host) {
        System.out.println("Received POST request:" + json);
        System.out.println("Received POST request from:" + host);
        DatabaseHandler.processCrawlRequest(decodeJson(json), host);

        return null;
    }

    @RequestMapping(value = "/mark", method = RequestMethod.POST)
    public String mark(@RequestParam("json") String json, @RequestHeader(value="HOST") String host) throws SQLException {
        System.out.println("Received POST request:" + json);
        System.out.println("Received POST request from:" + host);
        DatabaseHandler.processMarkRequest(decodeJson(json).get(0), host);

        return null;
    }

  @Bean
  public static DataSource dataSource() throws SQLException {
    if (dbUrl == null || dbUrl.isEmpty()) {
      return new HikariDataSource();
    } else {
      HikariConfig config = new HikariConfig();
      config.setJdbcUrl(dbUrl);
      return new HikariDataSource(config);
    }
  }

    private static Map<Integer, Article> decodeJson(String input) {
        try {
            HashMap<Integer, Article> request = new HashMap<>();
            // TODO : decode json
            return request;
        } catch (Exception e) {
            return null;
        }
    }
}

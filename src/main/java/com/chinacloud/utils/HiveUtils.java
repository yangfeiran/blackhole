package com.chinacloud.utils;

import org.apache.log4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.sql.*;

/**
 * Created by Administrator on 2017/6/22.
 */
@Component("hiveUtils")
@ConfigurationProperties(prefix = "datafeed_hive")
public class HiveUtils {
    private final Logger log = Logger.getLogger(HiveUtils.class);
    private static final String JDBC_DRIVER_NAME = "org.apache.hive.jdbc.HiveDriver";

    private String hive_url;
    private String hive_username;
    private String hive_password;
    private boolean hive_auth;

    static {
        try {
            Class.forName(JDBC_DRIVER_NAME);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getHive_url() {
        return hive_url;
    }

    public void setHive_url(String hive_url) {
        this.hive_url = hive_url;
    }

    public String getHive_username() {
        return hive_username;
    }

    public void setHive_username(String hive_username) {
        this.hive_username = hive_username;
    }

    public String getHive_password() {
        return hive_password;
    }

    public void setHive_password(String hive_password) {
        this.hive_password = hive_password;
    }

    public boolean isHive_auth() {
        return hive_auth;
    }

    public void setHive_auth(boolean auth) {
        this.hive_auth = auth;
    }


    public boolean createHiveTable(String database, String table, String schema, String path, String targetType, String targetFile) {
        try {
            Connection connection = hive_auth ? DriverManager.getConnection(hive_url, hive_username, hive_password) : DriverManager.getConnection(hive_url);
            Statement statement = connection.createStatement();
            String constructor = schema.replaceAll("\\{\"", "").replaceAll("\":\"", " ").replaceAll("}", "").replaceAll("\"", "");
            String query = "";
            if (targetType.equalsIgnoreCase("csv")) {
                query = "create external table " + database + "." + table + "(" + constructor + ") ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' LOCATION '" + path + "' tblproperties (\"skip.header.line.count\"=\"1\")";
            } else if (targetType.equalsIgnoreCase("avro")) {
                query = "create external table " + database + "." + table + "(" + constructor + ") row format serde 'org.apache.hadoop.hive.serde2.avro.AvroSerDe' stored as inputformat 'org.apache.hadoop.hive.ql.io.avro.AvroContainerInputFormat' outputformat 'org.apache.hadoop.hive.ql.io.avro.AvroContainerOutputFormat' location '" + path + "' tblproperties  ('avro.schema.url'='" + path + "/../schema/" + targetFile + ".avro";
            } else if (targetType.equalsIgnoreCase("orc")) {
            }
            log.info("create hive table query: " + query);
            boolean result = statement.execute(query);
            statement.close();
            connection.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public String checkTotal(String database, String table) {
        try {
            String result = new String();
            Connection connection = hive_auth ? DriverManager.getConnection(hive_url, hive_username, hive_password) : DriverManager.getConnection(hive_url);
            Statement statement = connection.createStatement();
            String query = "select count(1) as total from " + database + "." + table;
            log.info("select total count from hive table query: " + query);
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                result = resultSet.getString("total");
            }
            resultSet.close();
            statement.close();
            connection.close();
            log.info("total is: " + result);
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return "0";
        }
    }

    public static void main(String[] args) {
        HiveUtils hiveUtils = new HiveUtils();
        hiveUtils.setHive_url("jdbc:hive2://172.16.50.81:10000");
        hiveUtils.setHive_username("hdfs");
        hiveUtils.setHive_auth(true);
        hiveUtils.checkTotal("chinacloud", "jobname");
    }
}

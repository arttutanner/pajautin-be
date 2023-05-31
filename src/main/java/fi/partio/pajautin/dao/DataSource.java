package fi.partio.pajautin.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DataSource {

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    public static Properties properties;

    private DataSource() {
    }

    public static Connection getConnection()  {

        if (ds == null) init();

        try {
            System.out.println("Getting connection, "+ds.getHikariPoolMXBean().getActiveConnections()+" active connections");
            Connection conn =  ds.getConnection();
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void init() {
        try {

            System.out.println("Initializing datasource");
            Properties properties = getProperties();
            String url = "jdbc:mariadb://" + properties.getProperty("database.host") + "/" + properties.getProperty("database.name");
            config.setJdbcUrl(url);
            config.setDriverClassName("org.mariadb.jdbc.Driver");
            config.setUsername(properties.getProperty("database.user"));
            config.setPassword(properties.getProperty("database.password"));
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            ds = new HikariDataSource(config);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Properties getProperties() throws IOException {
        if (properties != null) return properties;

        properties = new Properties();
        properties.load(new FileInputStream("pajautin.properties"));
        return properties;
    }

}
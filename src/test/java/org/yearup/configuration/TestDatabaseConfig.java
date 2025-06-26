package org.yearup.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;

@TestConfiguration
public class TestDatabaseConfig {

    // This will create and start a Microsoft SQL Server container
    // The image is compatible with Azure SQL's T-SQL dialect.
    private static final MSSQLServerContainer<?> sqlServerContainer =
            new MSSQLServerContainer<>(DockerImageName.parse("mcr.microsoft.com/mssql/server:2022-latest"))
                    .acceptLicense();

    static {
        sqlServerContainer.start();
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName(sqlServerContainer.getDriverClassName());
        dataSource.setUrl(sqlServerContainer.getJdbcUrl());
        dataSource.setUsername(sqlServerContainer.getUsername());
        dataSource.setPassword(sqlServerContainer.getPassword());
        return dataSource;
    }
}




















//package org.yearup.configuration;
//
//import org.apache.ibatis.jdbc.ScriptRunner;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.jdbc.datasource.SingleConnectionDataSource;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
//import javax.sql.DataSource;
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//import java.io.Reader;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import java.sql.Statement;
//
//@Configuration
//public class TestDatabaseConfig
//{
//    private final String serverUrl;
//    private final String testDb;
//    private final String username;
//    private final String password;
//
//    @Autowired
//    public TestDatabaseConfig(@Value("${datasource.url}") String serverUrl,
//                              @Value("${datasource.username}") String username,
//                              @Value("${datasource.password}") String password,
//                              @Value("${datasource.testdb}") String testDb)
//    {
//        this.serverUrl = serverUrl;
//        this.testDb = testDb;
//        this.username = username;
//        this.password = password;
//    }
//
//    @PostConstruct
//    public void setup() {
//
//        try(Connection connection = DriverManager.getConnection(serverUrl + "/sys", username, password);
//            Statement statement = connection.createStatement();
//        )
//        {
//            statement.execute("DROP DATABASE IF EXISTS " + testDb + ";");
//            statement.execute("CREATE DATABASE " + testDb + ";");
//        }
//        catch (SQLException ignored) {}
//    }
//
//    @PreDestroy
//    public void cleanup() {
//
//        try(Connection connection = DriverManager.getConnection(serverUrl + "/sys", username, password);
//            Statement statement = connection.createStatement();
//        )
//        {
//            statement.execute("DROP DATABASE IF EXISTS " + testDb + ";");
//        }
//        catch (SQLException ignored){}
//
//    }
//
//
//    @Bean
//    public DataSource dataSource() throws SQLException, IOException
//    {
//        SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
//        dataSource.setUrl(String.format("%s/%s", serverUrl, testDb));
//        dataSource.setUsername(username);
//        dataSource.setPassword(password);
//        dataSource.setAutoCommit(false);
//        dataSource.setSuppressClose(true);
//
//        ScriptRunner runner = new ScriptRunner(dataSource.getConnection());
//        Reader reader = new BufferedReader(new FileReader((new ClassPathResource("test-data.sql")).getFile().getAbsolutePath()));
//        runner.runScript(reader);
//        dataSource.getConnection().commit();
//
//        return dataSource;
//    }
//}

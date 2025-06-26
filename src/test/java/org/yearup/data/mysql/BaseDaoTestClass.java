package org.yearup.data.mysql;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;
import java.sql.SQLException;

@SpringBootTest
public abstract class BaseDaoTestClass {

    // 1. Define the container. It will be a singleton for all tests that inherit this class.
    static final MSSQLServerContainer<?> sqlServerContainer;

    static {
        // 2. Start the container.
        sqlServerContainer = new MSSQLServerContainer<>(DockerImageName.parse("mcr.microsoft.com/mssql/server:2022-latest"))
                .acceptLicense();
        sqlServerContainer.start();
    }

    // 3. Dynamically set the datasource properties for the Spring context.
    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", sqlServerContainer::getJdbcUrl);
        registry.add("spring.datasource.username", sqlServerContainer::getUsername);
        registry.add("spring.datasource.password", sqlServerContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", sqlServerContainer::getDriverClassName);
    }

    @Autowired
    protected DataSource dataSource;

    @BeforeEach
    public void setupTestDatabase() throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        // A more robust way to clean the database
        jdbcTemplate.execute("IF OBJECT_ID('shopping_cart', 'U') IS NOT NULL DROP TABLE shopping_cart;");
        jdbcTemplate.execute("IF OBJECT_ID('order_line_items', 'U') IS NOT NULL DROP TABLE order_line_items;");
        jdbcTemplate.execute("IF OBJECT_ID('orders', 'U') IS NOT NULL DROP TABLE orders;");
        jdbcTemplate.execute("IF OBJECT_ID('products', 'U') IS NOT NULL DROP TABLE products;");
        jdbcTemplate.execute("IF OBJECT_ID('categories', 'U') IS NOT NULL DROP TABLE categories;");
        jdbcTemplate.execute("IF OBJECT_ID('profiles', 'U') IS NOT NULL DROP TABLE profiles;");
        jdbcTemplate.execute("IF OBJECT_ID('users', 'U') IS NOT NULL DROP TABLE users;");

        // Run the test-data script to repopulate the database
        ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("test-data.sql"));
    }
}



















//package org.yearup.data.mysql;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.yearup.configuration.TestDatabaseConfig;
//
//import javax.sql.DataSource;
//import java.sql.Connection;
//import java.sql.SQLException;
//
//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = TestDatabaseConfig.class)
//public abstract class BaseDaoTestClass
//{
//    @Autowired
//    protected DataSource dataSource;
//
//    @AfterEach
//    public void rollback() throws SQLException
//    {
//        Connection connection = dataSource.getConnection();
//        if(!connection.getAutoCommit())
//        {
//            dataSource.getConnection().rollback();
//        }
//    }
//}

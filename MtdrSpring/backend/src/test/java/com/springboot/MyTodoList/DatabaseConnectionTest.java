package com.springboot.MyTodoList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.oracle.OracleContainer;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import oracle.jdbc.pool.OracleDataSource;



@SpringBootTest
@Testcontainers
public class DatabaseConnectionTest {

    @Container
    public static OracleContainer oracleContainer = new OracleContainer("gvenzl/oracle-free:23.6-slim-faststart")
            .withStartupTimeout(Duration.ofMinutes(15))
            .withUsername("TODOUSER")
            .withPassword("Spring-boot101")
            .withInitScript("javadevlocal.sql");


    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", oracleContainer::getJdbcUrl);
        registry.add("spring.datasource.username", oracleContainer::getUsername);
        registry.add("spring.datasource.password", oracleContainer::getPassword);
    }

    static OracleDataSource ds;

    @BeforeAll
    static void setUp() throws SQLException {
        try {
            oracleContainer.start();
        } catch (Exception e) {
            // Print container logs for debugging
            System.out.println("Container logs:");
            System.out.println(oracleContainer.getLogs());
            throw e; // Re-throw the exception to fail the test
        }
        // Configure the OracleDataSource to use the database container
        ds = new OracleDataSource();
        ds.setURL(oracleContainer.getJdbcUrl());
        ds.setUser(oracleContainer.getUsername());
        ds.setPassword(oracleContainer.getPassword());
    }


    @Test
    void insertTwoItems() throws SQLException {
        try (Connection connection = ds.getConnection();
             Statement statement = connection.createStatement()) {

            // Insert two items into the database
            statement.executeUpdate("INSERT INTO TODOUSER.TASKS (NAME) VALUES ('Item 1')");
            statement.executeUpdate("INSERT INTO TODOUSER.TASKS (NAME) VALUES ('Item 2')");

            // Verify that the items were inserted
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM TODOUSER.TASKS");
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                Assertions.assertEquals(2, count, "Expected 2 items in the database");
            } else {
                Assertions.fail("Failed to retrieve item count from the database");
            }
        }
    }
}

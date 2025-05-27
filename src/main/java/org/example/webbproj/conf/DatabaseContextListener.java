package org.example.webbproj.conf;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@WebListener
public class DatabaseContextListener implements ServletContextListener {

    private Connection connection;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            Class.forName("org.postgresql.Driver");

            String dbHost = System.getenv("DB_HOST");
            int dbPort = Integer.parseInt(System.getenv("DB_PORT"));  // Важно!
            String dbName = System.getenv("DB_NAME");
            String dbUser = System.getenv("DB_USER");
            String dbPassword = System.getenv("DB_PASSWORD");

            String url = String.format("jdbc:postgresql://%s:%d/%s", dbHost, dbPort, dbName);
            System.out.println("Connecting to: " + url); // Для отладки
            connection = DriverManager.getConnection(url, dbUser, dbPassword);

            sce.getServletContext().setAttribute("dbConnection", connection);
            System.out.println("Database connection initialized.");

        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error initializing database connection: " + e.getMessage());
            throw new RuntimeException("Failed to initialize database connection", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}
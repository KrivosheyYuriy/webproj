package org.example.webbproj.conf;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.flywaydb.core.Flyway;

@WebListener
public class FlywayServletContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        String dbHost = System.getenv("DB_HOST");
        int dbPort = Integer.parseInt(System.getenv("DB_PORT"));
        String dbName = System.getenv("DB_NAME");
        String dbUser = System.getenv("DB_USER");
        String dbPassword = System.getenv("DB_PASSWORD");

        String url = String.format("jdbc:postgresql://%s:%d/%s", dbHost, dbPort, dbName);
        System.out.println("Flyway connecting to: " + url); // Для отладки

        Flyway flyway = Flyway.configure()
                .dataSource(url, dbUser, dbPassword)
                .locations("classpath:db/migration")
                .load();

        flyway.migrate();
    }
}

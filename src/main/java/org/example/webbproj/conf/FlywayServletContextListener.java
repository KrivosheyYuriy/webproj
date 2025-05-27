package org.example.webbproj.conf;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.flywaydb.core.Flyway;

@WebListener
public class FlywayServletContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        String url = System.getenv("DB_URL") == null ? "jdbc:postgresql://localhost:5432/webbproj" : System.getenv("DB_URL");
        System.out.println(System.getenv("DB_URL"));
        String user = "postgres";
        String password = "123";

        Flyway flyway = Flyway.configure()
                .dataSource(url, user, password)
                .locations("classpath:db/migration")
                .load();

        flyway.migrate();
    }
}

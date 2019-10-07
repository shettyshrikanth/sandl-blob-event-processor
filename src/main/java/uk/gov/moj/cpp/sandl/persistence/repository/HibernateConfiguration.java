package uk.gov.moj.cpp.sandl.persistence.repository;

import static java.lang.System.getenv;

import uk.gov.moj.cpp.sandl.persistence.entity.CourtSchedule;

import org.hibernate.cfg.Configuration;

public class HibernateConfiguration {
    private final String dbUrl = getenv("sandl_db_url");
    private final String user = getenv("sandl_db_user");
    private final String pwd = getenv("sandl_db_pwd");

    public Configuration createHibernateConfiguration() {

        final Configuration config = new Configuration()
                .setProperty("hibernate.connection.driver_class", "com.microsoft.sqlserver.jdbc.SQLServerDriver")
                .setProperty("hibernate.connection.url", dbUrl)
                .setProperty("hibernate.connection.username", this.user)
                .setProperty("hibernate.connection.password", this.pwd)
                .setProperty("hibernate.connection.autocommit", "true")
                .setProperty("hibernate.show_sql", "false")
                .setProperty("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect")
                .setProperty("hibernate.show_sql", "false")
                .setProperty("hibernate.hbm2ddl.auto", "update");


        config.addAnnotatedClass(CourtSchedule.class);

        return config;
    }
}

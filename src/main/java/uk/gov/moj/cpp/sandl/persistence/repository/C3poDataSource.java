package uk.gov.moj.cpp.sandl.persistence.repository;

import static java.lang.System.getenv;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class C3poDataSource {
    private static ComboPooledDataSource cpds = new ComboPooledDataSource();

    static {
        try {
            cpds.setDriverClass("org.h2.Driver");
            cpds.setJdbcUrl(getenv("sandl_db_url"));
            cpds.setUser(getenv("sandl_db_user"));
            cpds.setPassword(getenv("sandl_db_pwd"));
        } catch (PropertyVetoException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection()  {
        try {
            return cpds.getConnection();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private C3poDataSource(){}
}

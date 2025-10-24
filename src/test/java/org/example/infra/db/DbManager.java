package org.example.infra.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DbManager {
    private static HikariDataSource ds;

    public static synchronized DataSource getDataSource() {
        if (ds == null) {
            DbConfig cfg = new DbConfig();
            try {
                if (!cfg.driver().isBlank()) {
                    Class.forName(cfg.driver());
                }
                HikariConfig hk = new HikariConfig();
                hk.setJdbcUrl(cfg.url());
                hk.setUsername(cfg.user());
                hk.setPassword(cfg.pass());
                hk.setMaximumPoolSize(5);
                hk.setMinimumIdle(1);
                hk.setConnectionTimeout(15000);
                hk.setIdleTimeout(60000);
                hk.setMaxLifetime(300000);
                ds = new HikariDataSource(hk);
            } catch (Exception e) {
                throw new RuntimeException("Error inicializando pool de BD", e);
            }
        }
        return ds;
    }

    public static synchronized void close() {
        if (ds != null) {
            ds.close();
            ds = null;
        }
    }
}

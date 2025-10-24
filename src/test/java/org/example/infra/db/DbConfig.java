package org.example.infra.db;

import java.io.InputStream;
import java.util.Properties;

public class DbConfig {
    private final Properties props = new Properties();

    public DbConfig() {
        try (InputStream in = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("db.properties")) {
            if (in != null) props.load(in);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo leer db.properties", e);
        }
    }

    private String get(String key) {
        String env = System.getenv(key);
        if (env != null && !env.isBlank()) return env;
        return props.getProperty(key, "");
    }

    public String url()    { return get("DB_URL"); }
    public String user()   { return get("DB_USER"); }
    public String pass()   { return get("DB_PASS"); }
    public String driver() { return get("DB_DRIVER"); }
}

package org.example.infra.db;

import java.io.InputStream;
import java.sql.*;
import java.util.*;

public class Db {

    private static Properties cargarConfiguracion() {
        Properties props = new Properties();
        try (InputStream input = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("DB.properties")) {

            if (input == null) {
                throw new IllegalStateException(" No se encontró el archivo DB.properties. Verifica que esté en src/main/resources.");
            }
            props.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Error al cargar DB.properties", e);
        }
        return props;
    }

    public static Connection obtenerConexion() {
        try {
            Properties p = cargarConfiguracion();
            String url = p.getProperty("DB_URL");
            String user = p.getProperty("DB_USER");
            String pass = p.getProperty("DB_PASS");
            String driver = p.getProperty("DB_DRIVER");

            Class.forName(driver);
            return DriverManager.getConnection(url, user, pass);
        } catch (Exception e) {
            throw new RuntimeException("Error al conectar con la base de datos", e);
        }
    }

    /** Ejecuta una consulta y devuelve todos los resultados */
    public static List<Map<String, Object>> ejecutarConsulta(String sql) {
        List<Map<String, Object>> resultados = new ArrayList<>();

        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnas = meta.getColumnCount();

            while (rs.next()) {
                Map<String, Object> fila = new LinkedHashMap<>();
                for (int i = 1; i <= columnas; i++) {
                    fila.put(meta.getColumnLabel(i), rs.getObject(i));
                }
                resultados.add(fila);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error al ejecutar consulta SQL", e);
        }

        return resultados;
    }
}

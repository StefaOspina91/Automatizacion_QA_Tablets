package org.example.infra.db;

import org.junit.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DbSmokeTest {

    @Test
    public void conectarDirecto() throws Exception {
        // Ajusta SOLO si cambian credenciales
        String url  = "jdbc:sqlserver://254401.database.windows.net:1433;databaseName=BQC;encrypt=true;trustServerCertificate=false;loginTimeout=30";
        String user = "GsBetaUser@254401";
        String pass = "GhtQaUserAllC++--44";

        try (Connection cn = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = cn.prepareStatement("SELECT 1");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            System.out.println("OK SELECT 1 = " + rs.getInt(1));
        }
    }
}


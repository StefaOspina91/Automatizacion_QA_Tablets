package org.example.infra.db;


import org.junit.Test;

public class DbSmokeTest {
    @Test
    public void probarConexion() {
        new Db().queryOne("SELECT 1 AS ok");
        System.out.println("✅ Conexión a BD exitosa");
    }
}

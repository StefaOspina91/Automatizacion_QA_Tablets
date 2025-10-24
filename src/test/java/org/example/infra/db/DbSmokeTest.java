package org.example.infra.db;

import org.junit.Test;
import java.util.Map;

public class DbSmokeTest {
    @Test
    public void probarConexion() {
        try {
            Map<String,Object> r = new Db().queryOne("SELECT 1 AS ok");
            System.out.println("✅ Conexión a BD exitosa. Resultado: " + r);
        } catch (Exception e) {
            System.out.println("❌ Error de conexión: " + e.getMessage());
            e.printStackTrace();
            throw e; // marca el test en rojo si falla
        }
    }
}



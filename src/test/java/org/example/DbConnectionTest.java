package org.example.infra.db;

import org.junit.Test;
import java.util.*;

public class DbConnectionTest {

    @Test
    public void obtenerIdentifiersPorPasos() {
        System.out.println("🔹 Iniciando consultas paso a paso...");

        // 1) Últimos 20 Shipments (2 semanas)
        String q1 =
                "SELECT TOP 5 ShipmentId " +
                        "FROM tblShipments " +
                        "WHERE CreationUTCDate >= DATEADD(WEEK, -2, GETDATE()) " +
                        "ORDER BY CreationUTCDate DESC";

        List<Map<String, Object>> shipments = Db.ejecutarConsulta(q1);

        if (shipments.isEmpty()) {
            System.out.println(" No se encontraron Shipments en las últimas 2 semanas.");
            return;
        }

        System.out.println(" Shipments encontrados: " + shipments.size());

        // 2) ShipmentGroupId por cada ShipmentId
        Set<String> shipmentGroupIds = new LinkedHashSet<>();
        for (Map<String, Object> row : shipments) {
            String shipmentId = String.valueOf(row.get("ShipmentId"));
            String q2 = "SELECT ShipmentGroupId FROM tblShipmentGroups WHERE ShipmentId = '" + shipmentId + "'";
            List<Map<String, Object>> groups = Db.ejecutarConsulta(q2);

            for (Map<String, Object> g : groups) {
                shipmentGroupIds.add(String.valueOf(g.get("ShipmentGroupId")));
            }
        }

        if (shipmentGroupIds.isEmpty()) {
            System.out.println(" No se encontraron grupos para los Shipments.");
            return;
        }

        System.out.println(" Grupos encontrados: " + shipmentGroupIds.size());

        // 3) Identifiers de cada ShipmentGroupId
        List<String> identifiers = new ArrayList<>();
        for (String groupId : shipmentGroupIds) {
            String q3 = "SELECT Identifier FROM tblShipmentGroupsItems WHERE ShipmentGroupId = '" + groupId + "'";
            List<Map<String, Object>> items = Db.ejecutarConsulta(q3);

            for (Map<String, Object> it : items) {
                identifiers.add(String.valueOf(it.get("Identifier")));
            }
        }

        if (identifiers.isEmpty()) {
            System.out.println(" No se encontraron Identifiers en tblShipmentGroupsItems.");
        } else {
            System.out.println(" Identifiers encontrados (" + identifiers.size() + "):");
            identifiers.forEach(System.out::println);
        }

        System.out.println("🔹 Consultas completadas exitosamente.");
    }

    // ─────────────────────────────────────────────────────────────
    // NUEVO: método utilitario para los tests de Appium
    // Devuelve UNA USDA (Identifier) de los últimos 14 días
    // ─────────────────────────────────────────────────────────────
    public String obtenerUnaUsda() {
        try {
            // Versión eficiente con JOINs (ajusta nombres si difieren):
            String q =
                    "SELECT TOP 1 i.Identifier " +
                            "FROM tblShipments s " +
                            "JOIN tblShipmentGroups g ON g.ShipmentId = s.ShipmentId " +
                            "JOIN tblShipmentGroupsItems i ON i.ShipmentGroupId = g.ShipmentGroupId " +
                            "WHERE s.CreationUTCDate >= DATEADD(WEEK, -2, GETDATE()) " +
                            "ORDER BY s.CreationUTCDate DESC";

            List<Map<String, Object>> rows = Db.ejecutarConsulta(q);
            if (rows != null && !rows.isEmpty()) {
                String usda = String.valueOf(rows.get(0).get("Identifier"));
                System.out.println(" USDA obtenida desde BD: " + usda);
                return usda;
            } else {
                System.out.println("️ No se encontró USDA en el rango consultado.");
                return null;
            }
        } catch (Exception e) {
            System.err.println(" Error consultando USDA: " + e.getMessage());
            return null;
        }
    }
}

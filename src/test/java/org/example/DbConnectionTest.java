package org.example.infra.db;

import org.junit.Test;
import java.util.*;

public class DbConnectionTest {

    @Test
    public void obtenerIdentifiersPorPasos() {
        System.out.println("🔹 Iniciando consultas paso a paso...");

        // 1 Obtener los últimos 20 Shipments de las 2 últimas semanas
        String q1 =
                "SELECT TOP 20 ShipmentId " +
                        "FROM tblShipments " +
                        "WHERE CreationUTCDate >= DATEADD(WEEK, -2, GETDATE()) " +
                        "ORDER BY CreationUTCDate DESC";

        List<Map<String, Object>> shipments = Db.ejecutarConsulta(q1);

        if (shipments.isEmpty()) {
            System.out.println(" No se encontraron Shipments en las últimas 2 semanas.");
            return;
        }

        System.out.println(" Shipments encontrados: " + shipments.size());

        // 2 Obtener ShipmentGroupId por cada ShipmentId
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
            System.out.println("⚠️ No se encontraron grupos para los Shipments.");
            return;
        }

        System.out.println("✅ Grupos encontrados: " + shipmentGroupIds.size());

        // 3 Obtener Identifiers de cada ShipmentGroupId
        List<String> identifiers = new ArrayList<>();
        for (String groupId : shipmentGroupIds) {
            String q3 = "SELECT Identifier FROM tblShipmentGroupsItems WHERE ShipmentGroupId = '" + groupId + "'";
            List<Map<String, Object>> items = Db.ejecutarConsulta(q3);

            for (Map<String, Object> it : items) {
                identifiers.add(String.valueOf(it.get("Identifier")));
            }
        }

        if (identifiers.isEmpty()) {
            System.out.println("⚠️ No se encontraron Identifiers en tblShipmentGroupsItems.");
        } else {
            System.out.println("✅ Identifiers encontrados (" + identifiers.size() + "):");
            identifiers.forEach(System.out::println);
        }

        System.out.println("🔹 Consultas completadas exitosamente.");
    }
}

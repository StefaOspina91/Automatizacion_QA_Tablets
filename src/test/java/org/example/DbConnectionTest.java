package org.example.infra.db;

import org.junit.Test;
import java.util.*;

public class DbConnectionTest {

    @Test
    public void obtenerIdentifiersPorPasos() {
        System.out.println("🔹 Iniciando consultas paso a paso (rango: 3 semanas)...");
        // Paso 1: primer ShipmentId del rango (3 semanas)
        String qShipment =
                "SELECT TOP (1) s.ShipmentId " +
                        "FROM tblShipments s " +
                        "WHERE s.ShipmentDate >= DATEADD(WEEK, -3, CAST(GETDATE() AS date)) " +
                        "  AND s.ShipmentDate <  DATEADD(DAY, 1, CAST(GETDATE() AS date)) " +
                        "ORDER BY s.ShipmentDate ASC, s.ShipmentId;";

        List<Map<String, Object>> r1 = Db.ejecutarConsulta(qShipment);
        if (r1 == null || r1.isEmpty()) {
            System.out.println("⚠️ No hay Shipments en las últimas 3 semanas.");
            return;
        }
        String shipmentId = String.valueOf(r1.get(0).get("ShipmentId"));
        System.out.println("✅ ShipmentId elegido: " + shipmentId);

        // Paso 2: primer ShipmentGroupId del Shipment elegido (orden por GUID asc)
        String qGroup =
                "SELECT TOP (1) g.ShipmentGroupId " +
                        "FROM tblShipmentGroups g " +
                        "WHERE g.ShipmentId = '" + shipmentId + "' " +
                        "ORDER BY g.ShipmentGroupId ASC;";

        List<Map<String, Object>> r2 = Db.ejecutarConsulta(qGroup);
        if (r2 == null || r2.isEmpty()) {
            System.out.println("⚠️ No hay grupos para el Shipment " + shipmentId);
            return;
        }
        String groupId = String.valueOf(r2.get(0).get("ShipmentGroupId"));
        System.out.println("✅ ShipmentGroupId elegido: " + groupId);

        // Paso 3: identifiers del grupo (todas, orden alfabético)
        String qItems =
                "SELECT i.Identifier " +
                        "FROM tblShipmentGroupsItems i " +
                        "WHERE i.ShipmentGroupId = '" + groupId + "' " +
                        "ORDER BY i.Identifier ASC;";

        List<Map<String, Object>> r3 = Db.ejecutarConsulta(qItems);
        if (r3 == null || r3.isEmpty()) {
            System.out.println("⚠️ No hay Identifiers para el grupo " + groupId);
            return;
        }

        System.out.println("✅ Identifiers encontrados (" + r3.size() + "):");
        r3.stream()
                .map(m -> String.valueOf(m.get("Identifier")))
                .forEach(System.out::println);

        System.out.println("🔹 Consultas completadas.");
    }

    /**
     * Devuelve UNA USDA (Identifier) replicando el flujo manual:
     *  1) Primer Shipment del rango de 3 semanas (por ShipmentDate asc)
     *  2) Primer Group de ese Shipment (ShipmentGroupId asc)
     *  3) Primer Identifier del grupo (alfabético)
     */
    public String obtenerUnaUsda() {
        try {
            // Paso 1
            String qShipment =
                    "SELECT TOP (1) s.ShipmentId " +
                            "FROM tblShipments s " +
                            "WHERE s.ShipmentDate >= DATEADD(WEEK, -3, CAST(GETDATE() AS date)) " +
                            "  AND s.ShipmentDate <  DATEADD(DAY, 1, CAST(GETDATE() AS date)) " +
                            "ORDER BY s.ShipmentDate ASC, s.ShipmentId;";

            List<Map<String, Object>> r1 = Db.ejecutarConsulta(qShipment);
            if (r1 == null || r1.isEmpty()) {
                System.out.println("⚠️ No hay Shipments en las últimas 3 semanas.");
                return null;
            }
            String shipmentId = String.valueOf(r1.get(0).get("ShipmentId"));

            // Paso 2
            String qGroup =
                    "SELECT TOP (1) g.ShipmentGroupId " +
                            "FROM tblShipmentGroups g " +
                            "WHERE g.ShipmentId = '" + shipmentId + "' " +
                            "ORDER BY g.ShipmentGroupId ASC;";

            List<Map<String, Object>> r2 = Db.ejecutarConsulta(qGroup);
            if (r2 == null || r2.isEmpty()) {
                System.out.println("⚠️ No hay grupos para el Shipment " + shipmentId);
                return null;
            }
            String groupId = String.valueOf(r2.get(0).get("ShipmentGroupId"));

            // Paso 3 (una USDA: primera alfabética)
            String qItem =
                    "SELECT TOP (1) i.Identifier " +
                            "FROM tblShipmentGroupsItems i " +
                            "WHERE i.ShipmentGroupId = '" + groupId + "' " +
                            "ORDER BY i.Identifier ASC;";

            List<Map<String, Object>> r3 = Db.ejecutarConsulta(qItem);
            if (r3 != null && !r3.isEmpty()) {
                String usda = String.valueOf(r3.get(0).get("Identifier"));
                System.out.println("✅ USDA obtenida: " + usda +
                        "  (ShipmentId=" + shipmentId + ", GroupId=" + groupId + ")");
                return usda;
            } else {
                System.out.println("⚠️ No se encontró Identifier para el grupo " + groupId);
                return null;
            }

        } catch (Exception e) {
            System.err.println("❌ Error consultando USDA: " + e.getMessage());
            return null;
        }
    }
}

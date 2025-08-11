package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbConnection {

    // Configura los detalles de tu conexión
    // NOTA: La cadena de conexión se ajusta para usar el usuario y contraseña
    private static final String DB_URL = "jdbc:sqlserver://253201.database.windows.net:1433;databaseName=your_database_name;";
    private static final String USER = "GsBetaUser";
    private static final String PASS = "GhtQaUserAllC+--+32";

    /**
     * Establece la conexión con la base de datos SQL Server.
     * @return El objeto Connection si la conexión es exitosa.
     * @throws SQLException Si ocurre un error de conexión o el driver no se encuentra.
     */
    public static Connection connect() throws SQLException {
        try {
            return DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Cierra la conexión de la base de datos de manera segura.
     * @param connection El objeto Connection a cerrar.
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Conexión cerrada exitosamente.");
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }

    /**
     * Ejemplo de uso para una consulta SELECT.
     * @param query La consulta SQL a ejecutar.
     * @return Un objeto ResultSet con los resultados de la consulta.
     */
    public static ResultSet executeQuery(String query) throws SQLException {
        Connection connection = connect();
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }
}
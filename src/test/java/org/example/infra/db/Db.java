package org.example.infra.db;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class Db {
    private final DataSource ds = DbManager.getDataSource();

    public Map<String,Object> queryOne(String sql, Object... params) {
        List<Map<String,Object>> list = queryList(sql, params);
        return list.isEmpty() ? Collections.emptyMap() : list.get(0);
    }

    public List<Map<String,Object>> queryList(String sql, Object... params) {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i+1, params[i]);
            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String,Object>> rows = new ArrayList<>();
                ResultSetMetaData md = rs.getMetaData();
                int cols = md.getColumnCount();
                while (rs.next()) {
                    Map<String,Object> row = new LinkedHashMap<>();
                    for (int i=1;i<=cols;i++) {
                        row.put(md.getColumnLabel(i), rs.getObject(i));
                    }
                    rows.add(row);
                }
                return rows;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error ejecutando query: " + sql, e);
        }
    }

    public int execute(String sql, Object... params) {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i+1, params[i]);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error ejecutando update: " + sql, e);
        }
    }
}

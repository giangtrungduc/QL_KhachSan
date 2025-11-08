package com.example.ql_khachsan.untils;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * Quản lý kết nối Database với HikariCP
 */
public class DatabaseConnection {
    // ==================== CẤU HÌNH ====================
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "qlkhachsan";
    private static final String DB_USER = "root"; // tên cũng vậy
    private static final String DB_PASSWORD = "duc123"; // mật khẩu tự đổi

    private static final String DB_URL = String.format(
            "jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Ho_Chi_Minh&useSSL=false",
            DB_HOST, DB_PORT, DB_NAME
    );

    private static HikariDataSource dataSource;

    // ==================== KHỞI TẠO ====================

    static {
        try {
            HikariConfig config = new HikariConfig();

            // Cấu hình cơ bản
            config.setJdbcUrl(DB_URL);
            config.setUsername(DB_USER);
            config.setPassword(DB_PASSWORD);
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");

            // Connection pool
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);

            // Tối ưu MySQL
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            config.setPoolName("QLKhachSan-Pool");

            dataSource = new HikariDataSource(config);
            System.out.println("✅ Kết nối Database thành công!");

        } catch (Exception e) {
            System.err.println("❌ Lỗi kết nối Database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== METHODS ====================

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource chưa được khởi tạo!");
        }
        return dataSource.getConnection();
    }

    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("🔒 Đã đóng Connection Pool");
        }
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("❌ Test connection failed: " + e.getMessage());
            return false;
        }
    }

    public static void printPoolStats() {
        if (dataSource != null) {
            System.out.println("📊 Active: " + dataSource.getHikariPoolMXBean().getActiveConnections() +
                    " | Idle: " + dataSource.getHikariPoolMXBean().getIdleConnections() +
                    " | Total: " + dataSource.getHikariPoolMXBean().getTotalConnections());
        }
    }

    /**
     * Liệt kê tất cả các bảng (TABLE) có trong database.
     */
    public static void listTables() {
        java.util.List<String> tableNames = new java.util.ArrayList<>();
        String catalog = "";

        try (Connection conn = getConnection()) {
            catalog = conn.getCatalog();
            DatabaseMetaData metaData = conn.getMetaData();

            try (java.sql.ResultSet rs = metaData.getTables(catalog, null, "%", new String[]{"TABLE"})) {
                System.out.println("🔎 Đang kiểm tra các bảng trong database '" + catalog + "'...");
                while (rs.next()) {
                    // Cột "TABLE_NAME" chứa tên bảng
                    tableNames.add(rs.getString("TABLE_NAME"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi liệt kê bảng: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // In kết quả
        if (tableNames.isEmpty()) {
            System.out.println("Không tìm thấy bảng nào trong database '" + catalog + "'.");
        } else {
            System.out.println("Tìm thấy tổng cộng: " + tableNames.size() + " bảng:");
            for (String tableName : tableNames) {
                System.out.println("  -> " + tableName);
            }
        }
    }
}

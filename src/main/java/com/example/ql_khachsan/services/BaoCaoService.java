package com.example.ql_khachsan.services;

import com.example.ql_khachsan.config.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class BaoCaoService {

    // Lấy tổng doanh thu theo tháng/năm
    public double getTongDoanhThu(int thang, int nam) {
        String sql = """
            SELECT COALESCE(SUM(hd.tong_tien), 0) as tong_doanh_thu
            FROM HOADON hd
            JOIN PHIEUDATPHONG pdp ON hd.MaDP = pdp.MaDP
            WHERE MONTH(pdp.NgayNhan) = ? AND YEAR(pdp.NgayNhan) = ?
            AND pdp.TrangThaiDP = 'Hoàn thành'
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, thang);
            stmt.setInt(2, nam);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("tong_doanh_thu");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Lấy số lượt đặt phòng theo tháng/năm
    public int getSoLuotDatPhong(int thang, int nam) {
        String sql = """
            SELECT COUNT(*) as so_luot_dat
            FROM PHIEUDATPHONG 
            WHERE MONTH(NgayNhan) = ? AND YEAR(NgayNhan) = ?
            AND TrangThaiDP IN ('Đã đặt', 'Đang sử dụng', 'Hoàn thành')
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, thang);
            stmt.setInt(2, nam);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("so_luot_dat");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Lấy tỷ lệ lấp đầy phòng
    public double getTyLeLapPhong(int thang, int nam) {
        String sql = """
            SELECT 
                (COUNT(DISTINCT pdp.MaPhong) * 100.0 / 
                (SELECT COUNT(*) FROM PHONG WHERE TrangThai != 'Bảo trì')) as ty_le_lap
            FROM PHIEUDATPHONG pdp
            WHERE MONTH(pdp.NgayNhan) = ? AND YEAR(pdp.NgayNhan) = ?
            AND pdp.TrangThaiDP IN ('Đã đặt', 'Đang sử dụng', 'Hoàn thành')
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, thang);
            stmt.setInt(2, nam);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("ty_le_lap");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Lấy loại phòng có doanh thu cao nhất
    public String getLoaiPhongTop(int thang, int nam) {
        String sql = """
            SELECT lp.TenLoai, SUM(hd.tong_tien) as doanh_thu
            FROM HOADON hd
            JOIN PHIEUDATPHONG pdp ON hd.MaDP = pdp.MaDP
            JOIN PHONG p ON pdp.MaPhong = p.MaPhong
            JOIN LOAIPHONG lp ON p.MaLoai = lp.MaLoai
            WHERE MONTH(pdp.NgayNhan) = ? AND YEAR(pdp.NgayNhan) = ?
            AND pdp.TrangThaiDP = 'Hoàn thành'
            GROUP BY lp.TenLoai
            ORDER BY doanh_thu DESC
            LIMIT 1
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, thang);
            stmt.setInt(2, nam);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("TenLoai");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Không có dữ liệu";
    }

    // Lấy dữ liệu cho biểu đồ doanh thu theo tháng
    public Map<String, Double> getDoanhThuTheoThang(int nam) {
        Map<String, Double> doanhThuThang = new HashMap<>();
        String sql = """
            SELECT MONTH(pdp.NgayNhan) as thang, SUM(hd.tong_tien) as doanh_thu
            FROM HOADON hd
            JOIN PHIEUDATPHONG pdp ON hd.MaDP = pdp.MaDP
            WHERE YEAR(pdp.NgayNhan) = ?
            AND pdp.TrangThaiDP = 'Hoàn thành'
            GROUP BY MONTH(pdp.NgayNhan)
            ORDER BY thang
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, nam);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int thang = rs.getInt("thang");
                double doanhThu = rs.getDouble("doanh_thu");
                doanhThuThang.put("Tháng " + thang, doanhThu);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doanhThuThang;
    }

    // Lấy chi tiết doanh thu cho bảng
    public Map<String, Object> getChiTietDoanhThu(int thang, int nam) {
        Map<String, Object> chiTiet = new HashMap<>();
        String sql = """
            SELECT 
                pdp.MaDP,
                p.TenPhong,
                lp.TenLoai,
                pdp.NgayNhan,
                pdp.NgayTra,
                hd.tong_tien as doanh_thu
            FROM HOADON hd
            JOIN PHIEUDATPHONG pdp ON hd.MaDP = pdp.MaDP
            JOIN PHONG p ON pdp.MaPhong = p.MaPhong
            JOIN LOAIPHONG lp ON p.MaLoai = lp.MaLoai
            WHERE MONTH(pdp.NgayNhan) = ? AND YEAR(pdp.NgayNhan) = ?
            AND pdp.TrangThaiDP = 'Hoàn thành'
            ORDER BY pdp.NgayNhan
        """;

        // TODO: Implement chi tiết
        return chiTiet;
    }
}
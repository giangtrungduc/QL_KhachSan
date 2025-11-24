package com.example.ql_khachsan.dao;

import com.example.ql_khachsan.models.BaoCaoDTO;
import com.example.ql_khachsan.untils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BaoCaoDAO {

    public double getTongDoanhThu(String whereCondition) {
        String sql = "SELECT SUM(DATEDIFF(DP.NgayTra, DP.NgayNhan) * DP.DonGiaThucTe) as TotalRevenue " +
                "FROM HOADON HD " +
                "JOIN PHIEUDATPHONG DP ON HD.MaDP = DP.MaDP " +
                whereCondition;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("TotalRevenue");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public int getTongLuotDat(String whereCondition) {
        String sql = "SELECT COUNT(*) as TotalBookings FROM HOADON HD " + whereCondition;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("TotalBookings");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getTopLoaiPhong(String whereCondition) {
        String sql = "SELECT LP.TenLoai, SUM(DATEDIFF(DP.NgayTra, DP.NgayNhan) * DP.DonGiaThucTe) as DoanhThu " +
                "FROM HOADON HD " +
                "JOIN PHIEUDATPHONG DP ON HD.MaDP = DP.MaDP " +
                "JOIN PHONG P ON DP.MaPhong = P.MaPhong " +
                "JOIN LOAIPHONG LP ON P.MaLoai = LP.MaLoai " +
                whereCondition +
                " GROUP BY LP.TenLoai " +
                "ORDER BY DoanhThu DESC LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getString("TenLoai");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "---";
    }

    public List<BaoCaoDTO> getBaoCaoChiTiet(String whereCondition, String groupBy) {
        List<BaoCaoDTO> list = new ArrayList<>();

        String sql = "SELECT " + groupBy + " as ThoiGian, " +
                "COUNT(HD.MaHD) as SoLuot, " +
                "SUM(DATEDIFF(DP.NgayTra, DP.NgayNhan) * DP.DonGiaThucTe) as DoanhThu " +
                "FROM HOADON HD " +
                "JOIN PHIEUDATPHONG DP ON HD.MaDP = DP.MaDP " +
                whereCondition +
                " GROUP BY " + groupBy +
                " ORDER BY ThoiGian ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new BaoCaoDTO(
                        rs.getString("ThoiGian"),
                        rs.getInt("SoLuot"),
                        rs.getDouble("DoanhThu")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Tính tổng số phòng
    public int getTongSoPhong() {
        String sql = "SELECT COUNT(*) as TongSoPhong FROM PHONG";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("TongSoPhong");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 10; // Mặc định 10 phòng nếu có lỗi
    }

    // Tính tổng số đêm đã đặt
    public int getTongSoDemDaDat(String whereCondition) {
        String sql = "SELECT SUM(DATEDIFF(DP.NgayTra, DP.NgayNhan)) as TongSoDem " +
                "FROM HOADON HD " +
                "JOIN PHIEUDATPHONG DP ON HD.MaDP = DP.MaDP " +
                whereCondition;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("TongSoDem");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
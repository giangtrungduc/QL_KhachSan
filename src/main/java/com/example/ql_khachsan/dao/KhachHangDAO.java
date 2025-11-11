package com.example.ql_khachsan.dao;

import com.example.ql_khachsan.models.KhachHang;
import com.example.ql_khachsan.untils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO {

    private static final String TBL_NAME = "KHACHHANG";
    private static final String COL_SDT = "`SDT`";

    /**
     * Hàm nội bộ để map ResultSet sang KhachHang
     */
    private KhachHang mapResultSetToKhachHang(ResultSet rs) throws SQLException {
        KhachHang kh = new KhachHang();
        kh.setMaKH(rs.getString("MaKH"));
        kh.setHoTen(rs.getString("HoTen"));
        kh.setCccd(rs.getString("CCCD"));
        kh.setSdt(rs.getString("SDT"));
        kh.setEmail(rs.getString("Email"));
        kh.setTaiKhoan(rs.getString("TaiKhoan"));
        kh.setMatKhau(rs.getString("MatKhau"));
        return kh;
    }

    // ====================== GET ALL ======================
    public List<KhachHang> getAll() {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT MaKH, HoTen, CCCD, " + COL_SDT +
                ", Email, TaiKhoan, MatKhau FROM " + TBL_NAME;

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToKhachHang(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ====================== GET BY ID (Bổ sung) ======================
    public KhachHang getById(String maKH) {
        String sql = "SELECT MaKH, HoTen, CCCD, " + COL_SDT +
                ", Email, TaiKhoan, MatKhau FROM " + TBL_NAME + " WHERE MaKH = ?";

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, maKH);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToKhachHang(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ====================== INSERT ======================
    public boolean insert(KhachHang kh) throws SQLException {
        String sql = "INSERT INTO " + TBL_NAME +
                " (MaKH, HoTen, CCCD, " + COL_SDT + ", Email, TaiKhoan, MatKhau) VALUES (?,?,?,?,?,?,?)";

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, kh.getMaKH());
            ps.setString(2, kh.getHoTen());
            ps.setString(3, kh.getCccd());
            ps.setString(4, kh.getSdt());
            ps.setString(5, kh.getEmail());
            ps.setString(6, kh.getTaiKhoan());
            ps.setString(7, kh.getMatKhau());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    // ====================== UPDATE ======================
    public boolean update(KhachHang kh) {
        String sql = "UPDATE " + TBL_NAME + " SET HoTen=?, CCCD=?, " + COL_SDT +
                "=?, Email=?, TaiKhoan=?, MatKhau=? WHERE MaKH=?";

        // Sửa: Dùng try-with-resources
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, kh.getHoTen());
            ps.setString(2, kh.getCccd());
            ps.setString(3, kh.getSdt());
            ps.setString(4, kh.getEmail());
            ps.setString(5, kh.getTaiKhoan());
            ps.setString(6, kh.getMatKhau());
            ps.setString(7, kh.getMaKH());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ====================== DELETE ======================
    public boolean delete(String maKH) {
        String sql = "DELETE FROM " + TBL_NAME + " WHERE MaKH=?";

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, maKH);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // =================================================================
    // CÁC HÀM NGHIỆP VỤ (Bổ sung)
    // =================================================================

    /**
     * Hàm nghiệp vụ: Phục vụ chức năng Đăng nhập
     */
    public KhachHang login(String taiKhoan, String matKhau) {
        String sql = "SELECT * FROM " + TBL_NAME + " WHERE TaiKhoan = ? AND MatKhau = ?";

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, taiKhoan);
            ps.setString(2, matKhau);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToKhachHang(rs); // Đăng nhập thành công
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Đăng nhập thất bại
    }

    /**
     * Hàm nghiệp vụ: Phục vụ chức năng Đăng ký (Kiểm tra trùng lặp)
     */
    public boolean checkIfExists(String taiKhoan, String email, String sdt, String cccd) {
        String sql = "SELECT 1 FROM " + TBL_NAME + " WHERE TaiKhoan = ? OR Email = ? OR " + COL_SDT + " = ? OR CCCD = ?";

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, taiKhoan);
            ps.setString(2, email);
            ps.setString(3, sdt);
            ps.setString(4, cccd);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Trả về true nếu tìm thấy (đã tồn tại)
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true; // Mặc định là true để ngăn đăng ký nếu có lỗi DB
        }
    }
}
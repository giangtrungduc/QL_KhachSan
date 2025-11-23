package com.example.ql_khachsan.dao;

import com.example.ql_khachsan.models.KhachHang;
import com.example.ql_khachsan.untils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO {

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

    public List<KhachHang> getAll() {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM KHACHHANG";
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

    public KhachHang getById(String maKH) {
        String sql = "SELECT * FROM KHACHHANG WHERE MaKH = ?";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, maKH);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToKhachHang(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(KhachHang kh) throws SQLException {
        String sql = "INSERT INTO KHACHHANG (MaKH, HoTen, CCCD, SDT, Email, TaiKhoan, MatKhau) VALUES (?,?,?,?,?,?,?)";
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
        }
    }

    public boolean update(KhachHang kh) {
        String sql = "UPDATE KHACHHANG SET HoTen=?, CCCD=?, SDT=?, Email=?, TaiKhoan=?, MatKhau=? WHERE MaKH=?";
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

    public boolean delete(String maKH) {
        String sql = "DELETE FROM KHACHHANG WHERE MaKH=?";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, maKH);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public KhachHang login(String taiKhoan, String matKhau) {
        String sql = "SELECT * FROM KHACHHANG WHERE TaiKhoan = ? AND MatKhau = ?";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, taiKhoan);
            ps.setString(2, matKhau);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToKhachHang(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean checkIfExists(String taiKhoan, String email, String sdt, String cccd) {
        String sql = "SELECT 1 FROM KHACHHANG WHERE TaiKhoan = ? OR Email = ? OR SDT = ? OR CCCD = ?";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, taiKhoan);
            ps.setString(2, email);
            ps.setString(3, sdt);
            ps.setString(4, cccd);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }
}
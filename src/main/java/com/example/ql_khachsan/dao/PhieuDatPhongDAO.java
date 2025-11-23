package com.example.ql_khachsan.dao;

import com.example.ql_khachsan.models.PhieuDatPhong;
import com.example.ql_khachsan.untils.DatabaseConnection;
import com.example.ql_khachsan.untils.TrangThaiDatPhong;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhieuDatPhongDAO {

    private PhieuDatPhong mapResultSetToPhieuDatPhong(ResultSet rs) throws SQLException {
        PhieuDatPhong pdp = new PhieuDatPhong();
        pdp.setMaDP(rs.getString("MaDP"));
        pdp.setNgayDat(rs.getTimestamp("NgayDat").toLocalDateTime());
        pdp.setNgayNhan(rs.getTimestamp("NgayNhan").toLocalDateTime());
        pdp.setNgayTra(rs.getTimestamp("NgayTra").toLocalDateTime());
        pdp.setDonGiaThucTe(rs.getBigDecimal("DonGiaThucTe"));
        pdp.setTrangThaiDatPhong(TrangThaiDatPhong.fromDbValue(rs.getString("TrangThaiDP")));
        pdp.setMaKH(rs.getString("MaKH"));
        pdp.setMaPhong(rs.getString("MaPhong"));
        return pdp;
    }

    public List<PhieuDatPhong> getAll() {
        List<PhieuDatPhong> danhSachPDP = new ArrayList<>();
        String sql = "SELECT * FROM PHIEUDATPHONG";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                danhSachPDP.add(mapResultSetToPhieuDatPhong(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachPDP;
    }

    public PhieuDatPhong getById(String maDP) {
        String sql = "SELECT * FROM PHIEUDATPHONG WHERE MaDP = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maDP);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToPhieuDatPhong(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(PhieuDatPhong pdp) throws SQLException {
        String sql = "INSERT INTO PHIEUDATPHONG (MaDP, NgayDat, NgayNhan, NgayTra, DonGiaThucTe, TrangThaiDP, MaKH, MaPhong) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pdp.getMaDP());
            ps.setTimestamp(2, Timestamp.valueOf(pdp.getNgayDat()));
            ps.setTimestamp(3, Timestamp.valueOf(pdp.getNgayNhan()));
            ps.setTimestamp(4, Timestamp.valueOf(pdp.getNgayTra()));
            ps.setBigDecimal(5, pdp.getDonGiaThucTe());
            ps.setString(6, pdp.getTrangThaiDatPhong().getDbValue());
            ps.setString(7, pdp.getMaKH());
            ps.setString(8, pdp.getMaPhong());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(PhieuDatPhong pdp) {
        String sql = "UPDATE PHIEUDATPHONG SET NgayDat = ?, NgayNhan = ?, NgayTra = ?, DonGiaThucTe = ?, TrangThaiDP = ?, MaKH = ?, MaPhong = ? WHERE MaDP = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(pdp.getNgayDat()));
            ps.setTimestamp(2, Timestamp.valueOf(pdp.getNgayNhan()));
            ps.setTimestamp(3, Timestamp.valueOf(pdp.getNgayTra()));
            ps.setBigDecimal(4, pdp.getDonGiaThucTe());
            ps.setString(5, pdp.getTrangThaiDatPhong().getDbValue());
            ps.setString(6, pdp.getMaKH());
            ps.setString(7, pdp.getMaPhong());
            ps.setString(8, pdp.getMaDP());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String maDP) {
        String sql = "DELETE FROM PHIEUDATPHONG WHERE MaDP = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maDP);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<PhieuDatPhong> findByKhachHangId(String maKH) {
        List<PhieuDatPhong> danhSachPDP = new ArrayList<>();
        String sql = "SELECT * FROM PHIEUDATPHONG WHERE MaKH = ? ORDER BY NgayDat DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKH);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSachPDP.add(mapResultSetToPhieuDatPhong(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachPDP;
    }

    public PhieuDatPhong findActiveBookingByRoomId(String maPhong) {
        String sql = "SELECT * FROM PHIEUDATPHONG WHERE MaPhong = ? AND (TrangThaiDP = ? OR TrangThaiDP = ?) ORDER BY NgayDat DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhong);
            ps.setString(2, TrangThaiDatPhong.DA_DAT.getDbValue());
            ps.setString(3, TrangThaiDatPhong.DANG_SU_DUNG.getDbValue());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToPhieuDatPhong(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
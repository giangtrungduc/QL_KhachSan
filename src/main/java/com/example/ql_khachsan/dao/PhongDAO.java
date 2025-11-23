package com.example.ql_khachsan.dao;

import com.example.ql_khachsan.models.Phong;
import com.example.ql_khachsan.models.PhongDetail;
import com.example.ql_khachsan.untils.DatabaseConnection;
import com.example.ql_khachsan.untils.TrangThaiDatPhong;
import com.example.ql_khachsan.untils.TrangThaiPhong;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PhongDAO {

    private PhongDetail mapResultSetToPhongDetail(ResultSet rs) throws SQLException {
        PhongDetail phongDetail = new PhongDetail();
        phongDetail.setMaPhong(rs.getString("MaPhong"));
        phongDetail.setTenPhong(rs.getString("TenPhong"));
        phongDetail.setTrangThai(TrangThaiPhong.fromDbValue(rs.getString("TrangThai")));
        phongDetail.setMaLoai(rs.getString("MaLoai"));
        phongDetail.setTenLoai(rs.getString("TenLoai"));
        phongDetail.setSoNguoiTD(rs.getInt("SoNguoiTD"));
        phongDetail.setDonGia(rs.getBigDecimal("DonGia"));
        return phongDetail;
    }

    public List<PhongDetail> getAll() {
        List<PhongDetail> phongList = new ArrayList<>();
        String sql = "SELECT p.MaPhong, p.TenPhong, p.TrangThai, p.MaLoai, " +
                "lp.TenLoai, lp.SoNguoiTD, lp.DonGia " +
                "FROM PHONG p JOIN LOAIPHONG lp ON p.MaLoai = lp.MaLoai";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                phongList.add(mapResultSetToPhongDetail(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return phongList;
    }

    public boolean insert(Phong phong) {
        String sql = "INSERT INTO PHONG (MaPhong, TenPhong, TrangThai, MaLoai) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, phong.getMaPhong());
            ps.setString(2, phong.getTenPhong());
            ps.setString(3, phong.getTrangThai().getDbValue());
            ps.setString(4, phong.getMaLoai());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Phong phong) {
        String sql = "UPDATE PHONG SET TenPhong = ?, TrangThai = ?, MaLoai = ? WHERE MaPhong = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, phong.getTenPhong());
            ps.setString(2, phong.getTrangThai().getDbValue());
            ps.setString(3, phong.getMaLoai());
            ps.setString(4, phong.getMaPhong());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String maPhong) {
        String sql = "DELETE FROM PHONG WHERE MaPhong = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, maPhong);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<PhongDetail> findAllAvailableRooms(LocalDateTime ngayNhan, LocalDateTime ngayTra) {
        List<PhongDetail> availableRooms = new ArrayList<>();
        String sql = "SELECT p.MaPhong, p.TenPhong, p.TrangThai, p.MaLoai, " +
                "lp.TenLoai, lp.SoNguoiTD, lp.DonGia " +
                "FROM PHONG p JOIN LOAIPHONG lp ON p.MaLoai = lp.MaLoai " +
                "WHERE p.TrangThai = ? " +
                "AND NOT EXISTS (" +
                "    SELECT 1 FROM PHIEUDATPHONG pdp " +
                "    WHERE pdp.MaPhong = p.MaPhong " +
                "    AND pdp.TrangThaiDP IN (?, ?) " +
                "    AND (pdp.NgayNhan < ? AND pdp.NgayTra > ?)" +
                ")";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, TrangThaiPhong.TRONG.getDbValue());
            ps.setString(2, TrangThaiDatPhong.DA_DAT.getDbValue());
            ps.setString(3, TrangThaiDatPhong.DANG_SU_DUNG.getDbValue());
            ps.setTimestamp(4, Timestamp.valueOf(ngayTra));
            ps.setTimestamp(5, Timestamp.valueOf(ngayNhan));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    availableRooms.add(mapResultSetToPhongDetail(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return availableRooms;
    }
}
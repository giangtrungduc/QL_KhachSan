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
    private Phong mapResultSetToPhong(ResultSet rs) throws SQLException {
        Phong phong = new Phong();
        phong.setMaPhong(rs.getString("MaPhong"));
        phong.setTenPhong(rs.getString("TenPhong"));
        phong.setTrangThai(TrangThaiPhong.fromDbValue(rs.getString("TrangThai")));
        phong.setMaLoai(rs.getString("MaLoai"));
        return phong;
    }

    private PhongDetail mapResultSetToPhongDetail(ResultSet rs) throws SQLException {
        PhongDetail phongDetail = new PhongDetail();

        // Thuộc tính từ PHONG
        phongDetail.setMaPhong(rs.getString("MaPhong"));
        phongDetail.setTenPhong(rs.getString("TenPhong"));
        phongDetail.setTrangThai(TrangThaiPhong.fromDbValue(rs.getString("TrangThai")));

        // Thuộc tính từ LOAIPHONG (Do câu lệnh JOIN)
        phongDetail.setMaLoai(rs.getString("MaLoai"));
        phongDetail.setTenLoai(rs.getString("TenLoai"));
        phongDetail.setSoNguoiTD(rs.getInt("SoNguoiTD"));
        phongDetail.setDonGia(rs.getBigDecimal("DonGia"));

        return phongDetail;
    }

    public List<PhongDetail> getAll() {
        List<PhongDetail> phongList = new ArrayList<>();
        // Sử dụng JOIN để lấy thông tin chi tiết Loại Phòng
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
            System.err.println("Lỗi khi lấy danh sách phòng chi tiết (getAll): " + e.getMessage());
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

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm phòng: " + e.getMessage());
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

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật phòng " + phong.getMaPhong() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String maPhong) {
        String sql = "DELETE FROM PHONG WHERE MaPhong = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, maPhong);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa phòng " + maPhong + ": " + e.getMessage());
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
            System.err.println("Lỗi khi tìm phòng trống: " + e.getMessage());
            e.printStackTrace();
        }
        return availableRooms;
    }

    public List<PhongDetail> searchAdvanced(String keyword, TrangThaiPhong status, String maLoai) {
        List<PhongDetail> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT p.MaPhong, p.TenPhong, p.TrangThai, p.MaLoai, " +
                        "lp.TenLoai, lp.SoNguoiTD, lp.DonGia " +
                        "FROM PHONG p JOIN LOAIPHONG lp ON p.MaLoai = lp.MaLoai " +
                        "WHERE (p.MaPhong LIKE ? OR p.TenPhong LIKE ?) ");

        if (status != null) {
            sql.append(" AND p.TrangThai = ?");
        }

        if (maLoai != null && !maLoai.isEmpty()) {
            sql.append(" AND p.MaLoai = ?");
        }
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            // Thiết lập tham số cho Từ khóa
            String searchPattern = "%" + keyword + "%";
            int paramIndex = 1;
            ps.setString(paramIndex++, searchPattern);
            ps.setString(paramIndex++, searchPattern);

            // Thiết lập tham số cho Trạng thái (nếu có)
            if (status != null) {
                ps.setString(paramIndex++, status.getDbValue());
            }

            // Thiết lập tham số cho Loại phòng (nếu có)
            if (maLoai != null && !maLoai.isEmpty()) {
                ps.setString(paramIndex++, maLoai);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToPhongDetail(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
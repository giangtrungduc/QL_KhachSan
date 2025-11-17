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

    /**
     * Chuyển đổi ResultSet thành đối tượng Phong (Dùng cho các thao tác CRUD cơ bản).
     */
    private Phong mapResultSetToPhong(ResultSet rs) throws SQLException {
        Phong phong = new Phong();
        phong.setMaPhong(rs.getString("MaPhong"));
        phong.setTenPhong(rs.getString("TenPhong"));
        phong.setTrangThai(TrangThaiPhong.fromDbValue(rs.getString("TrangThai")));
        phong.setMaLoai(rs.getString("MaLoai"));
        return phong;
    }

    /**
     * Chuyển đổi ResultSet thành đối tượng PhongDetail (Dùng cho getAll chi tiết).
     */
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

    // ==================== 1. getAll (Chi tiết) ====================

    /**
     * Lấy danh sách tất cả phòng, bao gồm chi tiết Loại Phòng (JOIN LOAIPHONG).
     */
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

    // ==================== 2. insert ====================

    /**
     * Thêm mới một phòng vào database.
     */
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

    // ==================== 3. update ====================

    /**
     * Cập nhật thông tin phòng (Trừ mã phòng).
     * Hàm này cũng được dùng bởi các Trigger
     */
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

    // ==================== 4. delete ====================

    /**
     * Xóa một phòng theo mã phòng.
     */
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

    // ==================== 5. findAllAvailableRooms ====================

    /**
     * Tìm danh sách phòng TRỐNG trong khoảng thời gian cụ thể (nghiệp vụ chính).
     */
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

            ps.setString(1, TrangThaiPhong.AVAILABLE.getDbValue());
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
}
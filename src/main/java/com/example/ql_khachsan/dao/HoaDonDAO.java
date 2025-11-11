package com.example.ql_khachsan.dao;
import com.example.ql_khachsan.untils.DatabaseConnection;
import com.example.ql_khachsan.models.HoaDon;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {

    /**
     * Hàm nội bộ để map ResultSet sang HoaDon
     */
    private HoaDon mapResultSetToHoaDon(ResultSet rs) throws SQLException {
        HoaDon hd = new HoaDon();
        hd.setMaHD(rs.getString("MaHD"));
        hd.setGhiChu(rs.getString("GhiChu"));
        hd.setNgayLap(rs.getTimestamp("NgayLap").toLocalDateTime());
        hd.setMaDP(rs.getString("MaDP"));
        return hd;
    }

    // 1. Lấy tất cả hóa đơn
    public List<HoaDon> getAll() {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM HOADON";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToHoaDon(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Thêm hóa đơn (Nghiệp vụ Thanh toán)
    public boolean insert(HoaDon hd) throws SQLException {
        String sql = "INSERT INTO HOADON(MaHD, GhiChu, NgayLap, MaDP) VALUES (?, ?, ?, ?)";

        // Sửa: Dùng Connection Pool
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, hd.getMaHD());
            ps.setString(2, hd.getGhiChu());
            ps.setTimestamp(3, Timestamp.valueOf(hd.getNgayLap()));
            ps.setString(4, hd.getMaDP());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    // 3. Cập nhật hóa đơn
    public boolean update(HoaDon hd) {
        String sql = "UPDATE HOADON SET GhiChu = ?, NgayLap = ?, MaDP = ? WHERE MaHD = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, hd.getGhiChu());
            ps.setTimestamp(2, Timestamp.valueOf(hd.getNgayLap()));
            ps.setString(3, hd.getMaDP());
            ps.setString(4, hd.getMaHD());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4. Xóa hóa đơn
    public boolean delete(String maHD) {
        String sql = "DELETE FROM HOADON WHERE MaHD = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maHD);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // =================================================================
    // CÁC HÀM NGHIỆP VỤ (Bổ sung)
    // =================================================================

    /**
     * Hàm nghiệp vụ: Lấy hóa đơn bằng MaDP (Mã Phiếu Đặt Phòng)
     * Dùng để kiểm tra xem phiếu đặt đã được thanh toán hay chưa.
     */
    public HoaDon getByMaDP(String maDP) {
        String sql = "SELECT * FROM HOADON WHERE MaDP = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maDP);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToHoaDon(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Trả về null nếu chưa có hóa đơn
    }
}
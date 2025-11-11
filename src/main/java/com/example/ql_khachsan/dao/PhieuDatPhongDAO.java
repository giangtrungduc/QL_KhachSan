package com.example.ql_khachsan.dao;

import com.example.ql_khachsan.models.PhieuDatPhong;
import com.example.ql_khachsan.untils.TrangThaiDatPhong;
import com.example.ql_khachsan.untils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhieuDatPhongDAO {

    /**
     * Hàm nội bộ để map dữ liệu từ ResultSet sang đối tượng PhieuDatPhong
     */
    private PhieuDatPhong mapResultSetToPhieuDatPhong(ResultSet rs) throws SQLException {
        PhieuDatPhong pdp = new PhieuDatPhong();

        pdp.setMaDP(rs.getString("MaDP"));
        // Lấy DATETIME từ CSDL và chuyển thành LocalDateTime
        pdp.setNgayDat(rs.getTimestamp("NgayDat").toLocalDateTime());
        pdp.setNgayNhan(rs.getTimestamp("NgayNhan").toLocalDateTime());
        pdp.setNgayTra(rs.getTimestamp("NgayTra").toLocalDateTime());

        pdp.setDonGiaThucTe(rs.getBigDecimal("DonGiaThucTe"));

        // Chuyển String từ CSDL thành Enum
        pdp.setTrangThaiDatPhong(TrangThaiDatPhong.valueOf(rs.getString("TrangThaiDP")));

        pdp.setMaKH(rs.getString("MaKH"));
        pdp.setMaPhong(rs.getString("MaPhong"));

        return pdp;
    }

    /**
     * Lấy tất cả các phiếu đặt phòng có trong CSDL
     */
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

    /**
     * Lấy một phiếu đặt phòng bằng Mã DP
     */
    public PhieuDatPhong getById(String maDP) {
        String sql = "SELECT * FROM PHIEUDATPHONG WHERE MaDP = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maDP);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPhieuDatPhong(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Thêm mới một phiếu đặt phòng (Đặt phòng)
     * Tự động kích hoạt Trigger 3 (chống trùng) và Trigger 1 (cập nhật phòng)
     */
    public boolean insert(PhieuDatPhong pdp) throws SQLException {
        String sql = "INSERT INTO PHIEUDATPHONG (MaDP, NgayDat, NgayNhan, NgayTra, DonGiaThucTe, TrangThaiDP, MaKH, MaPhong) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        // Dùng try-with-resources để đảm bảo kết nối được đóng
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, pdp.getMaDP());
            ps.setTimestamp(2, Timestamp.valueOf(pdp.getNgayDat()));
            ps.setTimestamp(3, Timestamp.valueOf(pdp.getNgayNhan()));
            ps.setTimestamp(4, Timestamp.valueOf(pdp.getNgayTra()));
            ps.setBigDecimal(5, pdp.getDonGiaThucTe());
            ps.setString(6, pdp.getTrangThaiDatPhong().toString()); // Chuyển Enum thành String
            ps.setString(7, pdp.getMaKH());
            ps.setString(8, pdp.getMaPhong());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            // Ném lỗi ra ngoài để JavaFX bắt và hiển thị cho người dùng
            // (Đặc biệt là lỗi "Phòng này đã được đặt" từ Trigger 3)
            throw e;
        }
    }

    /**
     * Cập nhật thông tin phiếu đặt (Check-in, Hủy phòng)
     * Tự động kích hoạt Trigger 1 (cập nhật phòng)
     */
    public boolean update(PhieuDatPhong pdp) {
        String sql = "UPDATE PHIEUDATPHONG SET NgayDat = ?, NgayNhan = ?, NgayTra = ?, DonGiaThucTe = ?, " +
                "TrangThaiDP = ?, MaKH = ?, MaPhong = ? WHERE MaDP = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(pdp.getNgayDat()));
            ps.setTimestamp(2, Timestamp.valueOf(pdp.getNgayNhan()));
            ps.setTimestamp(3, Timestamp.valueOf(pdp.getNgayTra()));
            ps.setBigDecimal(4, pdp.getDonGiaThucTe());
            ps.setString(5, pdp.getTrangThaiDatPhong().toString());
            ps.setString(6, pdp.getMaKH());
            ps.setString(7, pdp.getMaPhong());
            ps.setString(8, pdp.getMaDP()); // Điều kiện WHERE

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa một phiếu đặt phòng (ít dùng, thường chỉ 'Hủy phòng')
     */
    public boolean delete(String maDP) {
        String sql = "DELETE FROM PHIEUDATPHONG WHERE MaDP = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maDP);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // =================================================================
    // CÁC HÀM NGHIỆP VỤ (Business Methods)
    // =================================================================

    /**
     * Phục vụ chức năng "Xem lịch sử" của Khách hàng
     */
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

    /**
     * Phục vụ chức năng "Check-in" và "Thanh toán"
     * Tìm phiếu đang hoạt động (Đã đặt hoặc Đang sử dụng) của 1 phòng
     */
    public PhieuDatPhong findActiveBookingByRoomId(String maPhong) {
        String sql = "SELECT * FROM PHIEUDATPHONG WHERE MaPhong = ? AND (TrangThaiDP = ? OR TrangThaiDP = ?) " +
                "ORDER BY NgayDat DESC LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maPhong);
            // Lấy tên Enum (ví dụ: "DA_DAT") để so sánh trong CSDL
            ps.setString(2, TrangThaiDatPhong.DA_DAT.toString());
            ps.setString(3, TrangThaiDatPhong.DANG_SU_DUNG.toString());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPhieuDatPhong(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Không tìm thấy phiếu nào đang hoạt động
    }
}
package com.example.ql_khachsan.dao;

// Sửa: Import lớp Connection Pool của bạn
import com.example.ql_khachsan.untils.DatabaseConnection;
import com.example.ql_khachsan.models.LoaiPhong;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoaiPhongDAO {

    /**
     * Lấy tất cả các loại phòng
     */
    public List<LoaiPhong> getAll() {
        List<LoaiPhong> list = new ArrayList<>();
        String sql = "SELECT * FROM LOAIPHONG";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                LoaiPhong lp = new LoaiPhong();
                lp.setMaLoai(rs.getString("MaLoai"));
                lp.setTenLoai(rs.getString("TenLoai"));
                lp.setSoNguoiTD(rs.getInt("SoNguoiTD"));
                lp.setDonGia(rs.getBigDecimal("DonGia"));
                list.add(lp);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Thêm mới một loại phòng
     */
    public boolean insert(LoaiPhong lp) {
        String sql = "INSERT INTO LOAIPHONG(MaLoai, TenLoai, SoNguoiTD, DonGia) VALUES (?, ?, ?, ?)";

        // Sửa: Dùng Connection Pool
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, lp.getMaLoai());
            ps.setString(2, lp.getTenLoai());
            ps.setInt(3, lp.getSoNguoiTD());
            ps.setBigDecimal(4, lp.getDonGia());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật một loại phòng (Thêm)
     */
    public boolean update(LoaiPhong lp) {
        String sql = "UPDATE LOAIPHONG SET TenLoai = ?, SoNguoiTD = ?, DonGia = ? WHERE MaLoai = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, lp.getTenLoai());
            ps.setInt(2, lp.getSoNguoiTD());
            ps.setBigDecimal(3, lp.getDonGia());
            ps.setString(4, lp.getMaLoai());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa một loại phòng (Thêm)
     */
    public boolean delete(String maLoai) {
        String sql = "DELETE FROM LOAIPHONG WHERE MaLoai = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maLoai);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            // Lỗi này thường xảy ra khi cố xóa 1 Loại phòng
            // mà vẫn còn Phòng (PHONG) đang tham chiếu đến nó.
            e.printStackTrace();
            return false;
        }
    }
}
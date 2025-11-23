package com.example.ql_khachsan.dao;

import com.example.ql_khachsan.untils.DatabaseConnection;
import com.example.ql_khachsan.models.HoaDon;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeFormatter;

public class HoaDonDAO {

    // Định dạng ngày giờ cho SQL
    private final DateTimeFormatter dtfDB = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Hàm nội bộ để map ResultSet sang HoaDon (chỉ các trường cơ bản)
     */
    private HoaDon mapResultSetToHoaDon(ResultSet rs) throws SQLException {
        HoaDon hd = new HoaDon();
        hd.maHDProperty().set(rs.getString("MaHD"));
        hd.ghiChuProperty().set(rs.getString("GhiChu"));
        hd.ngayLapProperty().set(rs.getTimestamp("NgayLap").toLocalDateTime());
        hd.maDPProperty().set(rs.getString("MaDP"));
        return hd;
    }

    // [GIỮ NGUYÊN CÁC HÀM CRUD (getAll, insert, update, delete, getByMaDP) Ở ĐÂY]
    // ...

    // =================================================================
    // CÁC HÀM NGHIỆP VỤ (Chi tiết lịch sử hóa đơn)
    // =================================================================

    /**
     * Lấy danh sách hóa đơn chi tiết kèm thông tin KH, Phòng, Giá thực tế.
     * @param tenKH Tên khách hàng (dùng LIKE, có thể là null)
     * @param from Ngày lập HĐ từ (có thể là null)
     * @param to Ngày lập HĐ đến (có thể là null)
     * @return List<HoaDon> đã được điền đầy đủ thông tin chi tiết.
     */
    public List<HoaDon> getChiTietHoaDonList(String tenKH, LocalDate from, LocalDate to) {
        List<HoaDon> list = new ArrayList<>();

        // SQL JOIN lấy DonGiaThucTe và TenLoai
        String sql = "SELECT HD.MaHD, HD.NgayLap, HD.MaDP, HD.GhiChu, " +
                "DP.NgayNhan, DP.NgayTra, DP.DonGiaThucTe, " +
                "KH.HoTen, KH.SDT, KH.Email, P.TenPhong, LP.TenLoai " +
                "FROM HOADON HD " +
                "JOIN PHIEUDATPHONG DP ON HD.MaDP = DP.MaDP " +
                "JOIN PHONG P ON DP.MaPhong = P.MaPhong " +
                "JOIN LOAIPHONG LP ON P.MaLoai = LP.MaLoai " +
                "JOIN KHACHHANG KH ON DP.MaKH = KH.MaKH " +
                "WHERE 1=1";

        if (tenKH != null) sql += " AND KH.HoTen LIKE ?";
        if (from != null) sql += " AND HD.NgayLap >= ?";
        if (to != null) sql += " AND HD.NgayLap <= ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int idx = 1;
            if (tenKH != null) stmt.setString(idx++, "%" + tenKH + "%");
            if (from != null) stmt.setString(idx++, from.atStartOfDay().format(dtfDB));
            if (to != null) stmt.setString(idx++, to.atTime(23,59,59).format(dtfDB));

            ResultSet rs = stmt.executeQuery();
            int sttCounter = 1;
            while (rs.next()) {
                LocalDateTime ngayLap = rs.getTimestamp("NgayLap").toLocalDateTime();
                LocalDateTime ngayNhan = rs.getTimestamp("NgayNhan").toLocalDateTime();
                LocalDateTime ngayTra = rs.getTimestamp("NgayTra").toLocalDateTime();

                double donGiaMotDem = rs.getDouble("DonGiaThucTe");

                // Tính số đêm (làm tròn lên)
                long soGio = Duration.between(ngayNhan, ngayTra).toHours();
                long soDem = (soGio + 23) / 24;
                if (soDem == 0 && soGio > 0) soDem = 1;

                double tongTien = soDem * donGiaMotDem;

                // Tạo đối tượng và điền thông tin chi tiết
                HoaDon hd = new HoaDon(
                        sttCounter++,
                        rs.getString("MaHD"),
                        rs.getString("MaDP"),
                        rs.getString("GhiChu"),
                        ngayLap,
                        tongTien
                );

                hd.setTenKH(rs.getString("HoTen"));
                hd.setSdt(rs.getString("SDT"));
                hd.setEmail(rs.getString("Email"));
                hd.setTenPhong(rs.getString("TenPhong"));
                hd.setTenLoai(rs.getString("TenLoai"));
                hd.setDonGiaThucTe(donGiaMotDem);
                hd.setNgayNhan(ngayNhan);
                hd.setNgayTra(ngayTra);

                list.add(hd);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
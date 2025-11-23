package com.example.ql_khachsan.dao;

import com.example.ql_khachsan.models.HoaDon;
import com.example.ql_khachsan.untils.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {

    private final DateTimeFormatter dtfDB = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public boolean insert(HoaDon hd) {
        String sql = "INSERT INTO HOADON (MaHD, GhiChu, NgayLap, TongTien, MaDP) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hd.getMaHD());
            ps.setString(2, hd.getGhiChu());
            ps.setTimestamp(3, Timestamp.valueOf(hd.getNgayLap()));
            ps.setBigDecimal(4, hd.getTongTien());
            ps.setString(5, hd.getMaDP());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<HoaDon> getChiTietHoaDonList(String tenKH, LocalDate from, LocalDate to) {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT HD.MaHD, HD.NgayLap, HD.MaDP, HD.GhiChu, " +
                "DP.NgayNhan, DP.NgayTra, DP.DonGiaThucTe, " +
                "KH.HoTen, KH.SDT, KH.Email, P.TenPhong, LP.TenLoai " +
                "FROM HOADON HD " +
                "JOIN PHIEUDATPHONG DP ON HD.MaDP = DP.MaDP " +
                "JOIN PHONG P ON DP.MaPhong = P.MaPhong " +
                "JOIN LOAIPHONG LP ON P.MaLoai = LP.MaLoai " +
                "JOIN KHACHHANG KH ON DP.MaKH = KH.MaKH " +
                "WHERE 1=1";

        if (tenKH != null && !tenKH.isEmpty()) sql += " AND KH.HoTen LIKE ?";
        if (from != null) sql += " AND HD.NgayLap >= ?";
        if (to != null) sql += " AND HD.NgayLap <= ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int idx = 1;
            if (tenKH != null && !tenKH.isEmpty()) stmt.setString(idx++, "%" + tenKH + "%");
            if (from != null) stmt.setString(idx++, from.atStartOfDay().format(dtfDB));
            if (to != null) stmt.setString(idx++, to.atTime(23, 59, 59).format(dtfDB));

            ResultSet rs = stmt.executeQuery();
            int sttCounter = 1;
            while (rs.next()) {
                LocalDateTime ngayLap = rs.getTimestamp("NgayLap").toLocalDateTime();
                LocalDateTime ngayNhan = rs.getTimestamp("NgayNhan").toLocalDateTime();
                LocalDateTime ngayTra = rs.getTimestamp("NgayTra").toLocalDateTime();
                BigDecimal donGiaMotDem = rs.getBigDecimal("DonGiaThucTe");

                long soGio = Duration.between(ngayNhan, ngayTra).toHours();
                long soDem = (soGio + 23) / 24;
                if (soDem == 0 && soGio > 0) soDem = 1;

                BigDecimal tongTien = donGiaMotDem.multiply(BigDecimal.valueOf(soDem));

                HoaDon hd = new HoaDon(
                        sttCounter++,
                        rs.getString("MaHD"),
                        rs.getString("MaDP"),
                        rs.getString("GhiChu"),
                        ngayLap,
                        tongTien,
                        rs.getString("HoTen")
                );

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
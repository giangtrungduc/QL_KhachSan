package com.example.ql_khachsan.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import  java.util.List;

import com.example.ql_khachsan.models.LoaiPhong;

public class LoaiPhongDAO {

    private final String url = "jdbc:mysql://localhost:3306/qlkhachsan?useSSL=false&serverTimezone=UTC";
    private final String user = "root";
    private final String password = "123456";

    public List<LoaiPhong> getAllLoaiPhong() {
        List<LoaiPhong> list = new ArrayList<>();
        String sql = "SELECT * FROM LOAIPHONG";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new LoaiPhong(
                        rs.getString("MaLoai"),
                        rs.getString("TenLoai"),
                        rs.getInt("SoNguoiTD"),
                        rs.getDouble("DonGia")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertLoaiPhong(LoaiPhong lp) {
        String sql = "INSERT INTO LOAIPHONG(MaLoai, TenLoai, SoNguoiTD, DonGia) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, lp.getMaLoai());
            ps.setString(2, lp.getTenLoai());
            ps.setInt(3, lp.getSoNguoiTD());
            ps.setDouble(4, lp.getDonGia());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

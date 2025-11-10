package com.example.ql_khachsan.models;

// Lớp đại diện cho bảng PHONG
public class Phong {
    private String maPhong;
    private String tenPhong;
    private String trangThai; // VD: "Trống", "Đã đặt", "Đang sử dụng"
    private String maLoai; // Foreign Key tham chiếu đến LOAIPHONG

    // Constructor mặc định (cần thiết cho các framework/JDBC)
    public Phong() {
    }

    // Constructor đầy đủ
    public Phong(String maPhong, String tenPhong, String trangThai, String maLoai) {
        this.maPhong = maPhong;
        this.tenPhong = tenPhong;
        this.trangThai = trangThai;
        this.maLoai = maLoai;
    }

    // --- Getters and Setters ---

    public String getMaPhong() {
        return maPhong;
    }

    public void setMaPhong(String maPhong) {
        this.maPhong = maPhong;
    }

    public String getTenPhong() {
        return tenPhong;
    }

    public void setTenPhong(String tenPhong) {
        this.tenPhong = tenPhong;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getMaLoai() {
        return maLoai;
    }

    public void setMaLoai(String maLoai) {
        this.maLoai = maLoai;
    }
}

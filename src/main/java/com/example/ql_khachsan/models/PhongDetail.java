package com.example.ql_khachsan.models;

import java.math.BigDecimal; // Cần thiết cho DonGia

/**
 * Data Transfer Object (DTO) cho dữ liệu Phòng Chi Tiết.
 * Chứa thuộc tính của bảng PHONG và LOAIPHONG (sau khi JOIN).
 */
public class PhongDetail {
    // Thuộc tính từ bảng PHONG
    private String maPhong;
    private String tenPhong;
    private String trangThai;

    // Thuộc tính từ bảng LOAIPHONG
    private String maLoai; // Giữ lại để tham chiếu
    private String tenLoai;
    private int soNguoiTD;
    private BigDecimal donGia;

    // Constructor mặc định
    public PhongDetail() {
    }

    // Constructor đầy đủ (Tùy chọn, thường dùng cho việc tạo đối tượng thủ công)
    public PhongDetail(String maPhong, String tenPhong, String trangThai, String maLoai, String tenLoai, int soNguoiTD, BigDecimal donGia) {
        this.maPhong = maPhong;
        this.tenPhong = tenPhong;
        this.trangThai = trangThai;
        this.maLoai = maLoai;
        this.tenLoai = tenLoai;
        this.soNguoiTD = soNguoiTD;
        this.donGia = donGia;
    }

    // --- Getters and Setters ---

    // Thuộc tính từ PHONG
    public String getMaPhong() { return maPhong; }
    public void setMaPhong(String maPhong) { this.maPhong = maPhong; }

    public String getTenPhong() { return tenPhong; }
    public void setTenPhong(String tenPhong) { this.tenPhong = tenPhong; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    // Thuộc tính từ LOAIPHONG
    public String getMaLoai() { return maLoai; }
    public void setMaLoai(String maLoai) { this.maLoai = maLoai; }

    public String getTenLoai() { return tenLoai; }
    public void setTenLoai(String tenLoai) { this.tenLoai = tenLoai; }

    public int getSoNguoiTD() { return soNguoiTD; }
    public void setSoNguoiTD(int soNguoiTD) { this.soNguoiTD = soNguoiTD; }

    public BigDecimal getDonGia() { return donGia; }
    public void setDonGia(BigDecimal donGia) { this.donGia = donGia; }
}
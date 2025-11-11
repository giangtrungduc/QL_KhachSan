package com.example.ql_khachsan.models;

import javafx.beans.property.*;
import java.math.BigDecimal;

/**
 * DTO (ViewModel) chứa thông tin JOIN từ PHONG và LOAIPHONG.
 * Dùng để hiển thị lên giao diện JavaFX.
 */
public class PhongDetail {
    // Thuộc tính từ PHONG
    private final StringProperty maPhong;
    private final StringProperty tenPhong;
    private final StringProperty trangThai;

    // Thuộc tính từ LOAIPHONG
    private final StringProperty maLoai;
    private final StringProperty tenLoai;
    private final IntegerProperty soNguoiTD;
    private final ObjectProperty<BigDecimal> donGia;

    // Constructor mặc định
    public PhongDetail() {
        this.maPhong = new SimpleStringProperty();
        this.tenPhong = new SimpleStringProperty();
        this.trangThai = new SimpleStringProperty();
        this.maLoai = new SimpleStringProperty();
        this.tenLoai = new SimpleStringProperty();
        this.soNguoiTD = new SimpleIntegerProperty();
        this.donGia = new SimpleObjectProperty<>();
    }

    // --- Getters, Setters, và Properties ---

    public String getMaPhong() { return maPhong.get(); }
    public void setMaPhong(String maPhong) { this.maPhong.set(maPhong); }
    public StringProperty maPhongProperty() { return maPhong; }

    public String getTenPhong() { return tenPhong.get(); }
    public void setTenPhong(String tenPhong) { this.tenPhong.set(tenPhong); }
    public StringProperty tenPhongProperty() { return tenPhong; }

    public String getTrangThai() { return trangThai.get(); }
    public void setTrangThai(String trangThai) { this.trangThai.set(trangThai); }
    public StringProperty trangThaiProperty() { return trangThai; }

    public String getMaLoai() { return maLoai.get(); }
    public void setMaLoai(String maLoai) { this.maLoai.set(maLoai); }
    public StringProperty maLoaiProperty() { return maLoai; }

    public String getTenLoai() { return tenLoai.get(); }
    public void setTenLoai(String tenLoai) { this.tenLoai.set(tenLoai); }
    public StringProperty tenLoaiProperty() { return tenLoai; }

    public int getSoNguoiTD() { return soNguoiTD.get(); }
    public void setSoNguoiTD(int soNguoiTD) { this.soNguoiTD.set(soNguoiTD); }
    public IntegerProperty soNguoiTDProperty() { return soNguoiTD; }

    public BigDecimal getDonGia() { return donGia.get(); }
    public void setDonGia(BigDecimal donGia) { this.donGia.set(donGia); }
    public ObjectProperty<BigDecimal> donGiaProperty() { return donGia; }
}
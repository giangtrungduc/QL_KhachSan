package com.example.ql_khachsan.models;

import com.example.ql_khachsan.untils.TrangThaiPhong;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

// Lớp đại diện cho bảng PHONG (dùng cho Insert/Update)
public class Phong {
    private final StringProperty maPhong;
    private final StringProperty tenPhong;
    private final ObjectProperty<TrangThaiPhong> trangThai;
    private final StringProperty maLoai;

    // Constructor mặc định
    public Phong() {
        this.maPhong = new SimpleStringProperty();
        this.tenPhong = new SimpleStringProperty();
        this.trangThai = new SimpleObjectProperty<>();
        this.maLoai = new SimpleStringProperty();
    }

    // Constructor đầy đủ
    public Phong(String maPhong, String tenPhong, String trangThai, String maLoai) {
        this.maPhong = new SimpleStringProperty(maPhong);
        this.tenPhong = new SimpleStringProperty(tenPhong);
        this.trangThai = new SimpleObjectProperty<>(TrangThaiPhong.valueOf(trangThai));
        this.maLoai = new SimpleStringProperty(maLoai);
    }

    public Phong(String maPhong, String tenPhong, TrangThaiPhong trangThai, String maLoai) {
        this.maPhong = new SimpleStringProperty(maPhong);
        this.tenPhong = new SimpleStringProperty(tenPhong);
        this.trangThai = new SimpleObjectProperty<>(trangThai);
        this.maLoai = new SimpleStringProperty(maLoai);
    }

    // --- Getters, Setters, và Properties ---

    public String getMaPhong() { return maPhong.get(); }
    public void setMaPhong(String maPhong) { this.maPhong.set(maPhong); }
    public StringProperty maPhongProperty() { return maPhong; }

    public String getTenPhong() { return tenPhong.get(); }
    public void setTenPhong(String tenPhong) { this.tenPhong.set(tenPhong); }
    public StringProperty tenPhongProperty() { return tenPhong; }

    public TrangThaiPhong getTrangThai() { return trangThai.get(); }
    public void setTrangThai(TrangThaiPhong trangThai) { this.trangThai.set(trangThai); }
    public ObjectProperty<TrangThaiPhong> trangThaiProperty() { return trangThai; }

    public String getMaLoai() { return maLoai.get(); }
    public void setMaLoai(String maLoai) { this.maLoai.set(maLoai); }
    public StringProperty maLoaiProperty() { return maLoai; }
}
package com.example.ql_khachsan.models;

import com.example.ql_khachsan.untils.TrangThaiPhong;
import javafx.beans.property.*;

public class Phong {
    private final StringProperty maPhong;
    private final StringProperty tenPhong;
    private final ObjectProperty<TrangThaiPhong> trangThai;
    private final StringProperty maLoai;

    public Phong() {
        this.maPhong = new SimpleStringProperty();
        this.tenPhong = new SimpleStringProperty();
        this.trangThai = new SimpleObjectProperty<>(TrangThaiPhong.TRONG);
        this.maLoai = new SimpleStringProperty();
    }

    public Phong(String maPhong, String tenPhong, TrangThaiPhong trangThai, String maLoai) {
        this.maPhong = new SimpleStringProperty(maPhong);
        this.tenPhong = new SimpleStringProperty(tenPhong);
        this.trangThai = new SimpleObjectProperty<>(trangThai);
        this.maLoai = new SimpleStringProperty(maLoai);
    }

    public Phong(String maPhong, String tenPhong, String trangThaiStr, String maLoai) {
        this.maPhong = new SimpleStringProperty(maPhong);
        this.tenPhong = new SimpleStringProperty(tenPhong);
        this.trangThai = new SimpleObjectProperty<>(TrangThaiPhong.fromDbValue(trangThaiStr));
        this.maLoai = new SimpleStringProperty(maLoai);
    }

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
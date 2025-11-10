package com.example.ql_khachsan.models;

import javafx.beans.property.*;

public class LoaiPhong {
    private StringProperty maLoai;
    private StringProperty tenLoai;
    private IntegerProperty soNguoiTD;
    private DoubleProperty donGia;

    public LoaiPhong(String maLoai, String tenLoai, int soNguoiTD, double donGia) {
        this.maLoai = new SimpleStringProperty(maLoai);
        this.tenLoai = new SimpleStringProperty(tenLoai);
        this.soNguoiTD = new SimpleIntegerProperty(soNguoiTD);
        this.donGia = new SimpleDoubleProperty(donGia);
    }

    // Getter/Setter + Property
    public String getMaLoai() { return maLoai.get(); }
    public void setMaLoai(String value) { maLoai.set(value); }
    public StringProperty maLoaiProperty() { return maLoai; }

    public String getTenLoai() { return tenLoai.get(); }
    public void setTenLoai(String value) { tenLoai.set(value); }
    public StringProperty tenLoaiProperty() { return tenLoai; }

    public int getSoNguoiTD() { return soNguoiTD.get(); }
    public void setSoNguoiTD(int value) { soNguoiTD.set(value); }
    public IntegerProperty soNguoiTDProperty() { return soNguoiTD; }

    public double getDonGia() { return donGia.get(); }
    public void setDonGia(double value) { donGia.set(value); }
    public DoubleProperty donGiaProperty() { return donGia; }
}

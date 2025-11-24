package com.example.ql_khachsan.models;

import javafx.beans.property.*;
import java.math.BigDecimal;

public class LoaiPhong {
    private final StringProperty maLoai;
    private final StringProperty tenLoai;
    private final IntegerProperty soNguoiTD;
    private final ObjectProperty<BigDecimal> donGia;

    public LoaiPhong() {
        this.maLoai = new SimpleStringProperty();
        this.tenLoai = new SimpleStringProperty();
        this.soNguoiTD = new SimpleIntegerProperty();
        this.donGia = new SimpleObjectProperty<>();
    }

    public LoaiPhong(String maLoai, String tenLoai, int soNguoiTD, BigDecimal donGia) {
        this.maLoai = new SimpleStringProperty(maLoai);
        this.tenLoai = new SimpleStringProperty(tenLoai);
        this.soNguoiTD = new SimpleIntegerProperty(soNguoiTD);
        this.donGia = new SimpleObjectProperty<>(donGia);
    }

    public String getMaLoai() { return maLoai.get(); }
    public void setMaLoai(String value) { maLoai.set(value); }
    public StringProperty maLoaiProperty() { return maLoai; }

    public String getTenLoai() { return tenLoai.get(); }
    public void setTenLoai(String value) { tenLoai.set(value); }
    public StringProperty tenLoaiProperty() { return tenLoai; }

    public int getSoNguoiTD() { return soNguoiTD.get(); }
    public void setSoNguoiTD(int value) { soNguoiTD.set(value); }
    public IntegerProperty soNguoiTDProperty() { return soNguoiTD; }

    public BigDecimal getDonGia() { return donGia.get(); }
    public void setDonGia(BigDecimal value) { donGia.set(value); }
    public ObjectProperty<BigDecimal> donGiaProperty() { return donGia; }

    @Override
    public String toString() {
        return tenLoai.get();
    }
}
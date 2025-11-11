package com.example.ql_khachsan.models;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.time.LocalDateTime;

/**
 * Model đại diện cho bảng HOADON
 */
public class HoaDon {
    // Thuộc tính đã chuyển đổi
    private final StringProperty maHD;
    private final StringProperty ghiChu;
    private final ObjectProperty<LocalDateTime> ngayLap;
    private final StringProperty maDP;

    /**
     * Constructor rỗng
     */
    public HoaDon() {
        this.maHD = new SimpleStringProperty();
        this.ghiChu = new SimpleStringProperty();
        this.ngayLap = new SimpleObjectProperty<>();
        this.maDP = new SimpleStringProperty();
    }

    /**
     * Constructor đầy đủ
     */
    public HoaDon(String maHD, String ghiChu, LocalDateTime ngayLap, String maDP) {
        this.maHD = new SimpleStringProperty(maHD);
        this.ghiChu = new SimpleStringProperty(ghiChu);
        this.ngayLap = new SimpleObjectProperty<>(ngayLap);
        this.maDP = new SimpleStringProperty(maDP);
    }

    // --- Getters, Setters, và Properties (Mô hình chuẩn JavaFX) ---

    public String getMaHD() { return maHD.get(); }
    public void setMaHD(String maHD) { this.maHD.set(maHD); }
    public StringProperty maHDProperty() { return maHD; }

    public String getGhiChu() { return ghiChu.get(); }
    public void setGhiChu(String ghiChu) { this.ghiChu.set(ghiChu); }
    public StringProperty ghiChuProperty() { return ghiChu; }

    public LocalDateTime getNgayLap() { return ngayLap.get(); }
    public void setNgayLap(LocalDateTime ngayLap) { this.ngayLap.set(ngayLap); }
    public ObjectProperty<LocalDateTime> ngayLapProperty() { return ngayLap; }

    public String getMaDP() { return maDP.get(); }
    public void setMaDP(String maDP) { this.maDP.set(maDP); }
    public StringProperty maDPProperty() { return maDP; }

    @Override
    public String toString() {
        return "HoaDon{" +
                "maHD=" + maHD.get() +
                ", ngayLap=" + ngayLap.get() +
                ", maDP=" + maDP.get() +
                '}';
    }
}
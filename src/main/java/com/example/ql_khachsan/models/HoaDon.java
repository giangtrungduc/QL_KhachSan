package com.example.ql_khachsan.models;

import javafx.beans.property.*;

import java.time.LocalDateTime;

public class HoaDon {
    private final IntegerProperty stt;
    private final StringProperty maHD;
    private final StringProperty maDP;
    private final StringProperty ghiChu;
    private final ObjectProperty<LocalDateTime> ngayLap;
    private final DoubleProperty tongTien;

    public HoaDon() {
        this.stt = new SimpleIntegerProperty();
        this.maHD = new SimpleStringProperty();
        this.maDP = new SimpleStringProperty();
        this.ghiChu = new SimpleStringProperty();
        this.ngayLap = new SimpleObjectProperty<>();
        this.tongTien = new SimpleDoubleProperty();
    }

    public HoaDon(int stt, String maHD, String maDP, String ghiChu, LocalDateTime ngayLap, double tongTien) {
        this.stt = new SimpleIntegerProperty(stt);
        this.maHD = new SimpleStringProperty(maHD);
        this.maDP = new SimpleStringProperty(maDP);
        this.ghiChu = new SimpleStringProperty(ghiChu);
        this.ngayLap = new SimpleObjectProperty<>(ngayLap);
        this.tongTien = new SimpleDoubleProperty(tongTien);
    }

    // --- Getters / Setters / Property ---
    public int getStt() { return stt.get(); }
    public void setStt(int value) { stt.set(value); }
    public IntegerProperty sttProperty() { return stt; }

    public String getMaHD() { return maHD.get(); }
    public void setMaHD(String value) { maHD.set(value); }
    public StringProperty maHDProperty() { return maHD; }

    public String getMaDP() { return maDP.get(); }
    public void setMaDP(String value) { maDP.set(value); }
    public StringProperty maDPProperty() { return maDP; }

    public String getGhiChu() { return ghiChu.get(); }
    public void setGhiChu(String value) { ghiChu.set(value); }
    public StringProperty ghiChuProperty() { return ghiChu; }

    public LocalDateTime getNgayLap() { return ngayLap.get(); }
    public void setNgayLap(LocalDateTime value) { ngayLap.set(value); }
    public ObjectProperty<LocalDateTime> ngayLapProperty() { return ngayLap; }

    public double getTongTien() { return tongTien.get(); }
    public void setTongTien(double value) { tongTien.set(value); }
    public DoubleProperty tongTienProperty() { return tongTien; }
}

package com.example.ql_khachsan.models;

import javafx.beans.property.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class HoaDon {
    private final IntegerProperty stt;
    private final StringProperty maHD;
    private final StringProperty maDP;
    private final StringProperty ghiChu;
    private final ObjectProperty<LocalDateTime> ngayLap;
    private final ObjectProperty<BigDecimal> tongTien;
    private final StringProperty tenKH;

    private String sdt;
    private String email;
    private String tenPhong;
    private String tenLoai;
    private BigDecimal donGiaThucTe;
    private LocalDateTime ngayNhan;
    private LocalDateTime ngayTra;

    public HoaDon() {
        this.stt = new SimpleIntegerProperty();
        this.maHD = new SimpleStringProperty();
        this.maDP = new SimpleStringProperty();
        this.ghiChu = new SimpleStringProperty();
        this.ngayLap = new SimpleObjectProperty<>();
        this.tongTien = new SimpleObjectProperty<>();
        this.tenKH = new SimpleStringProperty();
    }

    public HoaDon(int stt, String maHD, String maDP, String ghiChu, LocalDateTime ngayLap, BigDecimal tongTien, String tenKH) {
        this.stt = new SimpleIntegerProperty(stt);
        this.maHD = new SimpleStringProperty(maHD);
        this.maDP = new SimpleStringProperty(maDP);
        this.ghiChu = new SimpleStringProperty(ghiChu);
        this.ngayLap = new SimpleObjectProperty<>(ngayLap);
        this.tongTien = new SimpleObjectProperty<>(tongTien);
        this.tenKH = new SimpleStringProperty(tenKH);
    }

    public int getStt() { return stt.get(); }
    public IntegerProperty sttProperty() { return stt; }

    public String getMaHD() { return maHD.get(); }
    public StringProperty maHDProperty() { return maHD; }

    public String getMaDP() { return maDP.get(); }
    public StringProperty maDPProperty() { return maDP; }

    public String getGhiChu() { return ghiChu.get(); }
    public StringProperty ghiChuProperty() { return ghiChu; }

    public LocalDateTime getNgayLap() { return ngayLap.get(); }
    public ObjectProperty<LocalDateTime> ngayLapProperty() { return ngayLap; }

    public BigDecimal getTongTien() { return tongTien.get(); }
    public ObjectProperty<BigDecimal> tongTienProperty() { return tongTien; }

    public String getTenKH() { return tenKH.get(); }
    public void setTenKH(String tenKH) { this.tenKH.set(tenKH); }
    public StringProperty tenKHProperty() { return tenKH; }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTenPhong() { return tenPhong; }
    public void setTenPhong(String tenPhong) { this.tenPhong = tenPhong; }

    public String getTenLoai() { return tenLoai; }
    public void setTenLoai(String tenLoai) { this.tenLoai = tenLoai; }

    public BigDecimal getDonGiaThucTe() { return donGiaThucTe; }
    public void setDonGiaThucTe(BigDecimal donGiaThucTe) { this.donGiaThucTe = donGiaThucTe; }

    public LocalDateTime getNgayNhan() { return ngayNhan; }
    public void setNgayNhan(LocalDateTime ngayNhan) { this.ngayNhan = ngayNhan; }

    public LocalDateTime getNgayTra() { return ngayTra; }
    public void setNgayTra(LocalDateTime ngayTra) { this.ngayTra = ngayTra; }
}
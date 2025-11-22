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

    // --- Bổ sung thông tin chi tiết ---
    private String tenKhachHang;
    private String sdt;
    private String email;
    private String tenPhong;
    private LocalDateTime ngayNhan;
    private LocalDateTime ngayTra;

    // Constructor cơ bản cho TableView
    public HoaDon(int stt, String maHD, String maDP, String ghiChu, LocalDateTime ngayLap, double tongTien) {
        this.stt = new SimpleIntegerProperty(stt);
        this.maHD = new SimpleStringProperty(maHD);
        this.maDP = new SimpleStringProperty(maDP);
        this.ghiChu = new SimpleStringProperty(ghiChu);
        this.ngayLap = new SimpleObjectProperty<>(ngayLap);
        this.tongTien = new SimpleDoubleProperty(tongTien);
    }

    public HoaDon() {
        // Khởi tạo tất cả các trường Property
        this.stt = new SimpleIntegerProperty();
        this.maHD = new SimpleStringProperty();
        this.maDP = new SimpleStringProperty();
        this.ghiChu = new SimpleStringProperty();
        this.ngayLap = new SimpleObjectProperty<>();
        this.tongTien = new SimpleDoubleProperty();

        // Khởi tạo các trường chi tiết
        this.tenKhachHang = null;
        this.sdt = null;
        this.email = null;
        this.tenPhong = null;
        this.ngayNhan = null;
        this.ngayTra = null;
    }
    // Getters and Setters cho các thuộc tính chi tiết (không cần Property vì không hiển thị trên TableView chính)

    public String getTenKhachHang() { return tenKhachHang; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTenPhong() { return tenPhong; }
    public void setTenPhong(String tenPhong) { this.tenPhong = tenPhong; }

    public LocalDateTime getNgayNhan() { return ngayNhan; }
    public void setNgayNhan(LocalDateTime ngayNhan) { this.ngayNhan = ngayNhan; }

    public LocalDateTime getNgayTra() { return ngayTra; }
    public void setNgayTra(LocalDateTime ngayTra) { this.ngayTra = ngayTra; }

    // Getters và Property cho TableView
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

    public double getTongTien() { return tongTien.get(); }
    public DoubleProperty tongTienProperty() { return tongTien; }
}
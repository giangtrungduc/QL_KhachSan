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

    private final StringProperty tenKH;

    // --- Bổ sung thông tin chi tiết (Lấy từ JOIN) ---
    // Các trường này không cần Property vì chúng chỉ hiển thị chi tiết trong cửa sổ pop-up,
    // không phải cột chính của TableView.
    private String sdt;
    private String email;
    private String tenPhong;
    private String tenLoai;
    private double donGiaThucTe;
    private LocalDateTime ngayNhan;
    private LocalDateTime ngayTra;


    // Constructor cơ bản cho TableView (Thêm tham số tenKhachHang)
    public HoaDon(int stt, String maHD, String maDP, String ghiChu, LocalDateTime ngayLap, double tongTien, String tenKH) {
        this.stt = new SimpleIntegerProperty(stt);
        this.maHD = new SimpleStringProperty(maHD);
        this.maDP = new SimpleStringProperty(maDP);
        this.ghiChu = new SimpleStringProperty(ghiChu);
        this.ngayLap = new SimpleObjectProperty<>(ngayLap);
        this.tongTien = new SimpleDoubleProperty(tongTien);
        this.tenKH = new SimpleStringProperty(tenKH);
    }

    // Constructor (Chỉ dùng cho Controller khi gọi DAO)
    // Giữ nguyên constructor này nhưng cần đảm bảo các Property được khởi tạo
    public HoaDon(int stt, String maHD, String maDP, String ghiChu, LocalDateTime ngayLap, double tongTien) {
        this(stt, maHD, maDP, ghiChu, ngayLap, tongTien, null); // Chuyển sang constructor đầy đủ
    }

    // Constructor rỗng (Dùng cho DAO)
    public HoaDon() {
        this.stt = new SimpleIntegerProperty();
        this.maHD = new SimpleStringProperty();
        this.maDP = new SimpleStringProperty();
        this.ghiChu = new SimpleStringProperty();
        this.ngayLap = new SimpleObjectProperty<>();
        this.tongTien = new SimpleDoubleProperty();
        this.tenKH = new SimpleStringProperty();

        // Khởi tạo các trường chi tiết
        this.sdt = null;
        this.email = null;
        this.tenPhong = null;
        this.tenLoai = null;
        this.donGiaThucTe = 0.0;
        this.ngayNhan = null;
        this.ngayTra = null;
    }

    // --- Getters và Setters cho các thuộc tính liên kết (PROPERTY) ---





    // --- Getters và Setters cho các thuộc tính chi tiết (Non-Property) ---
    // Loại bỏ trường 'private String tenKhachHang;' cũ (nếu có) để tránh nhầm lẫn

    public String getTenKH() { return tenKH.get(); }
    public void setTenKH(String tenKH) { this.tenKH.set(tenKH); }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTenPhong() { return tenPhong; }
    public void setTenPhong(String tenPhong) { this.tenPhong = tenPhong; }

    public String getTenLoai() { return tenLoai; }
    public void setTenLoai(String tenLoai) { this.tenLoai = tenLoai; }

    public double getDonGiaThucTe() { return donGiaThucTe; }
    public void setDonGiaThucTe(double donGiaThucTe) { this.donGiaThucTe = donGiaThucTe; }

    public LocalDateTime getNgayNhan() { return ngayNhan; }
    public void setNgayNhan(LocalDateTime ngayNhan) { this.ngayNhan = ngayNhan; }

    public LocalDateTime getNgayTra() { return ngayTra; }
    public void setNgayTra(LocalDateTime ngayTra) { this.ngayTra = ngayTra; }

    // --- Getters và Property cho TableView (Khác) ---

    public StringProperty tenKHProperty() { return tenKH; }

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
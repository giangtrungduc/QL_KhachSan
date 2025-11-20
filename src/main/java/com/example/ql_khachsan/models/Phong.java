package com.example.ql_khachsan.models;

import com.example.ql_khachsan.untils.TrangThaiPhong;
import javafx.beans.property.*;
import java.math.BigDecimal;

public class Phong {
    private final StringProperty maPhong;
    private final StringProperty tenPhong;
    // Thêm giá phòng (để tương thích với code cũ, dù giá thực tế ở bảng LoaiPhong)
    private final ObjectProperty<BigDecimal> giaPhong;
    private final ObjectProperty<TrangThaiPhong> trangThai;
    private final StringProperty maLoai;

    // Constructor mặc định
    public Phong() {
        this.maPhong = new SimpleStringProperty();
        this.tenPhong = new SimpleStringProperty();
        this.giaPhong = new SimpleObjectProperty<>(BigDecimal.ZERO); // Mặc định là 0
        // Sử dụng Enum TRONG làm mặc định
        this.trangThai = new SimpleObjectProperty<>(TrangThaiPhong.TRONG);
        this.maLoai = new SimpleStringProperty();
    }

    // Constructor đầy đủ (Dùng khi load từ DB lên)
    public Phong(String maPhong, String tenPhong, BigDecimal giaPhong, TrangThaiPhong trangThai, String maLoai) {
        this.maPhong = new SimpleStringProperty(maPhong);
        this.tenPhong = new SimpleStringProperty(tenPhong);
        this.giaPhong = new SimpleObjectProperty<>(giaPhong);
        this.trangThai = new SimpleObjectProperty<>(trangThai);
        this.maLoai = new SimpleStringProperty(maLoai);
    }

    // Constructor hỗ trợ load trạng thái từ String (nếu DB trả về String)
    public Phong(String maPhong, String tenPhong, BigDecimal giaPhong, String trangThaiStr, String maLoai) {
        this.maPhong = new SimpleStringProperty(maPhong);
        this.tenPhong = new SimpleStringProperty(tenPhong);
        this.giaPhong = new SimpleObjectProperty<>(giaPhong);
        // Dùng fromDbValue để convert chuỗi từ DB sang Enum an toàn
        this.trangThai = new SimpleObjectProperty<>(TrangThaiPhong.fromDbValue(trangThaiStr));
        this.maLoai = new SimpleStringProperty(maLoai);
    }

    // --- Getters, Setters, và Properties ---

    public String getMaPhong() { return maPhong.get(); }
    public void setMaPhong(String maPhong) { this.maPhong.set(maPhong); }
    public StringProperty maPhongProperty() { return maPhong; }

    public String getTenPhong() { return tenPhong.get(); }
    public void setTenPhong(String tenPhong) { this.tenPhong.set(tenPhong); }
    public StringProperty tenPhongProperty() { return tenPhong; }

    // Getter/Setter cho Giá phòng
    public BigDecimal getGiaPhong() { return giaPhong.get(); }
    public void setGiaPhong(BigDecimal giaPhong) { this.giaPhong.set(giaPhong); }
    public ObjectProperty<BigDecimal> giaPhongProperty() { return giaPhong; }

    public TrangThaiPhong getTrangThai() { return trangThai.get(); }
    public void setTrangThai(TrangThaiPhong trangThai) { this.trangThai.set(trangThai); }
    public ObjectProperty<TrangThaiPhong> trangThaiProperty() { return trangThai; }

    public String getMaLoai() { return maLoai.get(); }
    public void setMaLoai(String maLoai) { this.maLoai.set(maLoai); }
    public StringProperty maLoaiProperty() { return maLoai; }
}
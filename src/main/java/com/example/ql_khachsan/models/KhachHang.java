package com.example.ql_khachsan.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.util.Objects;

public class KhachHang {
    private final StringProperty maKH;
    private final StringProperty hoTen;
    private final StringProperty cccd;
    private final StringProperty sdt;
    private final StringProperty email;
    private final StringProperty taiKhoan;
    private final StringProperty matKhau;

    // Constructor rỗng
    public KhachHang() {
        this.maKH = new SimpleStringProperty();
        this.hoTen = new SimpleStringProperty();
        this.cccd = new SimpleStringProperty();
        this.sdt = new SimpleStringProperty();
        this.email = new SimpleStringProperty();
        this.taiKhoan = new SimpleStringProperty();
        this.matKhau = new SimpleStringProperty();
    }

    // Constructor đầy đủ
    public KhachHang(String maKH, String hoTen, String cccd, String sdt,
                     String email, String taiKhoan, String matKhau) {
        this.maKH = new SimpleStringProperty(maKH);
        this.hoTen = new SimpleStringProperty(hoTen);
        this.cccd = new SimpleStringProperty(cccd);
        this.sdt = new SimpleStringProperty(sdt);
        this.email = new SimpleStringProperty(email);
        this.taiKhoan = new SimpleStringProperty(taiKhoan);
        this.matKhau = new SimpleStringProperty(matKhau);
    }

    // --- Getters, Setters và Properties ---

    public String getMaKH() { return maKH.get(); }
    public void setMaKH(String maKH) { this.maKH.set(maKH); }
    public StringProperty maKHProperty() { return maKH; }

    public String getHoTen() { return hoTen.get(); }
    public void setHoTen(String hoTen) { this.hoTen.set(hoTen); }
    public StringProperty hoTenProperty() { return hoTen; }

    public String getCccd() { return cccd.get(); }
    public void setCccd(String cccd) { this.cccd.set(cccd); }
    public StringProperty cccdProperty() { return cccd; }

    public String getSdt() { return sdt.get(); }
    public void setSdt(String sdt) { this.sdt.set(sdt); }
    public StringProperty sdtProperty() { return sdt; }

    public String getEmail() { return email.get(); }
    public void setEmail(String email) { this.email.set(email); }
    public StringProperty emailProperty() { return email; }

    public String getTaiKhoan() { return taiKhoan.get(); }
    public void setTaiKhoan(String taiKhoan) { this.taiKhoan.set(taiKhoan); }
    public StringProperty taiKhoanProperty() { return taiKhoan; }

    public String getMatKhau() { return matKhau.get(); }
    public void setMatKhau(String matKhau) { this.matKhau.set(matKhau); }
    public StringProperty matKhauProperty() { return matKhau; }

    // --- equals, hashCode, toString (Giữ nguyên) ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KhachHang khachHang = (KhachHang) o;
        return Objects.equals(getMaKH(), khachHang.getMaKH());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMaKH());
    }

    @Override
    public String toString() {
        return "KhachHang{" + "maKH=" + maKH.get() + ", hoTen=" + hoTen.get() + '}';
    }
}
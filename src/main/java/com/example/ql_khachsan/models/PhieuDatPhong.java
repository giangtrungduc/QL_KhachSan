package com.example.ql_khachsan.models;

import com.example.ql_khachsan.untils.TrangThaiDatPhong;
import javafx.beans.property.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PhieuDatPhong {
    private final StringProperty maDP;
    private final ObjectProperty<LocalDateTime> ngayDat;
    private final ObjectProperty<LocalDateTime> ngayNhan;
    private final ObjectProperty<LocalDateTime> ngayTra;
    private final ObjectProperty<BigDecimal> donGiaThucTe;
    private final ObjectProperty<TrangThaiDatPhong> trangThaiDatPhong;
    private final StringProperty maKH;
    private final StringProperty maPhong;

    public PhieuDatPhong() {
        this.maDP = new SimpleStringProperty();
        this.ngayDat = new SimpleObjectProperty<>();
        this.ngayNhan = new SimpleObjectProperty<>();
        this.ngayTra = new SimpleObjectProperty<>();
        this.donGiaThucTe = new SimpleObjectProperty<>();
        this.trangThaiDatPhong = new SimpleObjectProperty<>();
        this.maKH = new SimpleStringProperty();
        this.maPhong = new SimpleStringProperty();
    }

    public PhieuDatPhong(String maDP, LocalDateTime ngayDat, LocalDateTime ngayNhan, LocalDateTime ngayTra,
                         BigDecimal donGiaThucTe, TrangThaiDatPhong trangThaiDatPhong,
                         String maKH, String maPhong) {
        this.maDP = new SimpleStringProperty(maDP);
        this.ngayDat = new SimpleObjectProperty<>(ngayDat);
        this.ngayNhan = new SimpleObjectProperty<>(ngayNhan);
        this.ngayTra = new SimpleObjectProperty<>(ngayTra);
        this.donGiaThucTe = new SimpleObjectProperty<>(donGiaThucTe);
        this.trangThaiDatPhong = new SimpleObjectProperty<>(trangThaiDatPhong);
        this.maKH = new SimpleStringProperty(maKH);
        this.maPhong = new SimpleStringProperty(maPhong);
    }

    public String getMaDP() { return maDP.get(); }
    public void setMaDP(String maDP) { this.maDP.set(maDP); }
    public StringProperty maDPProperty() { return maDP; }

    public LocalDateTime getNgayDat() { return ngayDat.get(); }
    public void setNgayDat(LocalDateTime ngayDat) { this.ngayDat.set(ngayDat); }
    public ObjectProperty<LocalDateTime> ngayDatProperty() { return ngayDat; }

    public LocalDateTime getNgayNhan() { return ngayNhan.get(); }
    public void setNgayNhan(LocalDateTime ngayNhan) { this.ngayNhan.set(ngayNhan); }
    public ObjectProperty<LocalDateTime> ngayNhanProperty() { return ngayNhan; }

    public LocalDateTime getNgayTra() { return ngayTra.get(); }
    public void setNgayTra(LocalDateTime ngayTra) { this.ngayTra.set(ngayTra); }
    public ObjectProperty<LocalDateTime> ngayTraProperty() { return ngayTra; }

    public BigDecimal getDonGiaThucTe() { return donGiaThucTe.get(); }
    public void setDonGiaThucTe(BigDecimal donGiaThucTe) { this.donGiaThucTe.set(donGiaThucTe); }
    public ObjectProperty<BigDecimal> donGiaThucTeProperty() { return donGiaThucTe; }

    public TrangThaiDatPhong getTrangThaiDatPhong() { return trangThaiDatPhong.get(); }
    public void setTrangThaiDatPhong(TrangThaiDatPhong trangThaiDatPhong) { this.trangThaiDatPhong.set(trangThaiDatPhong); }
    public ObjectProperty<TrangThaiDatPhong> trangThaiDatPhongProperty() { return trangThaiDatPhong; }

    public String getMaKH() { return maKH.get(); }
    public void setMaKH(String maKH) { this.maKH.set(maKH); }
    public StringProperty maKHProperty() { return maKH; }

    public String getMaPhong() { return maPhong.get(); }
    public void setMaPhong(String maPhong) { this.maPhong.set(maPhong); }
    public StringProperty maPhongProperty() { return maPhong; }
}
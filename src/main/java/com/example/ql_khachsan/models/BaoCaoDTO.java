package com.example.ql_khachsan.models;

public class BaoCaoDTO {
    private String thoiGian;
    private int soLuotDat;
    private double doanhThu;

    public BaoCaoDTO() {
    }

    public BaoCaoDTO(String thoiGian, int soLuotDat, double doanhThu) {
        this.thoiGian = thoiGian;
        this.soLuotDat = soLuotDat;
        this.doanhThu = doanhThu;
    }

    public String getThoiGian() { return thoiGian; }
    public void setThoiGian(String thoiGian) { this.thoiGian = thoiGian; }

    public int getSoLuotDat() { return soLuotDat; }
    public void setSoLuotDat(int soLuotDat) { this.soLuotDat = soLuotDat; }

    public double getDoanhThu() { return doanhThu; }
    public void setDoanhThu(double doanhThu) { this.doanhThu = doanhThu; }
}
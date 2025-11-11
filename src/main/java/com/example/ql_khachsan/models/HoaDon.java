import java.time.LocalDateTime;

public class HoaDon {
    private String maHD;
    private String ghiChu;
    private LocalDateTime ngayLap;
    private String maDP;

    public HoaDon() {}

    public HoaDon(String maHD, String ghiChu, LocalDateTime ngayLap, String maDP) {
        this.maHD = maHD;
        this.ghiChu = ghiChu;
        this.ngayLap = ngayLap;
        this.maDP = maDP;
    }

    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public LocalDateTime getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDateTime ngayLap) {
        this.ngayLap = ngayLap;
    }

    public String getMaDP() {
        return maDP;
    }

    public void setMaDP(String maDP) {
        this.maDP = maDP;
    }

    @Override
    public String toString() {
        return "HoaDon{" +
                "maHD='" + maHD + '\'' +
                ", ghiChu='" + ghiChu + '\'' +
                ", ngayLap=" + ngayLap +
                ", maDP='" + maDP + '\'' +
                '}';
    }
}

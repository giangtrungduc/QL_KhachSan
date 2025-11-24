package com.example.ql_khachsan.controllers;

import com.example.ql_khachsan.models.HoaDon;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class HoaDonController {

    @FXML private Label lblTenKhachHang;
    @FXML private Label lblSDT;
    @FXML private Label lblEmail;
    @FXML private Label lblPhong;
    @FXML private Label lblLoaiPhong;
    @FXML private Label lblDonGiaThucTe;
    @FXML private Label lblNgayNhan;
    @FXML private Label lblNgayTra;
    @FXML private Label lblSoDem;

    @FXML private Label lblMaHD;
    @FXML private Label lblNgayLap;
    @FXML private Label lblGhiChu;
    @FXML private Label lblTongTien;
    @FXML private Button btnExport;

    private final DateTimeFormatter dtfView = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void setData(HoaDon hd) {
        long soGio = Duration.between(hd.getNgayNhan(), hd.getNgayTra()).toHours();
        long soDem = (soGio + 23) / 24;
        if (soDem == 0 && soGio > 0) soDem = 1;

        lblMaHD.setText(hd.getMaHD());
        lblNgayLap.setText(hd.getNgayLap().format(dtfView));
        lblGhiChu.setText("Ghi chú: " + (hd.getGhiChu() != null ? hd.getGhiChu() : ""));

        lblTenKhachHang.setText(hd.getTenKH());
        lblSDT.setText(hd.getSdt());
        lblEmail.setText(hd.getEmail());

        lblPhong.setText(hd.getTenPhong());
        lblLoaiPhong.setText(hd.getTenLoai());
        lblDonGiaThucTe.setText(String.format("%,.0f VNĐ", hd.getDonGiaThucTe()));
        lblNgayNhan.setText(hd.getNgayNhan().format(dtfView));
        lblNgayTra.setText(hd.getNgayTra().format(dtfView));
        lblSoDem.setText(String.valueOf(soDem));

        lblTongTien.setText(String.format("%,.0f VNĐ", hd.getTongTien()));
    }

    @FXML
    private void handleExport() {
        try {
            VBox root = (VBox) btnExport.getParent();
            btnExport.setVisible(false);
            WritableImage image = root.snapshot(null, null);
            btnExport.setVisible(true);

            String maHD = lblMaHD.getText().trim();
            File file = new File("HoaDon_" + maHD + ".png");
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);

            new Alert(Alert.AlertType.INFORMATION, "Đã xuất hóa đơn: " + file.getAbsolutePath()).showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
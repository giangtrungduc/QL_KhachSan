package com.example.ql_khachsan.controllers;

import com.example.ql_khachsan.models.HoaDon;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class HoaDonController {

    @FXML private Label lblTenKhachHang;
    @FXML private Label lblMaHD;
    @FXML private Label lblMaDP;
    @FXML private Label lblNgayLap;
    @FXML private Label lblGhiChu;
    @FXML private Label lblTongTien;
    @FXML private Button btnExport;

    private final DateTimeFormatter dtfView = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void setData(HoaDon hd, String tenKhachHang) {
        lblTenKhachHang.setText("Khách hàng: " + tenKhachHang);
        lblMaHD.setText("Mã hóa đơn: " + hd.getMaHD());
        lblMaDP.setText("Mã đặt phòng: " + hd.getMaDP());
        lblNgayLap.setText("Ngày lập: " + hd.getNgayLap().format(dtfView));
        lblGhiChu.setText("Ghi chú: " + hd.getGhiChu());
        lblTongTien.setText("Tổng tiền: " + String.format("%.2f", hd.getTongTien()));
    }

    @FXML
    private void handleExport() {
        try {
            VBox root = (VBox) btnExport.getParent();
            WritableImage image = root.snapshot(null, null);
            File file = new File("HoaDon_" + lblMaHD.getText().replaceAll("\\D+", "") + ".png");
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            System.out.println("Xuất hóa đơn thành công: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

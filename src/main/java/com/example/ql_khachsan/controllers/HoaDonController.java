package com.example.ql_khachsan.controllers;

import com.example.ql_khachsan.models.HoaDon;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javafx.scene.control.Alert;

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
    @FXML private Label lblNgayNhan;
    @FXML private Label lblNgayTra;
    @FXML private Label lblSoDem;

    @FXML private Label lblMaHD; // Thêm
    // @FXML private Label lblMaDP; // Không cần thiết hiển thị
    @FXML private Label lblNgayLap;
    @FXML private Label lblGhiChu;
    @FXML private Label lblTongTien;
    @FXML private Label lblTongTienPhong; // Thêm label hiển thị chi tiết tiền phòng

    @FXML private Button btnExport;

    private final DateTimeFormatter dtfView = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void setData(HoaDon hd) {
        // Tính số đêm để hiển thị
        long soGio = Duration.between(hd.getNgayNhan(), hd.getNgayTra()).toHours();
        long soDem = (soGio + 23) / 24;
        if (soDem == 0 && soGio > 0) soDem = 1;

        // --- Thông tin chung ---
        lblMaHD.setText("Mã HĐ: " + hd.getMaHD());
        lblNgayLap.setText("Ngày lập: " + hd.getNgayLap().format(dtfView));
        lblGhiChu.setText("Ghi chú: " + (hd.getGhiChu() != null ? hd.getGhiChu() : ""));

        // --- Thông tin Khách hàng ---
        lblTenKhachHang.setText("Khách hàng: " + hd.getTenKhachHang());
        lblSDT.setText("SĐT: " + hd.getSdt());
        lblEmail.setText("Email: " + hd.getEmail());

        // --- Thông tin Đặt phòng ---
        lblPhong.setText("Phòng: " + hd.getTenPhong());
        lblNgayNhan.setText("Ngày nhận: " + hd.getNgayNhan().format(dtfView));
        lblNgayTra.setText("Ngày trả: " + hd.getNgayTra().format(dtfView));
        lblSoDem.setText("Số đêm: " + soDem);

        // --- Tổng tiền ---
        // Tổng tiền hiện tại chỉ tính tiền phòng
        String tongTienFormat = String.format("%,.2f VNĐ", hd.getTongTien());
        lblTongTien.setText(tongTienFormat);
        lblTongTienPhong.setText(tongTienFormat);
    }

    @FXML
    private void handleExport() {
        try {
            // Lấy VBox (root) để chụp ảnh
            VBox root = (VBox) btnExport.getParent();
            WritableImage image = root.snapshot(null, null);

            // Xóa ký tự không phải số trong MaHD để tạo tên file
            String maHD = lblMaHD.getText().replace("Mã HĐ: ", "").trim();
            File file = new File("HoaDon_" + maHD + ".png");

            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);

            // Hiển thị thông báo thành công
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Xuất hóa đơn thành công tại: " + file.getAbsolutePath());
            alert.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Lỗi khi xuất hóa đơn: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
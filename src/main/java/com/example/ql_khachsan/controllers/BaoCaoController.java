package com.example.ql_khachsan.controllers;

import com.example.ql_khachsan.services.BaoCaoService;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.text.DecimalFormat;
import java.util.Map;

public class BaoCaoController {

    // Các field FXML giữ nguyên...
    @FXML private ComboBox<String> cbKieuBaoCao;
    @FXML private ComboBox<String> cbThang;
    @FXML private ComboBox<String> cbQuy;
    @FXML private ComboBox<String> cbNam;
    @FXML private Button btnXem;
    @FXML private Label txtTongDoanhThu;
    @FXML private Label txtLuotDat;
    @FXML private Label txtTyLeLap;
    @FXML private Label txtLoaiPhongTop;
    @FXML private LineChart<String, Number> chartDoanhThu;
    @FXML private TableView<Object> tableChiTiet;

    private BaoCaoService baoCaoService = new BaoCaoService();
    private DecimalFormat formatter = new DecimalFormat("#,### VNĐ");

    @FXML
    public void initialize() {
        setupComboBoxes();
        setupChart();
        setupTable();
        setupEventHandlers();

        // Khởi tạo với dữ liệu rỗng
        clearAllData();
    }

    private void setupComboBoxes() {
        // Thiết lập dữ liệu cho combobox (giữ nguyên)
        cbKieuBaoCao.getItems().addAll("Theo tháng", "Theo quý", "Theo năm");
        cbKieuBaoCao.setValue("Theo tháng");

        // Tháng
        ObservableList<String> months = FXCollections.observableArrayList();
        for (int i = 1; i <= 12; i++) {
            months.add("Tháng " + i);
        }
        cbThang.setItems(months);
        cbThang.setValue("Tháng 1");

        // Quý
        cbQuy.getItems().addAll("Quý 1", "Quý 2", "Quý 3", "Quý 4");
        cbQuy.setValue("Quý 1");

        // Năm (lấy năm hiện tại và các năm trước)
        ObservableList<String> years = FXCollections.observableArrayList();
        int currentYear = java.time.Year.now().getValue();
        for (int i = currentYear - 2; i <= currentYear; i++) {
            years.add(String.valueOf(i));
        }
        cbNam.setItems(years);
        cbNam.setValue(String.valueOf(currentYear));
    }

    private void setupChart() {
        chartDoanhThu.setTitle("Biểu đồ doanh thu");
        chartDoanhThu.setLegendVisible(false);
    }

    private void setupTable() {
        // Setup table columns...
    }

    private void setupEventHandlers() {
        btnXem.setOnAction(event -> loadBaoCaoThucTe());
    }

    private void loadBaoCaoThucTe() {
        try {
            // Lấy năm từ combobox
            String namStr = cbNam.getValue();
            if (namStr == null || namStr.isEmpty()) {
                showAlert("Lỗi", "Vui lòng chọn năm!");
                return;
            }
            int nam = Integer.parseInt(namStr);

            String kieuBaoCao = cbKieuBaoCao.getValue();

            if ("Theo tháng".equals(kieuBaoCao)) {
                // Lấy tháng từ combobox
                String thangStr = cbThang.getValue();
                if (thangStr == null || thangStr.isEmpty()) {
                    showAlert("Lỗi", "Vui lòng chọn tháng!");
                    return;
                }

                // Xử lý chuỗi "Tháng X" thành số
                int thang = extractMonthNumber(thangStr);
                if (thang == -1) {
                    showAlert("Lỗi", "Định dạng tháng không hợp lệ!");
                    return;
                }

                loadDuLieuThang(thang, nam);

            } else if ("Theo quý".equals(kieuBaoCao)) {
                // Xử lý theo quý
                String quy = cbQuy.getValue();
                if (quy == null || quy.isEmpty()) {
                    showAlert("Lỗi", "Vui lòng chọn quý!");
                    return;
                }
                loadDuLieuQuy(quy, nam);

            } else {
                // Xử lý theo năm
                loadDuLieuNam(nam);
            }

        } catch (NumberFormatException e) {
            showAlert("Lỗi", "Định dạng số không hợp lệ!");
            e.printStackTrace();
        } catch (Exception e) {
            showAlert("Lỗi", "Có lỗi xảy ra: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Phương thức trích xuất số tháng từ chuỗi
    private int extractMonthNumber(String monthString) {
        try {
            if (monthString.startsWith("Tháng ")) {
                return Integer.parseInt(monthString.substring(6).trim());
            }
            return Integer.parseInt(monthString.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void loadDuLieuThang(int thang, int nam) {
        // Lấy dữ liệu từ service
        double tongDoanhThu = baoCaoService.getTongDoanhThu(thang, nam);
        int soLuotDat = baoCaoService.getSoLuotDatPhong(thang, nam);
        double tyLeLap = baoCaoService.getTyLeLapPhong(thang, nam);
        String loaiPhongTop = baoCaoService.getLoaiPhongTop(thang, nam);

        // Cập nhật UI
        txtTongDoanhThu.setText(formatter.format(tongDoanhThu));
        txtLuotDat.setText(String.valueOf(soLuotDat));
        txtTyLeLap.setText(String.format("%.1f%%", tyLeLap));
        txtLoaiPhongTop.setText(loaiPhongTop);

        // Load biểu đồ cho cả năm để so sánh
        loadBieuDoNam(nam);
    }

    private void loadBieuDoNam(int nam) {
        chartDoanhThu.getData().clear();

        Map<String, Double> doanhThuTheoThang = baoCaoService.getDoanhThuTheoThang(nam);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map.Entry<String, Double> entry : doanhThuTheoThang.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        chartDoanhThu.getData().add(series);
    }

    private void loadDuLieuQuy(String quy, int nam) {
        // TODO: Implement xử lý theo quý
        showAlert("Thông báo", "Tính năng báo cáo theo quý đang phát triển");
    }

    private void loadDuLieuNam(int nam) {
        // TODO: Implement xử lý theo năm
        showAlert("Thông báo", "Tính năng báo cáo theo năm đang phát triển");
    }

    private void clearAllData() {
        txtTongDoanhThu.setText("0 VNĐ");
        txtLuotDat.setText("0");
        txtTyLeLap.setText("0%");
        txtLoaiPhongTop.setText("---");
        chartDoanhThu.getData().clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
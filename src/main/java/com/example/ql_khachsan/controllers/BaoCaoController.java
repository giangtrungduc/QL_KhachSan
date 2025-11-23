package com.example.ql_khachsan.controllers;

import com.example.ql_khachsan.dao.BaoCaoDAO;
import com.example.ql_khachsan.models.BaoCaoDTO;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;

public class BaoCaoController {

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
    @FXML private TableView<BaoCaoTableData> tableChiTiet;
    @FXML private TableColumn<BaoCaoTableData, String> colThoiGian;
    @FXML private TableColumn<BaoCaoTableData, Integer> colLuotDat;
    @FXML private TableColumn<BaoCaoTableData, Double> colDoanhThuPhong;
    @FXML private TableColumn<BaoCaoTableData, Double> colTong;

    private final ObservableList<BaoCaoTableData> tableData = FXCollections.observableArrayList();
    private final DecimalFormat formatter = new DecimalFormat("#,### VNĐ");
    private final BaoCaoDAO baoCaoDAO = new BaoCaoDAO();

    public static class BaoCaoTableData {
        private final SimpleStringProperty thoiGian;
        private final SimpleIntegerProperty soLuotDat;
        private final SimpleDoubleProperty doanhThuPhong;
        private final SimpleDoubleProperty tongDoanhThu;

        public BaoCaoTableData(String thoiGian, int soLuotDat, double doanhThuPhong, double tongDoanhThu) {
            this.thoiGian = new SimpleStringProperty(thoiGian);
            this.soLuotDat = new SimpleIntegerProperty(soLuotDat);
            this.doanhThuPhong = new SimpleDoubleProperty(doanhThuPhong);
            this.tongDoanhThu = new SimpleDoubleProperty(tongDoanhThu);
        }

        public SimpleStringProperty thoiGianProperty() { return thoiGian; }
        public SimpleIntegerProperty soLuotDatProperty() { return soLuotDat; }
        public SimpleDoubleProperty doanhThuPhongProperty() { return doanhThuPhong; }
        public SimpleDoubleProperty tongDoanhThuProperty() { return tongDoanhThu; }
    }

    @FXML
    public void initialize() {
        setupComboBoxes();
        setupChart();
        setupTable();
        setupEventHandlers();
        loadBaoCaoThucTe();
    }

    private void setupComboBoxes() {
        cbKieuBaoCao.getItems().addAll("Theo tháng", "Theo quý", "Theo năm");
        cbKieuBaoCao.setValue("Theo tháng");

        ObservableList<String> months = FXCollections.observableArrayList();
        for (int i = 1; i <= 12; i++) months.add("Tháng " + i);
        cbThang.setItems(months);
        cbThang.setValue("Tháng " + LocalDate.now().getMonthValue());

        cbQuy.getItems().addAll("Quý 1", "Quý 2", "Quý 3", "Quý 4");
        cbQuy.setValue("Quý 1");

        ObservableList<String> years = FXCollections.observableArrayList();
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear - 2; i <= currentYear; i++) years.add(String.valueOf(i));
        cbNam.setItems(years);
        cbNam.setValue(String.valueOf(currentYear));

        cbQuy.setVisible(false);
    }

    private void setupChart() {
        NumberAxis yAxis = (NumberAxis) chartDoanhThu.getYAxis();
        yAxis.setTickLabelFormatter(new StringConverter<>() {
            private final DecimalFormat df = new DecimalFormat("#,###");
            @Override public String toString(Number object) { return df.format(object.doubleValue()); }
            @Override public Number fromString(String string) { return 0; }
        });
    }

    private void setupTable() {
        colThoiGian.setCellValueFactory(d -> d.getValue().thoiGianProperty());
        colLuotDat.setCellValueFactory(d -> d.getValue().soLuotDatProperty().asObject());
        colDoanhThuPhong.setCellValueFactory(d -> d.getValue().doanhThuPhongProperty().asObject());
        colTong.setCellValueFactory(d -> d.getValue().tongDoanhThuProperty().asObject());

        colDoanhThuPhong.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : formatter.format(item));
            }
        });
        colTong.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : formatter.format(item));
                setStyle("-fx-font-weight: bold;");
            }
        });

        tableChiTiet.setItems(tableData);
    }

    private void setupEventHandlers() {
        btnXem.setOnAction(e -> loadBaoCaoThucTe());
        cbKieuBaoCao.setOnAction(e -> {
            String type = cbKieuBaoCao.getValue();
            cbThang.setVisible("Theo tháng".equals(type));
            cbQuy.setVisible("Theo quý".equals(type));

            resetUI();
        });
    }

    private void resetUI() {
        txtTongDoanhThu.setText("0 VNĐ");
        txtLuotDat.setText("0");
        txtTyLeLap.setText("0%");
        txtLoaiPhongTop.setText("---");
        chartDoanhThu.getData().clear();
        tableData.clear();
    }

    private void loadBaoCaoThucTe() {
        String kieu = cbKieuBaoCao.getValue();
        int nam = Integer.parseInt(cbNam.getValue());
        String whereCondition;
        String groupBy;
        String timeLabelPrefix;

        if ("Theo tháng".equals(kieu)) {
            int thang = Integer.parseInt(cbThang.getValue().replace("Tháng ", "").trim());
            whereCondition = "WHERE MONTH(NgayLap) = " + thang + " AND YEAR(NgayLap) = " + nam;
            groupBy = "DATE(NgayLap)";
            timeLabelPrefix = "Ngày";
        } else if ("Theo quý".equals(kieu)) {
            int quy = Integer.parseInt(cbQuy.getValue().replace("Quý ", "").trim());
            whereCondition = "WHERE QUARTER(NgayLap) = " + quy + " AND YEAR(NgayLap) = " + nam;
            groupBy = "MONTH(NgayLap)";
            timeLabelPrefix = "Tháng";
        } else {
            whereCondition = "WHERE YEAR(NgayLap) = " + nam;
            groupBy = "MONTH(NgayLap)";
            timeLabelPrefix = "Tháng";
        }

        // 1. Lấy tổng quan từ DAO
        double totalRevenue = baoCaoDAO.getTongDoanhThu(whereCondition);
        int totalBookings = baoCaoDAO.getTongLuotDat(whereCondition);
        String topRoom = baoCaoDAO.getTopLoaiPhong(whereCondition);

        txtTongDoanhThu.setText(formatter.format(totalRevenue));
        txtLuotDat.setText(String.valueOf(totalBookings));
        txtTyLeLap.setText("---"); // Tính năng nâng cao, để tạm
        txtLoaiPhongTop.setText(topRoom);

        // 2. Lấy chi tiết biểu đồ & bảng từ DAO
        List<BaoCaoDTO> dataList = baoCaoDAO.getBaoCaoChiTiet(whereCondition, groupBy);

        chartDoanhThu.getData().clear();
        tableData.clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doanh thu");

        for (BaoCaoDTO item : dataList) {
            String label = item.getThoiGian();
            if (timeLabelPrefix.equals("Tháng")) {
                label = "Tháng " + label;
            } else if (timeLabelPrefix.equals("Ngày")) {
                // Format lại ngày cho đẹp nếu cần
                label = item.getThoiGian().substring(8, 10); // Lấy ngày
            }

            series.getData().add(new XYChart.Data<>(label, item.getDoanhThu()));
            tableData.add(new BaoCaoTableData(label, item.getSoLuotDat(), item.getDoanhThu(), item.getDoanhThu()));
        }
        chartDoanhThu.getData().add(series);
    }
}
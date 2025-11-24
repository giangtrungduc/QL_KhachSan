package com.example.ql_khachsan.controllers;

import com.example.ql_khachsan.dao.BaoCaoDAO;
import com.example.ql_khachsan.models.BaoCaoDTO;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
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
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

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

        public String getThoiGian() { return thoiGian.get(); }
        public int getSoLuotDat() { return soLuotDat.get(); }
        public double getDoanhThuPhong() { return doanhThuPhong.get(); }
        public double getTongDoanhThu() { return tongDoanhThu.get(); }
    }

    @FXML
    public void initialize() {
        try {
            setupComboBoxes();
            setupChart();
            setupTable();
            setupEventHandlers();
            loadBaoCaoThucTe();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi khởi tạo", "Có lỗi xảy ra khi khởi tạo: " + e.getMessage());
        }
    }

    private void setupComboBoxes() {
        cbKieuBaoCao.getItems().addAll("Theo tháng", "Theo quý", "Theo năm");
        cbKieuBaoCao.setValue("Theo tháng");

        // Tháng
        for (int i = 1; i <= 12; i++) {
            cbThang.getItems().add("Tháng " + i);
        }
        cbThang.setValue("Tháng " + LocalDate.now().getMonthValue());

        // Quý
        cbQuy.getItems().addAll("Quý 1", "Quý 2", "Quý 3", "Quý 4");
        cbQuy.setValue("Quý 1");

        // Năm
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear - 2; i <= currentYear; i++) {
            cbNam.getItems().add(String.valueOf(i));
        }
        cbNam.setValue(String.valueOf(currentYear));

        cbQuy.setVisible(false);
    }

    private void setupChart() {
        try {
            NumberAxis yAxis = (NumberAxis) chartDoanhThu.getYAxis();
            yAxis.setTickLabelFormatter(new StringConverter<Number>() {
                private final DecimalFormat df = new DecimalFormat("#,###");
                @Override 
                public String toString(Number object) { 
                    return object == null ? "" : df.format(object.doubleValue()); 
                }
                @Override 
                public Number fromString(String string) { 
                    return 0; 
                }
            });
            
            chartDoanhThu.setAnimated(true);
            chartDoanhThu.setCreateSymbols(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupTable() {
        try {
            colThoiGian.setCellValueFactory(cellData -> cellData.getValue().thoiGian);
            colLuotDat.setCellValueFactory(cellData -> cellData.getValue().soLuotDat.asObject());
            colDoanhThuPhong.setCellValueFactory(cellData -> cellData.getValue().doanhThuPhong.asObject());
            colTong.setCellValueFactory(cellData -> cellData.getValue().tongDoanhThu.asObject());

            // Format tiền tệ
            colDoanhThuPhong.setCellFactory(column -> new TableCell<BaoCaoTableData, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(formatter.format(item));
                    }
                }
            });

            colTong.setCellFactory(column -> new TableCell<BaoCaoTableData, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(formatter.format(item));
                        setStyle("-fx-font-weight: bold;");
                    }
                }
            });

            tableChiTiet.setItems(tableData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupEventHandlers() {
        btnXem.setOnAction(e -> {
            animateButton(btnXem);
            loadBaoCaoThucTe();
        });
        
        cbKieuBaoCao.setOnAction(e -> {
            String type = cbKieuBaoCao.getValue();
            cbThang.setVisible("Theo tháng".equals(type));
            cbQuy.setVisible("Theo quý".equals(type));
            resetUI();
        });
    }

    private void animateButton(Button button) {
        ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
        st.setToX(0.95);
        st.setToY(0.95);
        st.setOnFinished(event -> {
            ScaleTransition st2 = new ScaleTransition(Duration.millis(100), button);
            st2.setToX(1.0);
            st2.setToY(1.0);
            st2.play();
        });
        st.play();
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
        try {
            String kieu = cbKieuBaoCao.getValue();
            int nam = Integer.parseInt(cbNam.getValue());
            String whereCondition;
            String groupBy;

            if ("Theo tháng".equals(kieu)) {
                int thang = Integer.parseInt(cbThang.getValue().replace("Tháng ", "").trim());
                whereCondition = " WHERE MONTH(HD.NgayLap) = " + thang + " AND YEAR(HD.NgayLap) = " + nam;
                groupBy = "DATE(HD.NgayLap)";
            } else if ("Theo quý".equals(kieu)) {
                int quy = Integer.parseInt(cbQuy.getValue().replace("Quý ", "").trim());
                whereCondition = " WHERE QUARTER(HD.NgayLap) = " + quy + " AND YEAR(HD.NgayLap) = " + nam;
                groupBy = "MONTH(HD.NgayLap)";
            } else {
                whereCondition = " WHERE YEAR(HD.NgayLap) = " + nam;
                groupBy = "MONTH(HD.NgayLap)";
            }

            // Lấy dữ liệu
            double totalRevenue = baoCaoDAO.getTongDoanhThu(whereCondition);
            int totalBookings = baoCaoDAO.getTongLuotDat(whereCondition);
            String topRoom = baoCaoDAO.getTopLoaiPhong(whereCondition);
            double tyLeLapDay = calculateTyLeLapDay(whereCondition, kieu, nam);

            // Cập nhật UI
            txtTongDoanhThu.setText(formatter.format(totalRevenue));
            txtLuotDat.setText(String.valueOf(totalBookings));
            txtTyLeLap.setText(String.format("%.1f%%", tyLeLapDay));
            txtLoaiPhongTop.setText(topRoom != null ? topRoom : "---");

            // Biểu đồ và bảng
            updateChartAndTable(whereCondition, groupBy, kieu);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi tải dữ liệu", "Có lỗi xảy ra khi tải báo cáo: " + e.getMessage());
        }
    }

    private double calculateTyLeLapDay(String whereCondition, String kieuBaoCao, int nam) {
        try {
            int tongSoDemDaDat = baoCaoDAO.getTongSoDemDaDat(whereCondition);
            int tongSoPhong = baoCaoDAO.getTongSoPhong();
            
            int soNgayTrongKy = getSoNgayTrongKy(kieuBaoCao, nam);
            int tongSoDemCoTheDat = tongSoPhong * soNgayTrongKy;
            
            if (tongSoDemCoTheDat == 0) return 0.0;
            
            double tyLe = ((double) tongSoDemDaDat / tongSoDemCoTheDat) * 100;
            return Math.min(tyLe, 100.0);
            
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    private int getSoNgayTrongKy(String kieuBaoCao, int nam) {
        try {
            if ("Theo tháng".equals(kieuBaoCao)) {
                int thang = Integer.parseInt(cbThang.getValue().replace("Tháng ", "").trim());
                YearMonth yearMonth = YearMonth.of(nam, thang);
                return yearMonth.lengthOfMonth();
            } else if ("Theo quý".equals(kieuBaoCao)) {
                int quy = Integer.parseInt(cbQuy.getValue().replace("Quý ", "").trim());
                int startMonth = (quy - 1) * 3 + 1;
                int totalDays = 0;
                for (int i = 0; i < 3; i++) {
                    YearMonth yearMonth = YearMonth.of(nam, startMonth + i);
                    totalDays += yearMonth.lengthOfMonth();
                }
                return totalDays;
            } else {
                // Theo năm
                return LocalDate.of(nam, 1, 1).lengthOfYear();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 30; // Mặc định 30 ngày nếu có lỗi
        }
    }

    private void updateChartAndTable(String whereCondition, String groupBy, String kieuBaoCao) {
        try {
            chartDoanhThu.getData().clear();
            tableData.clear();

            List<BaoCaoDTO> dataList = baoCaoDAO.getBaoCaoChiTiet(whereCondition, groupBy);
            
            if (dataList.isEmpty()) {
                return;
            }

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Doanh thu");

            // Sắp xếp và thêm dữ liệu
            List<BaoCaoDTO> sortedData = dataList.stream()
                    .sorted((a, b) -> a.getThoiGian().compareTo(b.getThoiGian()))
                    .collect(Collectors.toList());

            for (BaoCaoDTO item : sortedData) {
                String label = formatTimeLabel(item.getThoiGian(), kieuBaoCao);
                series.getData().add(new XYChart.Data<>(label, item.getDoanhThu()));
                
                tableData.add(new BaoCaoTableData(
                    label, 
                    item.getSoLuotDat(), 
                    item.getDoanhThu(), 
                    item.getDoanhThu()
                ));
            }

            chartDoanhThu.getData().add(series);
            animateChart();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String formatTimeLabel(String rawTime, String kieuBaoCao) {
        try {
            if ("Theo tháng".equals(kieuBaoCao)) {
                // Định dạng ngày: dd/MM
                if (rawTime.length() >= 10) {
                    return rawTime.substring(8, 10) + "/" + rawTime.substring(5, 7);
                }
            } else {
                // Định dạng tháng: Tháng X
                return "Tháng " + rawTime;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rawTime;
    }

    private void animateChart() {
        FadeTransition ft = new FadeTransition(Duration.millis(500), chartDoanhThu);
        ft.setFromValue(0.3);
        ft.setToValue(1.0);
        ft.play();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
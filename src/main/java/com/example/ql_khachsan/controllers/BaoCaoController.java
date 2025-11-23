package com.example.ql_khachsan.controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.CategoryAxis;
import javafx.util.StringConverter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import java.text.DecimalFormat;
import java.util.Random;

public class BaoCaoController {

    // ===================== COMBOBOX =====================
    @FXML
    private ComboBox<String> cbKieuBaoCao;

    @FXML
    private ComboBox<String> cbThang;

    @FXML
    private ComboBox<String> cbQuy;

    @FXML
    private ComboBox<String> cbNam;

    @FXML
    private Button btnXem;

    // ===================== DASHBOARD LABELS =====================
    @FXML
    private Label txtTongDoanhThu;

    @FXML
    private Label txtLuotDat;

    @FXML
    private Label txtTyLeLap;

    @FXML
    private Label txtLoaiPhongTop;

    // ===================== CHART & TABLE =====================
    @FXML
    private LineChart<String, Number> chartDoanhThu;

    @FXML
    private TableView<BaoCaoTableData> tableChiTiet;

    @FXML
    private TableColumn<BaoCaoTableData, String> colThoiGian;

    @FXML
    private TableColumn<BaoCaoTableData, Integer> colLuotDat;

    @FXML
    private TableColumn<BaoCaoTableData, Double> colDoanhThuPhong;

    @FXML
    private TableColumn<BaoCaoTableData, Double> colPhuPhi;

    @FXML
    private TableColumn<BaoCaoTableData, Double> colTong;

    private ObservableList<BaoCaoTableData> tableData = FXCollections.observableArrayList();
    private DecimalFormat formatter = new DecimalFormat("#,### VNĐ");
    private Random random = new Random();

    // ===================== INNER CLASS FOR TABLE DATA =====================
    public static class BaoCaoTableData {
        private final SimpleStringProperty thoiGian;
        private final SimpleIntegerProperty soLuotDat;
        private final SimpleDoubleProperty doanhThuPhong;
        private final SimpleDoubleProperty phuPhi;
        private final SimpleDoubleProperty tongDoanhThu;

        public BaoCaoTableData(String thoiGian, int soLuotDat, double doanhThuPhong, double phuPhi, double tongDoanhThu) {
            this.thoiGian = new SimpleStringProperty(thoiGian);
            this.soLuotDat = new SimpleIntegerProperty(soLuotDat);
            this.doanhThuPhong = new SimpleDoubleProperty(doanhThuPhong);
            this.phuPhi = new SimpleDoubleProperty(phuPhi);
            this.tongDoanhThu = new SimpleDoubleProperty(tongDoanhThu);
        }

        // Getter methods
        public String getThoiGian() { return thoiGian.get(); }
        public int getSoLuotDat() { return soLuotDat.get(); }
        public double getDoanhThuPhong() { return doanhThuPhong.get(); }
        public double getPhuPhi() { return phuPhi.get(); }
        public double getTongDoanhThu() { return tongDoanhThu.get(); }

        // Property methods (cần cho TableView)
        public SimpleStringProperty thoiGianProperty() { return thoiGian; }
        public SimpleIntegerProperty soLuotDatProperty() { return soLuotDat; }
        public SimpleDoubleProperty doanhThuPhongProperty() { return doanhThuPhong; }
        public SimpleDoubleProperty phuPhiProperty() { return phuPhi; }
        public SimpleDoubleProperty tongDoanhThuProperty() { return tongDoanhThu; }
    }

    @FXML
    public void initialize() {
        setupComboBoxes();
        setupChart();
        setupTable();
        setupEventHandlers();
        clearAllData();

        // PRE-LOAD BIỂU ĐỒ VỚI DỮ LIỆU MẶC ĐỊNH
        javafx.application.Platform.runLater(() -> {
            // Load dữ liệu mặc định cho tháng hiện tại
            int currentMonth = java.time.LocalDate.now().getMonthValue();
            int currentYear = java.time.Year.now().getValue();

            // Tạo biểu đồ mặc định trước
            loadBieuDoMauThang(currentMonth, currentYear, 10000000);
            forceChartLayout();
        });
    }

    // ===================== SETUP METHODS =====================
    private void setupComboBoxes() {
        // Thiết lập dữ liệu cho combobox
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

        // Năm
        ObservableList<String> years = FXCollections.observableArrayList();
        int currentYear = java.time.Year.now().getValue();
        for (int i = currentYear - 2; i <= currentYear; i++) {
            years.add(String.valueOf(i));
        }
        cbNam.setItems(years);
        cbNam.setValue(String.valueOf(currentYear));

        // Ẩn quý ban đầu (chỉ hiện khi chọn theo quý)
        cbQuy.setVisible(false);
    }

    private void setupChart() {
        if (chartDoanhThu == null) return;

        chartDoanhThu.setTitle("Biểu đồ doanh thu");
        chartDoanhThu.setLegendVisible(true);
        chartDoanhThu.setAnimated(true);
        chartDoanhThu.setCreateSymbols(true);

        // Cấu hình trục Y để hiển thị số tiền
        NumberAxis yAxis = (NumberAxis) chartDoanhThu.getYAxis();
        yAxis.setTickLabelFormatter(new StringConverter<Number>() {
            private DecimalFormat df = new DecimalFormat("#,###");

            @Override
            public String toString(Number object) {
                if (object == null) return "";
                return df.format(object.doubleValue()) + " VNĐ";
            }

            @Override
            public Number fromString(String string) {
                try {
                    if (string == null || string.isEmpty()) return 0;
                    String cleanString = string.replace(" VNĐ", "").replace(",", "");
                    return Double.parseDouble(cleanString);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        });
    }

    private void setupTable() {
        // Kết nối TableColumn với model
        colThoiGian.setCellValueFactory(cellData -> cellData.getValue().thoiGianProperty());
        colLuotDat.setCellValueFactory(cellData -> cellData.getValue().soLuotDatProperty().asObject());
        colDoanhThuPhong.setCellValueFactory(cellData -> cellData.getValue().doanhThuPhongProperty().asObject());
        colPhuPhi.setCellValueFactory(cellData -> cellData.getValue().phuPhiProperty().asObject());
        colTong.setCellValueFactory(cellData -> cellData.getValue().tongDoanhThuProperty().asObject());

        // Định dạng cột số tiền
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

        colPhuPhi.setCellFactory(column -> new TableCell<BaoCaoTableData, Double>() {
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
                }
                setStyle("-fx-font-weight: bold;");
            }
        });

        // Gán dữ liệu cho TableView
        tableChiTiet.setItems(tableData);
    }

    private void setupEventHandlers() {
        btnXem.setOnAction(event -> loadBaoCaoThucTe());

        // Xử lý thay đổi kiểu báo cáo
        cbKieuBaoCao.setOnAction(event -> handleKieuBaoCaoChange());
    }

    private void handleKieuBaoCaoChange() {
        String kieuBaoCao = cbKieuBaoCao.getValue();

        if ("Theo tháng".equals(kieuBaoCao)) {
            cbThang.setVisible(true);
            cbQuy.setVisible(false);
        } else if ("Theo quý".equals(kieuBaoCao)) {
            cbThang.setVisible(false);
            cbQuy.setVisible(true);
        } else {
            // Theo năm
            cbThang.setVisible(false);
            cbQuy.setVisible(false);
        }

        // Clear biểu đồ khi đổi kiểu báo cáo
        chartDoanhThu.getData().clear();
        tableData.clear();
    }

    // ===================== MAIN METHOD =====================
    private void loadBaoCaoThucTe() {
        try {
            String kieuBaoCao = cbKieuBaoCao.getValue();
            String namStr = cbNam.getValue();

            if (namStr == null || namStr.isEmpty()) {
                showAlert("Lỗi", "Vui lòng chọn năm!");
                return;
            }

            int nam = Integer.parseInt(namStr);

            if ("Theo tháng".equals(kieuBaoCao)) {
                String thangStr = cbThang.getValue();
                if (thangStr == null || thangStr.isEmpty()) {
                    showAlert("Lỗi", "Vui lòng chọn tháng!");
                    return;
                }

                int thang = extractMonthNumber(thangStr);
                if (thang == -1) {
                    showAlert("Lỗi", "Định dạng tháng không hợp lệ!");
                    return;
                }

                // DÙNG DỮ LIỆU MẪU
                loadDuLieuMauThang(thang, nam);

            } else if ("Theo quý".equals(kieuBaoCao)) {
                String quyStr = cbQuy.getValue();
                if (quyStr == null || quyStr.isEmpty()) {
                    showAlert("Lỗi", "Vui lòng chọn quý!");
                    return;
                }

                // Xử lý theo quý
                loadDuLieuMauQuy(quyStr, nam);
            } else {
                // Xử lý theo năm
                loadDuLieuMauNam(nam);
            }

        } catch (Exception e) {
            showAlert("Lỗi", "Có lỗi xảy ra: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ===================== DATA METHODS (DÙNG DỮ LIỆU MẪU) =====================
    private void loadDuLieuMauThang(int thang, int nam) {
        System.out.println("🔄 Đang load dữ liệu MẪU tháng " + thang + "/" + nam);

        // Tạo dữ liệu mẫu ngẫu nhiên dựa trên tháng
        double tongDoanhThu = generateSampleRevenue(thang);
        int soLuotDat = generateSampleBookings(thang);
        double tyLeLap = generateSampleOccupancy(thang);
        String loaiPhongTop = generateSampleTopRoomType(thang);

        // Cập nhật UI với dữ liệu mẫu
        txtTongDoanhThu.setText(formatter.format(tongDoanhThu));
        txtLuotDat.setText(String.valueOf(soLuotDat));
        txtTyLeLap.setText(String.format("%.1f%%", tyLeLap));
        txtLoaiPhongTop.setText(loaiPhongTop);

        // Load biểu đồ theo TUẦN trong tháng
        loadBieuDoMauThang(thang, nam, tongDoanhThu);
        refreshChart();
        javafx.application.Platform.runLater(() -> {
            forceChartLayout();
        });
        // Load dữ liệu cho bảng (theo ngày trong tháng)
        loadTableDataForMonth(thang, nam, tongDoanhThu);

        System.out.println("✅ Đã load dữ liệu mẫu tháng " + thang + " thành công!");
    }

    private void loadDuLieuMauQuy(String quy, int nam) {
        System.out.println("🔄 Đang load dữ liệu MẪU " + quy + "/" + nam);

        int startMonth = getStartMonthOfQuarter(quy);

        // Tính tổng doanh thu cho cả quý
        double tongDoanhThu = 0;
        int tongLuotDat = 0;
        double tongTyLeLap = 0;

        for (int i = 0; i < 3; i++) {
            int thang = startMonth + i;
            tongDoanhThu += generateSampleRevenue(thang);
            tongLuotDat += generateSampleBookings(thang);
            tongTyLeLap += generateSampleOccupancy(thang);
        }

        // Tính trung bình
        int soLuotDat = tongLuotDat;
        double tyLeLap = tongTyLeLap / 3;
        String loaiPhongTop = getTopRoomTypeForQuarter(quy);

        // Cập nhật UI
        txtTongDoanhThu.setText(formatter.format(tongDoanhThu));
        txtLuotDat.setText(String.valueOf(soLuotDat));
        txtTyLeLap.setText(String.format("%.1f%%", tyLeLap));
        txtLoaiPhongTop.setText(loaiPhongTop);

        // Load biểu đồ theo THÁNG trong quý
        loadBieuDoMauQuy(quy, nam, startMonth, tongDoanhThu);

        // Load dữ liệu cho bảng (theo tháng trong quý)
        loadTableDataForQuarter(quy, nam, startMonth, tongDoanhThu);

        System.out.println("✅ Đã load dữ liệu mẫu " + quy + " thành công!");
    }

    private void loadDuLieuMauNam(int nam) {
        System.out.println("🔄 Đang load dữ liệu MẪU năm " + nam);

        // Tính tổng doanh thu cho cả năm
        double tongDoanhThu = 0;
        int tongLuotDat = 0;
        double tongTyLeLap = 0;

        for (int thang = 1; thang <= 12; thang++) {
            tongDoanhThu += generateSampleRevenue(thang);
            tongLuotDat += generateSampleBookings(thang);
            tongTyLeLap += generateSampleOccupancy(thang);
        }

        double tyLeLap = tongTyLeLap / 12;
        String loaiPhongTop = "Phòng Suite";

        // Cập nhật UI
        txtTongDoanhThu.setText(formatter.format(tongDoanhThu));
        txtLuotDat.setText(String.valueOf(tongLuotDat));
        txtTyLeLap.setText(String.format("%.1f%%", tyLeLap));
        txtLoaiPhongTop.setText(loaiPhongTop);

        // Load biểu đồ theo QUÝ trong năm
        loadBieuDoMauNam(nam, tongDoanhThu);

        // Load dữ liệu cho bảng (theo quý trong năm)
        loadTableDataForYear(nam, tongDoanhThu);

        System.out.println("✅ Đã load dữ liệu mẫu năm " + nam + " thành công!");
    }

    // ===================== BIỂU ĐỒ METHODS =====================
    private void loadBieuDoMauThang(int thang, int nam, double tongDoanhThu) {
        // SỬ DỤNG Platform.runLater() VÀ THÊM DELAY
        javafx.application.Platform.runLater(() -> {
            try {
                // ĐỢI 100ms ĐỂ ĐẢM BẢO UI ĐÃ SẴN SÀNG
                new Thread(() -> {
                    try {
                        Thread.sleep(100);
                        javafx.application.Platform.runLater(() -> {
                            try {
                                chartDoanhThu.getData().clear();

                                XYChart.Series<String, Number> series = new XYChart.Series<>();
                                series.setName("Tháng " + thang);

                                String[] tuan = {"Tuần 1", "Tuần 2", "Tuần 3", "Tuần 4"};
                                double[] tyLePhanBo = {0.2, 0.25, 0.3, 0.25};

                                for (int i = 0; i < tuan.length; i++) {
                                    double doanhThuTuan = tongDoanhThu * tyLePhanBo[i] * (0.9 + random.nextDouble() * 0.2);
                                    series.getData().add(new XYChart.Data<>(tuan[i], doanhThuTuan));
                                }

                                chartDoanhThu.getData().add(series);
                                chartDoanhThu.setTitle("Biểu đồ doanh thu tháng " + thang + "/" + nam);

                                // FORCE LAYOUT MULTIPLE TIMES
                                chartDoanhThu.requestLayout();
                                chartDoanhThu.applyCss();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();

            } catch (Exception e) {
                System.err.println("❌ Lỗi khi load biểu đồ: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    private void forceChartLayout() {
        if (chartDoanhThu == null) return;

        // Force layout multiple times
        chartDoanhThu.requestLayout();
        chartDoanhThu.applyCss();

        // Thêm delay và force lại
        javafx.application.Platform.runLater(() -> {
            chartDoanhThu.requestLayout();
            chartDoanhThu.applyCss();

            // Force thêm lần nữa sau 200ms
            new Thread(() -> {
                try {
                    Thread.sleep(200);
                    javafx.application.Platform.runLater(() -> {
                        chartDoanhThu.requestLayout();
                        chartDoanhThu.applyCss();
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }
    private void refreshChart() {
        if (chartDoanhThu != null) {
            // Force refresh chart
            chartDoanhThu.setAnimated(false); // Tắt animation để load nhanh hơn
            chartDoanhThu.requestLayout();

            // Bật lại animation sau khi render
            javafx.application.Platform.runLater(() -> {
                chartDoanhThu.setAnimated(true);
            });
        }
    }
    private void loadBieuDoMauQuy(String quy, int nam, int startMonth, double tongDoanhThu) {
        chartDoanhThu.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(quy);

        String[] thangTrongQuy = {
                "Tháng " + startMonth,
                "Tháng " + (startMonth + 1),
                "Tháng " + (startMonth + 2)
        };

        // Phân bổ doanh thu cho 3 tháng trong quý
        double[] tyLePhanBo = {0.3, 0.35, 0.35}; // Tháng cuối quý thường cao hơn

        for (int i = 0; i < thangTrongQuy.length; i++) {
            double doanhThuThang = tongDoanhThu * tyLePhanBo[i] * (0.9 + random.nextDouble() * 0.2);
            series.getData().add(new XYChart.Data<>(thangTrongQuy[i], doanhThuThang));
        }

        chartDoanhThu.getData().add(series);
        chartDoanhThu.setTitle("Biểu đồ doanh thu " + quy + "/" + nam);
    }

    private void loadBieuDoMauNam(int nam, double tongDoanhThu) {
        chartDoanhThu.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Năm " + nam);

        String[] quyTrongNam = {"Quý 1", "Quý 2", "Quý 3", "Quý 4"};

        // Phân bổ doanh thu cho 4 quý trong năm
        double[] tyLePhanBo = {0.2, 0.25, 0.3, 0.25}; // Quý 3 (mùa hè) thường cao nhất

        for (int i = 0; i < quyTrongNam.length; i++) {
            double doanhThuQuy = tongDoanhThu * tyLePhanBo[i] * (0.9 + random.nextDouble() * 0.2);
            series.getData().add(new XYChart.Data<>(quyTrongNam[i], doanhThuQuy));
        }

        chartDoanhThu.getData().add(series);
        chartDoanhThu.setTitle("Biểu đồ doanh thu năm " + nam);
    }

    // ===================== TABLE DATA METHODS =====================
    private void loadTableDataForMonth(int thang, int nam, double tongDoanhThu) {
        tableData.clear();

        int soNgayTrongThang = getDaysInMonth(thang, nam);
        double doanhThuTrungBinhNgay = tongDoanhThu / soNgayTrongThang;

        // Tạo dữ liệu cho 7 ngày đầu tháng (để demo)
        for (int ngay = 1; ngay <= Math.min(7, soNgayTrongThang); ngay++) {
            String thoiGian = String.format("%02d/%02d/%d", ngay, thang, nam);
            int soLuotDat = 2 + random.nextInt(5); // 2-7 lượt/ngày
            double doanhThuPhong = doanhThuTrungBinhNgay * (0.7 + random.nextDouble() * 0.6);
            double phuPhi = doanhThuPhong * 0.1 * random.nextDouble(); // 0-10% phụ phí
            double tong = doanhThuPhong + phuPhi;

            tableData.add(new BaoCaoTableData(thoiGian, soLuotDat, doanhThuPhong, phuPhi, tong));
        }
    }

    private void loadTableDataForQuarter(String quy, int nam, int startMonth, double tongDoanhThu) {
        tableData.clear();

        String[] tenThang = {"Tháng " + startMonth, "Tháng " + (startMonth + 1), "Tháng " + (startMonth + 2)};
        double[] tyLePhanBo = {0.3, 0.35, 0.35};

        for (int i = 0; i < 3; i++) {
            String thoiGian = tenThang[i];
            int soLuotDat = 25 + random.nextInt(20); // 25-45 lượt/tháng
            double doanhThuPhong = tongDoanhThu * tyLePhanBo[i] * (0.9 + random.nextDouble() * 0.2);
            double phuPhi = doanhThuPhong * 0.15; // 15% phụ phí
            double tong = doanhThuPhong + phuPhi;

            tableData.add(new BaoCaoTableData(thoiGian, soLuotDat, doanhThuPhong, phuPhi, tong));
        }
    }

    private void loadTableDataForYear(int nam, double tongDoanhThu) {
        tableData.clear();

        String[] tenQuy = {"Quý 1", "Quý 2", "Quý 3", "Quý 4"};
        double[] tyLePhanBo = {0.2, 0.25, 0.3, 0.25};

        for (int i = 0; i < 4; i++) {
            String thoiGian = tenQuy[i] + "/" + nam;
            int soLuotDat = 80 + random.nextInt(40); // 80-120 lượt/quý
            double doanhThuPhong = tongDoanhThu * tyLePhanBo[i] * (0.9 + random.nextDouble() * 0.2);
            double phuPhi = doanhThuPhong * 0.12; // 12% phụ phí
            double tong = doanhThuPhong + phuPhi;

            tableData.add(new BaoCaoTableData(thoiGian, soLuotDat, doanhThuPhong, phuPhi, tong));
        }
    }

    // ===================== GENERATE SAMPLE DATA =====================
    private double generateSampleRevenue(int thang) {
        // Doanh thu tăng dần từ tháng 1 đến tháng 12
        double baseRevenue = 15000000; // 15 triệu
        double monthlyIncrease = 2000000; // tăng 2 triệu mỗi tháng
        return baseRevenue + (thang * monthlyIncrease) + random.nextInt(5000000);
    }

    private int generateSampleBookings(int thang) {
        // Số lượt đặt tăng theo mùa
        int baseBookings = 30;
        int seasonalAdjustment = (thang >= 3 && thang <= 8) ? 20 : 0; // Mùa cao điểm
        return baseBookings + thang * 2 + seasonalAdjustment + random.nextInt(15);
    }

    private double generateSampleOccupancy(int thang) {
        // Tỷ lệ lấp phòng theo mùa
        double baseOccupancy = 60.0;
        double seasonalAdjustment = (thang >= 5 && thang <= 10) ? 15.0 : 0.0;
        return baseOccupancy + (thang * 1.5) + seasonalAdjustment + random.nextDouble() * 10;
    }

    private String generateSampleTopRoomType(int thang) {
        String[] roomTypes = {"Phòng Standard", "Phòng Deluxe", "Phòng Suite", "Phòng Family"};
        // Thay đổi loại phòng top theo tháng
        int index = (thang - 1) % roomTypes.length;
        return roomTypes[index];
    }

    private String getTopRoomTypeForQuarter(String quy) {
        switch (quy) {
            case "Quý 1": return "Phòng Deluxe";
            case "Quý 2": return "Phòng Suite";
            case "Quý 3": return "Phòng Family";
            case "Quý 4": return "Phòng Suite";
            default: return "Phòng Deluxe";
        }
    }

    // ===================== UTILITY METHODS =====================
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

    private int getStartMonthOfQuarter(String quy) {
        switch (quy) {
            case "Quý 1": return 1;
            case "Quý 2": return 4;
            case "Quý 3": return 7;
            case "Quý 4": return 10;
            default: return 1;
        }
    }

    private int getDaysInMonth(int thang, int nam) {
        switch (thang) {
            case 2: return (nam % 4 == 0 && (nam % 100 != 0 || nam % 400 == 0)) ? 29 : 28;
            case 4: case 6: case 9: case 11: return 30;
            default: return 31;
        }
    }

    private void clearAllData() {
        txtTongDoanhThu.setText("0 VNĐ");
        txtLuotDat.setText("0");
        txtTyLeLap.setText("0%");
        txtLoaiPhongTop.setText("---");
        chartDoanhThu.getData().clear();
        tableData.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void refreshData() {
        loadBaoCaoThucTe();
    }
}
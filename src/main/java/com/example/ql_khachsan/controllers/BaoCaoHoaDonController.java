package com.example.ql_khachsan.controllers;

import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;
import javafx.util.Callback;
import javafx.scene.control.TableCell;
import com.example.ql_khachsan.models.HoaDon;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;


import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BaoCaoHoaDonController {

    @FXML private TableView<HoaDon> tblHoaDon;
    @FXML private TableColumn<HoaDon, Integer> colSTT;
    @FXML private TableColumn<HoaDon, String> colMaHD;
    @FXML private TableColumn<HoaDon, String> colMaDP;
    @FXML private TableColumn<HoaDon, String> colNgayLap;
    @FXML private TableColumn<HoaDon, String> colGhiChu;
    @FXML private TableColumn<HoaDon, String> colTongTien;

    @FXML private TableColumn<HoaDon, Void> colChiTiet; // thêm cột nút chi tiết

    @FXML private TextField txtMaKH;
    @FXML private DatePicker dpFrom;
    @FXML private DatePicker dpTo;

    private final ObservableList<HoaDon> data = FXCollections.observableArrayList();

    private final String url = "jdbc:mysql://localhost:3306/qlkhachsan?useSSL=false&serverTimezone=UTC";
    private final String user = "root";
    private final String password = "123456";

    private final DateTimeFormatter dtfDB = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final DateTimeFormatter dtfView = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        colSTT.setCellValueFactory(c -> c.getValue().sttProperty().asObject());
        colMaHD.setCellValueFactory(c -> c.getValue().maHDProperty());
        colMaDP.setCellValueFactory(c -> c.getValue().maDPProperty());
        colGhiChu.setCellValueFactory(c -> c.getValue().ghiChuProperty());
        colTongTien.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.format("%.2f", c.getValue().getTongTien())));
        colNgayLap.setCellValueFactory(c -> {
            LocalDateTime dt = c.getValue().getNgayLap();
            return new ReadOnlyStringWrapper(dt != null ? dt.format(dtfView) : "");
        });

        addButtonToTable(); // thêm nút chi tiết

        loadData(null, null, null);
    }

    @FXML
    private void handleFilter() {
        String maKH = txtMaKH.getText().trim();
        LocalDate from = dpFrom.getValue();
        LocalDate to = dpTo.getValue();
        loadData(maKH.isEmpty() ? null : maKH, from, to);
    }

    @FXML
    private void handleExport() {
        System.out.println("Xuất báo cáo...");
        // TODO: xuất Excel / PDF
    }

    private void loadData(String maKH, LocalDate from, LocalDate to) {
        data.clear();
        String sql = "SELECT HD.MaHD, HD.NgayLap, HD.MaDP, HD.GhiChu, " +
                "DP.NgayNhan, DP.NgayTra, LP.DonGia " +
                "FROM HOADON HD " +
                "JOIN PHIEUDATPHONG DP ON HD.MaDP = DP.MaDP " +
                "JOIN PHONG P ON DP.MaPhong = P.MaPhong " +
                "JOIN LOAIPHONG LP ON P.MaLoai = LP.MaLoai " +
                "JOIN KHACHHANG KH ON DP.MaKH = KH.MaKH " +
                "WHERE 1=1";

        if (maKH != null) sql += " AND KH.MaKH = ?";
        if (from != null) sql += " AND HD.NgayLap >= ?";
        if (to != null) sql += " AND HD.NgayLap <= ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int idx = 1;
            if (maKH != null) stmt.setString(idx++, maKH);
            if (from != null) stmt.setString(idx++, from.atStartOfDay().format(dtfDB));
            if (to != null) stmt.setString(idx++, to.atTime(23,59,59).format(dtfDB));

            ResultSet rs = stmt.executeQuery();
            int sttCounter = 1;
            while (rs.next()) {
                LocalDateTime ngayLap = rs.getTimestamp("NgayLap").toLocalDateTime();
                LocalDateTime ngayNhan = rs.getTimestamp("NgayNhan").toLocalDateTime();
                LocalDateTime ngayTra = rs.getTimestamp("NgayTra").toLocalDateTime();
                double donGia = rs.getDouble("DonGia");
                long soNgay = java.time.Duration.between(ngayNhan, ngayTra).toDays();
                double tongTien = soNgay * donGia;

                data.add(new HoaDon(
                        sttCounter++,
                        rs.getString("MaHD"),
                        rs.getString("MaDP"),
                        rs.getString("GhiChu"),
                        ngayLap,
                        tongTien
                ));
            }

            tblHoaDon.setItems(data);

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Lỗi khi lấy dữ liệu từ database!");
            alert.showAndWait();
        }
    }

    private void addButtonToTable() {
        Callback<TableColumn<HoaDon, Void>, TableCell<HoaDon, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<HoaDon, Void> call(final TableColumn<HoaDon, Void> param) {
                return new TableCell<>() {

                    private final Button btn = new Button("Chi tiết");

                    {
                        btn.setOnAction(event -> {
                            HoaDon hd = getTableView().getItems().get(getIndex());
                            showChiTiet(hd);
                        });
                        btn.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        };
        colChiTiet.setCellFactory(cellFactory);
    }

    private void showChiTiet(HoaDon hd) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ql_khachsan/views/HoaDon.fxml"));
            Parent root = loader.load();

            // Lấy controller để truyền dữ liệu
            HoaDonController controller = loader.getController();
            controller.setData(hd, "Tên khách hàng ở đây");

            Stage stage = new Stage();
            stage.setTitle("Chi tiết hóa đơn");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // chặn tương tác với stage khác
            stage.showAndWait(); // hoặc stage.show() nếu không cần chặn
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Không thể mở chi tiết hóa đơn!");
            alert.showAndWait();
        }
    }


}

package com.example.ql_khachsan.controllers;

import com.example.ql_khachsan.dao.HoaDonDAO;
import com.example.ql_khachsan.models.HoaDon;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LichSuHoaDonController {
    @FXML private TableView<HoaDon> tblHoaDon;
    @FXML private TableColumn<HoaDon, Integer> colSTT;
    @FXML private TableColumn<HoaDon, String> colMaHD;
    @FXML private TableColumn<HoaDon, String> colHoTen;
    @FXML private TableColumn<HoaDon, String> colMaDP;
    @FXML private TableColumn<HoaDon, String> colNgayLap;
    @FXML private TableColumn<HoaDon, String> colGhiChu;
    @FXML private TableColumn<HoaDon, String> colTongTien;
    @FXML private TableColumn<HoaDon, Void> colChiTiet;

    @FXML private TextField txtMaKH;
    @FXML private DatePicker dpFrom;
    @FXML private DatePicker dpTo;

    private final ObservableList<HoaDon> data = FXCollections.observableArrayList();
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final DateTimeFormatter dtfView = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        colSTT.setCellValueFactory(c -> c.getValue().sttProperty().asObject());
        colMaHD.setCellValueFactory(c -> c.getValue().maHDProperty());
        colHoTen.setCellValueFactory(c -> c.getValue().tenKHProperty());
        colMaDP.setCellValueFactory(c -> c.getValue().maDPProperty());
        colGhiChu.setCellValueFactory(c -> c.getValue().ghiChuProperty());

        colTongTien.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.format("%,.0f VNĐ", c.getValue().getTongTien())));

        colNgayLap.setCellValueFactory(c -> {
            LocalDateTime dt = c.getValue().getNgayLap();
            return new ReadOnlyStringWrapper(dt != null ? dt.format(dtfView) : "");
        });

        addButtonToTable();
        loadData(null, null, null);
    }

    @FXML
    private void handleFilter() {
        String tenKH = txtMaKH.getText().trim();
        LocalDate from = dpFrom.getValue();
        LocalDate to = dpTo.getValue();
        loadData(tenKH.isEmpty() ? null : tenKH, from, to);
    }

    private void loadData(String tenKH, LocalDate from, LocalDate to) {
        data.clear();
        try {
            List<HoaDon> list = hoaDonDAO.getChiTietHoaDonList(tenKH, from, to);
            data.addAll(list);
            tblHoaDon.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Lỗi khi lấy dữ liệu: " + e.getMessage()).showAndWait();
        }
    }

    private void addButtonToTable() {
        Callback<TableColumn<HoaDon, Void>, TableCell<HoaDon, Void>> cellFactory = param -> new TableCell<>() {
            private final Button btn = new Button("Chi tiết");
            {
                btn.getStyleClass().add("btn-detail");
                btn.setOnAction(event -> {
                    HoaDon hd = getTableView().getItems().get(getIndex());
                    showChiTiet(hd);
                });
            }
            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(btn);
            }
        };
        colChiTiet.setCellFactory(cellFactory);
    }

    private void showChiTiet(HoaDon hd) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ql_khachsan/views/HoaDonView.fxml"));
            Parent root = loader.load();
            HoaDonController controller = loader.getController();
            controller.setData(hd);
            Stage stage = new Stage();
            stage.setTitle("Chi tiết hóa đơn: " + hd.getMaHD());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
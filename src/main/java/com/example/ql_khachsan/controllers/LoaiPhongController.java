package com.example.ql_khachsan.controllers;

import com.example.ql_khachsan.dao.LoaiPhongDAO;
import com.example.ql_khachsan.models.LoaiPhong;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class LoaiPhongController {

    @FXML private TextField txtMaLoai, txtTenLoai, txtSoNguoi, txtDonGia;
    @FXML private TableView<LoaiPhong> tblLoaiPhong;
    @FXML private TableColumn<LoaiPhong, String> colMaLoai, colTenLoai;
    @FXML private TableColumn<LoaiPhong, Integer> colSoNguoi;
    @FXML private TableColumn<LoaiPhong, Double> colDonGia;

    private ObservableList<LoaiPhong> dsLoaiPhong = FXCollections.observableArrayList();
    private LoaiPhongDAO dao = new LoaiPhongDAO();

    @FXML
    public void initialize() {
        colMaLoai.setCellValueFactory(data -> data.getValue().maLoaiProperty());
        colTenLoai.setCellValueFactory(data -> data.getValue().tenLoaiProperty());
        colSoNguoi.setCellValueFactory(data -> data.getValue().soNguoiTDProperty().asObject());
        colDonGia.setCellValueFactory(data -> data.getValue().donGiaProperty().asObject());

        dsLoaiPhong.addAll(dao.getAllLoaiPhong());
        tblLoaiPhong.setItems(dsLoaiPhong);
    }

    @FXML
    private void handleAddLoaiPhong() {
        try {
            LoaiPhong lp = new LoaiPhong(
                txtMaLoai.getText(),
                txtTenLoai.getText(),
                Integer.parseInt(txtSoNguoi.getText()),
                Double.parseDouble(txtDonGia.getText())
            );

            if (dao.insertLoaiPhong(lp)) {
                dsLoaiPhong.add(lp);
                clearInputs();
            } else {
                showAlert("Lỗi", "Không thêm được loại phòng", Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException e) {
            showAlert("Lỗi", "Số người hoặc đơn giá không hợp lệ", Alert.AlertType.ERROR);
        }
    }

    private void clearInputs() {
        txtMaLoai.clear();
        txtTenLoai.clear();
        txtSoNguoi.clear();
        txtDonGia.clear();
    }

    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

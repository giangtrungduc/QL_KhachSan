package com.example.ql_khachsan.controllers;

import com.example.ql_khachsan.dao.LoaiPhongDAO;
import com.example.ql_khachsan.dao.PhongDAO;
import com.example.ql_khachsan.models.LoaiPhong;
import com.example.ql_khachsan.models.Phong;
import com.example.ql_khachsan.models.PhongDetail;
import com.example.ql_khachsan.untils.TrangThaiPhong;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class PhongController implements Initializable {

    @FXML private TextField txtMaPhong;
    @FXML private TextField txtTenPhong;
    @FXML private TextField txtGiaPhong;
    @FXML private TextField txtSoNguoi;
    @FXML private TextField txtTimKiem;
    @FXML private ComboBox<LoaiPhong> cbLoaiPhong;
    @FXML private ComboBox<TrangThaiPhong> cbTrangThai;

    @FXML private Button btnThem;
    @FXML private Button btnSua;
    @FXML private Button btnXoa;
    @FXML private Button btnLamMoi;

    @FXML private TableView<PhongDetail> tvPhong;
    @FXML private TableColumn<PhongDetail, String> colMaPhong;
    @FXML private TableColumn<PhongDetail, String> colTenPhong;
    @FXML private TableColumn<PhongDetail, Integer> colSoNguoi;
    @FXML private TableColumn<PhongDetail, String> colLoaiPhong;
    @FXML private TableColumn<PhongDetail, BigDecimal> colGiaPhong;
    @FXML private TableColumn<PhongDetail, TrangThaiPhong> colTrangThai;

    private PhongDAO phongDAO;
    private LoaiPhongDAO loaiPhongDAO;
    private ObservableList<PhongDetail> danhSachPhong;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        phongDAO = new PhongDAO();
        loaiPhongDAO = new LoaiPhongDAO();
        danhSachPhong = FXCollections.observableArrayList();

        loadData();

        FilteredList<PhongDetail> filteredData = new FilteredList<>(danhSachPhong, p -> true);

        txtTimKiem.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(phong -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (phong.getMaPhong().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (phong.getTenPhong().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else return phong.getTenLoai().toLowerCase().contains(lowerCaseFilter);
            });
        });

        SortedList<PhongDetail> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tvPhong.comparatorProperty());
        tvPhong.setItems(sortedData);

        txtMaPhong.setEditable(false);
        txtSoNguoi.setEditable(false);

        txtTenPhong.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!txtMaPhong.isDisabled()) {
                if (newValue != null) {
                    String chiLaySo = newValue.replaceAll("[^0-9]", "");
                    txtMaPhong.setText("P" + chiLaySo);
                }
            }
        });

        cbLoaiPhong.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if (newVal.getDonGia() != null) {
                    txtGiaPhong.setText(newVal.getDonGia().toPlainString());
                }
                txtSoNguoi.setText(String.valueOf(newVal.getSoNguoiTD()));
            }
        });

        cbTrangThai.setItems(FXCollections.observableArrayList(TrangThaiPhong.values()));
        cbLoaiPhong.setItems(FXCollections.observableArrayList(loaiPhongDAO.getAll()));

        setupTableColumns();

        tvPhong.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                fillForm(newVal);
            }
        });

        btnThem.setOnAction(e -> themPhong());
        btnSua.setOnAction(e -> suaPhong());
        btnXoa.setOnAction(e -> xoaPhong());
        btnLamMoi.setOnAction(e -> lamMoiForm());
    }

    private void loadData() {
        danhSachPhong.setAll(phongDAO.getAll());
    }

    private void setupTableColumns() {
        colMaPhong.setCellValueFactory(cell -> cell.getValue().maPhongProperty());
        colTenPhong.setCellValueFactory(cell -> cell.getValue().tenPhongProperty());
        colSoNguoi.setCellValueFactory(cell -> cell.getValue().soNguoiTDProperty().asObject());
        colLoaiPhong.setCellValueFactory(cell -> cell.getValue().tenLoaiProperty());

        colGiaPhong.setCellValueFactory(cell -> cell.getValue().donGiaProperty());
        colGiaPhong.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    NumberFormat vnFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                    setText(vnFormat.format(item));
                }
            }
        });

        colTrangThai.setCellValueFactory(cell -> cell.getValue().trangThaiProperty());
    }

    private void themPhong() {
        if (!validateInput()) return;
        try {
            Phong p = new Phong();
            p.setMaPhong(txtMaPhong.getText());
            p.setTenPhong(txtTenPhong.getText());

            if (cbLoaiPhong.getValue() != null) {
                p.setMaLoai(cbLoaiPhong.getValue().getMaLoai());
            }
            p.setTrangThai(cbTrangThai.getValue());

            if (phongDAO.insert(p)) {
                thongBao("Thành công", "Thêm phòng mới thành công!", Alert.AlertType.INFORMATION);
                loadData();
                lamMoiForm();
            } else {
                thongBao("Thất bại", "Không thể thêm phòng (Có thể trùng Mã phòng?)", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            thongBao("Lỗi", "Dữ liệu không hợp lệ: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void suaPhong() {
        PhongDetail selected = tvPhong.getSelectionModel().getSelectedItem();
        if (selected == null) {
            thongBao("Chú ý", "Vui lòng chọn phòng cần sửa", Alert.AlertType.WARNING);
            return;
        }

        if (!validateInput()) return;

        try {
            Phong p = new Phong();
            p.setMaPhong(selected.getMaPhong());
            p.setTenPhong(txtTenPhong.getText());
            if (cbLoaiPhong.getValue() != null) {
                p.setMaLoai(cbLoaiPhong.getValue().getMaLoai());
            }
            p.setTrangThai(cbTrangThai.getValue());

            if (phongDAO.update(p)) {
                thongBao("Thành công", "Cập nhật thành công!", Alert.AlertType.INFORMATION);
                loadData();
            } else {
                thongBao("Lỗi", "Cập nhật thất bại!", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void xoaPhong() {
        PhongDetail selected = tvPhong.getSelectionModel().getSelectedItem();
        if (selected == null) {
            thongBao("Chú ý", "Vui lòng chọn phòng cần xóa", Alert.AlertType.WARNING);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Xóa phòng " + selected.getMaPhong() + "?");
        Optional<ButtonType> res = alert.showAndWait();

        if (res.isPresent() && res.get() == ButtonType.OK) {
            if (phongDAO.delete(selected.getMaPhong())) {
                loadData();
                lamMoiForm();
                thongBao("Thành công", "Đã xóa phòng", Alert.AlertType.INFORMATION);
            } else {
                thongBao("Lỗi", "Xóa thất bại", Alert.AlertType.ERROR);
            }
        }
    }

    private void fillForm(PhongDetail p) {
        txtMaPhong.setText(p.getMaPhong());
        txtMaPhong.setDisable(true);
        txtTenPhong.setText(p.getTenPhong());
        txtSoNguoi.setText(String.valueOf(p.getSoNguoiTD()));
        cbTrangThai.setValue(p.getTrangThai());

        String maLoaiCanTim = p.getMaLoai();
        if (maLoaiCanTim != null) {
            for (LoaiPhong lp : cbLoaiPhong.getItems()) {
                if (lp.getMaLoai().equals(maLoaiCanTim)) {
                    cbLoaiPhong.setValue(lp);
                    break;
                }
            }
        }

        if (p.getDonGia() != null) {
            txtGiaPhong.setText(p.getDonGia().toPlainString());
        }
    }

    private void lamMoiForm() {
        txtMaPhong.clear();
        txtMaPhong.setDisable(false);
        txtTenPhong.clear();
        txtGiaPhong.clear();
        txtSoNguoi.clear();
        cbLoaiPhong.setValue(null);
        cbTrangThai.setValue(null);
        tvPhong.getSelectionModel().clearSelection();
    }

    private boolean validateInput() {
        return !txtMaPhong.getText().isEmpty() && !txtTenPhong.getText().isEmpty()
                && cbLoaiPhong.getValue() != null && cbTrangThai.getValue() != null;
    }

    private void thongBao(String title, String content, Alert.AlertType type) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }
}
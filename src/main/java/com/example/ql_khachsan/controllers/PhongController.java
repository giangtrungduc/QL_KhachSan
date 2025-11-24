package com.example.ql_khachsan.controllers;

import com.example.ql_khachsan.dao.LoaiPhongDAO;
import com.example.ql_khachsan.dao.PhongDAO;
import com.example.ql_khachsan.models.LoaiPhong;
import com.example.ql_khachsan.models.Phong;
import com.example.ql_khachsan.models.PhongDetail;
import com.example.ql_khachsan.untils.TrangThaiPhong;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class PhongController implements Initializable {

    // --- FXML Fields ---
    @FXML private ComboBox<TrangThaiPhong> cbLocTrangThai;
    @FXML private ComboBox<LoaiPhong> cbLocLoaiPhong;
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

    // --- TableView ---
    @FXML private TableView<PhongDetail> tvPhong;

    @FXML private TableColumn<PhongDetail, Integer> colSTT;
    @FXML private TableColumn<PhongDetail, String> colMaPhong;
    @FXML private TableColumn<PhongDetail, String> colTenPhong;
    @FXML private TableColumn<PhongDetail, Integer> colSoNguoi;
    @FXML private TableColumn<PhongDetail, String> colLoaiPhong;
    @FXML private TableColumn<PhongDetail, BigDecimal> colGiaPhong;
    @FXML private TableColumn<PhongDetail, TrangThaiPhong> colTrangThai;

    private PhongDAO phongDAO;
    private LoaiPhongDAO loaiPhongDAO;
    private ObservableList<PhongDetail> danhSachPhong;

    private String maPhongDangChon = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        phongDAO = new PhongDAO();
        loaiPhongDAO = new LoaiPhongDAO();
        danhSachPhong = FXCollections.observableArrayList();

        tvPhong.setItems(danhSachPhong);

        loadData();

        // --- 1. SETUP BỘ LỌC TRẠNG THÁI (Thêm mục "Tất cả") ---
        ObservableList<TrangThaiPhong> listTrangThai = FXCollections.observableArrayList(TrangThaiPhong.values());
        listTrangThai.add(0, null);
        cbLocTrangThai.setItems(listTrangThai);
        setupComboBoxPlaceholder(cbLocTrangThai, "Tất cả trạng thái");

        // --- 2. SETUP BỘ LỌC LOẠI PHÒNG (Thêm mục "Tất cả") ---
        List<LoaiPhong> listLoai = loaiPhongDAO.getAll();
        ObservableList<LoaiPhong> listLocLoai = FXCollections.observableArrayList(listLoai);
        listLocLoai.add(0, null);
        cbLocLoaiPhong.setItems(listLocLoai);
        setupComboBoxPlaceholder(cbLocLoaiPhong, "Tất cả loại phòng");

        // --- 3. BẮT SỰ KIỆN TÌM KIẾM (ĐÃ SỬA GỌN) ---
        // Cả 3 ô này khi thay đổi đều gọi chung 1 hàm thucHienTimKiem
        txtTimKiem.textProperty().addListener((obs, old, newVal) -> thucHienTimKiem());
        cbLocTrangThai.valueProperty().addListener((obs, old, newVal) -> thucHienTimKiem());
        cbLocLoaiPhong.valueProperty().addListener((obs, old, newVal) -> thucHienTimKiem());

        // --- 4. TÔ MÀU BẢNG ---
        setupRowColor();

        // --- SETUP UI AUTO FILL ---
        txtSoNguoi.setEditable(false);
        txtGiaPhong.setEditable(false);

        cbLoaiPhong.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if (newVal.getDonGia() != null) txtGiaPhong.setText(newVal.getDonGia().toPlainString());
                txtSoNguoi.setText(String.valueOf(newVal.getSoNguoiTD()));
            }
        });

        cbTrangThai.setItems(FXCollections.observableArrayList(TrangThaiPhong.values()));
        cbLoaiPhong.setItems(FXCollections.observableArrayList(loaiPhongDAO.getAll()));

        setupTableColumns();

        tvPhong.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) fillForm(newVal);
        });

        btnThem.setOnAction(e -> themPhong());
        btnSua.setOnAction(e -> suaPhong());
        btnXoa.setOnAction(e -> xoaPhong());
        btnLamMoi.setOnAction(e -> lamMoiForm());

        lamMoiForm();
    }

    private void loadData() {
        danhSachPhong.setAll(phongDAO.getAll());
    }

    // --- HÀM TÌM KIẾM CHÍNH (THAY THẾ CHO timKiemTuDB CŨ) ---
    private void thucHienTimKiem() {
        String tuKhoa = txtTimKiem.getText() == null ? "" : txtTimKiem.getText().trim();
        TrangThaiPhong trangThai = cbLocTrangThai.getValue();

        String maLoai = null;
        if (cbLocLoaiPhong.getValue() != null) {
            maLoai = cbLocLoaiPhong.getValue().getMaLoai();
        }

        // [QUAN TRỌNG] Bạn nhớ phải có hàm searchAdvanced bên PhongDAO nhé!
        danhSachPhong.setAll(phongDAO.searchAdvanced(tuKhoa, trangThai, maLoai));
    }

    private void setupTableColumns() {
        colSTT.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(tvPhong.getItems().indexOf(column.getValue()) + 1));
        colSTT.setSortable(false);

        colMaPhong.setCellValueFactory(cell -> cell.getValue().maPhongProperty());
        colTenPhong.setCellValueFactory(cell -> cell.getValue().tenPhongProperty());
        colSoNguoi.setCellValueFactory(cell -> cell.getValue().soNguoiTDProperty().asObject());
        colLoaiPhong.setCellValueFactory(cell -> cell.getValue().tenLoaiProperty());

        colGiaPhong.setCellValueFactory(cell -> cell.getValue().donGiaProperty());
        colGiaPhong.setCellFactory(column -> new TableCell<PhongDetail, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(item));
            }
        });

        colTrangThai.setCellValueFactory(cell -> cell.getValue().trangThaiProperty());
    }

    // --- TÔ MÀU DÒNG (ĐÃ BỎ PHẦN BẢO TRÌ) ---
    private void setupRowColor() {
        tvPhong.setRowFactory(tv -> new TableRow<PhongDetail>() {
            @Override
            protected void updateItem(PhongDetail item, boolean empty) {
                super.updateItem(item, empty);
                setStyle("");

                if (item == null || empty) {
                    return;
                }

                TrangThaiPhong status = item.getTrangThai();

                if (status == TrangThaiPhong.TRONG) {
                    // Màu xanh nhạt
                    setStyle("-fx-background-color: #d4edda;");
                } else if (status == TrangThaiPhong.DA_DAT) {
                    // Màu vàng nhạt
                    setStyle("-fx-background-color: #fff3cd;");
                } else if (status == TrangThaiPhong.DANG_SU_DUNG) {
                    // Màu đỏ nhạt
                    setStyle("-fx-background-color: #f8d7da;");
                }
            }
        });
    }

    // ... (GIỮ NGUYÊN CÁC HÀM CRUD VÀ HELPER PHÍA DƯỚI CỦA BẠN) ...

    private void themPhong() {
        if (!validateInput()) return;
        try {
            Phong p = new Phong();
            String tenPhong = txtTenPhong.getText();
            String soPhong = tenPhong.replaceAll("[^0-9]", "");
            if (soPhong.isEmpty()) {
                thongBao("Lỗi", "Tên phòng phải chứa số (VD: Phòng 101)", Alert.AlertType.ERROR);
                return;
            }
            p.setMaPhong("P" + soPhong);
            p.setTenPhong(tenPhong);

            if (cbLoaiPhong.getValue() != null) {
                p.setMaLoai(cbLoaiPhong.getValue().getMaLoai());
            }
            p.setTrangThai(TrangThaiPhong.TRONG);

            if (phongDAO.insert(p)) {
                thongBao("Thành công", "Thêm phòng thành công!", Alert.AlertType.INFORMATION);
                loadData();
                lamMoiForm();
            } else {
                thongBao("Thất bại", "Mã phòng đã tồn tại!", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            thongBao("Lỗi", "Lỗi: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void suaPhong() {
        if (maPhongDangChon == null) {
            thongBao("Chú ý", "Vui lòng chọn phòng cần sửa", Alert.AlertType.WARNING);
            return;
        }
        if (!validateInput()) return;
        try {
            Phong p = new Phong();
            p.setMaPhong(maPhongDangChon);
            p.setTenPhong(txtTenPhong.getText());
            if (cbLoaiPhong.getValue() != null) {
                p.setMaLoai(cbLoaiPhong.getValue().getMaLoai());
            }
            p.setTrangThai(cbTrangThai.getValue());

            if (phongDAO.update(p)) {
                thongBao("Thành công", "Cập nhật thành công!", Alert.AlertType.INFORMATION);
                loadData();
                lamMoiForm();
            } else {
                thongBao("Lỗi", "Cập nhật thất bại!", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void xoaPhong() {
        if (maPhongDangChon == null) {
            thongBao("Chú ý", "Vui lòng chọn phòng cần xóa", Alert.AlertType.WARNING);
            return;
        }
        PhongDetail selected = tvPhong.getSelectionModel().getSelectedItem();
        if (selected.getTrangThai() == TrangThaiPhong.DA_DAT || selected.getTrangThai() == TrangThaiPhong.DANG_SU_DUNG) {
            thongBao("Cảnh báo", "Không thể xóa phòng đang có khách!", Alert.AlertType.WARNING);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText(null);
        alert.setContentText("Bạn có thực sự muốn xóa phòng " + selected.getTenPhong() + "?");
        Optional<ButtonType> res = alert.showAndWait();

        if (res.isPresent() && res.get() == ButtonType.OK) {
            if (phongDAO.delete(maPhongDangChon)) {
                loadData();
                lamMoiForm();
                thongBao("Thành công", "Đã xóa phòng", Alert.AlertType.INFORMATION);
            } else {
                thongBao("Lỗi", "Xóa thất bại", Alert.AlertType.ERROR);
            }
        }
    }

    private void fillForm(PhongDetail p) {
        maPhongDangChon = p.getMaPhong();
        txtTenPhong.setText(p.getTenPhong());
        txtSoNguoi.setText(String.valueOf(p.getSoNguoiTD()));
        cbTrangThai.setDisable(false);
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
        if (p.getDonGia() != null) txtGiaPhong.setText(p.getDonGia().toPlainString());
    }

    private void lamMoiForm() {
        maPhongDangChon = null;
        txtTenPhong.clear();
        txtGiaPhong.clear();
        txtSoNguoi.clear();
        cbLoaiPhong.setValue(null);
        tvPhong.getSelectionModel().clearSelection();
        cbTrangThai.setValue(TrangThaiPhong.TRONG);
        cbTrangThai.setDisable(true);
    }

    private boolean validateInput() {
        return !txtTenPhong.getText().isEmpty() && cbLoaiPhong.getValue() != null;
    }

    private void thongBao(String title, String content, Alert.AlertType type) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }

    private <T> void setupComboBoxPlaceholder(ComboBox<T> comboBox, String placeholder) {
        ListCell<T> cellFactory = new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(placeholder);
                else setText(item.toString());
            }
        };
        comboBox.setButtonCell(cellFactory);
        comboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(placeholder);
                else setText(item.toString());
            }
        });
    }
}
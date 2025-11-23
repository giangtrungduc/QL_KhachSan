package com.example.ql_khachsan.controllers;

import com.example.ql_khachsan.dao.LoaiPhongDAO;
import com.example.ql_khachsan.dao.PhongDAO;
import com.example.ql_khachsan.models.LoaiPhong;
import com.example.ql_khachsan.models.Phong;
import com.example.ql_khachsan.models.PhongDetail;
import com.example.ql_khachsan.untils.TrangThaiPhong;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

    // --- FXML Fields ---
    @FXML private TextField txtMaPhong;
    @FXML private TextField txtTenPhong;
    @FXML private TextField txtGiaPhong;
    @FXML private TextField txtSoNguoi;     // Mới: Hiển thị số người
    @FXML private TextField txtTimKiem;     // Mới: Ô tìm kiếm
    @FXML private ComboBox<LoaiPhong> cbLoaiPhong;
    @FXML private ComboBox<TrangThaiPhong> cbTrangThai;

    @FXML private Button btnThem;
    @FXML private Button btnSua;
    @FXML private Button btnXoa;
    @FXML private Button btnLamMoi;

    // --- TableView: Sử dụng PhongDetail ---
    @FXML private TableView<PhongDetail> tvPhong;

    @FXML private TableColumn<PhongDetail, String> colMaPhong;
    @FXML private TableColumn<PhongDetail, String> colTenPhong;
    @FXML private TableColumn<PhongDetail, Integer> colSoNguoi; // Mới
    @FXML private TableColumn<PhongDetail, String> colLoaiPhong;
    @FXML private TableColumn<PhongDetail, BigDecimal> colGiaPhong;
    @FXML private TableColumn<PhongDetail, TrangThaiPhong> colTrangThai;

    // --- Data Fields ---
    private PhongDAO phongDAO;
    private LoaiPhongDAO loaiPhongDAO;
    private ObservableList<PhongDetail> danhSachPhong;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        phongDAO = new PhongDAO();
        loaiPhongDAO = new LoaiPhongDAO();

        // 1. Khởi tạo danh sách
        danhSachPhong = FXCollections.observableArrayList();

        // 2. Load dữ liệu gốc từ DB
        loadData();

        // --- [CHỨC NĂNG TÌM KIẾM] ---
        // Tạo FilteredList bao lấy danh sách gốc
        javafx.collections.transformation.FilteredList<PhongDetail> filteredData =
                new javafx.collections.transformation.FilteredList<>(danhSachPhong, p -> true);

        // Bắt sự kiện khi gõ vào ô tìm kiếm
        txtTimKiem.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(phong -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                // Tìm theo Mã, Tên phòng, hoặc Tên loại
                if (phong.getMaPhong().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (phong.getTenPhong().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (phong.getTenLoai().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });

        // Bao lại bằng SortedList để sắp xếp được
        javafx.collections.transformation.SortedList<PhongDetail> sortedData =
                new javafx.collections.transformation.SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tvPhong.comparatorProperty());

        // Gán dữ liệu đã lọc vào bảng
        tvPhong.setItems(sortedData);
        // --- [KẾT THÚC TÌM KIẾM] ---

        // Cấu hình TextField không cho sửa thủ công
        txtMaPhong.setEditable(false);
        txtSoNguoi.setEditable(false);

        // --- [AUTO FILL MÃ PHÒNG] ---
        txtTenPhong.textProperty().addListener((observable, oldValue, newValue) -> {
            // Chỉ tự động điền khi đang THÊM MỚI (không bị disable)
            if (!txtMaPhong.isDisabled()) {
                if (newValue != null) {
                    // Lấy số từ tên phòng (VD: "Phòng 101" -> "101")
                    String chiLaySo = newValue.replaceAll("[^0-9]", "");
                    txtMaPhong.setText("P" + chiLaySo);
                }
            }
        });

        // --- [AUTO FILL GIÁ & SỐ NGƯỜI KHI CHỌN LOẠI PHÒNG] ---
        cbLoaiPhong.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // Điền Giá
                if (newVal.getDonGia() != null) {
                    txtGiaPhong.setText(newVal.getDonGia().toPlainString());
                }
                // Điền Số người
                txtSoNguoi.setText(String.valueOf(newVal.getSoNguoiTD()));
            }
        });

        // 3. Setup UI ComboBox
        cbTrangThai.setItems(FXCollections.observableArrayList(TrangThaiPhong.values()));
        cbLoaiPhong.setItems(FXCollections.observableArrayList(loaiPhongDAO.getAll()));

        setupTableColumns();

        // 4. Sự kiện click bảng
        tvPhong.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                fillForm(newVal);
            }
        });

        // 5. Button actions
        btnThem.setOnAction(e -> themPhong());
        btnSua.setOnAction(e -> suaPhong());
        btnXoa.setOnAction(e -> xoaPhong());
        btnLamMoi.setOnAction(e -> lamMoiForm());
    }

    private void loadData() {
        // Lấy dữ liệu PhongDetail từ DAO
        danhSachPhong.setAll(phongDAO.getAll());
    }

    private void setupTableColumns() {
        colMaPhong.setCellValueFactory(cell -> cell.getValue().maPhongProperty());
        colTenPhong.setCellValueFactory(cell -> cell.getValue().tenPhongProperty());
        colSoNguoi.setCellValueFactory(cell -> cell.getValue().soNguoiTDProperty().asObject());
        colLoaiPhong.setCellValueFactory(cell -> cell.getValue().tenLoaiProperty());

        colGiaPhong.setCellValueFactory(cell -> cell.getValue().donGiaProperty());
        colGiaPhong.setCellFactory(column -> new TableCell<PhongDetail, BigDecimal>() {
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

    // --- CRUD Operations ---

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
        txtMaPhong.setDisable(true); // Không cho sửa mã
        txtTenPhong.setText(p.getTenPhong());
        txtSoNguoi.setText(String.valueOf(p.getSoNguoiTD()));
        cbTrangThai.setValue(p.getTrangThai());

        // Logic chọn lại ComboBox theo Mã Loại
        String maLoaiCanTim = p.getMaLoai();
        if (maLoaiCanTim != null) {
            for (LoaiPhong lp : cbLoaiPhong.getItems()) {
                if (lp.getMaLoai().equals(maLoaiCanTim)) {
                    cbLoaiPhong.setValue(lp);
                    break;
                }
            }
        }

        // Điền giá (lấy từ DB hoặc ghi đè nếu cần)
        if (p.getDonGia() != null) {
            txtGiaPhong.setText(p.getDonGia().toPlainString());
        }
    }

    private void lamMoiForm() {
        txtMaPhong.clear();
        txtMaPhong.setDisable(false); // Cho phép nhập lại mã
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
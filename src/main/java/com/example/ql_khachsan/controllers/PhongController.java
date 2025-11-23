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
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class PhongController implements Initializable {

    // --- FXML Fields ---
    // Đã XÓA txtMaPhong vì bạn không muốn hiển thị nó nữa
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

    // [MỚI] Cột STT
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

    // Biến tạm để lưu mã phòng đang được chọn (vì đã xóa ô text field mã phòng nên cần biến này để biết đang sửa ai)
    private String maPhongDangChon = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        phongDAO = new PhongDAO();
        loaiPhongDAO = new LoaiPhongDAO();
        danhSachPhong = FXCollections.observableArrayList();

        loadData();

        // --- SETUP TÌM KIẾM ---
        javafx.collections.transformation.FilteredList<PhongDetail> filteredData =
                new javafx.collections.transformation.FilteredList<>(danhSachPhong, p -> true);

        txtTimKiem.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(phong -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                if (phong.getMaPhong().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (phong.getTenPhong().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (phong.getTenLoai().toLowerCase().contains(lowerCaseFilter)) return true;
                return false;
            });
        });
        javafx.collections.transformation.SortedList<PhongDetail> sortedData =
                new javafx.collections.transformation.SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tvPhong.comparatorProperty());
        tvPhong.setItems(sortedData);

        // --- SETUP UI AUTO FILL ---
        txtSoNguoi.setEditable(false);
        txtGiaPhong.setEditable(false); // Giá tiền đi theo loại phòng nên cũng khóa luôn cho chuẩn

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

        // [QUAN TRỌNG] Khởi động form ở trạng thái "Thêm mới" -> Set mặc định UI
        lamMoiForm();
    }

    private void loadData() {
        danhSachPhong.setAll(phongDAO.getAll());
    }

    private void setupTableColumns() {
        // [LOGIC MỚI] Cột STT tự động tăng
        colSTT.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(tvPhong.getItems().indexOf(column.getValue()) + 1));
        // Để STT cập nhật lại khi sort hoặc filter, ta cần reset lại cell factory (hoặc đơn giản dùng cách này cho list nhỏ)
        colSTT.setSortable(false); // Không cần sort theo STT

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

    // --- CRUD Operations ---

    private void themPhong() {
        if (!validateInput()) return;

        try {
            Phong p = new Phong();

            // [LOGIC MỚI] Tự động sinh Mã Phòng từ Tên Phòng (Vừa nhập)
            // Vì đã xóa ô nhập mã, ta phải tạo mã ngầm ở đây
            String tenPhong = txtTenPhong.getText();
            String soPhong = tenPhong.replaceAll("[^0-9]", "");
            if (soPhong.isEmpty()) {
                thongBao("Lỗi", "Tên phòng phải chứa số để tạo mã (Ví dụ: Phòng 101)", Alert.AlertType.ERROR);
                return;
            }
            p.setMaPhong("P" + soPhong); // Tự động tạo P101
            p.setTenPhong(tenPhong);

            if (cbLoaiPhong.getValue() != null) {
                p.setMaLoai(cbLoaiPhong.getValue().getMaLoai());
            }

            // [LOGIC MỚI] Bắt buộc là TRONG khi thêm mới
            p.setTrangThai(TrangThaiPhong.TRONG);

            if (phongDAO.insert(p)) {
                thongBao("Thành công", "Thêm phòng " + p.getMaPhong() + " thành công!", Alert.AlertType.INFORMATION);
                loadData();
                lamMoiForm();
            } else {
                thongBao("Thất bại", "Mã phòng " + p.getMaPhong() + " đã tồn tại!", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            thongBao("Lỗi", "Có lỗi xảy ra: " + e.getMessage(), Alert.AlertType.ERROR);
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
            // Lấy mã từ biến tạm (vì không còn ô text field để lấy)
            p.setMaPhong(maPhongDangChon);
            p.setTenPhong(txtTenPhong.getText());

            if (cbLoaiPhong.getValue() != null) {
                p.setMaLoai(cbLoaiPhong.getValue().getMaLoai());
            }
            p.setTrangThai(cbTrangThai.getValue());

            if (phongDAO.update(p)) {
                thongBao("Thành công", "Cập nhật thành công!", Alert.AlertType.INFORMATION);
                loadData();
                lamMoiForm(); // Reset lại form sau khi sửa
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

        // Logic chặn xóa nếu phòng đang bận (giữ nguyên logic cũ của bạn)
        PhongDetail selected = tvPhong.getSelectionModel().getSelectedItem();
        if (selected.getTrangThai() == TrangThaiPhong.DA_DAT || selected.getTrangThai() == TrangThaiPhong.DANG_SU_DUNG) {
            thongBao("Cảnh báo", "Không thể xóa phòng đang có khách hoặc đã đặt!", Alert.AlertType.WARNING);
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
        // Lưu mã phòng đang chọn vào biến tạm
        maPhongDangChon = p.getMaPhong();

        txtTenPhong.setText(p.getTenPhong());
        txtSoNguoi.setText(String.valueOf(p.getSoNguoiTD()));

        // [LOGIC MỚI] Khi SỬA thì CHO PHÉP chỉnh trạng thái
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
        // Reset biến tạm
        maPhongDangChon = null;

        txtTenPhong.clear();
        txtGiaPhong.clear();
        txtSoNguoi.clear();
        cbLoaiPhong.setValue(null);
        tvPhong.getSelectionModel().clearSelection();

        // [LOGIC MỚI] UX CHO THÊM MỚI
        // 1. Tự động set Trạng thái là TRỐNG
        cbTrangThai.setValue(TrangThaiPhong.TRONG);
        // 2. KHÓA ComboBox lại (người dùng nhìn thấy là TRỐNG nhưng không đổi được)
        cbTrangThai.setDisable(true);
    }

    private boolean validateInput() {
        // Không check txtMaPhong nữa vì đã xóa
        // Không check cbTrangThai vì đã set mặc định
        return !txtTenPhong.getText().isEmpty() && cbLoaiPhong.getValue() != null;
    }

    private void thongBao(String title, String content, Alert.AlertType type) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }
}
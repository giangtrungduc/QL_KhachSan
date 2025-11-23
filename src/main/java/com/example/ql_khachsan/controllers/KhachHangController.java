package com.example.ql_khachsan.controllers;

import com.example.ql_khachsan.dao.KhachHangDAO;
import com.example.ql_khachsan.models.KhachHang;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class KhachHangController {

    // ========== TABLE ==========
    @FXML private TableView<KhachHang> tblKhachHang;
    @FXML private TableColumn<KhachHang, String> colMaKH;
    @FXML private TableColumn<KhachHang, String> colHoTen;
    @FXML private TableColumn<KhachHang, String> colCCCD;
    @FXML private TableColumn<KhachHang, String> colSDT;
    @FXML private TableColumn<KhachHang, String> colEmail;
    @FXML private TableColumn<KhachHang, String> colTaiKhoan;

    // ========== FORM ==========
    @FXML private TextField txtMaKH;
    @FXML private TextField txtHoTen;
    @FXML private TextField txtCCCD;
    @FXML private TextField txtSDT;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTaiKhoan;

    // --- MẬT KHẨU + NÚT MẮT ---
    @FXML private PasswordField txtMatKhau;          // ô mật khẩu ẩn
    @FXML private TextField txtMatKhauVisible;       // ô mật khẩu hiện
    @FXML private Button btnTogglePassword;          // nút 👁
    private boolean passwordVisible = false;         // >>> NEW

    // ========== SEARCH ==========
    @FXML private TextField txtSearch;

    private final KhachHangDAO khachHangDAO = new KhachHangDAO();
    private final ObservableList<KhachHang> khachHangList = FXCollections.observableArrayList();

    // ================= INIT =================
    @FXML
    public void initialize() {
        colMaKH.setCellValueFactory(cell -> cell.getValue().maKHProperty());
        colHoTen.setCellValueFactory(cell -> cell.getValue().hoTenProperty());
        colCCCD.setCellValueFactory(cell -> cell.getValue().cccdProperty());
        colSDT.setCellValueFactory(cell -> cell.getValue().sdtProperty());
        colEmail.setCellValueFactory(cell -> cell.getValue().emailProperty());
        colTaiKhoan.setCellValueFactory(cell -> cell.getValue().taiKhoanProperty());

        setupPasswordField();          // >>> NEW
        loadData();

        tblKhachHang.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> showKhachHangDetails(newSel)
        );
    }

    // >>> NEW – cấu hình trạng thái ban đầu của 2 ô mật khẩu
    private void setupPasswordField() {
        passwordVisible = false;
        txtMatKhauVisible.setVisible(false);
        txtMatKhauVisible.setManaged(false);

        txtMatKhau.setVisible(true);
        txtMatKhau.setManaged(true);

        if (btnTogglePassword != null) {
            btnTogglePassword.setText("👁");
        }
    }

    // ================= DATA =================
    private void loadData() {
        List<KhachHang> list = khachHangDAO.getAll();
        khachHangList.setAll(list);
        tblKhachHang.setItems(khachHangList);
    }

    private void showKhachHangDetails(KhachHang kh) {
        if (kh == null) {
            clearForm();
            return;
        }
        txtMaKH.setText(kh.getMaKH());
        txtHoTen.setText(kh.getHoTen());
        txtCCCD.setText(kh.getCccd());
        txtSDT.setText(kh.getSdt());
        txtEmail.setText(kh.getEmail());
        txtTaiKhoan.setText(kh.getTaiKhoan());

        // >>> NEW – đồng bộ 2 ô mật khẩu
        txtMatKhau.setText(kh.getMatKhau());
        txtMatKhauVisible.setText(kh.getMatKhau());
    }

    private void clearForm() {
        txtMaKH.clear();
        txtHoTen.clear();
        txtCCCD.clear();
        txtSDT.clear();
        txtEmail.clear();
        txtTaiKhoan.clear();
        txtMatKhau.clear();
        txtMatKhauVisible.clear();

        tblKhachHang.getSelectionModel().clearSelection();
        setupPasswordField();          // >>> NEW
    }

    // Helper lấy mật khẩu hiện tại (dù đang ẩn hay hiện)
    // >>> NEW
    private String getCurrentPassword() {
        return passwordVisible
                ? txtMatKhauVisible.getText().trim()
                : txtMatKhau.getText().trim();
    }

    // ================== BUTTONS ==================

    @FXML
    private void handleAdd() {
        if (!validateInput(true)) return;

        KhachHang kh = new KhachHang(
                txtMaKH.getText().trim(),
                txtHoTen.getText().trim(),
                txtCCCD.getText().trim(),
                txtSDT.getText().trim(),
                txtEmail.getText().trim(),
                txtTaiKhoan.getText().trim(),
                getCurrentPassword()                  // >>> NEW
        );

        try {
            boolean ok = khachHangDAO.insert(kh);
            if (ok) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", null, "Đã thêm khách hàng mới.");
                loadData();
                clearForm();
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", null, "Không thể thêm khách hàng.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi SQL", null, e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        KhachHang selected = tblKhachHang.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Chú ý", null, "Vui lòng chọn một khách hàng để cập nhật.");
            return;
        }
        if (!validateInput(false)) return;

        selected.setHoTen(txtHoTen.getText().trim());
        selected.setCccd(txtCCCD.getText().trim());
        selected.setSdt(txtSDT.getText().trim());
        selected.setEmail(txtEmail.getText().trim());
        selected.setTaiKhoan(txtTaiKhoan.getText().trim());
        selected.setMatKhau(getCurrentPassword());   // >>> NEW

        boolean ok = khachHangDAO.update(selected);
        if (ok) {
            showAlert(Alert.AlertType.INFORMATION, "Thành công", null, "Đã cập nhật khách hàng.");
            loadData();
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", null, "Không thể cập nhật khách hàng.");
        }
    }

    @FXML
    private void handleDelete() {
        KhachHang selected = tblKhachHang.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Chú ý", null, "Vui lòng chọn khách hàng để xóa.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText("Bạn có chắc muốn xóa khách hàng này?");
        confirm.setContentText("Mã KH: " + selected.getMaKH() + "\nHọ tên: " + selected.getHoTen());

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean ok = khachHangDAO.delete(selected.getMaKH());
            if (ok) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", null, "Đã xóa khách hàng.");
                loadData();
                clearForm();
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", null, "Không thể xóa khách hàng.");
            }
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            tblKhachHang.setItems(khachHangList);
            return;
        }

        List<KhachHang> filtered = khachHangList.stream()
                .filter(k ->
                        (k.getMaKH() != null && k.getMaKH().toLowerCase().contains(keyword)) ||
                                (k.getHoTen() != null && k.getHoTen().toLowerCase().contains(keyword)) ||
                                (k.getCccd() != null && k.getCccd().toLowerCase().contains(keyword)) ||
                                (k.getSdt() != null && k.getSdt().toLowerCase().contains(keyword)) ||
                                (k.getEmail() != null && k.getEmail().toLowerCase().contains(keyword))
                )
                .collect(Collectors.toList());

        tblKhachHang.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void handleRefresh() {
        txtSearch.clear();
        loadData();
    }

    @FXML
    private void handleClearForm() {
        clearForm();
    }

    // ================== NÚT MẮT ==================
    @FXML
    private void togglePassword() {
        if (passwordVisible) {
            // đang hiện -> ẩn
            txtMatKhauVisible.setVisible(false);
            txtMatKhauVisible.setManaged(false);

            txtMatKhau.setText(txtMatKhauVisible.getText());
            txtMatKhau.setVisible(true);
            txtMatKhau.setManaged(true);

            btnTogglePassword.setText("👁");
            passwordVisible = false;
        } else {
            // đang ẩn -> hiện
            txtMatKhauVisible.setText(txtMatKhau.getText());
            txtMatKhauVisible.setVisible(true);
            txtMatKhauVisible.setManaged(true);

            txtMatKhau.setVisible(false);
            txtMatKhau.setManaged(false);

            btnTogglePassword.setText("🙈");
            passwordVisible = true;
        }
    }

    // ================== VALIDATE + ALERT ==================
    private boolean validateInput(boolean isAdd) {
        StringBuilder err = new StringBuilder();
        if (isAdd && txtMaKH.getText().trim().isEmpty())
            err.append("- Mã khách hàng không được để trống.\n");
        if (txtHoTen.getText().trim().isEmpty())
            err.append("- Họ tên không được để trống.\n");
        if (txtCCCD.getText().trim().isEmpty())
            err.append("- CCCD không được để trống.\n");
        if (txtSDT.getText().trim().isEmpty())
            err.append("- SĐT không được để trống.\n");
        if (txtEmail.getText().trim().isEmpty())
            err.append("- Email không được để trống.\n");
        if (txtTaiKhoan.getText().trim().isEmpty())
            err.append("- Tài khoản không được để trống.\n");
        if (getCurrentPassword().isEmpty())
            err.append("- Mật khẩu không được để trống.\n");

        if (!err.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng kiểm tra:", err.toString());
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        if (content != null && !content.isEmpty()) alert.setContentText(content);
        alert.showAndWait();
    }
}

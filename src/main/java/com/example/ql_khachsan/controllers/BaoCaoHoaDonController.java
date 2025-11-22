package com.example.ql_khachsan.controllers;

import com.example.ql_khachsan.models.HoaDon;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.*;
import java.time.Duration;
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
    @FXML private TableColumn<HoaDon, Void> colChiTiet;

    @FXML private TextField txtMaKH;
    @FXML private DatePicker dpFrom;
    @FXML private DatePicker dpTo;

    private final ObservableList<HoaDon> data = FXCollections.observableArrayList();

    // Thay đổi thông tin kết nối nếu cần
    private final String url = "jdbc:mysql://localhost:3306/qlkhachsan?useSSL=false&serverTimezone=UTC";
    private final String user = "root";
    private final String password = "123456";

    private final DateTimeFormatter dtfDB = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final DateTimeFormatter dtfView = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        // Cập nhật cell value factory
        colSTT.setCellValueFactory(c -> c.getValue().sttProperty().asObject());
        colMaHD.setCellValueFactory(c -> c.getValue().maHDProperty());
        colMaDP.setCellValueFactory(c -> c.getValue().maDPProperty());
        colGhiChu.setCellValueFactory(c -> c.getValue().ghiChuProperty());

        // Định dạng cột Tổng tiền
        colTongTien.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.format("%,.2f VNĐ", c.getValue().getTongTien())));

        // Định dạng cột Ngày lập
        colNgayLap.setCellValueFactory(c -> {
            LocalDateTime dt = c.getValue().getNgayLap();
            return new ReadOnlyStringWrapper(dt != null ? dt.format(dtfView) : "");
        });

        addButtonToTable();
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
        // Không thay đổi phần này vì đây là TODO
        System.out.println("Xuất báo cáo...");
    }

    private void loadData(String maKH, LocalDate from, LocalDate to) {
        data.clear();
        // Cập nhật SQL để lấy tất cả thông tin cần thiết cho cả báo cáo và chi tiết
        String sql = "SELECT HD.MaHD, HD.NgayLap, HD.MaDP, HD.GhiChu, " +
                "DP.NgayNhan, DP.NgayTra, KH.HoTen, KH.SDT, KH.Email, P.TenPhong, LP.DonGia " +
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
            // Dùng String để tránh lỗi định dạng Timestamp
            if (from != null) stmt.setString(idx++, from.atStartOfDay().format(dtfDB));
            if (to != null) stmt.setString(idx++, to.atTime(23,59,59).format(dtfDB));

            ResultSet rs = stmt.executeQuery();
            int sttCounter = 1;
            while (rs.next()) {
                LocalDateTime ngayLap = rs.getTimestamp("NgayLap").toLocalDateTime();
                LocalDateTime ngayNhan = rs.getTimestamp("NgayNhan").toLocalDateTime();
                LocalDateTime ngayTra = rs.getTimestamp("NgayTra").toLocalDateTime();
                double donGiaMotDem = rs.getDouble("DonGia");

                // Tính số đêm (chứ không phải số ngày)
                // Lưu ý: Tính số đêm là NgayTra - NgayNhan. Ta sẽ làm tròn lên hoặc xuống tùy quy tắc khách sạn.
                // Ở đây, ta dùng Duration và làm tròn lên thành số ngày nếu có sự chênh lệch giờ
                long soGio = Duration.between(ngayNhan, ngayTra).toHours();
                long soDem = (soGio + 23) / 24; // Làm tròn lên số ngày/đêm
                if (soDem == 0 && soGio > 0) soDem = 1; // Tối thiểu 1 đêm nếu có ở

                double tongTien = soDem * donGiaMotDem; // Đây là tiền phòng, chưa bao gồm dịch vụ

                // Tạo đối tượng HoaDon, lưu trữ cả thông tin chi tiết (SDT, Email, TenPhong...)
                // để truyền sang controller chi tiết mà không cần truy vấn lại CSDL
                HoaDon hd = new HoaDon(
                        sttCounter++,
                        rs.getString("MaHD"),
                        rs.getString("MaDP"),
                        rs.getString("GhiChu"),
                        ngayLap,
                        tongTien
                );
                // Lưu thông tin chi tiết vào đối tượng (giả định class HoaDon có setter cho các trường này)
                hd.setTenKhachHang(rs.getString("HoTen"));
                hd.setSdt(rs.getString("SDT"));
                hd.setEmail(rs.getString("Email"));
                hd.setTenPhong(rs.getString("TenPhong"));
                hd.setNgayNhan(ngayNhan);
                hd.setNgayTra(ngayTra);

                data.add(hd);
            }

            tblHoaDon.setItems(data);

        } catch (SQLException e) {
            // Hiển thị lỗi ra console và Alert
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Lỗi khi lấy dữ liệu từ database: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void addButtonToTable() {
        Callback<TableColumn<HoaDon, Void>, TableCell<HoaDon, Void>> cellFactory = param -> new TableCell<>() {

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
        colChiTiet.setCellFactory(cellFactory);
    }

    private void showChiTiet(HoaDon hd) {
        try {
            // Đảm bảo đường dẫn FXML là chính xác theo cấu trúc dự án
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ql_khachsan/views/HoaDon.fxml"));
            Parent root = loader.load();

            HoaDonController controller = loader.getController();
            // Truyền toàn bộ đối tượng HoaDon (đã có thông tin chi tiết)
            controller.setData(hd);

            Stage stage = new Stage();
            stage.setTitle("Chi tiết hóa đơn: " + hd.getMaHD());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Không thể mở chi tiết hóa đơn: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
package com.example.ql_khachsan.untils;

public enum TrangThaiPhong {
    TRONG("Trống"),
    DA_DAT("Đã Đặt"),
    DANG_SU_DUNG("Đang Sử Dụng");

    private final String value;

    TrangThaiPhong(String value) {
        this.value = value;
    }

    // Hàm này để DAO lấy giá trị lưu xuống DB
    public String getDbValue() {
        return value;
    }

    // Hàm này để ComboBox/TableView hiển thị
    @Override
    public String toString() {
        return value;
    }

    // Hàm này để convert từ DB (Tiếng Việt) ngược lại thành Enum
    public static TrangThaiPhong fromDbValue(String value) {
        for (TrangThaiPhong item : values()) {
            // So sánh không phân biệt hoa thường
            if (item.value.equalsIgnoreCase(value)) {
                return item;
            }
        }
        // Nếu DB lưu sai hoặc null, trả về mặc định là TRONG
        return TRONG;
    }
}


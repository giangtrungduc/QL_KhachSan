package com.example.ql_khachsan.untils;

public enum TrangThaiPhong {
    TRONG("Trống"),
    DA_DAT("Đã đặt"),
    DANG_SU_DUNG("Đang sử dụng");

    private final String value;

    TrangThaiPhong(String value) {
        this.value = value;
    }

    public String getDbValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static TrangThaiPhong fromDbValue(String value) {
        for (TrangThaiPhong item : values()) {
            if (item.value.equalsIgnoreCase(value)) {
                return item;
            }
        }
        return TRONG;
    }
}
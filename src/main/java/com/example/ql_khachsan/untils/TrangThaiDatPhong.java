package com.example.ql_khachsan.untils;

public enum TrangThaiDatPhong {
    DA_DAT("Đã đặt"),
    DANG_SU_DUNG("Đang sử dụng"),
    DANG_TRONG("Đang trống"),
    DA_HUY("Đã hủy");

    private final String dbValue;

    TrangThaiDatPhong(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static TrangThaiDatPhong fromDbValue(String dbValue) {
        for (TrangThaiDatPhong t : TrangThaiDatPhong.values()) {
            if (t.dbValue.equalsIgnoreCase(dbValue)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Trạng thái đặt phòng không hợp lệ: " + dbValue);
    }
}
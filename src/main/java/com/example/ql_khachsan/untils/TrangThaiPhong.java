package com.example.ql_khachsan.untils;

public enum TrangThaiPhong {
    AVAILABLE("Trống"),
    BOOKED("Đã đặt"),
    OCCUPIED("Đang sử dụng");

    private final String dbValue;

    TrangThaiPhong(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static TrangThaiPhong fromDbValue(String dbValue) {
        for (TrangThaiPhong t : TrangThaiPhong.values()) {
            if (t.dbValue.equals(dbValue)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Lỗi: " + dbValue);
    }
}

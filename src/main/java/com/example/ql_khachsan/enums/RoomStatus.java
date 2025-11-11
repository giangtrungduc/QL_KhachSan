package com.example.ql_khachsan.enums;

// Enum đại diện cho trạng thái của Phòng (bảng PHONG)
public enum RoomStatus {
    AVAILABLE("Trống"),             // Phù hợp với 'Trống' trong DB
    BOOKED("Đã đặt"),               // Phù hợp với 'Đã đặt' trong DB
    OCCUPIED("Đang sử dụng");       // Phù hợp với 'Đang sử dụng' trong DB


    private final String dbValue;

    // Constructor để gán giá trị chuỗi DB
    RoomStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    // Phương thức để lấy giá trị chuỗi khi tương tác với Database
    public String getDbValue() {
        return dbValue;
    }

    // Phương thức tĩnh để chuyển từ giá trị DB sang Enum object
    public static RoomStatus fromDbValue(String dbValue) {
        for (RoomStatus status : RoomStatus.values()) {
            if (status.dbValue.equalsIgnoreCase(dbValue)) {
                return status;
            }
        }
        // Xử lý lỗi nếu giá trị DB không hợp lệ
        throw new IllegalArgumentException("Trạng thái phòng không hợp lệ: " + dbValue);
    }
}
-- =============================================
-- 1. KHỞI TẠO DATABASE
-- =============================================
DROP DATABASE IF EXISTS qlkhachsan;
CREATE DATABASE qlkhachsan CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE qlkhachsan;

-- =============================================
-- 2. TẠO BẢNG
-- =============================================

-- Bảng KHACHHANG
CREATE TABLE KHACHHANG (
                           MaKH VARCHAR(50) PRIMARY KEY,
                           HoTen VARCHAR(100) NOT NULL,
                           CCCD VARCHAR(20) NOT NULL UNIQUE,
                           SDT VARCHAR(15) NOT NULL UNIQUE,
                           Email VARCHAR(100) NOT NULL UNIQUE,
                           TaiKhoan VARCHAR(50) NOT NULL UNIQUE,
                           MatKhau VARCHAR(255) NOT NULL
);

-- Bảng LOAIPHONG
CREATE TABLE LOAIPHONG (
                           MaLoai VARCHAR(50) PRIMARY KEY,
                           TenLoai VARCHAR(100) NOT NULL,
                           SoNguoiTD INT NOT NULL,
                           DonGia DECIMAL(15, 2) NOT NULL
);

-- Bảng PHONG
CREATE TABLE PHONG (
                       MaPhong VARCHAR(50) PRIMARY KEY,
                       TenPhong VARCHAR(100) NOT NULL,
                       TrangThai VARCHAR(50) NOT NULL DEFAULT 'Trống',
                       MaLoai VARCHAR(50) NOT NULL,
                       FOREIGN KEY (MaLoai) REFERENCES LOAIPHONG(MaLoai) ON UPDATE CASCADE
);

-- Bảng PHIEUDATPHONG
CREATE TABLE PHIEUDATPHONG (
                               MaDP VARCHAR(50) PRIMARY KEY,
                               NgayDat DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               NgayNhan DATETIME NOT NULL,
                               NgayTra DATETIME NOT NULL,
                               DonGiaThucTe DECIMAL(15, 2) NOT NULL,
                               TrangThaiDP VARCHAR(50) NOT NULL, -- 'Đã đặt', 'Đang sử dụng', 'Đã hủy', 'Hoàn thành'
                               MaKH VARCHAR(50) NOT NULL,
                               MaPhong VARCHAR(50) NOT NULL,
                               FOREIGN KEY (MaKH) REFERENCES KHACHHANG(MaKH) ON UPDATE CASCADE,
                               FOREIGN KEY (MaPhong) REFERENCES PHONG(MaPhong) ON UPDATE CASCADE,
                               CONSTRAINT CK_Ngay CHECK (NgayTra > NgayNhan)
);

-- Bảng HOADON
CREATE TABLE HOADON (
                        MaHD VARCHAR(50) PRIMARY KEY,
                        GhiChu VARCHAR(255) NULL,
                        NgayLap DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        MaDP VARCHAR(50) NOT NULL UNIQUE,
                        FOREIGN KEY (MaDP) REFERENCES PHIEUDATPHONG(MaDP) ON UPDATE CASCADE
);

-- =============================================
-- 3. TRIGGERS (Giữ nguyên logic của bạn)
-- =============================================
DELIMITER $$

-- Trigger 1: Cập nhật trạng thái phòng khi INSERT phiếu đặt
CREATE TRIGGER trg_after_booking_insert
    AFTER INSERT ON PHIEUDATPHONG
    FOR EACH ROW
BEGIN
    DECLARE new_room_status VARCHAR(50);
    IF NEW.TrangThaiDP = 'Đã đặt' THEN SET new_room_status = 'Đã đặt';
    ELSEIF NEW.TrangThaiDP = 'Đang sử dụng' THEN SET new_room_status = 'Đang sử dụng';
    ELSEIF NEW.TrangThaiDP = 'Đã hủy' OR NEW.TrangThaiDP = 'Hoàn thành' THEN SET new_room_status = 'Trống';
END IF;

IF new_room_status IS NOT NULL THEN
UPDATE PHONG SET TrangThai = new_room_status WHERE MaPhong = NEW.MaPhong;
END IF;
END$$

-- Trigger 2: Cập nhật trạng thái phòng khi UPDATE phiếu đặt
CREATE TRIGGER trg_after_booking_update
    AFTER UPDATE ON PHIEUDATPHONG
    FOR EACH ROW
BEGIN
    DECLARE new_room_status VARCHAR(50);
    IF NEW.TrangThaiDP = 'Đã đặt' THEN SET new_room_status = 'Đã đặt';
    ELSEIF NEW.TrangThaiDP = 'Đang sử dụng' THEN SET new_room_status = 'Đang sử dụng';
    ELSEIF NEW.TrangThaiDP = 'Đã hủy' OR NEW.TrangThaiDP = 'Hoàn thành' THEN SET new_room_status = 'Trống';
END IF;

IF new_room_status IS NOT NULL THEN
UPDATE PHONG SET TrangThai = new_room_status WHERE MaPhong = NEW.MaPhong;
END IF;
END$$

-- Trigger 3: Tự động hoàn thành phiếu đặt khi tạo Hóa đơn
CREATE TRIGGER trg_after_payment_checkout
    AFTER INSERT ON HOADON
    FOR EACH ROW
BEGIN
    UPDATE PHIEUDATPHONG SET TrangThaiDP = 'Hoàn thành' WHERE MaDP = NEW.MaDP;
    END$$

    -- Trigger 4: Chống trùng lịch
    CREATE TRIGGER trg_before_booking_conflict
        BEFORE INSERT ON PHIEUDATPHONG
        FOR EACH ROW
    BEGIN
        DECLARE conflict_count INT;
        SELECT COUNT(*) INTO conflict_count
        FROM PHIEUDATPHONG
        WHERE MaPhong = NEW.MaPhong
          AND TrangThaiDP IN ('Đã đặt', 'Đang sử dụng')
          AND (NEW.NgayNhan < NgayTra) AND (NEW.NgayTra > NgayNhan);

        IF conflict_count > 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Lỗi: Phòng này đã bị trùng lịch!';
    END IF;
    END$$

    DELIMITER ;

-- =============================================
-- 4. DỮ LIỆU MẪU (DATA SEEDING)
-- =============================================

-- 4.1 Thêm Loại Phòng
    INSERT INTO LOAIPHONG (MaLoai, TenLoai, SoNguoiTD, DonGia) VALUES
                                                                   ('L01', 'Standard (Tiêu chuẩn)', 2, 500000),
                                                                   ('L02', 'Deluxe (Sang trọng)', 2, 800000),
                                                                   ('L03', 'Family (Gia đình)', 4, 1200000),
                                                                   ('L04', 'VIP Suite', 2, 2000000);

-- 4.2 Thêm Phòng
    INSERT INTO PHONG (MaPhong, TenPhong, TrangThai, MaLoai) VALUES
                                                                 ('P101', 'Phòng 101', 'Trống', 'L01'),
                                                                 ('P102', 'Phòng 102', 'Trống', 'L01'),
                                                                 ('P201', 'Phòng 201', 'Trống', 'L02'),
                                                                 ('P202', 'Phòng 202', 'Trống', 'L02'),
                                                                 ('P301', 'Phòng 301', 'Trống', 'L03'),
                                                                 ('P401', 'Phòng VIP 401', 'Trống', 'L04');

-- 4.3 Thêm Khách Hàng
    INSERT INTO KHACHHANG (MaKH, HoTen, CCCD, SDT, Email, TaiKhoan, MatKhau) VALUES
                                                                                 ('KH01', 'Nguyễn Văn An', '001234567890', '0901234567', 'an.nguyen@email.com', 'user1', '123'),
                                                                                 ('KH02', 'Trần Thị Bích', '001234567891', '0901234568', 'bich.tran@email.com', 'user2', '123'),
                                                                                 ('KH03', 'Lê Hoàng Nam', '001234567892', '0901234569', 'nam.le@email.com', 'user3', '123');

    -- 4.4 Thêm Phiếu Đặt Phòng (Chú ý: Trigger sẽ tự cập nhật trạng thái phòng)

-- KH01 đặt phòng P101 (Đang sử dụng)
    INSERT INTO PHIEUDATPHONG (MaDP, NgayDat, NgayNhan, NgayTra, DonGiaThucTe, TrangThaiDP, MaKH, MaPhong) VALUES
        ('DP01', NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 2 DAY), 500000, 'Đang sử dụng', 'KH01', 'P101');

-- KH02 đặt phòng P201 (Đã đặt trước cho tuần sau)
    INSERT INTO PHIEUDATPHONG (MaDP, NgayDat, NgayNhan, NgayTra, DonGiaThucTe, TrangThaiDP, MaKH, MaPhong) VALUES
        ('DP02', NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), DATE_ADD(NOW(), INTERVAL 9 DAY), 800000, 'Đã đặt', 'KH02', 'P201');

-- KH03 đặt phòng P102 (Đã check-out/Hoàn thành trước đó)
    INSERT INTO PHIEUDATPHONG (MaDP, NgayDat, NgayNhan, NgayTra, DonGiaThucTe, TrangThaiDP, MaKH, MaPhong) VALUES
        ('DP03', '2023-10-01 08:00:00', '2023-10-01 14:00:00', '2023-10-03 12:00:00', 500000, 'Đã đặt', 'KH03', 'P102');
    -- Lưu ý: DP03 được insert trạng thái 'Đã đặt' trước để demo trigger thanh toán bên dưới

-- 4.5 Thêm Hóa Đơn (Thanh toán cho DP03)
-- Trigger `trg_after_payment_checkout` sẽ tự động chuyển DP03 sang 'Hoàn thành' và P102 sang 'Trống'
    INSERT INTO HOADON (MaHD, GhiChu, NgayLap, MaDP) VALUES
        ('HD01', 'Thanh toán tiền phòng và dịch vụ nước uống', '2023-10-03 12:05:00', 'DP03');
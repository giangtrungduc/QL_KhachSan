-- Tạo CSDL với bảng mã UTF-8 để hỗ trợ tiếng Việt
CREATE DATABASE qlkhachsan CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE qlkhachsan;

-- Bảng KHACHHANG
CREATE TABLE IF NOT EXISTS KHACHHANG (
    MaKH VARCHAR(50) PRIMARY KEY,
    HoTen VARCHAR(100) NOT NULL,
    CCCD VARCHAR(12) NOT NULL UNIQUE,
    SDT VARCHAR(10) NOT NULL UNIQUE,
    Email VARCHAR(100) NOT NULL UNIQUE,
    TaiKhoan VARCHAR(50) NOT NULL UNIQUE,
    MatKhau VARCHAR(255) NOT NULL
    );

-- Bảng LOAIPHONG
CREATE TABLE IF NOT EXISTS LOAIPHONG (
    MaLoai VARCHAR(50) PRIMARY KEY,
    TenLoai VARCHAR(100) NOT NULL,
    SoNguoiTD INT NOT NULL,
    DonGia DECIMAL(10, 2) NOT NULL
    );

-- Bảng PHONG
CREATE TABLE IF NOT EXISTS PHONG (
    MaPhong VARCHAR(50) PRIMARY KEY,
    TenPhong VARCHAR(100) NOT NULL,
    TrangThai VARCHAR(50) NOT NULL,
    MaLoai VARCHAR(50) NOT NULL,

    FOREIGN KEY (MaLoai) REFERENCES LOAIPHONG(MaLoai)
    );

-- Bảng PHIEUDATPHONG
CREATE TABLE IF NOT EXISTS PHIEUDATPHONG (
    MaDP VARCHAR(50) PRIMARY KEY,
    NgayDat DATETIME NOT NULL,      -- Giữ DATETIME để biết thời điểm đặt
    NgayNhan DATETIME NOT NULL,         -- Đổi sang DATE
    NgayTra DATETIME NOT NULL,          -- Đổi sang DATE
    DonGiaThucTe DECIMAL(10, 2) NOT NULL,
    TrangThaiDP VARCHAR(50) NOT NULL,
    MaKH VARCHAR(50) NOT NULL,
    MaPhong VARCHAR(50) NOT NULL,

    -- Một Phiếu đặt THUỘC về một Khách hàng
    FOREIGN KEY (MaKH) REFERENCES KHACHHANG(MaKH),
    -- Một Phiếu đặt đặt một Phòng
    FOREIGN KEY (MaPhong) REFERENCES PHONG(MaPhong)
    );

-- Bảng HOADON
CREATE TABLE IF NOT EXISTS HOADON (
    MaHD VARCHAR(50) PRIMARY KEY,
    GhiChu VARCHAR(255) NULL,
    NgayLap DATETIME NOT NULL,
    MaDP VARCHAR(50) NOT NULL UNIQUE,

    -- Logic này đã đúng: Một Hóa đơn CÓ một Phiếu đặt
    FOREIGN KEY (MaDP) REFERENCES PHIEUDATPHONG(MaDP)
    );


-- trigger hỗ trợ

-- Trigger 1: Xử lý khi THÊM MỚI phiếu đặt
DELIMITER $$
CREATE TRIGGER trg_after_booking_insert
    AFTER INSERT ON PHIEUDATPHONG
    FOR EACH ROW
BEGIN
    DECLARE new_room_status VARCHAR(50);

    -- Quyết định trạng thái phòng mới dựa trên trạng thái phiếu đặt
    IF NEW.TrangThaiDP = 'Đã đặt' THEN
        SET new_room_status = 'Đã đặt';
    ELSEIF NEW.TrangThaiDP = 'Đang sử dụng' THEN
        SET new_room_status = 'Đang sử dụng';
    ELSEIF NEW.TrangThaiDP = 'Đã hủy' OR NEW.TrangThaiDP = 'Hoàn thành' THEN
        SET new_room_status = 'Trống';
END IF;

-- Cập nhật bảng PHONG
IF new_room_status IS NOT NULL THEN
UPDATE PHONG
SET TrangThai = new_room_status
WHERE MaPhong = NEW.MaPhong;
END IF;
END$$
DELIMITER ;

-- Trigger 2: Xử lý khi CẬP NHẬT phiếu đặt (Check-in, Hủy)
DELIMITER $$
CREATE TRIGGER trg_after_booking_update
    AFTER UPDATE ON PHIEUDATPHONG
    FOR EACH ROW
BEGIN
    DECLARE new_room_status VARCHAR(50);

    -- Quyết định trạng thái phòng mới dựa trên trạng thái phiếu đặt
    IF NEW.TrangThaiDP = 'Đã đặt' THEN
        SET new_room_status = 'Đã đặt';
    ELSEIF NEW.TrangThaiDP = 'Đang sử dụng' THEN
        SET new_room_status = 'Đang sử dụng';
    -- Nếu phiếu hoàn thành hoặc bị hủy
    ELSEIF NEW.TrangThaiDP = 'Đã hủy' OR NEW.TrangThaiDP = 'Hoàn thành' THEN
        SET new_room_status = 'Trống';
END IF;

-- Cập nhật bảng PHONG
IF new_room_status IS NOT NULL THEN
UPDATE PHONG
SET TrangThai = new_room_status
WHERE MaPhong = NEW.MaPhong;
END IF;
END$$
DELIMITER ;

-- Trigger 3: Tự động Check-out khi thanh toán
DELIMITER $$
CREATE TRIGGER trg_after_payment_checkout
    AFTER INSERT ON HOADON
    FOR EACH ROW
BEGIN
    DECLARE v_MaPhong VARCHAR(50);

    -- 1. Lấy mã phòng từ phiếu đặt tương ứng
    SELECT MaPhong INTO v_MaPhong
    FROM PHIEUDATPHONG
    WHERE MaDP = NEW.MaDP;

    -- 2. Cập nhật phiếu đặt thành 'Hoàn thành'
    UPDATE PHIEUDATPHONG
    SET TrangThaiDP = 'Hoàn thành'
    WHERE MaDP = NEW.MaDP;

    -- 3. Cập nhật phòng thành 'Trống'
    UPDATE PHONG
    SET TrangThai = 'Trống'
    WHERE MaPhong = v_MaPhong;
    END$$
    DELIMITER ;

-- Trigger 4: Chống trùng lịch đặt phòng
DELIMITER $$
    CREATE TRIGGER trg_before_booking_conflict
        BEFORE INSERT ON PHIEUDATPHONG
        FOR EACH ROW
    BEGIN
        DECLARE conflict_count INT;

    -- Đếm số phiếu đặt KHÁC (trừ phiếu 'Đã hủy' và 'Hoàn thành') mà bị TRÙNG PHÒNG và TRÙNG THỜI GIAN
        SELECT COUNT(*) INTO conflict_count
        FROM PHIEUDATPHONG
        WHERE MaPhong = NEW.MaPhong
          AND (TrangThaiDP = 'Đã đặt' OR TrangThaiDP = 'Đang sử dụng')
          -- Công thức kiểm tra 2 khoảng thời gian giao nhau: (Start1 < End2) AND (Start2 < End1)
          AND (NEW.NgayNhan < NgayTra) AND (NEW.NgayTra > NgayNhan);

        -- Nếu tìm thấy (count > 0), hủy bỏ lệnh INSERT
        IF conflict_count > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Lỗi: Phòng này đã được đặt trong khoảng thời gian này.';
    END IF;
    END$$
    DELIMITER ;
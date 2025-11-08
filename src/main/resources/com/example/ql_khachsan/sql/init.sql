-- Tạo CSDL với bảng mã UTF-8 để hỗ trợ tiếng Việt
CREATE DATABASE qlkhachsan CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE qlkhachsan;

-- Bảng KHACHHANG (Sửa: Thêm độ dài cho VARCHAR và UNIQUE)
CREATE TABLE IF NOT EXISTS KHACHHANG (
    MaKH VARCHAR(50) PRIMARY KEY,
    HoTen VARCHAR(100) NOT NULL,
    CCCD VARCHAR(12) NOT NULL UNIQUE,
    SĐT VARCHAR(10) NOT NULL UNIQUE,
    Email VARCHAR(100) NOT NULL UNIQUE,
    TaiKhoan VARCHAR(50) NOT NULL UNIQUE,
    MatKhau VARCHAR(255) NOT NULL
    );

-- Bảng LOAIPHONG (Sửa: Thêm độ dài và DECIMAL)
CREATE TABLE IF NOT EXISTS LOAIPHONG (
    MaLoai VARCHAR(50) PRIMARY KEY,
    TenLoai VARCHAR(100) NOT NULL,
    SoNguoiTD INT NOT NULL,
    DonGia DECIMAL(10, 2) NOT NULL
    );

-- Bảng PHONG (Sửa: Thêm độ dài và Khóa ngoại)
CREATE TABLE IF NOT EXISTS PHONG (
    MaPhong VARCHAR(50) PRIMARY KEY,
    TenPhong VARCHAR(100) NOT NULL,
    TrangThai VARCHAR(50) NOT NULL,
    MaLoai VARCHAR(50) NOT NULL,

    -- Sửa logic: Một Phòng THUỘC một Loại phòng
    FOREIGN KEY (MaLoai) REFERENCES LOAIPHONG(MaLoai)
    );

-- Bảng PHIEUDATPHONG (Sửa: Thêm độ dài, Khóa ngoại, và đổi sang DATE)
CREATE TABLE IF NOT EXISTS PHIEUDATPHONG (
    MaDP VARCHAR(50) PRIMARY KEY,
    NgayDat DATETIME NOT NULL,      -- Giữ DATETIME để biết thời điểm đặt
    NgayNhan DATE NOT NULL,         -- Đổi sang DATE
    NgayTra DATE NOT NULL,          -- Đổi sang DATE
    DonGiaThucTe DECIMAL(10, 2) NOT NULL,
    TrangThaiDP VARCHAR(50) NOT NULL,
    MaKH VARCHAR(50) NOT NULL,
    MaPhong VARCHAR(50) NOT NULL,

    -- Sửa logic: Một Phiếu đặt THUỘC về một Khách hàng
    FOREIGN KEY (MaKH) REFERENCES KHACHHANG(MaKH),
    -- Sửa logic: Một Phiếu đặt đặt một Phòng
    FOREIGN KEY (MaPhong) REFERENCES PHONG(MaPhong)
    );

-- Bảng HOADON (Sửa: Thêm độ dài và ràng buộc 1-1)
CREATE TABLE IF NOT EXISTS HOADON (
    MaHD VARCHAR(50) PRIMARY KEY,
    GhiChu VARCHAR(255) NULL,
    NgayLap DATETIME NOT NULL,
    MaDP VARCHAR(50) NOT NULL UNIQUE,

    -- Logic này đã đúng: Một Hóa đơn CÓ một Phiếu đặt
    FOREIGN KEY (MaDP) REFERENCES PHIEUDATPHONG(MaDP)
    );
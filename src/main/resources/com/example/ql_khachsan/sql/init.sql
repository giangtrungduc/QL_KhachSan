-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: qlkhachsan
-- ------------------------------------------------------
-- Server version	8.4.6

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `hoadon`
--

DROP TABLE IF EXISTS `hoadon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hoadon` (
                          `MaHD` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
                          `GhiChu` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `NgayLap` datetime NOT NULL,
                          `MaDP` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
                          PRIMARY KEY (`MaHD`),
                          UNIQUE KEY `MaDP` (`MaDP`),
                          CONSTRAINT `hoadon_ibfk_1` FOREIGN KEY (`MaDP`) REFERENCES `phieudatphong` (`MaDP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hoadon`
--

LOCK TABLES `hoadon` WRITE;
/*!40000 ALTER TABLE `hoadon` DISABLE KEYS */;
INSERT INTO `hoadon` VALUES ('HD01','1','2025-11-10 10:00:00','DP01'),('HD02','2','2025-11-10 10:00:00','DP02'),('HD03','3','2025-11-10 10:00:00','DP03');
/*!40000 ALTER TABLE `hoadon` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `khachhang`
--

DROP TABLE IF EXISTS `khachhang`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `khachhang` (
                             `MaKH` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
                             `HoTen` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
                             `CCCD` varchar(12) COLLATE utf8mb4_unicode_ci NOT NULL,
                             `SDT` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
                             `Email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
                             `TaiKhoan` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
                             `MatKhau` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                             PRIMARY KEY (`MaKH`),
                             UNIQUE KEY `CCCD` (`CCCD`),
                             UNIQUE KEY `SDT` (`SDT`),
                             UNIQUE KEY `Email` (`Email`),
                             UNIQUE KEY `TaiKhoan` (`TaiKhoan`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `khachhang`
--

LOCK TABLES `khachhang` WRITE;
/*!40000 ALTER TABLE `khachhang` DISABLE KEYS */;
INSERT INTO `khachhang` VALUES ('KH01','Bùi Văn Đạt','123456789011','1111111111','dat@gmail.com','dat','123456'),('KH02','Hoàng Văn Dũng','123456789012','2222222222','dung@gmail.com','dung','123456'),('KH03','Nguyễn Trung Đức ','123456789013','3333333333','duc@gmail.com','duc','123456'),('KH04','Cao Công Hòe','123456789014','4444444444','hoe@gmail.com','hoe','123456'),('KH05','Nguyễn Văn Triều','123456789015','5555555555','trieu@gmail.com','trieu','123456'),('KH06','Lê Thành Thông','123456789016','6666666666','thong@gmail.com','thong','123456'),('KH07','Trần Văn Mạnh','123456789017','7777777777','manh@gmail.com','manh','123456'),('KH08','Nguyễn Văn Huy','123456789018','8888888888','huy@gmail.com','huy','123456'),('KH09','Cao Văn Vũ','123456789019','9999999999','vu@gmail.com','vu','123456'),('KH10','Lương Duy Hoàng','123456789110','1010101010','hoang@gmail.com','hoang','123456');
/*!40000 ALTER TABLE `khachhang` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `loaiphong`
--

DROP TABLE IF EXISTS `loaiphong`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `loaiphong` (
                             `MaLoai` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
                             `TenLoai` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
                             `SoNguoiTD` int NOT NULL,
                             `DonGia` decimal(10,2) NOT NULL,
                             PRIMARY KEY (`MaLoai`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `loaiphong`
--

LOCK TABLES `loaiphong` WRITE;
/*!40000 ALTER TABLE `loaiphong` DISABLE KEYS */;
INSERT INTO `loaiphong` VALUES ('L01','Phòng đơn',2,10000.00),('L02','Phòng dôi',4,20000.00),('L03','Phòng VIP đơn',2,40000.00),('L04','Phòng VIP đôi',4,100000.00);
/*!40000 ALTER TABLE `loaiphong` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `phieudatphong`
--

DROP TABLE IF EXISTS `phieudatphong`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `phieudatphong` (
                                 `MaDP` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
                                 `NgayDat` datetime NOT NULL,
                                 `NgayNhan` datetime NOT NULL,
                                 `NgayTra` datetime NOT NULL,
                                 `DonGiaThucTe` decimal(10,2) NOT NULL,
                                 `TrangThaiDP` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
                                 `MaKH` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
                                 `MaPhong` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
                                 PRIMARY KEY (`MaDP`),
                                 KEY `MaKH` (`MaKH`),
                                 KEY `MaPhong` (`MaPhong`),
                                 CONSTRAINT `phieudatphong_ibfk_1` FOREIGN KEY (`MaKH`) REFERENCES `khachhang` (`MaKH`),
                                 CONSTRAINT `phieudatphong_ibfk_2` FOREIGN KEY (`MaPhong`) REFERENCES `phong` (`MaPhong`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `phieudatphong`
--

LOCK TABLES `phieudatphong` WRITE;
/*!40000 ALTER TABLE `phieudatphong` DISABLE KEYS */;
INSERT INTO `phieudatphong` VALUES ('DP01','2025-01-01 10:00:00','2025-11-10 10:00:00','2025-12-11 10:00:00',10000.00,'Hoàn thành','KH01','P01'),('DP02','2025-01-01 10:00:00','2025-11-10 10:00:00','2025-12-11 10:00:00',20000.00,'Hoàn thành','KH04','P03'),('DP03','2025-01-01 10:00:00','2025-11-10 10:00:00','2025-12-11 10:00:00',29990.00,'Hoàn thành','KH06','P06');
/*!40000 ALTER TABLE `phieudatphong` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `phong`
--

DROP TABLE IF EXISTS `phong`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `phong` (
                         `MaPhong` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
                         `TenPhong` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
                         `TrangThai` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
                         `MaLoai` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
                         PRIMARY KEY (`MaPhong`),
                         KEY `MaLoai` (`MaLoai`),
                         CONSTRAINT `phong_ibfk_1` FOREIGN KEY (`MaLoai`) REFERENCES `loaiphong` (`MaLoai`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `phong`
--

LOCK TABLES `phong` WRITE;
/*!40000 ALTER TABLE `phong` DISABLE KEYS */;
INSERT INTO `phong` VALUES ('P01','Phòng 1','Trống','L01'),('P02','Phòng 2','Trống','L02'),('P03','Phòng 3','Trống','L03'),('P04','Phòng 4','Trống','L04'),('P05','Phòng 5','Trống','L01'),('P06','Phòng 6','Trống','L02'),('P07','Phòng 7','Trống','L02'),('P08','Phòng 8','Trống','L01'),('P09','Phòng 9','Trống','L02'),('P10','Phòng 10','Trống','L03');
/*!40000 ALTER TABLE `phong` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-20 15:41:25

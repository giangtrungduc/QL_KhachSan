import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {

    private Connection conn;

    public HoaDonDAO(Connection conn) {
        this.conn = conn;
    }

    // 1. Lấy tất cả hóa đơn
    public List<HoaDon> getAll() {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM HOADON";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                HoaDon hd = new HoaDon();
                hd.setMaHD(rs.getString("MaHD"));
                hd.setGhiChu(rs.getString("GhiChu"));
                hd.setNgayLap(rs.getTimestamp("NgayLap").toLocalDateTime());
                hd.setMaDP(rs.getString("MaDP"));
                list.add(hd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Thêm hóa đơn
    public boolean insert(HoaDon hd) {
        String sql = "INSERT INTO HOADON(MaHD, GhiChu, NgayLap, MaDP) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hd.getMaHD());
            ps.setString(2, hd.getGhiChu());
            ps.setTimestamp(3, Timestamp.valueOf(hd.getNgayLap()));
            ps.setString(4, hd.getMaDP());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 3. Cập nhật hóa đơn
    public boolean update(HoaDon hd) {
        String sql = "UPDATE HOADON SET GhiChu = ?, NgayLap = ?, MaDP = ? WHERE MaHD = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hd.getGhiChu());
            ps.setTimestamp(2, Timestamp.valueOf(hd.getNgayLap()));
            ps.setString(3, hd.getMaDP());
            ps.setString(4, hd.getMaHD());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4. Xóa hóa đơn
    public boolean delete(String maHD) {
        String sql = "DELETE FROM HOADON WHERE MaHD = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHD);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

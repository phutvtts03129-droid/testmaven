package login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import DBConnect.DBConnectx;

/**
 * DAO cho bảng TaiKhoan(id, username, password, hoTen) trong database Nhat.
 */
public class TaiKhoanDAO {

    private static final int MAX_USERNAME = 50;   // username VARCHAR(50)
    private static final int MAX_PASSWORD = 100;  // password VARCHAR(100)
    private static final int MAX_HOTEN = 100;     // hoTen NVARCHAR(100)
    private static final int LOI_TRUNG_UNIQUE_CONSTRAINT = 2627;
    private static final int LOI_TRUNG_UNIQUE_INDEX = 2601;

    /**
     * Đăng ký tài khoản mới.
     * @return true nếu tạo thành công; false nếu dữ liệu trống/null, quá dài,
     *         username đã tồn tại hoặc lỗi DB.
     */
    public boolean dangKy(String username, String password, String hoTen) {
        if (rong(username) || rong(password) || rong(hoTen)) {
            return false;
        }
        username = username.trim();
        hoTen = hoTen.trim();
        if (username.length() > MAX_USERNAME
                || password.length() > MAX_PASSWORD
                || hoTen.length() > MAX_HOTEN) {
            return false;
        }

        String sql = "INSERT INTO TaiKhoan (username, password, hoTen) VALUES (?, ?, ?)";
        try (Connection conn = DBConnectx.getConnection()) {
            if (conn == null) {
                return false; // không kết nối được DB
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, hoTen);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            // Trùng username là trường hợp bình thường, không in stack trace
            if (e.getErrorCode() != LOI_TRUNG_UNIQUE_CONSTRAINT
                    && e.getErrorCode() != LOI_TRUNG_UNIQUE_INDEX) {
                e.printStackTrace();
            }
            return false;
        }
    }

    /**
     * Đăng nhập. Username được trim; mật khẩu so sánh chính xác trong Java
     * (phân biệt hoa/thường, không phụ thuộc collation của SQL Server).
     */
    public boolean dangNhap(String username, String password) {
        if (rong(username) || password == null || password.isEmpty()) {
            return false;
        }
        String sql = "SELECT password FROM TaiKhoan WHERE username = ?";
        try (Connection conn = DBConnectx.getConnection()) {
            if (conn == null) {
                return false;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username.trim());
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() && password.equals(rs.getString("password"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean rong(String s) {
        return s == null || s.trim().isEmpty();
    }
}
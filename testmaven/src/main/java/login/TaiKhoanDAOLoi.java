package login;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import DBConnect.DBConnectx;

/**
 * BẢN CỐ Ý CÓ LỖI của TaiKhoanDAO, dùng để kiểm tra xem bộ test có "bắt" được lỗi hay không.
 *
 * Cách dùng: đổi số ở BUG bên dưới (0 = code đúng, 1..11 = từng lỗi), lưu file,
 * rồi chạy TaiKhoanDAOLoiTest và xem test nào đỏ.
 *
 *  BUG  Lỗi được cài                                          Test dự kiến đỏ
 *   0   Không lỗi (đối chứng)                                 (không có)
 *   1   Đăng nhập không kiểm tra mật khẩu                     TC06, TC10, TC17, TC19, TC20
 *   2   So mật khẩu không phân biệt hoa/thường                TC17
 *   3   Đăng ký cho phép username trống                       TC03
 *   4   Đăng ký cho phép mật khẩu trống                       TC04
 *   5   Đăng ký cho phép họ tên trống                         TC14
 *   6   Không kiểm tra độ dài username                        (không test nào đỏ: lỗi "sống sót")
 *   7   Đăng nhập không trim username                         TC18
 *   8   Không xử lý null khi đăng nhập (NullPointerException) TC13 (hiện là Error)
 *   9   Nối chuỗi SQL (lỗ hổng SQL Injection)                 TC17, TC19, TC20
 *  10   Đăng ký trùng username vẫn báo thành công             TC02
 *  11   Lưu mật khẩu bị cắt còn 5 ký tự                       TC05, TC17, TC18
 */
public class TaiKhoanDAOLoi {

    /** Đổi số này để chọn lỗi cần thử. */
    public static int BUG = 11;

    private static final int MAX_USERNAME = 50;
    private static final int MAX_PASSWORD = 100;
    private static final int MAX_HOTEN = 100;
    private static final int LOI_TRUNG_UNIQUE_CONSTRAINT = 2627;
    private static final int LOI_TRUNG_UNIQUE_INDEX = 2601;

    public boolean dangKy(String username, String password, String hoTen) {
        // BUG 3, 4, 5: bỏ kiểm tra trống (chỉ còn kiểm tra null)
        boolean usernameOk = (BUG == 3) ? username != null : !rong(username);
        boolean passwordOk = (BUG == 4) ? password != null : !rong(password);
        boolean hoTenOk = (BUG == 5) ? hoTen != null : !rong(hoTen);
        if (!usernameOk || !passwordOk || !hoTenOk) {
            return false;
        }
        username = username.trim();
        hoTen = hoTen.trim();

        // BUG 6: bỏ kiểm tra độ dài username
        boolean usernameQuaDai = BUG != 6 && username.length() > MAX_USERNAME;
        if (usernameQuaDai || password.length() > MAX_PASSWORD || hoTen.length() > MAX_HOTEN) {
            return false;
        }

        // BUG 11: lưu mật khẩu bị cắt còn 5 ký tự
        String matKhauLuu = (BUG == 11) ? password.substring(0, Math.min(5, password.length())) : password;

        String sql = "INSERT INTO TaiKhoan (username, password, hoTen) VALUES (?, ?, ?)";
        try (Connection conn = DBConnectx.getConnection()) {
            if (conn == null) {
                return false;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                ps.setString(2, matKhauLuu);
                ps.setString(3, hoTen);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            boolean trung = e.getErrorCode() == LOI_TRUNG_UNIQUE_CONSTRAINT
                    || e.getErrorCode() == LOI_TRUNG_UNIQUE_INDEX;
            if (trung) {
                return BUG == 10; // BUG 10: trùng username mà vẫn báo thành công
            }
            e.printStackTrace();
            return false;
        }
    }

    public boolean dangNhap(String username, String password) {
        if (BUG == 8) {
            // BUG 8: gọi password.isEmpty() khi password = null -> NullPointerException
            if (password.isEmpty() || rong(username)) {
                return false;
            }
        } else if (rong(username) || password == null || password.isEmpty()) {
            return false;
        }

        try (Connection conn = DBConnectx.getConnection()) {
            if (conn == null) {
                return false;
            }
            if (BUG == 9) {
                return dangNhapNoiChuoi(conn, username, password);
            }

            String sql = "SELECT password FROM TaiKhoan WHERE username = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                // BUG 7: không trim username
                ps.setString(1, (BUG == 7) ? username : username.trim());
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return false;
                    }
                    if (BUG == 1) {
                        return true; // BUG 1: không kiểm tra mật khẩu
                    }
                    String matKhauDb = rs.getString("password");
                    // BUG 2: không phân biệt hoa/thường
                    return (BUG == 2) ? password.equalsIgnoreCase(matKhauDb) : password.equals(matKhauDb);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** BUG 9: nối chuỗi thẳng vào câu SQL, dính SQL Injection. */
    private boolean dangNhapNoiChuoi(Connection conn, String username, String password) throws SQLException {
        String sql = "SELECT 1 FROM TaiKhoan WHERE username = '" + username.trim()
                + "' AND password = '" + password + "'";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            return rs.next();
        }
    }

    private boolean rong(String s) {
        return s == null || s.trim().isEmpty();
    }
}
package DAO;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import DBConnect.DBConnectx;
import login.TaiKhoanDAO;

/**
 * Test đăng ký / đăng nhập của TaiKhoanDAO.
 * Yêu cầu: SQL Server đang chạy, DBConnectx trỏ đúng database Nhat có bảng TaiKhoan(id, username, password, hoTen).
 * Test tự tạo tài khoản admin/123456 (nếu chưa có) và tự xóa các tài khoản test_* sau khi chạy.
 */
public class TaiKhoanDAOTest {

    static final String ADMIN_USER = "admin";
    static final String ADMIN_PASS = "123456";

    TaiKhoanDAO dao = new TaiKhoanDAO();

    // ---------- chuẩn bị & dọn dữ liệu ----------

    @BeforeAll
    static void chuanBiDuLieu() throws SQLException {
        try (Connection conn = DBConnectx.getConnection()) {
            assertNotNull(conn, "Không kết nối được SQL Server, kiểm tra DBConnectx (DBNAME = Nhat) và dịch vụ SQL Server");
        }
        // Nếu admin đã có sẵn (từ script SQL) thì dangKy trả false, không sao
        new TaiKhoanDAO().dangKy(ADMIN_USER, ADMIN_PASS, "Quản trị viên");
    }

    @AfterAll
    static void donDepDuLieu() throws SQLException {
        try (Connection conn = DBConnectx.getConnection()) {
            if (conn == null) {
                return;
            }
            try (Statement st = conn.createStatement()) {
                st.executeUpdate("DELETE FROM TaiKhoan WHERE username LIKE 'test[_]%'");
            }
        }
    }

    // Tạo username riêng để tránh trùng dữ liệu
    String taoUsername() {
        return "test_" + UUID.randomUUID().toString().substring(0, 8);
    }

    // ---------- ĐĂNG KÝ ----------

    // TC01: Đăng ký thành công
    @Test
    public void testDangKyThanhCong() {
        assertTrue(dao.dangKy(taoUsername(), "123456", "Nguyen Van A"));
    }

    // TC02: Đăng ký username đã tồn tại
    @Test
    public void testDangKyTrungUsername() {
        String username = taoUsername();
        assertTrue(dao.dangKy(username, "123456", "Nguyen Van A"));
        assertFalse(dao.dangKy(username, "654321", "Nguyen Van B"));
    }

    // TC03: Đăng ký username trống
    @Test
    public void testDangKyUsernameTrong() {
        assertFalse(dao.dangKy("", "123456", "Nguyen Van A"));
    }

    // TC04: Đăng ký password trống
    @Test
    public void testDangKyPasswordTrong() {
        assertFalse(dao.dangKy(taoUsername(), "", "Nguyen Van A"));
    }

    // TC14: Đăng ký họ tên trống
    @Test
    public void testDangKyHoTenTrong() {
        assertFalse(dao.dangKy(taoUsername(), "123456", ""));
    }

    // TC15: Đăng ký tất cả tham số null
    @Test
    public void testDangKyNull() {
        assertFalse(dao.dangKy(null, null, null));
    }

    // TC16: Đăng ký username quá dài (> 50 ký tự)
    @Test
    public void testDangKyUsernameQuaDai() {
        String dai = "test_" + "a".repeat(60);
        assertFalse(dao.dangKy(dai, "123456", "Nguyen Van A"));
    }

    // ---------- ĐĂNG NHẬP ----------

    // TC05: Đăng nhập thành công
    @Test
    public void testDangNhapThanhCong() {
        String username = taoUsername();
        assertTrue(dao.dangKy(username, "123456", "Nguyen Van A"));
        assertTrue(dao.dangNhap(username, "123456"));
    }

    // TC06: Đăng nhập sai mật khẩu
    @Test
    public void testDangNhapSaiMatKhau() {
        String username = taoUsername();
        assertTrue(dao.dangKy(username, "123456", "Nguyen Van A"));
        assertFalse(dao.dangNhap(username, "999999"));
    }

    // TC07: Đăng nhập sai username
    @Test
    public void testDangNhapSaiUsername() {
        assertFalse(dao.dangNhap("username_khong_ton_tai", "123456"));
    }

    // TC08: Đăng nhập username trống
    @Test
    public void testDangNhapUsernameTrong() {
        assertFalse(dao.dangNhap("", "123456"));
    }

    // TC09: Đăng nhập thành công bằng tài khoản admin
    @Test
    public void testDangNhapAdminThanhCong() {
        assertTrue(dao.dangNhap(ADMIN_USER, ADMIN_PASS));
    }

    // TC10: Đăng nhập admin sai mật khẩu
    @Test
    public void testDangNhapAdminSaiPassword() {
        assertFalse(dao.dangNhap(ADMIN_USER, "999999"));
    }

    // TC11: Đăng nhập tài khoản không tồn tại
    @Test
    public void testDangNhapTaiKhoanKhongTonTai() {
        assertFalse(dao.dangNhap("user_khong_co", "123456"));
    }

    // TC12: Đăng nhập password trống
    @Test
    public void testDangNhapPasswordTrong() {
        assertFalse(dao.dangNhap(ADMIN_USER, ""));
    }

    // TC13: Đăng nhập username và password null
    @Test
    public void testDangNhapNull() {
        assertFalse(dao.dangNhap(null, null));
    }

    // TC17: Mật khẩu phân biệt hoa/thường
    @Test
    public void testDangNhapMatKhauPhanBietHoaThuong() {
        String username = taoUsername();
        assertTrue(dao.dangKy(username, "Abc@123", "Nguyen Van A"));
        assertTrue(dao.dangNhap(username, "Abc@123"));
        assertFalse(dao.dangNhap(username, "abc@123"));
    }

    // TC18: Username có khoảng trắng đầu/cuối vẫn đăng nhập được (được trim)
    @Test
    public void testDangNhapUsernameCoKhoangTrang() {
        String username = taoUsername();
        assertTrue(dao.dangKy(username, "123456", "Nguyen Van A"));
        assertTrue(dao.dangNhap("  " + username + "  ", "123456"));
    }

    // TC19: Mật khẩu có khoảng trắng cuối không được coi là đúng
    @Test
    public void testDangNhapMatKhauThuaKhoangTrang() {
        String username = taoUsername();
        assertTrue(dao.dangKy(username, "1256", "Nguyen Van A"));
        assertFalse(dao.dangNhap(username, "3456 "));
    }

    // ---------- BẢO MẬT ----------

    // TC20: SQL Injection không đăng nhập được
    @Test
    public void testDangNhapSqlInjection() {
        assertFalse(dao.dangNhap("' OR '1'='1' --", "bat_ky"));
        assertFalse(dao.dangNhap("admin' --", "bat_ky"));
        assertFalse(dao.dangNhap(ADMIN_USER, "' OR '1'='1"));
    }
}
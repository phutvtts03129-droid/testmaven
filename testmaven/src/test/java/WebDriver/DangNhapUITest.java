package WebDriver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DangNhapUITest extends BaseTest {

    @Test
    @DisplayName("TC01 - Đăng nhập ADMIN thành công -> Chuyển hướng /admin")
    public void testLoginAdminSuccess() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.login("admin", "admin123");

        Assertions.assertTrue(driver.getCurrentUrl().contains("/admin"),
            "Admin đăng nhập thành công nhưng không chuyển về trang /admin!");
    }

    @Test
    @DisplayName("TC02 - Đăng nhập EMPLOYEE thành công -> Chuyển hướng /nhan-vien")
    public void testLoginEmployeeSuccess() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.login("nam_dev", "123456");

        Assertions.assertTrue(driver.getCurrentUrl().contains("/nhan-vien"),
            "Nhân viên đăng nhập thành công nhưng không chuyển về /nhan-vien!");
    }

    @Test
    @DisplayName("TC03 - Đăng nhập CUSTOMER bằng Email thành công -> Chuyển hướng /trang-chu")
    public void testLoginCustomerWithEmailSuccess() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.login("thanhtu99@gmail.com", "123456");

        Assertions.assertTrue(driver.getCurrentUrl().contains("/trang-chu"),
            "Khách hàng đăng nhập thành công nhưng không chuyển về /trang-chu!");
    }

    @Test
    @DisplayName("TC04 - Đăng nhập thất bại khi nhập sai mật khẩu")
    public void testLoginWrongPassword() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.login("admin", "sai_mat_khau_999");

        Assertions.assertEquals("Tai khoan hoac mat khau khong dung, hoac da bi khoa!", 
            loginPage.getErrorMessage());
    }

    @Test
    @DisplayName("TC05 - Đăng nhập thất bại với tài khoản bị khóa (status = 0)")
    public void testLoginLockedAccount() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.login("tuan_spam", "123456");

        Assertions.assertEquals("Tai khoan hoac mat khau khong dung, hoac da bi khoa!", 
            loginPage.getErrorMessage());
    }

    @Test
    @DisplayName("TC06 - Đăng nhập thất bại khi bỏ trống thông tin")
    public void testLoginEmptyFields() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.login("", "");

        // HTML5 required trên input sẽ chặn submit và giữ nguyên trang /dang-nhap
        Assertions.assertTrue(driver.getCurrentUrl().contains("/dang-nhap"));
    }
}
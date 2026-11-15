package WebDriver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

public class TrangChuUITest extends BaseTest {

	@Test
    @DisplayName("TC01 - Kiểm tra Header, Banner và tìm kiếm sản phẩm trên thanh tìm kiếm")
    public void testHeaderAndHeroVisible() {
        HomePage home = new HomePage(driver);
        home.open();

        // 1. Kiểm tra tiêu đề và các thành phần giao diện chính
        Assertions.assertEquals("Nông Sản Xanh - Tươi ngon từ thiên nhiên", driver.getTitle());
        Assertions.assertTrue(home.isHeaderAndHeroDisplayed(),
            "Thiếu thành phần trên Header hoặc Hero Banner!");

        // 2. Nhập từ khóa vào form tìm kiếm và kiểm tra có sản phẩm hiển thị ra
        String tuKhoa = "Cà chua";
        home.searchProduct(tuKhoa);

        Assertions.assertTrue(driver.getCurrentUrl().contains("/tim-kiem"),
            "Không chuyển sang trang kết quả tìm kiếm (/tim-kiem)!");
        Assertions.assertTrue(home.getSearchResultCount() > 0,
            "Không tìm thấy sản phẩm nào hiển thị cho từ khóa: " + tuKhoa);
    }

    @Test
    @DisplayName("TC02 - Kiểm tra dữ liệu Danh mục & Sản phẩm nổi bật tải từ SQL Server")
    public void testDatabaseContentLoaded() {
        HomePage home = new HomePage(driver);
        home.open();

        Assertions.assertTrue(home.getCategoryCount() > 0, "Danh mục sản phẩm bị trống!");
        Assertions.assertTrue(home.getFeaturedProductCount() > 0, "Danh sách sản phẩm nổi bật bị trống!");
    }

    @Test
    @DisplayName("TC03 - Kiểm tra hiển thị đủ 6 khung Combo tiết kiệm từ JS")
    public void testComboSectionRendered() {
        HomePage home = new HomePage(driver);
        home.open();

        Assertions.assertEquals(6, home.getComboCount(),
            "Trang chủ phải hiển thị đủ 6 combo nông sản!");
    }

    @Test
    @DisplayName("TC04 - Kiểm tra thêm sản phẩm vào giỏ hàng tăng số lượng badge")
    public void testAddToCartUpdatesBadge() {
        HomePage home = new HomePage(driver);
        home.open();

        Assertions.assertEquals("0", home.getCartCount());
        home.clickFirstAddToCart();
        Assertions.assertEquals("1", home.getCartCount(),
            "Số lượng trên icon Giỏ hàng không tăng sau khi bấm Thêm vào giỏ!");
    }

    @Test
    @DisplayName("TC05 - Kiểm tra nút Yêu thích (Tim) cập nhật số lượng trên Header")
    public void testFavoriteHeartUpdatesBadge() {
        HomePage home = new HomePage(driver);
        home.open();

        Assertions.assertEquals("0", home.getFavoriteCount());
        home.clickFirstFavoriteHeart();
        Assertions.assertEquals("1", home.getFavoriteCount(),
            "Số lượng Yêu thích không tăng sau khi bấm tim!");
    }

    @Test
    @DisplayName("TC06 - Kiểm tra chuyển đổi chế độ Sáng / Tối qua đèn bàn góc trái")
    public void testDarkModeToggle() {
        HomePage home = new HomePage(driver);
        home.open();

        Assertions.assertEquals("light", home.getCurrentTheme());
        home.toggleDarkModeViaLamp();
        Assertions.assertEquals("dark", home.getCurrentTheme(),
            "Không chuyển sang giao diện ban đêm (data-theme='dark')!");
    }

    @Test
    @DisplayName("TC07 - Kiểm tra tương tác nhân vật Trợ lý AI Milk mở khung chat")
    public void testMilkAiAssistantOpens() {
        HomePage home = new HomePage(driver);
        home.open();

        home.openMilkAssistant();
        Assertions.assertTrue(home.isMilkChatOpen(),
            "Khung chat Trợ lý AI Milk không mở ra khi click vào nhân vật!");
    }

    @Test
    @DisplayName("TC08 - Kiểm tra điều hướng sang trang Danh mục (/danh-muc)")
    public void testNavigateToDanhMuc() {
        HomePage home = new HomePage(driver);
        home.open();

        home.goToDanhMucPage();
        Assertions.assertTrue(driver.getCurrentUrl().contains("/danh-muc"),
            "Không chuyển sang được trang Danh mục!");
        Assertions.assertTrue(driver.findElement(By.cssSelector(".cat-hero h1")).isDisplayed());
    }

    @Test
    @DisplayName("TC09 - Kiểm tra bấm 'Xem chi tiết' sản phẩm chuyển đúng sang /chi-tiet")
    public void testNavigateToProductDetail() {
        HomePage home = new HomePage(driver);
        home.open();

        home.clickFirstProductDetail();
        Assertions.assertTrue(driver.getCurrentUrl().contains("/chi-tiet?id="),
            "Không chuyển sang trang chi tiết sản phẩm!");
        Assertions.assertTrue(driver.findElement(By.id("spName")).isDisplayed());
    }

    @Test
    @DisplayName("TC10 - Kiểm tra bảo mật: Khách chưa đăng nhập bấm 'Thanh toán' bị chuyển về /dang-nhap")
    public void testCheckoutRequiresLogin() {
        HomePage home = new HomePage(driver);
        home.open();

        home.clickFirstProductPayNow();
        // ThanhToanServlet kiểm tra currentUser == null -> redirect /dang-nhap?redirect=/thanh-toan
        Assertions.assertTrue(driver.getCurrentUrl().contains("/dang-nhap"),
            "Lỗi bảo mật: Cho phép vào trang thanh toán khi chưa đăng nhập!");
    }
}
package WebDriver;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class HomePage {
    private WebDriver driver;
    private WebDriverWait wait;

    // 1. Header & Điều hướng
    private By brandLogo = By.cssSelector("header .brand");
    private By searchInput = By.cssSelector("header .search-box input[name='q']");
    private By cartBadge = By.id("cartCount");
    private By favBadge = By.id("favCount");
    private By navDanhMuc = By.cssSelector(".main-nav a[href='danh-muc']");

    // 2. Hero Banner & Đồng hồ
    private By heroSection = By.id("top");
    private By heroClock = By.id("heroClock");

    // 3. Danh mục & Sản phẩm nổi bật & Combo
    private By categoryCards = By.cssSelector("#danh-muc .cat-card");
    private By productCards = By.cssSelector("#san-pham .p-card");
    private By firstAddCartBtn = By.cssSelector("#san-pham .add-cart");
    private By firstHeartBtn = By.cssSelector("#san-pham .heart");
    private By firstDetailLink = By.cssSelector("#san-pham .btn-detail");
    private By firstPayLink = By.cssSelector("#san-pham .btn-pay");
    private By comboCards = By.cssSelector("#comboGrid .cb-card");

    // 4. Trợ lý AI Milk & Chế độ Sáng/Tối (trong home-extras.jspf & chat-ai.jspf)
    private By lampThemeSwitch = By.cssSelector(".fx-dock .lm");
    private By aiBotMascot = By.id("aiBot");
    private By aiChatPanel = By.id("aiPanel");
    private By aiInputText = By.id("aiText");
 // Bổ sung locators cho form tìm kiếm trên Header
    private By searchSubmitBtn = By.cssSelector("header .search-box button[type='submit']");
    private By searchResultItems = By.cssSelector(".pc, .p-card, .product-card");

    // Nhập từ khóa vào form tìm kiếm trên Header và bấm nút Tìm
    public void searchProduct(String keyword) {
        // Điều hướng sang trang /danh-muc nơi đã xử lý tìm kiếm ?q=...
        driver.get("http://localhost:8080/quanly-nongnghiep/danh-muc");
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".tool input[name='q']")));
        input.clear();
        input.sendKeys(keyword);
        driver.findElement(By.cssSelector(".tool button[type='submit']")).click();
    }

    // Đếm số lượng sản phẩm hiển thị trên trang kết quả tìm kiếm
    public int getSearchResultCount() {
        return driver.findElements(searchResultItems).size();
    }
    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void open() {
        driver.get("http://localhost:8080/quanly-nongnghiep/trang-chu");
        // Xóa localStorage để đảm bảo trạng thái yêu thích và giao diện mặc định mỗi lần test
        ((JavascriptExecutor) driver).executeScript("localStorage.clear();");
        driver.navigate().refresh();
    }

    public boolean isHeaderAndHeroDisplayed() {
        return driver.findElement(brandLogo).isDisplayed()
            && driver.findElement(searchInput).isDisplayed()
            && driver.findElement(heroSection).isDisplayed()
            && driver.findElement(heroClock).isDisplayed();
    }

    public int getCategoryCount() {
        return driver.findElements(categoryCards).size();
    }

    public int getFeaturedProductCount() {
        return driver.findElements(productCards).size();
    }

    public int getComboCount() {
        return driver.findElements(comboCards).size();
    }

    public void clickFirstAddToCart() {
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(firstAddCartBtn));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }

    public String getCartCount() {
        return driver.findElement(cartBadge).getText().trim();
    }

    public void clickFirstFavoriteHeart() {
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(firstHeartBtn));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }

    public String getFavoriteCount() {
        return driver.findElement(favBadge).getText().trim();
    }

    public void toggleDarkModeViaLamp() {
        WebElement lamp = wait.until(ExpectedConditions.presenceOfElementLocated(lampThemeSwitch));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", lamp);
    }

    public String getCurrentTheme() {
        return driver.findElement(By.tagName("html")).getAttribute("data-theme");
    }

    public void openMilkAssistant() {
        WebElement bot = wait.until(ExpectedConditions.presenceOfElementLocated(aiBotMascot));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", bot);
        wait.until(ExpectedConditions.visibilityOfElementLocated(aiChatPanel));
    }

    public boolean isMilkChatOpen() {
        return driver.findElement(aiChatPanel).isDisplayed()
            && driver.findElement(aiInputText).isDisplayed();
    }

    public void goToDanhMucPage() {
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(navDanhMuc));
        link.click();
    }

    public void clickFirstProductDetail() {
        WebElement link = wait.until(ExpectedConditions.presenceOfElementLocated(firstDetailLink));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", link);
    }

    public void clickFirstProductPayNow() {
        WebElement link = wait.until(ExpectedConditions.presenceOfElementLocated(firstPayLink));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", link);
    }
}
package WebDriver;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class LoginPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Locators khớp 100% với login.jsp
    private By lampShade = By.id("shadeBody");
    private By txtAccount = By.id("account");
    private By txtPassword = By.id("password");
    private By btnLogin = By.cssSelector("button.btn[type='submit']");
    private By lblError = By.cssSelector(".error");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void open() {
        driver.get("http://localhost:8080/quanly-nongnghiep/dang-nhap");
        
        // 1. Click vào chao đèn (#shadeBody) để "thắp sáng" và mở form đăng nhập
        wait.until(ExpectedConditions.elementToBeClickable(lampShade)).click();
        
        // 2. Chờ hiệu ứng transition (0.6s - 0.9s) hoàn tất và ô #account hiển thị rõ ràng
        wait.until(ExpectedConditions.visibilityOfElementLocated(txtAccount));
    }

    public void login(String account, String password) {
        driver.findElement(txtAccount).clear();
        driver.findElement(txtAccount).sendKeys(account);
        driver.findElement(txtPassword).clear();
        driver.findElement(txtPassword).sendKeys(password);
        driver.findElement(btnLogin).click();
    }

    public String getErrorMessage() {
        // Khi đăng nhập sai, servlet forward lại login.jsp (đèn tắt lại) -> bật đèn lại để đọc thông báo lỗi
        if (!driver.findElements(lblError).isEmpty()) {
            wait.until(ExpectedConditions.elementToBeClickable(lampShade)).click();
            return wait.until(ExpectedConditions.visibilityOfElementLocated(lblError)).getText();
        }
        return "";
    }
}
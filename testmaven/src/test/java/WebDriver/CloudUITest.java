package WebDriver;



import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class CloudUITest {
    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        // Cấu hình bắt buộc để Chrome chạy được trên Ubuntu Server của GitHub Actions
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @Test
    @DisplayName("CI-TC01: Kiểm tra đăng nhập thành công trên môi trường CI/CD")
    public void testCloudLoginSuccess() {
        driver.get("https://the-internet.herokuapp.com/login");

        driver.findElement(By.id("username")).sendKeys("tomsmith");
        driver.findElement(By.id("password")).sendKeys("SuperSecretPassword!");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        WebElement flashMsg = driver.findElement(By.id("flash"));
        Assertions.assertTrue(flashMsg.getText().contains("You logged into a secure area!"));
    }

    @Test
    @DisplayName("CI-TC02: Kiểm tra đăng nhập thất bại khi sai mật khẩu trên CI/CD")
    public void testCloudLoginFail() {
        driver.get("https://the-internet.herokuapp.com/login");

        driver.findElement(By.id("username")).sendKeys("tomsmith");
        driver.findElement(By.id("password")).sendKeys("sai_mat_khau");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        WebElement flashMsg = driver.findElement(By.id("flash"));
        Assertions.assertTrue(flashMsg.getText().contains("Your password is invalid!"));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
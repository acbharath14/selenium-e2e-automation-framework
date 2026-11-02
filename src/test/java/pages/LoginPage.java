package pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * The Swag Labs login page (https://www.saucedemo.com).
 * All selectors for this page live here — tests never touch them directly.
 */
public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final By USERNAME = By.id("user-name");
    private static final By PASSWORD = By.id("password");
    private static final By LOGIN_BUTTON = By.id("login-button");
    private static final By ERROR_MESSAGE = By.cssSelector("[data-test='error']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /** Types credentials and submits. Returns the inventory page on success. */
    public InventoryPage loginAs(String username, String password) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(USERNAME)).sendKeys(username);
        driver.findElement(PASSWORD).sendKeys(password);
        driver.findElement(LOGIN_BUTTON).click();
        return new InventoryPage(driver);
    }

    /** The red error banner text after a failed login; empty string if none is shown. */
    public String errorMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_MESSAGE)).getText();
    }
}

package pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * The Swag Labs checkout flow: your-information form, the overview
 * page with the price summary, and the final confirmation page.
 */
public class CheckoutPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final By FIRST_NAME = By.id("first-name");
    private static final By LAST_NAME = By.id("last-name");
    private static final By POSTAL_CODE = By.id("postal-code");
    private static final By CONTINUE_BUTTON = By.id("continue");
    private static final By SUBTOTAL_LABEL = By.cssSelector(".summary_subtotal_label");
    private static final By TAX_LABEL = By.cssSelector(".summary_tax_label");
    private static final By TOTAL_LABEL = By.cssSelector(".summary_total_label");
    private static final By FINISH_BUTTON = By.id("finish");
    private static final By COMPLETE_HEADER = By.cssSelector(".complete-header");

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /** Fills the "Your Information" form and continues to the overview page. */
    public void fillInformation(String firstName, String lastName, String postalCode) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(FIRST_NAME)).sendKeys(firstName);
        driver.findElement(LAST_NAME).sendKeys(lastName);
        driver.findElement(POSTAL_CODE).sendKeys(postalCode);
        driver.findElement(CONTINUE_BUTTON).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(SUBTOTAL_LABEL));
    }

    public String subtotalText() {
        return driver.findElement(SUBTOTAL_LABEL).getText();
    }

    public String taxText() {
        return driver.findElement(TAX_LABEL).getText();
    }

    public String totalText() {
        return driver.findElement(TOTAL_LABEL).getText();
    }

    /** Clicks Finish and waits for the order-confirmation page. */
    public void finishOrder() {
        wait.until(ExpectedConditions.elementToBeClickable(FINISH_BUTTON)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(COMPLETE_HEADER));
    }

    public String confirmationHeader() {
        return driver.findElement(COMPLETE_HEADER).getText();
    }
}

package pages;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * The Swag Labs shopping cart page.
 */
public class CartPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final By TITLE = By.cssSelector(".title");
    private static final By CART_ITEMS = By.cssSelector(".cart_item");
    private static final By ITEM_NAMES = By.cssSelector(".inventory_item_name");
    private static final By ITEM_PRICES = By.cssSelector(".inventory_item_price");
    private static final By CHECKOUT_BUTTON = By.id("checkout");

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(TITLE));
    }

    public List<String> itemNames() {
        return driver.findElements(ITEM_NAMES).stream()
                .map(WebElement::getText).toList();
    }

    public List<String> itemPrices() {
        return driver.findElements(ITEM_PRICES).stream()
                .map(WebElement::getText).toList();
    }

    public int itemCount() {
        return driver.findElements(CART_ITEMS).size();
    }

    /** Removes a product from the cart by its exact name. */
    public void removeItem(String productName) {
        String buttonId = "remove-" + productName.toLowerCase().replaceAll("[^a-z0-9]+", "-");
        wait.until(ExpectedConditions.elementToBeClickable(By.id(buttonId))).click();
        wait.until(d -> itemCount() == 0 || !itemNames().contains(productName));
    }

    public CheckoutPage startCheckout() {
        wait.until(ExpectedConditions.elementToBeClickable(CHECKOUT_BUTTON)).click();
        return new CheckoutPage(driver);
    }
}

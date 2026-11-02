package pages;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * The Swag Labs products (inventory) page.
 * Centralizes product listing, sorting, and add-to-cart actions.
 */
public class InventoryPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final By TITLE = By.cssSelector(".title");
    private static final By PRODUCT_NAMES = By.cssSelector(".inventory_item_name");
    private static final By PRODUCT_PRICES = By.cssSelector(".inventory_item_price");
    private static final By PRODUCT_IMAGES = By.cssSelector(".inventory_item_img img");
    private static final By SORT_DROPDOWN = By.cssSelector("[data-test='product-sort-container']");
    private static final By CART_BADGE = By.cssSelector(".shopping_cart_badge");
    private static final By CART_LINK = By.cssSelector(".shopping_cart_link");

    public InventoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(TITLE));
    }

    public String pageTitle() {
        return driver.findElement(TITLE).getText();
    }

    public List<String> productNames() {
        return driver.findElements(PRODUCT_NAMES).stream()
                .map(WebElement::getText).toList();
    }

    public List<String> productPrices() {
        return driver.findElements(PRODUCT_PRICES).stream()
                .map(WebElement::getText).toList();
    }

    /** True when all product images share the same src — the problem_user symptom
     * (every product shows the same wrong image, though each technically loads). */
    public boolean allImagesIdentical() {
        List<WebElement> images = driver.findElements(PRODUCT_IMAGES);
        return !images.isEmpty() && images.stream()
                .map(img -> img.getAttribute("src"))
                .distinct()
                .count() == 1;
    }

    public void sortBy(String visibleText) {
        new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(SORT_DROPDOWN)))
                .selectByVisibleText(visibleText);
        // Wait for the list to re-render after sorting.
        wait.until(d -> !productNames().isEmpty());
    }

    /** Adds a product to the cart by its exact name. */
    public void addToCart(String productName) {
        String buttonId = "add-to-cart-" + productName.toLowerCase().replaceAll("[^a-z0-9]+", "-");
        wait.until(ExpectedConditions.elementToBeClickable(By.id(buttonId))).click();
    }

    /** The number shown on the cart badge; 0 when the badge is absent. */
    public int cartCount() {
        List<WebElement> badges = driver.findElements(CART_BADGE);
        return badges.isEmpty() ? 0 : Integer.parseInt(badges.get(0).getText());
    }

    public CartPage openCart() {
        wait.until(ExpectedConditions.elementToBeClickable(CART_LINK)).click();
        return new CartPage(driver);
    }
}

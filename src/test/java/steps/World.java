package steps;

import factory.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import pages.CartPage;
import pages.CheckoutPage;
import pages.InventoryPage;
import pages.LoginPage;

/**
 * State shared by every step-definition class in a scenario.
 *
 * <p>PicoContainer creates one {@code World} per scenario and injects it
 * into each step class (and into {@link Hooks}) by constructor, so the
 * login, inventory, cart, and checkout steps all drive the same browser.
 * Page objects are built on demand from the driver — they hold no element
 * state of their own, only locators, so a fresh instance per call is
 * always safe.</p>
 */
public class World {

    /** Swag Labs demo store — the test target for this framework. */
    public static final String BASE_URL = "https://www.saucedemo.com";

    private WebDriver driver;

    /** Starts a fresh Chrome and opens the login page. Called from Hooks. */
    public void startBrowser() {
        driver = WebDriverFactory.create();
        driver.get(BASE_URL);
    }

    /** Quits the browser. Called from Hooks. */
    public void quitBrowser() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    public WebDriver driver() {
        return driver;
    }

    public LoginPage loginPage() {
        return new LoginPage(driver);
    }

    public InventoryPage inventoryPage() {
        return new InventoryPage(driver);
    }

    public CartPage cartPage() {
        return new CartPage(driver);
    }

    public CheckoutPage checkoutPage() {
        return new CheckoutPage(driver);
    }
}

package tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.CartPage;
import pages.InventoryPage;
import pages.LoginPage;
import util.TestData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Cart journeys: added products show up correctly, and removing
 * a product empties the cart.
 */
@Epic("Shopping")
@Feature("Cart")
class CartTest extends BaseTest {

    private InventoryPage inventory;

    @BeforeEach
    void signIn() {
        String[] creds = TestData.user("standard");
        new LoginPage(driver).loginAs(creds[0], creds[1]);
        inventory = new InventoryPage(driver);
    }

    @Test
    @DisplayName("Products added from the products page appear in the cart with correct names and prices")
    void addedProductsAppearInCart() {
        inventory.addToCart("Sauce Labs Backpack");
        inventory.addToCart("Sauce Labs Bike Light");
        CartPage cart = inventory.openCart();

        List<String> names = cart.itemNames();
        assertTrue(names.contains("Sauce Labs Backpack"), "Backpack should be in the cart");
        assertTrue(names.contains("Sauce Labs Bike Light"), "Bike Light should be in the cart");

        List<String> prices = cart.itemPrices();
        assertTrue(prices.contains("$29.99"), "Backpack price should be $29.99");
        assertTrue(prices.contains("$9.99"), "Bike Light price should be $9.99");
    }

    @Test
    @DisplayName("Removing a product from the cart leaves the cart empty")
    void removingProductEmptiesCart() {
        inventory.addToCart("Sauce Labs Backpack");
        CartPage cart = inventory.openCart();
        assertEquals(1, cart.itemCount(), "Cart should have one item before removal");
        cart.removeItem("Sauce Labs Backpack");
        assertEquals(0, cart.itemCount(), "Cart should be empty after removal");
    }
}

package tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.InventoryPage;
import pages.LoginPage;
import util.TestData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Product-listing journeys: the catalog renders, sorting works,
 * and adding a product updates the cart badge.
 */
@Epic("Shopping")
@Feature("Product inventory")
class InventoryTest extends BaseTest {

    private InventoryPage inventory;

    @BeforeEach
    void signIn() {
        String[] creds = TestData.user("standard");
        inventory = new LoginPage(driver).loginAs(creds[0], creds[1]);
    }

    @Test
    @DisplayName("Products page lists products with names and prices")
    void productsHaveNamesAndPrices() {
        List<String> names = inventory.productNames();
        List<String> prices = inventory.productPrices();
        assertFalse(names.isEmpty(), "Product list should not be empty");
        assertEquals(names.size(), prices.size(), "Every product should have a price");
        assertTrue(names.stream().allMatch(n -> !n.isBlank()), "Every product should have a name");
        assertTrue(prices.stream().allMatch(p -> p.startsWith("$")), "Prices should start with $");
    }

    @Test
    @DisplayName("Sorting by name A to Z puts products in alphabetical order")
    void sortByNameAToZ() {
        inventory.sortBy("Name (A to Z)");
        List<String> names = inventory.productNames();
        List<String> sorted = new ArrayList<>(names);
        sorted.sort(String.CASE_INSENSITIVE_ORDER);
        assertEquals(sorted, names, "Products should be in A-to-Z order");
    }

    @Test
    @DisplayName("Sorting by name Z to A puts products in reverse alphabetical order")
    void sortByNameZToA() {
        inventory.sortBy("Name (Z to A)");
        List<String> names = inventory.productNames();
        List<String> sorted = new ArrayList<>(names);
        sorted.sort(String.CASE_INSENSITIVE_ORDER.reversed());
        assertEquals(sorted, names, "Products should be in Z-to-A order");
    }

    @Test
    @DisplayName("Sorting by price low to high puts the cheapest product first")
    void sortByPriceLowToHigh() {
        inventory.sortBy("Price (low to high)");
        List<Double> prices = inventory.productPrices().stream()
                .map(p -> Double.parseDouble(p.replace("$", ""))).toList();
        List<Double> sorted = new ArrayList<>(prices);
        sorted.sort(Comparator.naturalOrder());
        assertEquals(sorted, prices, "Products should be sorted cheapest first");
    }

    @Test
    @DisplayName("Sorting by price high to low puts the most expensive product first")
    void sortByPriceHighToLow() {
        inventory.sortBy("Price (high to low)");
        List<Double> prices = inventory.productPrices().stream()
                .map(p -> Double.parseDouble(p.replace("$", ""))).toList();
        List<Double> sorted = new ArrayList<>(prices);
        sorted.sort(Comparator.reverseOrder());
        assertEquals(sorted, prices, "Products should be sorted most expensive first");
    }

    @Test
    @DisplayName("Adding a product from the products page makes the cart icon show one item")
    void addingProductUpdatesCartBadge() {
        assertEquals(0, inventory.cartCount(), "Cart should start empty");
        inventory.addToCart("Sauce Labs Backpack");
        assertEquals(1, inventory.cartCount(), "Cart badge should show 1 after adding a product");
    }
}

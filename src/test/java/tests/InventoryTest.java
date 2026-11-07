package tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
        new LoginPage(driver).loginAs(creds[0], creds[1]);
        inventory = new InventoryPage(driver);
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

    @ParameterizedTest(name = "{0}")
    @MethodSource("sortOptions")
    @DisplayName("Sort control orders the products correctly")
    void sortingOrdersProducts(String sortOption, boolean byPrice, Comparator<Comparable<?>> expectedOrder) {
        inventory.sortBy(sortOption);
        List<Comparable<?>> displayed = new ArrayList<>();
        if (byPrice) {
            inventory.productPrices().forEach(
                    p -> displayed.add(Double.parseDouble(p.replace("$", ""))));
        } else {
            displayed.addAll(inventory.productNames());
        }
        List<Comparable<?>> sorted = new ArrayList<>(displayed);
        sorted.sort(expectedOrder);
        assertEquals(sorted, displayed, "Products should be in " + sortOption + " order");
    }

    static Stream<Arguments> sortOptions() {
        Comparator<Comparable<?>> nameAsc =
                (a, b) -> ((String) a).compareToIgnoreCase((String) b);
        Comparator<Comparable<?>> nameDesc = nameAsc.reversed();
        Comparator<Comparable<?>> priceAsc =
                (a, b) -> Double.compare((Double) a, (Double) b);
        Comparator<Comparable<?>> priceDesc = priceAsc.reversed();
        return Stream.of(
                Arguments.of("Name (A to Z)", false, nameAsc),
                Arguments.of("Name (Z to A)", false, nameDesc),
                Arguments.of("Price (low to high)", true, priceAsc),
                Arguments.of("Price (high to low)", true, priceDesc));
    }

    @Test
    @DisplayName("Adding a product from the products page makes the cart icon show one item")
    void addingProductUpdatesCartBadge() {
        assertEquals(0, inventory.cartCount(), "Cart should start empty");
        inventory.addToCart("Sauce Labs Backpack");
        assertEquals(1, inventory.cartCount(), "Cart badge should show 1 after adding a product");
    }
}

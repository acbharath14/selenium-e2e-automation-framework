package steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import pages.InventoryPage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Steps for the product-listing journeys.
 */
public class InventorySteps {

    private final World world;

    public InventorySteps(World world) {
        this.world = world;
    }

    @Then("every product should have a name and a price")
    public void everyProductShouldHaveANameAndAPrice() {
        InventoryPage inventory = world.inventoryPage();
        List<String> names = inventory.productNames();
        List<String> prices = inventory.productPrices();
        assertFalse(names.isEmpty(), "Product list should not be empty");
        assertEquals(names.size(), prices.size(), "Every product should have a price");
        assertTrue(names.stream().allMatch(n -> !n.isBlank()), "Every product should have a name");
        assertTrue(prices.stream().allMatch(p -> p.startsWith("$")), "Prices should start with $");
    }

    @When("I sort the products by {string}")
    public void iSortTheProductsBy(String sortOption) {
        world.inventoryPage().sortBy(sortOption);
    }

    @Then("the products should be listed in {string} order")
    public void theProductsShouldBeListedInOrder(String sortOption) {
        InventoryPage inventory = world.inventoryPage();
        switch (sortOption) {
            case "Name (A to Z)" -> {
                List<String> names = inventory.productNames();
                List<String> sorted = new ArrayList<>(names);
                sorted.sort(String.CASE_INSENSITIVE_ORDER);
                assertEquals(sorted, names, "Products should be in A-to-Z order");
            }
            case "Name (Z to A)" -> {
                List<String> names = inventory.productNames();
                List<String> sorted = new ArrayList<>(names);
                sorted.sort(String.CASE_INSENSITIVE_ORDER.reversed());
                assertEquals(sorted, names, "Products should be in Z-to-A order");
            }
            case "Price (low to high)" -> {
                List<Double> prices = inventory.productPrices().stream()
                        .map(p -> Double.parseDouble(p.replace("$", ""))).toList();
                List<Double> sorted = new ArrayList<>(prices);
                sorted.sort(Comparator.naturalOrder());
                assertEquals(sorted, prices, "Products should be sorted cheapest first");
            }
            case "Price (high to low)" -> {
                List<Double> prices = inventory.productPrices().stream()
                        .map(p -> Double.parseDouble(p.replace("$", ""))).toList();
                List<Double> sorted = new ArrayList<>(prices);
                sorted.sort(Comparator.reverseOrder());
                assertEquals(sorted, prices, "Products should be sorted most expensive first");
            }
            default -> throw new IllegalArgumentException("Unknown sort option: " + sortOption);
        }
    }

    @Given("the cart is empty")
    public void theCartIsEmpty() {
        assertEquals(0, world.inventoryPage().cartCount(), "Cart should start empty");
    }

    @When("I add {string} to the cart")
    public void iAddToTheCart(String productName) {
        world.inventoryPage().addToCart(productName);
    }

    @Then("the cart icon should show {int} item")
    public void theCartIconShouldShowItems(int count) {
        assertEquals(count, world.inventoryPage().cartCount(),
                "Cart badge should show " + count + " after adding a product");
    }
}

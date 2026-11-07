package steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import pages.CartPage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Steps for the cart journeys.
 */
public class CartSteps {

    private final World world;

    public CartSteps(World world) {
        this.world = world;
    }

    @When("I open the cart")
    public void iOpenTheCart() {
        world.inventoryPage().openCart();
    }

    @Then("the cart should contain {string}")
    public void theCartShouldContain(String productName) {
        List<String> names = world.cartPage().itemNames();
        assertTrue(names.contains(productName), productName + " should be in the cart");
    }

    @Then("the cart should show the price {string}")
    public void theCartShouldShowThePrice(String price) {
        List<String> prices = world.cartPage().itemPrices();
        assertTrue(prices.contains(price), "Cart should show the price " + price);
    }

    @Then("the cart should contain {int} item")
    public void theCartShouldContainItems(int count) {
        assertEquals(count, world.cartPage().itemCount(), "Cart should have " + count + " item(s)");
    }

    @When("I remove {string} from the cart")
    public void iRemoveFromTheCart(String productName) {
        world.cartPage().removeItem(productName);
    }

    @Then("the cart should be empty")
    public void theCartShouldBeEmpty() {
        assertEquals(0, world.cartPage().itemCount(), "Cart should be empty after removal");
    }
}

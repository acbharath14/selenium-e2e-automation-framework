package steps;

import com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pages.CheckoutPage;
import util.TestData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Steps for the checkout journeys. The items to buy and the checkout
 * form values come from {@code src/test/resources/products.json},
 * as the JUnit tests read them.
 */
public class CheckoutSteps {

    private final World world;

    public CheckoutSteps(World world) {
        this.world = world;
    }

    @When("I add the checkout items to the cart")
    public void iAddTheCheckoutItemsToTheCart() {
        JsonNode data = TestData.checkout();
        for (JsonNode item : data.get("items")) {
            world.inventoryPage().addToCart(item.asText());
        }
    }

    @When("I start checkout")
    public void iStartCheckout() {
        world.cartPage().startCheckout();
    }

    @When("I fill in the checkout information")
    public void iFillInTheCheckoutInformation() {
        JsonNode data = TestData.checkout();
        world.checkoutPage().fillInformation(
                data.get("firstName").asText(),
                data.get("lastName").asText(),
                data.get("postalCode").asText());
    }

    @Then("the order summary should show {string}, {string}, and {string}")
    public void theOrderSummaryShouldShow(String subtotal, String tax, String total) {
        CheckoutPage checkout = world.checkoutPage();
        assertEquals(subtotal, checkout.subtotalText());
        assertEquals(tax, checkout.taxText());
        assertEquals(total, checkout.totalText());
    }

    @When("I finish the order")
    public void iFinishTheOrder() {
        world.checkoutPage().finishOrder();
    }

    @Then("I should see the order confirmation page")
    public void iShouldSeeTheOrderConfirmationPage() {
        assertTrue(world.checkoutPage().confirmationHeader().toLowerCase().contains("thank you"),
                "Confirmation page should thank the buyer, got: " + world.checkoutPage().confirmationHeader());
    }
}

package tests;

import com.fasterxml.jackson.databind.JsonNode;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.CartPage;
import pages.CheckoutPage;
import pages.InventoryPage;
import pages.LoginPage;
import util.TestData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Checkout journeys: the order summary shows correct totals,
 * and a completed order lands on the confirmation page.
 */
@Epic("Shopping")
@Feature("Checkout")
class CheckoutTest extends BaseTest {

    private CheckoutPage checkout;

    @BeforeEach
    void signInAndAddItems() {
        String[] creds = TestData.user("standard");
        InventoryPage inventory = new LoginPage(driver).loginAs(creds[0], creds[1]);
        JsonNode data = TestData.checkout();
        for (JsonNode item : data.get("items")) {
            inventory.addToCart(item.asText());
        }
        CartPage cart = inventory.openCart();
        checkout = cart.startCheckout();
        checkout.fillInformation(
                data.get("firstName").asText(),
                data.get("lastName").asText(),
                data.get("postalCode").asText());
    }

    @Test
    @DisplayName("Checkout summary shows $39.98 subtotal, $3.20 tax, and $43.18 total before the order is placed")
    void summaryShowsCorrectTotals() {
        JsonNode data = TestData.checkout();
        assertEquals(data.get("expectedSubtotal").asText(), checkout.subtotalText());
        assertEquals(data.get("expectedTax").asText(), checkout.taxText());
        assertEquals(data.get("expectedTotal").asText(), checkout.totalText());
    }

    @Test
    @DisplayName("Completing checkout with valid details shows the order confirmation page")
    void completingCheckoutShowsConfirmation() {
        checkout.finishOrder();
        assertTrue(checkout.confirmationHeader().toLowerCase().contains("thank you"),
                "Confirmation page should thank the buyer, got: " + checkout.confirmationHeader());
    }
}

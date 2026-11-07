package steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pages.InventoryPage;
import util.TestData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Steps for the login journeys. Credentials come from
 * {@code src/test/resources/users.json}, looked up by the user label
 * used in the feature files.
 */
public class LoginSteps {

    private final World world;

    public LoginSteps(World world) {
        this.world = world;
    }

    @Given("I am on the Swag Labs login page")
    public void iAmOnTheLoginPage() {
        // The Hooks @Before already opened the login page; confirm we are there.
        assertTrue(world.driver().getCurrentUrl().contains("saucedemo.com"),
                "Should be on the Swag Labs login page, got: " + world.driver().getCurrentUrl());
    }

    @Given("I am signed in as the standard user")
    public void iAmSignedInAsTheStandardUser() {
        String[] creds = TestData.user("standard");
        world.loginPage().loginAs(creds[0], creds[1]);
        // Waits for the products page title, mirroring the old @BeforeEach sign-in.
        world.inventoryPage();
    }

    @When("I sign in as {string}")
    public void iSignInAs(String userLabel) {
        String key;
        switch (userLabel) {
            case "standard" -> key = "standard";
            case "locked-out" -> key = "lockedOut";
            case "problem" -> key = "problem";
            case "performance glitch" -> key = "performanceGlitch";
            case "invalid" -> key = "invalid";
            default -> throw new IllegalArgumentException("Unknown user label: " + userLabel);
        }
        String[] creds = TestData.user(key);
        world.loginPage().loginAs(creds[0], creds[1]);
    }

    @Then("I should see the products page")
    public void iShouldSeeTheProductsPage() {
        InventoryPage inventory = world.inventoryPage();
        assertEquals("Products", inventory.pageTitle(),
                "User should land on the products page after login");
    }

    @Then("I should see the locked-out message")
    public void iShouldSeeTheLockedOutMessage() {
        assertTrue(world.loginPage().errorMessage().contains("locked out"),
                "Locked-out user should see the locked-out message, got: " + world.loginPage().errorMessage());
    }

    @Then("I should see the products page with broken images")
    public void iShouldSeeTheProductsPageWithBrokenImages() {
        InventoryPage inventory = world.inventoryPage();
        assertEquals("Products", inventory.pageTitle());
        assertTrue(inventory.allImagesIdentical(),
                "Problem user is known to see the same wrong image for all products");
    }

    @Then("I should see a credentials mismatch error")
    public void iShouldSeeACredentialsMismatchError() {
        assertTrue(world.loginPage().errorMessage().contains("do not match"),
                "Wrong credentials should show a mismatch error, got: " + world.loginPage().errorMessage());
    }
}

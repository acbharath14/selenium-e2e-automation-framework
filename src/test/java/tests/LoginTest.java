package tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.InventoryPage;
import pages.LoginPage;
import util.TestData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Login journeys against the Swag Labs demo store.
 * Credentials come from src/test/resources/users.json.
 */
@Epic("Authentication")
@Feature("Login")
class LoginTest extends BaseTest {

    @Test
    @DisplayName("Standard user signs in successfully and sees the products page")
    void standardUserSignsInSuccessfully() {
        String[] creds = TestData.user("standard");
        InventoryPage inventory = new LoginPage(driver).loginAs(creds[0], creds[1]);
        assertEquals("Products", inventory.pageTitle(),
                "Standard user should land on the products page after login");
    }

    @Test
    @DisplayName("Locked-out user is blocked and sees the locked-out message")
    void lockedOutUserSeesBlockedMessage() {
        String[] creds = TestData.user("lockedOut");
        LoginPage login = new LoginPage(driver);
        login.loginAs(creds[0], creds[1]);
        assertTrue(login.errorMessage().contains("locked out"),
                "Locked-out user should see the locked-out message, got: " + login.errorMessage());
    }

    @Test
    @DisplayName("Problem user signs in and sees the products page with broken product images")
    void problemUserSeesBrokenImages() {
        String[] creds = TestData.user("problem");
        InventoryPage inventory = new LoginPage(driver).loginAs(creds[0], creds[1]);
        assertEquals("Products", inventory.pageTitle());
        assertFalse(inventory.allImagesLoaded(),
                "Problem user is known to see broken product images");
    }

    @Test
    @DisplayName("Slow user signs in successfully and sees the products page")
    void slowUserSignsInSuccessfully() {
        String[] creds = TestData.user("performanceGlitch");
        InventoryPage inventory = new LoginPage(driver).loginAs(creds[0], creds[1]);
        assertEquals("Products", inventory.pageTitle(),
                "Slow user should eventually land on the products page");
    }

    @Test
    @DisplayName("Wrong username or password is rejected with an error message")
    void wrongCredentialsAreRejected() {
        String[] creds = TestData.user("invalid");
        LoginPage login = new LoginPage(driver);
        login.loginAs(creds[0], creds[1]);
        assertTrue(login.errorMessage().contains("do not match"),
                "Wrong credentials should show a mismatch error, got: " + login.errorMessage());
    }
}

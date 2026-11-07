package tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import pages.InventoryPage;
import pages.LoginPage;
import util.TestData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Login journeys against the Swag Labs demo store.
 * Credentials come from src/test/resources/users.json.
 */
@Epic("Authentication")
@Feature("Login")
class LoginTest extends BaseTest {

    @ParameterizedTest(name = "user key: {0}")
    @MethodSource("successfulLoginUsers")
    @DisplayName("Valid users sign in successfully and see the products page")
    void validUsersSignInSuccessfully(String userKey) {
        String[] creds = TestData.user(userKey);
        new LoginPage(driver).loginAs(creds[0], creds[1]);
        InventoryPage inventory = new InventoryPage(driver);
        assertEquals("Products", inventory.pageTitle(),
                "User " + userKey + " should land on the products page after login");
    }

    static Stream<String> successfulLoginUsers() {
        return Stream.of("standard", "performanceGlitch");
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
        new LoginPage(driver).loginAs(creds[0], creds[1]);
        InventoryPage inventory = new InventoryPage(driver);
        assertEquals("Products", inventory.pageTitle());
        assertTrue(inventory.allImagesIdentical(),
                "Problem user is known to see the same wrong image for all products");
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

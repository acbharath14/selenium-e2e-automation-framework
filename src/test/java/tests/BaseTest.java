package tests;

import factory.WebDriverFactory;
import io.qameta.allure.Allure;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Base class for every test in this framework.
 *
 * <ul>
 *   <li>Starts a fresh Chrome before each test and quits it afterwards —
 *       tests never share browser state.</li>
 *   <li>On failure, saves a screenshot to {@code target/screenshots/} and
 *       attaches it to the Allure report (via the {@link ScreenshotOnFailure}
 *       watcher below).</li>
 *   <li>Exposes {@link #explicitWait()} — a 10-second explicit wait every page
 *       object and test should use instead of {@code Thread.sleep}.</li>
 * </ul>
 */
@ExtendWith(BaseTest.ScreenshotOnFailure.class)
public abstract class BaseTest {

    /** Swag Labs demo store — the test target for this framework. */
    protected static final String BASE_URL = "https://www.saucedemo.com";

    protected WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        driver = WebDriverFactory.create();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(BASE_URL);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /** Explicit wait shared by page objects and tests — prefer this over sleeps. */
    protected WebDriverWait explicitWait() {
        return wait;
    }

    /**
     * JUnit 5 watcher that captures a screenshot when a test fails.
     * Runs after test execution but before {@code @AfterEach}, so the
     * browser is still open.
     */
    static class ScreenshotOnFailure implements AfterTestExecutionCallback {

        @Override
        public void afterTestExecution(ExtensionContext context) {
            if (context.getExecutionException().isEmpty()) {
                return; // test passed — nothing to capture
            }
            Object instance = context.getRequiredTestInstance();
            if (!(instance instanceof BaseTest baseTest) || baseTest.driver == null) {
                return;
            }
            if (!(baseTest.driver instanceof TakesScreenshot screenshotter)) {
                return;
            }
            byte[] png = screenshotter.getScreenshotAs(OutputType.BYTES);
            String testName = context.getTestMethod()
                    .map(m -> m.getName())
                    .orElse("unknown-test");
            saveScreenshotFile(testName, png);
            Allure.addAttachment("Failure screenshot", "image/png",
                    new ByteArrayInputStream(png), ".png");
        }

        private static void saveScreenshotFile(String testName, byte[] png) {
            try {
                Path dir = Paths.get("target", "screenshots");
                Files.createDirectories(dir);
                String safeName = testName.replaceAll("[^a-zA-Z0-9-_]", "_");
                Files.write(dir.resolve(safeName + ".png"), png);
            } catch (IOException e) {
                System.err.println("Could not save failure screenshot: " + e.getMessage());
            }
        }
    }
}

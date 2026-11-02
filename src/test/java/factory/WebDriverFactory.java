package factory;

import java.util.HashMap;
import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * Creates WebDriver instances.
 *
 * <p>Driver binaries come from Selenium Manager (built into Selenium 4) —
 * no manual downloads and no extra driver-manager dependency. Selenium
 * Manager detects the installed Chrome and fetches a matching driver on
 * first use.</p>
 *
 * <p>Headless mode is controlled by the {@code headless} system property:
 * {@code mvn test -Dheadless=true}. Useful for CI where there is no display.</p>
 */
public final class WebDriverFactory {

    private WebDriverFactory() {
    }

    public static WebDriver create() {
        ChromeOptions options = new ChromeOptions();
        if (Boolean.parseBoolean(System.getProperty("headless", "false"))) {
            options.addArguments("--headless=new");
        }
        // CI-friendly flags: no sandbox issues in containers, stable window size.
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--window-size=1440,900");
        // Suppress Chrome's "password was leaked in a data breach" warning, which
        // blocks interaction with saucedemo.com's login page until dismissed.
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.password_manager_leak_detection", false);
        options.setExperimentalOption("prefs", prefs);
        return new ChromeDriver(options);
    }
}

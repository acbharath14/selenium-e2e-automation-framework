package steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

/**
 * Scenario lifecycle: a fresh Chrome per scenario, screenshot on failure.
 * This is the Cucumber form of the lifecycle the JUnit tests had before
 * the move to scenarios — same browser-per-scenario rule, same
 * screenshot location.
 */
public class Hooks {

    private final World world;

    public Hooks(World world) {
        this.world = world;
    }

    @Before
    public void setUp() {
        world.startBrowser();
    }

    /**
     * Screenshot on failure. Runs before the quit hook (Cucumber runs
     * {@code @After} hooks in descending order), so the browser is
     * still open when the screenshot is taken.
     */
    @After(order = 10)
    public void screenshotOnFailure(Scenario scenario) {
        if (!scenario.isFailed() || !(world.driver() instanceof TakesScreenshot screenshotter)) {
            return;
        }
        byte[] png = screenshotter.getScreenshotAs(OutputType.BYTES);
        String safeName = scenario.getName().replaceAll("[^a-zA-Z0-9-_]", "_");
        saveScreenshotFile(safeName, png);
        Allure.addAttachment("Failure screenshot", "image/png",
                new ByteArrayInputStream(png), ".png");
    }

    @After(order = 1)
    public void tearDown() {
        world.quitBrowser();
    }

    private static void saveScreenshotFile(String safeName, byte[] png) {
        try {
            Path dir = Paths.get("target", "screenshots");
            Files.createDirectories(dir);
            Files.write(dir.resolve(safeName + ".png"), png);
        } catch (IOException e) {
            System.err.println("Could not save failure screenshot: " + e.getMessage());
        }
    }
}

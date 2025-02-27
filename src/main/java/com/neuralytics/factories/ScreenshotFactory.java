package com.neuralytics.factories;

import com.neuralytics.utils.LoggerUtil;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * A utility class for capturing and saving screenshots in a Selenium-based
 * testing framework.
 * Provides static methods to capture full-page screenshots via
 * {@link WebDriver} and element-specific
 * screenshots via {@link WebElement}, saving them to a designated directory
 * ("./screenshots/") with
 * timestamped filenames. Integrates with {@link ReportFactory} to log failures
 * when screenshot
 * operations encounter errors.
 *
 * <p>
 * This class is designed as a non-instantiable utility with thread-safe file
 * operations. The screenshot
 * directory is automatically created on class initialization if it does not
 * exist.
 *
 * <p>
 * Usage example:
 * 
 * <pre>
 * WebDriver driver = DriverFactory.getDriver();
 * String screenshotPath = ScreenshotFactory.captureFullPageScreenshot(driver, "LoginTest");
 * if (screenshotPath != null) {
 *     ReportFactory.getInstance().logInfo("Screenshot saved: " + screenshotPath);
 * }
 * </pre>
 *
 * <p>
 * Supported screenshot types include full-page (via WebDriver) and
 * element-specific (via WebElement).
 * Filenames are formatted as "{testName}_{type}_{timestamp}.png" (e.g.,
 * "LoginTest_fullPage_20250226-123456.png").
 */
public class ScreenshotFactory {

    /**
     * Logger instance for tracing screenshot operations and logging errors.
     */
    private static final Logger logger = LoggerUtil.getLogger(ScreenshotFactory.class);

    /**
     * The directory where screenshots are saved (relative path: "./screenshots/").
     */
    private static final String SCREENSHOT_DIR = "./screenshots/";

    /**
     * Date format used for timestamping screenshot filenames (e.g.,
     * "yyyyMMdd-HHmmss").
     */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd-HHmmss");

    /**
     * Static initializer to ensure the screenshot directory exists upon class
     * loading.
     */
    static {
        createScreenshotDirectory();
    }

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException if instantiation is attempted
     */
    private ScreenshotFactory() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated.");
    }

    /**
     * Creates the screenshot directory if it does not already exist.
     * Logs the operation’s success or failure. This method is called automatically
     * during class
     * initialization and before saving screenshots.
     */
    private static void createScreenshotDirectory() {
        File directory = new File(SCREENSHOT_DIR);
        logger.trace("Initializing screenshot directory at: {}", directory.getAbsolutePath());

        if (directory.exists()) {
            logger.debug("Screenshot directory already exists: {}", directory.getAbsolutePath());
            return;
        }

        if (directory.mkdirs()) {
            logger.info("Created screenshot directory at: {}", directory.getAbsolutePath());
        } else {
            logger.error("Failed to create screenshot directory at: {}", directory.getAbsolutePath());
        }
    }

    /**
     * Captures a full-page screenshot using the provided WebDriver instance.
     * Saves the screenshot to the screenshot directory with a filename
     * incorporating the test name
     * and timestamp. Requires the WebDriver to implement {@link TakesScreenshot},
     * which is typical
     * for local drivers (e.g., ChromeDriver) but must be verified for remote
     * setups.
     *
     * @param driver   the WebDriver instance to capture the screenshot from
     * @param testName the name of the test, used in the screenshot filename
     * @return the absolute path of the saved screenshot file, or null if the
     *         capture fails
     * @throws NullPointerException if driver or testName is null
     * @see ReportFactory#logFail(String)
     */
    public static String captureFullPageScreenshot(WebDriver driver, String testName) {
        Objects.requireNonNull(driver, "WebDriver cannot be null.");
        Objects.requireNonNull(testName, "Test name cannot be null.");

        try {
            if (!(driver instanceof TakesScreenshot)) {
                logger.error(
                        "Driver does not support taking screenshots: {}. Ensure you are using a compatible driver (e.g., ChromeDriver, FirefoxDriver) or have configured remote execution correctly.",
                        driver.getClass().getName());
                return null;
            }

            File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            return saveScreenshot(screenshotFile, testName, "fullPage");
        } catch (Exception e) {
            String errorMessage = String.format("Failed to capture full-page screenshot for test '%s': %s",
                    testName, e.getMessage());
            logger.error(errorMessage, e);
            ReportFactory.getInstance().logFail(errorMessage + " (Full Page)");
            return null;
        }
    }

    /**
     * Captures a screenshot of a specific WebElement.
     * Saves the screenshot to the screenshot directory with a filename
     * incorporating the test name
     * and timestamp. The WebElement must support screenshot capture (typically
     * available in modern
     * Selenium implementations).
     *
     * @param element  the WebElement to capture
     * @param testName the name of the test, used in the screenshot filename
     * @return the absolute path of the saved screenshot file, or null if the
     *         capture fails
     * @throws NullPointerException if element or testName is null
     * @see ReportFactory#logFail(String)
     */
    public static String captureElementScreenshot(WebElement element, String testName) {
        Objects.requireNonNull(element, "WebElement cannot be null.");
        Objects.requireNonNull(testName, "Test name cannot be null.");

        try {
            File screenshotFile = element.getScreenshotAs(OutputType.FILE);
            return saveScreenshot(screenshotFile, testName, "element");
        } catch (Exception e) {
            String errorMessage = String.format("Failed to capture element screenshot for test '%s': %s",
                    testName, e.getMessage());
            logger.error(errorMessage, e);
            ReportFactory.getInstance().logFail(errorMessage + " (Element)");
            return null;
        }
    }

    /**
     * Saves a screenshot file to the designated directory with a generated
     * filename.
     * This method is synchronized to prevent concurrent file write conflicts in
     * parallel test execution.
     * The filename format is "{testName}_{type}_{timestamp}.png", with
     * non-alphanumeric characters in
     * testName sanitized to underscores.
     *
     * @param screenshotFile the temporary screenshot file to save
     * @param testName       the name of the test, incorporated into the filename
     * @param type           the type of screenshot (e.g., "fullPage", "element")
     * @return the absolute path of the saved screenshot file, or null if saving
     *         fails
     * @throws NullPointerException if screenshotFile, testName, or type is null
     * @throws IOException          if the file cannot be copied to the destination
     * @see #createScreenshotDirectory()
     */
    private static synchronized String saveScreenshot(File screenshotFile, String testName, String type) {
        Objects.requireNonNull(screenshotFile, "Screenshot file cannot be null.");
        Objects.requireNonNull(testName, "Test name cannot be null.");
        Objects.requireNonNull(type, "Screenshot type cannot be null.");

        try {
            createScreenshotDirectory();

            String timestamp = DATE_FORMAT.format(new Date());
            String sanitizedTestName = testName.replaceAll("[^a-zA-Z0-9_-]", "_");
            String fileName = String.format("%s_%s_%s.png", sanitizedTestName, type, timestamp);

            File directory = new File(SCREENSHOT_DIR);
            File destFile = new File(directory, fileName);

            FileUtils.copyFile(screenshotFile, destFile);
            logger.info("Successfully saved screenshot to: {}", destFile.getAbsolutePath());
            return destFile.getAbsolutePath();
        } catch (IOException e) {
            String errorMessage = String.format("Failed to save screenshot for test '%s': %s", testName,
                    e.getMessage());
            logger.error(errorMessage, e);
            ReportFactory.getInstance().logFail(errorMessage + " (" + type + ")");
            return null;
        }
    }
}
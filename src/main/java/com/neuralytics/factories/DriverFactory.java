package com.neuralytics.factories;

import com.neuralytics.enums.BrowserType;
import com.neuralytics.exceptions.BrowserException;
import com.neuralytics.utils.ConfigLoader;
import com.neuralytics.utils.LoggerUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.slf4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * A factory class for creating and managing Selenium WebDriver instances.
 * This class provides thread-safe initialization of WebDriver instances based
 * on configuration properties,
 * supporting both local and remote (Selenium Grid) execution. It handles
 * browser selection, window sizing,
 * and lifecycle management (e.g., quitting the driver). Configuration is driven
 * by properties loaded via
 * {@link ConfigLoader}, with support for browsers defined in
 * {@link BrowserType}.
 *
 * <p>
 * Usage example:
 * 
 * <pre>
 * WebDriver driver = DriverFactory.initDriver();
 * DriverFactory.navigateToUrl("https://example.com");
 * // Perform testing actions
 * DriverFactory.quitDriver();
 * </pre>
 *
 * <p>
 * This class is designed to be thread-safe using {@link ThreadLocal} to store
 * WebDriver instances,
 * making it suitable for parallel test execution.
 */
public class DriverFactory {

    /**
     * Thread-local storage for WebDriver instances, ensuring thread safety during
     * parallel execution.
     */
    private static final ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();

    /**
     * Logger instance for recording initialization, navigation, and error events.
     */
    private static final Logger logger = LoggerUtil.getLogger(DriverFactory.class);

    /**
     * Manager for browser-specific options, initialized with configuration
     * properties.
     */
    private static OptionsManager optionsManager;

    /**
     * Initializes a WebDriver instance based on configuration properties.
     * The browser type, remote execution flag, and window size are determined from
     * properties loaded via
     * {@link ConfigLoader}. Supports local browsers (Chrome, Firefox, Edge, Safari)
     * and remote execution
     * via Selenium Grid. The initialized driver is stored in thread-local storage
     * and configured with
     * cookies cleared and window size set (maximized or custom dimensions if
     * specified).
     *
     * @return the initialized WebDriver instance
     * @throws BrowserException if the browser type is invalid, remote hub URL is
     *                          missing or malformed,
     *                          or window size dimensions are incorrectly formatted
     * @see ConfigLoader#loadProperties()
     * @see BrowserType
     */
    public static WebDriver initDriver() {
        final Properties prop = ConfigLoader.loadProperties();
        final String browserName = prop.getProperty("browser", "chrome").toLowerCase().trim();
        final boolean isRemote = Boolean.parseBoolean(prop.getProperty("remote", "false"));

        optionsManager = new OptionsManager(prop);

        final BrowserType browserType;
        try {
            browserType = BrowserType.valueOf(browserName.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid browser name specified in properties: {}", browserName, e);
            throw new BrowserException("Invalid browser name specified in properties: " + browserName, e);
        }

        logger.info("Initializing driver for browser: {}, remote: {}", browserType, isRemote);

        WebDriver driver;
        if (isRemote) {
            driver = initRemoteDriver(browserType, prop);
        } else {
            driver = initLocalDriver(browserType);
        }

        driver.manage().deleteAllCookies();
        String windowSize = prop.getProperty("window_size");
        if (windowSize != null && !windowSize.isBlank()
                && Boolean.parseBoolean(prop.getProperty("advanced_mode", "false"))) {
            String[] dimensions = windowSize.split(",");
            if (dimensions.length == 2) {
                try {
                    int width = Integer.parseInt(dimensions[0].trim());
                    int height = Integer.parseInt(dimensions[1].trim());
                    driver.manage().window().setSize(new org.openqa.selenium.Dimension(width, height));
                } catch (NumberFormatException e) {
                    logger.warn("Invalid window_size format: {}. Using maximize instead.", windowSize);
                    driver.manage().window().maximize();
                }
            } else {
                driver.manage().window().maximize();
            }
        } else {
            driver.manage().window().maximize();
        }
        tlDriver.set(driver);
        return driver;
    }

    /**
     * Initializes a local WebDriver instance for the specified browser type.
     * Uses browser-specific options provided by {@link OptionsManager} to configure
     * the driver.
     *
     * @param browserType the type of browser to initialize (e.g., CHROME, FIREFOX,
     *                    EDGE, SAFARI)
     * @return the initialized local WebDriver instance
     * @throws IllegalArgumentException if the browser type is not supported (though
     *                                  handled by enum)
     * @see OptionsManager#getBrowserOptions(String)
     */
    private static WebDriver initLocalDriver(final BrowserType browserType) {
        logger.info("Initializing local driver for browser: {}", browserType);
        return switch (browserType) {
            case CHROME -> new ChromeDriver((ChromeOptions) optionsManager.getBrowserOptions(browserType.name()));
            case FIREFOX -> new FirefoxDriver((FirefoxOptions) optionsManager.getBrowserOptions(browserType.name()));
            case EDGE -> new EdgeDriver((EdgeOptions) optionsManager.getBrowserOptions(browserType.name()));
            case SAFARI -> new SafariDriver((SafariOptions) optionsManager.getBrowserOptions(browserType.name()));
        };
    }

    /**
     * Initializes a remote WebDriver instance using Selenium Grid for the specified
     * browser type.
     * Requires a valid hub URL from the configuration properties to connect to the
     * Selenium Grid.
     *
     * @param browserType the type of browser to initialize remotely (e.g., CHROME,
     *                    FIREFOX, EDGE, SAFARI)
     * @param prop        the configuration properties containing the hub URL
     * @return the initialized remote WebDriver instance
     * @throws BrowserException if the hub URL is missing, blank, or malformed
     * @see OptionsManager#getBrowserOptions(String)
     */
    private static WebDriver initRemoteDriver(final BrowserType browserType, final Properties prop) {
        final String hubUrlStr = prop.getProperty("hubUrl");
        if (hubUrlStr == null || hubUrlStr.isBlank()) {
            logger.error("Remote execution selected but no hub URL provided in properties.");
            throw new BrowserException("Remote execution selected but no hub URL provided in properties.");
        }

        try {
            final URL hubUrl = new URL(hubUrlStr);
            logger.info("Initializing remote driver for browser: {} with hub URL: {}", browserType, hubUrl);
            return new RemoteWebDriver(hubUrl, optionsManager.getBrowserOptions(browserType.name()));
        } catch (MalformedURLException e) {
            logger.error("Invalid Selenium Grid Hub URL: {}", hubUrlStr, e);
            throw new BrowserException("Invalid Selenium Grid Hub URL: " + hubUrlStr, e);
        }
    }

    /**
     * Navigates the current WebDriver instance to the specified URL.
     *
     * @param url the URL to navigate to
     * @throws BrowserException      if the URL is null or blank
     * @throws IllegalStateException if no WebDriver instance is initialized
     * @see #getDriver()
     */
    public static void navigateToUrl(String url) {
        if (url == null || url.isBlank()) {
            logger.error("No URL specified");
            throw new BrowserException("No URL specified");
        }
        logger.info("Navigating to URL: {}", url);
        getDriver().get(url);
    }

    /**
     * Retrieves the WebDriver instance associated with the current thread.
     *
     * @return the current thread's WebDriver instance, or null if not initialized
     * @throws IllegalStateException if called before {@link #initDriver()} on the
     *                               current thread
     */
    public static WebDriver getDriver() {
        return tlDriver.get();
    }

    /**
     * Quits the current WebDriver instance and removes it from thread-local
     * storage.
     * Safe to call even if no driver is initialized (no-op in that case).
     *
     * @see #getDriver()
     */
    public static void quitDriver() {
        final WebDriver driver = tlDriver.get();
        if (driver != null) {
            logger.info("Quitting driver");
            driver.quit();
            tlDriver.remove();
        }
    }
}
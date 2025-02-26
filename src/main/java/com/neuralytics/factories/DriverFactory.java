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
import org.slf4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

public class DriverFactory {

    // ThreadLocal ensures that each thread gets its own WebDriver instance.
    private static final ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();
    private static final Logger logger = LoggerUtil.getLogger(DriverFactory.class);
    private static OptionsManager optionsManager;

    /**
     * Initializes the WebDriver instance based on the configuration properties.
     *
     * @return the initialized WebDriver.
     */
    public static WebDriver initDriver() {
        final Properties prop = ConfigLoader.loadProperties();
        // Get browser name (defaulting to "chrome") and remote flag.
        final String browserName = prop.getProperty("browser", "chrome").toLowerCase().trim();
        final boolean isRemote = Boolean.parseBoolean(prop.getProperty("remote", "false"));

        // Initialize the options manager with loaded properties.
        optionsManager = new OptionsManager(prop);

        final BrowserType browserType;
        try {
            browserType = BrowserType.valueOf(browserName.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid browser name specified in properties: {}", browserName, e);
            throw new BrowserException("Invalid browser name specified in properties: " + browserName, e);
        }

        logger.info("Initializing driver for browser: {}, remote: {}", browserType, isRemote);

        // Initialize local or remote driver based on the configuration.
        WebDriver driver;
        if (isRemote) {
            driver = initRemoteDriver(browserType, prop);
        } else {
            driver = initLocalDriver(browserType);
        }

        // Post driver initialization steps.
        driver.manage().deleteAllCookies();
        driver.manage().window().maximize();
        tlDriver.set(driver);
        return driver;
    }

    /**
     * Initializes a local WebDriver based on the specified browser type.
     *
     * @param browserType The type of browser to initialize.
     */
    private static WebDriver initLocalDriver(final BrowserType browserType) {
        logger.info("Initializing local driver for browser: {}", browserType);
        WebDriver driver;
        switch (browserType) {
            case CHROME:
                driver = new ChromeDriver((ChromeOptions) optionsManager.getBrowserOptions(browserType.name()));
                break;
            case FIREFOX:
                driver = new FirefoxDriver((FirefoxOptions) optionsManager.getBrowserOptions(browserType.name()));
                break;
            case EDGE:
                driver = new EdgeDriver((EdgeOptions) optionsManager.getBrowserOptions(browserType.name()));
                break;
            case SAFARI:
                driver = new SafariDriver();
                break;
            default:
                logger.error("Unsupported browser type: {}", browserType);
                throw new BrowserException("Unsupported browser type: " + browserType);
        }
        return driver;
    }

    /**
     * Initializes a remote WebDriver using Selenium Grid.
     *
     * @param browserType The type of browser to initialize.
     * @param prop        The loaded configuration properties.
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
            // For remote, the options manager returns the appropriate capabilities.
            return new RemoteWebDriver(hubUrl, optionsManager.getBrowserOptions(browserType.name()));
        } catch (MalformedURLException e) {
            logger.error("Invalid Selenium Grid Hub URL: {}", hubUrlStr, e);
            throw new BrowserException("Invalid Selenium Grid Hub URL: " + hubUrlStr, e);
        }
    }

    public static void navigateToUrl(String url) {
        if (url == null || url.isBlank()) {
            logger.error("No URL specified");
            throw new BrowserException("No URL specified");
        }
        logger.info("Navigating to URL: {}", url);
        getDriver().get(url);
    }

    /**
     * Returns the current thread's WebDriver instance.
     *
     * @return the WebDriver for the current thread.
     */
    public static WebDriver getDriver() {
        return tlDriver.get();
    }

    /**
     * Quits the WebDriver and removes it from the ThreadLocal storage.
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

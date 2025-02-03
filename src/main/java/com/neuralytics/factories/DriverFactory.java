package com.neuralytics.factories;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

import com.neuralytics.enums.BrowserType;
import com.neuralytics.exceptions.BrowserException;
import com.neuralytics.utils.ConfigLoader;

public class DriverFactory {

    // ThreadLocal ensures that each thread gets its own WebDriver instance.
    private static final ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();
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
            throw new BrowserException("Invalid browser name specified in properties: " + browserName, e);
        }

        // Initialize local or remote driver based on the configuration.
        if (isRemote) {
            initRemoteDriver(browserType, prop);
        } else {
            initLocalDriver(browserType);
        }

        // Post driver initialization steps.
        final WebDriver driver = getDriver();
        driver.manage().deleteAllCookies();
        driver.manage().window().maximize();

        final String appUrl = prop.getProperty("url");
        if (appUrl == null || appUrl.isBlank()) {
            throw new BrowserException("No URL specified in the configuration properties.");
        }
        driver.get(appUrl);

        return driver;
    }

    /**
     * Initializes a local WebDriver based on the specified browser type.
     *
     * @param browserType The type of browser to initialize.
     */
    private static void initLocalDriver(final BrowserType browserType) {
        switch (browserType) {
            case CHROME -> tlDriver.set(new ChromeDriver(
                    (ChromeOptions) optionsManager.getBrowserOptions("chrome")));
            case FIREFOX -> tlDriver.set(new FirefoxDriver(
                    (FirefoxOptions) optionsManager.getBrowserOptions("firefox")));
            case EDGE -> tlDriver.set(new EdgeDriver(
                    (EdgeOptions) optionsManager.getBrowserOptions("edge")));
            case SAFARI -> tlDriver.set(new SafariDriver());
            default -> throw new BrowserException("Unsupported browser type: " + browserType);
        }
    }

    /**
     * Initializes a remote WebDriver using Selenium Grid.
     *
     * @param browserType The type of browser to initialize.
     * @param prop        The loaded configuration properties.
     */
    private static void initRemoteDriver(final BrowserType browserType, final Properties prop) {
        final String hubUrlStr = prop.getProperty("hubUrl");
        if (hubUrlStr == null || hubUrlStr.isBlank()) {
            throw new BrowserException("Remote execution selected but no hub URL provided in properties.");
        }

        try {
            final URL hubUrl = new URL(hubUrlStr);
            // For remote, the options manager returns the appropriate capabilities.
            tlDriver.set(new RemoteWebDriver(hubUrl, optionsManager.getBrowserOptions(browserType.name())));
        } catch (MalformedURLException e) {
            throw new BrowserException("Invalid Selenium Grid Hub URL: " + hubUrlStr, e);
        }
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
            driver.quit();
            tlDriver.remove();
        }
    }
}

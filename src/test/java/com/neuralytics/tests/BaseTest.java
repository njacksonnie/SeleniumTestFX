package com.neuralytics.tests;

import com.neuralytics.factories.WebDriverFactory;
import com.neuralytics.utils.LoggerUtil;
import com.neuralytics.utils.ReportManager;
import com.neuralytics.utils.SeleniumWrapper;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;
import org.slf4j.Logger;
import java.lang.reflect.Method;

public class BaseTest {
    private static final Logger logger = LoggerUtil.getLogger(BaseTest.class);
    private static final ThreadLocal<WebDriver> threadLocalDriver = new ThreadLocal<>();
    protected SeleniumWrapper selenium;
    protected ReportManager report;

    // Shared WebDriver instance for reuse
    private static WebDriver sharedDriver;

    // Flag to indicate if the browser needs to be restarted
    private boolean shouldRestartBrowser = false;

    @BeforeSuite
    @Parameters({"browser", "headless"})
    public void setupSuite(String browser, String headless) {
        try {
            logger.info("Setting up browser for the test suite...");
            sharedDriver = WebDriverFactory.createDriver(browser, Boolean.parseBoolean(headless));
            logger.info("Browser setup completed.");
        } catch (Exception e) {
            logger.error("Error during browser setup: {}", e.getMessage(), e);
            throw e;
        }
    }

    @BeforeMethod
    @Parameters({"baseUrl"})
    public void setup(Method method, String baseUrl) {
        try {
            // Restart the browser if flagged
            if (shouldRestartBrowser) {
                logger.info("Restarting browser for test: {}", method.getName());
                if (sharedDriver != null) {
                    sharedDriver.quit();
                }
                sharedDriver = WebDriverFactory.createDriver("chrome", false); // Restart browser
                shouldRestartBrowser = false; // Reset the flag
            }
            logger.info("Setting up test: {}", method.getName());
            WebDriver driver = getDriver();
            threadLocalDriver.set(driver); // Ensure thread safety
            selenium = new SeleniumWrapper(driver);
            report = ReportManager.getInstance();
            report.startTest(method.getName());

            // Navigate to the base URL
            if (baseUrl != null && !baseUrl.isEmpty()) {
                logger.info("Navigating to base URL: {}", baseUrl);
                driver.get(baseUrl);
            } else {
                logger.warn("Base URL is not provided. Skipping navigation.");
            }
            logger.info("Test setup completed for: {}", method.getName());
        } catch (Exception e) {
            logger.error("Error during test setup for {}: {}", method.getName(), e.getMessage(), e);
            throw e;
        }
    }

    @AfterMethod
    public void tearDown() {
        try {
            logger.info("Tearing down test...");
            cleanUpCookies();
        } catch (Exception e) {
            logger.error("Error during cleanup: {}", e.getMessage(), e);
        } finally {
            report.endTest();
        }
    }

    @AfterSuite
    public void tearDownSuite() {
        try {
            logger.info("Tearing down browser for the test suite...");
            if (sharedDriver != null) {
                sharedDriver.quit();
                logger.info("Browser quit successfully.");
            }
        } catch (Exception e) {
            logger.error("Error while quitting browser: {}", e.getMessage(), e);
        } finally {
            report.tearDown(); // Flush ExtentReports and close log file
        }
    }

    /**
     * Marks the browser for restart before the next test.
     */
    protected void markBrowserForRestart() {
        logger.info("Marking browser for restart...");
        this.shouldRestartBrowser = true;
    }

    private void cleanUpCookies() {
        WebDriver driver = getDriver();
        if (driver != null) {
            try {
                driver.manage().deleteAllCookies();
                logger.info("All cookies deleted successfully.");
            } catch (Exception e) {
                logger.error("Error deleting cookies: {}", e.getMessage(), e);
            }
        } else {
            logger.warn("WebDriver is null. Unable to delete cookies.");
        }
    }

    protected WebDriver getDriver() {
        return sharedDriver; // Return the shared WebDriver instance
    }
}
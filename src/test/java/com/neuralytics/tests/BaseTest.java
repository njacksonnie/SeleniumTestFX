package com.neuralytics.tests;

import com.neuralytics.factories.WebDriverFactory;
import com.neuralytics.utils.LoggerUtil;
import com.neuralytics.utils.ReportManager;
import com.neuralytics.utils.SeleniumWrapper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.testng.annotations.*;

import java.lang.reflect.Method;

public class BaseTest {
    private static final Logger logger = LoggerUtil.getLogger(BaseTest.class);
    private static final ThreadLocal<WebDriver> threadLocalDriver = ThreadLocal.withInitial(() -> null);
    protected SeleniumWrapper selenium;
    protected ReportManager report;

    @BeforeMethod
    @Parameters({"browser", "headless", "baseUrl"})
    public void setup(Method method, String browser, @Optional("false") boolean headless, String baseUrl) {
        try {
            logger.info("Setting up test: {}", method.getName());

            WebDriver driver = WebDriverFactory.createDriver(browser, headless);
            threadLocalDriver.set(driver);

            selenium = new SeleniumWrapper(driver);
            report = ReportManager.getInstance();
            report.startTest(method.getName());

            navigateToBaseUrl(baseUrl);

            logger.info("Test setup completed for: {}", method.getName());
        } catch (WebDriverException e) {
            logger.error("WebDriver error in test {}: {}", method.getName(), e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in test {}: {}", method.getName(), e.getMessage(), e);
            throw new RuntimeException("Test setup failed: " + e.getMessage(), e);
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
            quitDriver();
        }
    }

    @AfterSuite
    public void tearDownSuite() {
        try {
            logger.info("Tearing down suite...");
            report.tearDown(); // Flush reports
        } catch (Exception e) {
            logger.error("Error during suite tearDown: {}", e.getMessage(), e);
        }
    }

    private void navigateToBaseUrl(String baseUrl) {
        WebDriver driver = threadLocalDriver.get();
        if (baseUrl != null && !baseUrl.isEmpty()) {
            logger.info("Navigating to base URL: {}", baseUrl);
            driver.get(baseUrl);
        } else {
            logger.warn("Base URL is not provided. Skipping navigation.");
        }
    }

    private void cleanUpCookies() {
        WebDriver driver = threadLocalDriver.get();
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

    private void quitDriver() {
        WebDriver driver = threadLocalDriver.get();
        if (driver != null) {
            try {
                driver.quit();
                logger.info("WebDriver quit successfully.");
            } catch (Exception e) {
                logger.error("Error quitting WebDriver: {}", e.getMessage(), e);
            } finally {
                threadLocalDriver.remove();
            }
        } else {
            logger.warn("WebDriver is null. Unable to quit driver.");
        }
    }

    protected WebDriver getDriver() {
        return threadLocalDriver.get();
    }
}
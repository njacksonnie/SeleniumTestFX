package com.neuralytics.utils;

import com.neuralytics.utils.ReportManager;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import java.time.Duration;
import java.util.Objects;

public class SeleniumWrapper {

    // Logger instance
    private static final Logger logger = LoggerUtil.getLogger(SeleniumWrapper.class);

    // WebDriver and WebDriverWait instances
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    /**
     * Constructor with Dependency Injection.
     *
     * @param driver  WebDriver instance
     * @param timeout Duration for explicit waits
     */
    public SeleniumWrapper(WebDriver driver, Duration timeout) {
        this.driver = Objects.requireNonNull(driver, "WebDriver cannot be null.");
        this.wait = new WebDriverWait(Objects.requireNonNull(driver), Objects.requireNonNull(timeout));
        logger.trace("Initialized SeleniumWrapper with timeout: {} seconds", timeout.getSeconds());
        ReportManager.getInstance().logInfo("SeleniumWrapper initialized with timeout: " + timeout.getSeconds() + " seconds");
    }

    /**
     * Default constructor with a default timeout of 10 seconds.
     *
     * @param driver WebDriver instance
     */
    public SeleniumWrapper(WebDriver driver) {
        this(driver, Duration.ofSeconds(10)); // Default timeout
    }

    /**
     * Clicks on an element after waiting for it to be clickable.
     *
     * @param locator Locator for the element to click
     * @throws TimeoutException if the element is not clickable within the timeout
     */
    public synchronized void click(By locator) {
        validateLocator(locator);
        try {
            logger.trace("Waiting for element to be clickable: {}", locator);
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            logger.info("Clicking element: {}", locator);
            ReportManager.getInstance().logInfo("Clicking element: " + locator);
            element.click();
            ReportManager.getInstance().logPass("Successfully clicked element: " + locator);
        } catch (TimeoutException e) {
            String errorMessage = "Timeout while waiting for element to be clickable: " + locator;
            logger.error(errorMessage, e);
            ReportManager.getInstance().logFail(errorMessage);
            throw e;
        } catch (Exception e) {
            String errorMessage = "Failed to click element: " + locator;
            logger.error(errorMessage, e);
            ReportManager.getInstance().logFail(errorMessage);
            throw e;
        }
    }

    /**
     * Types text into an element after waiting for it to be visible.
     *
     * @param locator Locator for the element to type into
     * @param text    Text to type into the element
     * @throws TimeoutException if the element is not visible within the timeout
     */
    public synchronized void type(By locator, String text) {
        validateLocator(locator);
        Objects.requireNonNull(text, "Text to type cannot be null.");
        try {
            logger.trace("Waiting for element to be visible: {}", locator);
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            logger.info("Typing '{}' in element: {}", text, locator);
            ReportManager.getInstance().logInfo("Typing '" + text + "' in element: " + locator);
            element.clear();
            element.sendKeys(text);
            ReportManager.getInstance().logPass("Successfully typed '" + text + "' in element: " + locator);
        } catch (TimeoutException e) {
            String errorMessage = "Timeout while waiting for element to be visible: " + locator;
            logger.error(errorMessage, e);
            ReportManager.getInstance().logFail(errorMessage);
            throw e;
        } catch (Exception e) {
            String errorMessage = "Failed to type in element: " + locator;
            logger.error(errorMessage, e);
            ReportManager.getInstance().logFail(errorMessage);
            throw e;
        }
    }

    /**
     * Checks if an element is displayed within the timeout.
     *
     * @param locator Locator for the element to check
     * @return true if the element is displayed, false otherwise
     */
    public synchronized boolean isDisplayed(By locator) {
        validateLocator(locator);
        try {
            logger.trace("Checking visibility of element: {}", locator);
            boolean isDisplayed = wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
            if (isDisplayed) {
                ReportManager.getInstance().logPass("Element is displayed: " + locator);
            } else {
                ReportManager.getInstance().logInfo("Element is not displayed: " + locator);
            }
            return isDisplayed;
        } catch (TimeoutException e) {
            String errorMessage = "Element " + locator + " not visible within timeout";
            logger.warn(errorMessage);
            ReportManager.getInstance().logInfo(errorMessage);
            return false;
        } catch (Exception e) {
            String errorMessage = "Error while checking visibility of element: " + locator;
            logger.error(errorMessage, e);
            ReportManager.getInstance().logFail(errorMessage);
            return false;
        }
    }

    /**
     * Retrieves the text of an element after waiting for it to be visible.
     *
     * @param locator Locator for the element to retrieve text from
     * @return The text of the element
     * @throws TimeoutException if the element is not visible within the timeout
     */
    public synchronized String getText(By locator) {
        validateLocator(locator);
        try {
            logger.trace("Getting text from element: {}", locator);
            String text = wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText();
            ReportManager.getInstance().logInfo("Retrieved text '" + text + "' from element: " + locator);
            return text;
        } catch (TimeoutException e) {
            String errorMessage = "Timeout while getting text from element: " + locator;
            logger.error(errorMessage, e);
            ReportManager.getInstance().logFail(errorMessage);
            throw e;
        } catch (Exception e) {
            String errorMessage = "Failed to get text from element: " + locator;
            logger.error(errorMessage, e);
            ReportManager.getInstance().logFail(errorMessage);
            throw e;
        }
    }

    /**
     * Waits for an element to be present in the DOM.
     *
     * @param locator Locator for the element to wait for
     * @throws TimeoutException if the element is not present within the timeout
     */
    public synchronized void waitForElementToBePresent(By locator) {
        validateLocator(locator);
        try {
            logger.trace("Waiting for presence of element: {}", locator);
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            ReportManager.getInstance().logPass("Element is present in the DOM: " + locator);
        } catch (TimeoutException e) {
            String errorMessage = "Timeout while waiting for presence of element: " + locator;
            logger.error(errorMessage, e);
            ReportManager.getInstance().logFail(errorMessage);
            throw e;
        } catch (Exception e) {
            String errorMessage = "Failed while waiting for presence of element: " + locator;
            logger.error(errorMessage, e);
            ReportManager.getInstance().logFail(errorMessage);
            throw e;
        }
    }

    /**
     * Validates that the provided locator is not null.
     *
     * @param locator Locator to validate
     */
    private void validateLocator(By locator) {
        Objects.requireNonNull(locator, "Locator cannot be null.");
    }

    /**
     * Getter for the WebDriver instance.
     *
     * @return WebDriver instance
     */
    public WebDriver getDriver() {
        return driver;
    }

    /**
     * Getter for the WebDriverWait instance.
     *
     * @return WebDriverWait instance
     */
    public WebDriverWait getWait() {
        return wait;
    }
}
package com.neuralytics.utils;

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
        this.wait = new WebDriverWait(this.driver, Objects.requireNonNull(timeout));
        logInitialization(timeout);
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
            logAction("Waiting for element to be clickable", locator);
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            logAction("Clicking element", locator);
            element.click();
            logSuccess("Successfully clicked element", locator);
        } catch (TimeoutException e) {
            handleException("Timeout while waiting for element to be clickable", locator, e);
        } catch (Exception e) {
            handleException("Failed to click element", locator, e);
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
            logAction("Waiting for element to be visible", locator);
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            logAction("Typing '" + text + "' in element", locator);
            element.clear();
            element.sendKeys(text);
            logSuccess("Successfully typed '" + text + "' in element", locator);
        } catch (TimeoutException e) {
            handleException("Timeout while waiting for element to be visible", locator, e);
        } catch (Exception e) {
            handleException("Failed to type in element", locator, e);
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
            logAction("Checking visibility of element", locator);
            boolean isDisplayed = wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
            logResult(isDisplayed, "Element is displayed", "Element is not displayed", locator);
            return isDisplayed;
        } catch (TimeoutException e) {
            logWarning("Element not visible within timeout", locator);
            return false;
        } catch (Exception e) {
            handleException("Error while checking visibility of element", locator, e);
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
            logAction("Getting text from element", locator);
            String text = wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText();
            logSuccess("Retrieved text '" + text + "' from element", locator);
            return text;
        } catch (TimeoutException e) {
            handleException("Timeout while getting text from element", locator, e);
            return null;
        } catch (Exception e) {
            handleException("Failed to get text from element", locator, e);
            return null;
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
            logAction("Waiting for presence of element", locator);
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            logSuccess("Element is present in the DOM", locator);
        } catch (TimeoutException e) {
            handleException("Timeout while waiting for presence of element", locator, e);
        } catch (Exception e) {
            handleException("Failed while waiting for presence of element", locator, e);
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
     * Logs the initialization of the SeleniumWrapper.
     *
     * @param timeout Duration for explicit waits
     */
    private void logInitialization(Duration timeout) {
        logger.trace("Initialized SeleniumWrapper with timeout: {} seconds", timeout.getSeconds());
        ReportManager.getInstance().logInfo("SeleniumWrapper initialized with timeout: " + timeout.getSeconds() + " seconds");
    }

    /**
     * Logs an action being performed.
     *
     * @param action  Description of the action
     * @param locator Locator for the element involved in the action
     */
    private void logAction(String action, By locator) {
        logger.trace("{}: {}", action, locator);
        ReportManager.getInstance().logInfo(action + ": " + locator);
    }

    /**
     * Logs a successful action.
     *
     * @param successMessage Description of the successful action
     * @param locator        Locator for the element involved in the action
     */
    private void logSuccess(String successMessage, By locator) {
        logger.info(successMessage + ": {}", locator);
        ReportManager.getInstance().logPass(successMessage + ": " + locator);
    }

    /**
     * Logs the result of an action.
     *
     * @param condition      Condition to check
     * @param successMessage Message to log if the condition is true
     * @param failureMessage Message to log if the condition is false
     * @param locator        Locator for the element involved in the action
     */
    private void logResult(boolean condition, String successMessage, String failureMessage, By locator) {
        if (condition) {
            logSuccess(successMessage, locator);
        } else {
            logger.info(failureMessage + ": {}", locator);
            ReportManager.getInstance().logInfo(failureMessage + ": " + locator);
        }
    }

    /**
     * Logs a warning message.
     *
     * @param warningMessage Description of the warning
     * @param locator        Locator for the element involved in the action
     */
    private void logWarning(String warningMessage, By locator) {
        logger.warn(warningMessage + ": {}", locator);
        ReportManager.getInstance().logInfo(warningMessage + ": " + locator);
    }

    /**
     * Handles exceptions by logging and rethrowing them.
     *
     * @param errorMessage Description of the error
     * @param locator      Locator for the element involved in the action
     * @param e            Exception to handle
     */
    private void handleException(String errorMessage, By locator, Exception e) {
        logger.error(errorMessage + ": {}", locator, e);
        ReportManager.getInstance().logFail(errorMessage + ": " + locator);
        throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
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
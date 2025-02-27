package com.neuralytics.utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.List;

/**
 * A utility class for managing explicit waits in Selenium WebDriver.
 * Provides methods to wait for various conditions such as element presence,
 * visibility, clickability,
 * page titles, URLs, alerts, frames, and window counts. Utilizes
 * {@link WebDriverWait} for standard waits
 * and {@link FluentWait} for customizable polling intervals, ensuring robust
 * synchronization in a
 * testing framework.
 *
 * <p>
 * Usage example:
 * 
 * <pre>
 * WebDriver driver = DriverFactory.getDriver();
 * WebElement element = WaitUtil.waitForElementVisible(driver, By.id("loginButton"), 10);
 * WaitUtil.clickWhenReady(driver, By.id("submit"), 5);
 * String title = WaitUtil.waitForTitleContains(driver, "Dashboard", 10);
 * </pre>
 *
 * <p>
 * All methods use timeouts specified in seconds and log their outcomes for
 * debugging purposes.
 *
 * @see WebDriverWait
 * @see FluentWait
 * @see ExpectedConditions
 */
public class WaitUtil {

    /**
     * Logger instance for tracing wait operations and logging errors.
     */
    private static final Logger logger = LoggerUtil.getLogger(WaitUtil.class);

    /**
     * Waits for an element to be present in the DOM of a page.
     * Presence indicates the element exists in the DOM, but it may not be visible.
     *
     * @param driver  the WebDriver instance to use
     * @param locator the locator of the target element
     * @param timeOut the maximum time to wait (in seconds)
     * @return the found {@link WebElement}
     * @throws TimeoutException if the element is not present within the timeout
     */
    public static WebElement waitForElementPresence(WebDriver driver, By locator, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        logger.info("Waited for element presence: {}", locator);
        return element;
    }

    /**
     * Waits for an element to be present in the DOM and visible on the page.
     * Visibility requires the element to be displayed with a height and width
     * greater than 0.
     *
     * @param driver  the WebDriver instance to use
     * @param locator the locator of the target element
     * @param timeOut the maximum time to wait (in seconds)
     * @return the visible {@link WebElement}
     * @throws TimeoutException if the element is not visible within the timeout
     */
    public static WebElement waitForElementVisible(WebDriver driver, By locator, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        logger.info("Waited for element visibility: {}", locator);
        return element;
    }

    /**
     * Waits for an element to be visible with custom polling using
     * {@link FluentWait}.
     * Polls at the specified interval, ignoring {@link NoSuchElementException}
     * during the wait.
     *
     * @param driver       the WebDriver instance to use
     * @param locator      the locator of the target element
     * @param timeOut      the maximum time to wait (in seconds)
     * @param intervalTime the polling interval (in seconds)
     * @return the visible {@link WebElement}
     * @throws TimeoutException if the element is not visible within the timeout
     */
    public static WebElement waitForElementVisible(WebDriver driver, By locator, int timeOut, int intervalTime) {
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeOut))
                .pollingEvery(Duration.ofSeconds(intervalTime))
                .ignoring(NoSuchElementException.class)
                .withMessage("===element is not found===");

        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        logger.info("Waited for element visibility with timeout {} and interval {}: {}", timeOut, intervalTime,
                locator);
        return element;
    }

    /**
     * Waits for at least one element matching the locator to be present in the DOM.
     *
     * @param driver  the WebDriver instance to use
     * @param locator the locator of the target elements
     * @param timeOut the maximum time to wait (in seconds)
     * @return a list of present {@link WebElement} instances
     * @throws TimeoutException if no elements are present within the timeout
     */
    public static List<WebElement> waitForPresenceOfElementsLocated(WebDriver driver, By locator, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        List<WebElement> elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
        logger.info("Waited for presence of elements: {}", locator);
        return elements;
    }

    /**
     * Waits for all elements matching the locator to be visible on the page.
     * Visibility requires all elements to be displayed with a height and width
     * greater than 0.
     * Returns an empty list if no elements are found within the timeout,
     * suppressing exceptions.
     *
     * @param driver  the WebDriver instance to use
     * @param locator the locator of the target elements
     * @param timeOut the maximum time to wait (in seconds)
     * @return a list of visible {@link WebElement} instances, or an empty list if
     *         none are found
     */
    public static List<WebElement> waitForVisibilityOfElementsLocated(WebDriver driver, By locator, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        try {
            List<WebElement> elements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
            logger.info("Waited for visibility of elements: {}", locator);
            return elements;
        } catch (Exception e) {
            logger.warn("No elements found for visibility check: {}", locator);
            return List.of();
        }
    }

    /**
     * Waits for an element to be visible and clickable, then clicks it.
     * Ensures the element is both displayed and enabled before performing the
     * click.
     *
     * @param driver  the WebDriver instance to use
     * @param locator the locator of the target element
     * @param timeOut the maximum time to wait (in seconds)
     * @throws TimeoutException if the element is not clickable within the timeout
     */
    public static void clickWhenReady(WebDriver driver, By locator, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
        logger.info("Waited for element to be clickable and clicked: {}", locator);
    }

    /**
     * Waits for the page title to contain the specified fraction.
     * Returns the current title even if the condition times out, logging an error
     * in that case.
     *
     * @param driver        the WebDriver instance to use
     * @param titleFraction the partial title text to wait for
     * @param timeOut       the maximum time to wait (in seconds)
     * @return the current page title
     */
    public static String waitForTitleContains(WebDriver driver, String titleFraction, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        String title = null;
        try {
            if (wait.until(ExpectedConditions.titleContains(titleFraction))) {
                title = driver.getTitle();
                logger.info("Waited for title containing '{}', actual title: {}", titleFraction, title);
            }
        } catch (TimeoutException e) {
            logger.error("Title not found containing '{}'", titleFraction);
            title = driver.getTitle();
        }
        return title;
    }

    /**
     * Waits for the page title to exactly match the specified value.
     * Returns the current title even if the condition times out, logging an error
     * in that case.
     *
     * @param driver   the WebDriver instance to use
     * @param titleVal the exact title text to wait for
     * @param timeOut  the maximum time to wait (in seconds)
     * @return the current page title
     */
    public static String waitForTitleToBe(WebDriver driver, String titleVal, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        String title = null;
        try {
            if (wait.until(ExpectedConditions.titleIs(titleVal))) {
                title = driver.getTitle();
                logger.info("Waited for title '{}', actual title: {}", titleVal, title);
            }
        } catch (TimeoutException e) {
            logger.error("Title not found: '{}'", titleVal);
            title = driver.getTitle();
        }
        return title;
    }

    /**
     * Waits for the page URL to contain the specified fraction.
     * Returns the current URL even if the condition times out, logging an error in
     * that case.
     *
     * @param driver      the WebDriver instance to use
     * @param urlFraction the partial URL text to wait for
     * @param timeOut     the maximum time to wait (in seconds)
     * @return the current page URL
     */
    public static String waitForURLContains(WebDriver driver, String urlFraction, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        String url = null;
        try {
            if (wait.until(ExpectedConditions.urlContains(urlFraction))) {
                url = driver.getCurrentUrl();
                logger.info("Waited for URL containing '{}', actual URL: {}", urlFraction, url);
            }
        } catch (TimeoutException e) {
            logger.error("URL not found containing '{}'", urlFraction);
            url = driver.getCurrentUrl();
        }
        return url;
    }

    /**
     * Waits for the page URL to exactly match the specified value.
     * Returns the current URL even if the condition times out, logging an error in
     * that case.
     *
     * @param driver   the WebDriver instance to use
     * @param urlValue the exact URL to wait for
     * @param timeOut  the maximum time to wait (in seconds)
     * @return the current page URL
     */
    public static String waitForURLToBe(WebDriver driver, String urlValue, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        String url = null;
        try {
            if (wait.until(ExpectedConditions.urlToBe(urlValue))) {
                url = driver.getCurrentUrl();
                logger.info("Waited for URL '{}', actual URL: {}", urlValue, url);
            }
        } catch (TimeoutException e) {
            logger.error("URL not found: '{}'", urlValue);
            url = driver.getCurrentUrl();
        }
        return url;
    }

    /**
     * Waits for a JavaScript alert to be present using {@link WebDriverWait}.
     *
     * @param driver  the WebDriver instance to use
     * @param timeOut the maximum time to wait (in seconds)
     * @return the present {@link Alert}
     * @throws TimeoutException if the alert is not present within the timeout
     */
    public static Alert waitForJSAlert(WebDriver driver, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        logger.info("Waited for JS alert");
        return alert;
    }

    /**
     * Waits for a JavaScript alert to be present with custom polling using
     * {@link FluentWait}.
     * Polls at the specified interval, ignoring {@link NoAlertPresentException}
     * during the wait.
     *
     * @param driver       the WebDriver instance to use
     * @param timeOut      the maximum time to wait (in seconds)
     * @param intervalTime the polling interval (in seconds)
     * @return the present {@link Alert}
     * @throws TimeoutException if the alert is not present within the timeout
     */
    public static Alert waitForJSAlert(WebDriver driver, int timeOut, int intervalTime) {
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeOut))
                .pollingEvery(Duration.ofSeconds(intervalTime))
                .ignoring(NoAlertPresentException.class)
                .withMessage("===alert is not found===");
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        logger.info("Waited for JS alert with timeout {} and interval {}", timeOut, intervalTime);
        return alert;
    }

    /**
     * Waits for a JavaScript alert and retrieves its text, then accepts it.
     *
     * @param driver  the WebDriver instance to use
     * @param timeOut the maximum time to wait (in seconds)
     * @return the text of the alert
     * @throws TimeoutException if the alert is not present within the timeout
     */
    public static String getAlertText(WebDriver driver, int timeOut) {
        Alert alert = waitForJSAlert(driver, timeOut);
        String text = alert.getText();
        alert.accept();
        logger.info("Got alert text: {}", text);
        return text;
    }

    /**
     * Waits for a JavaScript alert and accepts it.
     *
     * @param driver  the WebDriver instance to use
     * @param timeOut the maximum time to wait (in seconds)
     * @throws TimeoutException if the alert is not present within the timeout
     */
    public static void acceptAlert(WebDriver driver, int timeOut) {
        waitForJSAlert(driver, timeOut).accept();
        logger.info("Accepted JS alert");
    }

    /**
     * Waits for a JavaScript alert and dismisses it.
     *
     * @param driver  the WebDriver instance to use
     * @param timeOut the maximum time to wait (in seconds)
     * @throws TimeoutException if the alert is not present within the timeout
     */
    public static void dismissAlert(WebDriver driver, int timeOut) {
        waitForJSAlert(driver, timeOut).dismiss();
        logger.info("Dismissed JS alert");
    }

    /**
     * Waits for a JavaScript alert, sends keys to it, and accepts it.
     * Useful for prompt-style alerts requiring input.
     *
     * @param driver  the WebDriver instance to use
     * @param timeOut the maximum time to wait (in seconds)
     * @param value   the value to send to the alert
     * @throws TimeoutException if the alert is not present within the timeout
     */
    public static void alertSendKeys(WebDriver driver, int timeOut, String value) {
        Alert alert = waitForJSAlert(driver, timeOut);
        alert.sendKeys(value);
        alert.accept();
        logger.info("Sent keys to JS alert: {}", value);
    }

    /**
     * Waits for a frame to be available and switches to it using a locator.
     *
     * @param driver       the WebDriver instance to use
     * @param frameLocator the locator of the target frame
     * @param timeOut      the maximum time to wait (in seconds)
     * @throws TimeoutException if the frame is not available within the timeout
     */
    public static void waitForFrameByLocator(WebDriver driver, By frameLocator, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameLocator));
        logger.info("Waited for frame by locator: {}", frameLocator);
    }

    /**
     * Waits for a frame to be available and switches to it with custom polling
     * using a locator.
     * Polls at the specified interval, ignoring {@link NoSuchFrameException} during
     * the wait.
     *
     * @param driver       the WebDriver instance to use
     * @param frameLocator the locator of the target frame
     * @param timeOut      the maximum time to wait (in seconds)
     * @param intervalTime the polling interval (in seconds)
     * @throws TimeoutException if the frame is not available within the timeout
     */
    public static void waitForFrameByLocator(WebDriver driver, By frameLocator, int timeOut, int intervalTime) {
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeOut))
                .pollingEvery(Duration.ofSeconds(intervalTime))
                .ignoring(NoSuchFrameException.class)
                .withMessage("===frame is not found===");

        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameLocator));
        logger.info("Waited for frame by locator with timeout {} and interval {}: {}", timeOut, intervalTime,
                frameLocator);
    }

    /**
     * Waits for a frame to be available and switches to it using an index.
     *
     * @param driver     the WebDriver instance to use
     * @param frameIndex the index of the target frame (zero-based)
     * @param timeOut    the maximum time to wait (in seconds)
     * @throws TimeoutException if the frame is not available within the timeout
     */
    public static void waitForFrameByIndex(WebDriver driver, int frameIndex, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameIndex));
        logger.info("Waited for frame by index: {}", frameIndex);
    }

    /**
     * Waits for a frame to be available and switches to it using an ID or name.
     *
     * @param driver        the WebDriver instance to use
     * @param frameIDOrName the ID or name of the target frame
     * @param timeOut       the maximum time to wait (in seconds)
     * @throws TimeoutException if the frame is not available within the timeout
     */
    public static void waitForFrameByIndex(WebDriver driver, String frameIDOrName, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameIDOrName));
        logger.info("Waited for frame by ID or name: {}", frameIDOrName);
    }

    /**
     * Waits for a frame to be available and switches to it using a frame element.
     *
     * @param driver       the WebDriver instance to use
     * @param frameElement the {@link WebElement} representing the target frame
     * @param timeOut      the maximum time to wait (in seconds)
     * @throws TimeoutException if the frame is not available within the timeout
     */
    public static void waitForFrameByIndex(WebDriver driver, WebElement frameElement, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameElement));
        logger.info("Waited for frame by element: {}", frameElement);
    }

    /**
     * Waits for the number of browser windows to match the specified total.
     *
     * @param driver       the WebDriver instance to use
     * @param totalWindows the expected number of windows
     * @param timeOut      the maximum time to wait (in seconds)
     * @return true if the number of windows matches within the timeout, false
     *         otherwise
     * @throws TimeoutException if the condition is not met within the timeout
     */
    public static boolean waitForWindowsToBe(WebDriver driver, int totalWindows, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        boolean result = wait.until(ExpectedConditions.numberOfWindowsToBe(totalWindows));
        logger.info("Waited for number of windows to be: {}", totalWindows);
        return result;
    }

    /**
     * Waits for the page to be fully loaded by checking the document.readyState via
     * JavaScript.
     * Throws an exception if the page does not load within the timeout.
     *
     * @param driver  the WebDriver instance to use
     * @param timeOut the maximum time to wait (in seconds)
     * @throws RuntimeException if the page does not load completely within the
     *                          timeout
     */
    public static void isPageLoaded(WebDriver driver, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        String flag = wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete'"))
                .toString();

        if (Boolean.parseBoolean(flag)) {
            logger.info("Page is completely loaded");
        } else {
            logger.error("Page is not loaded");
            throw new RuntimeException("page is not loaded");
        }
    }
}
package com.neuralytics.utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.List;

public class WaitUtil {

    private static final Logger logger = LoggerUtil.getLogger(WaitUtil.class);

    /**
     * An expectation for checking that an element is present on the DOM of a page.
     * This does not necessarily mean that the element is visible.
     */
    public static WebElement waitForElementPresence(WebDriver driver, By locator, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        logger.info("Waited for element presence: {}", locator);
        return element;
    }

    /**
     * An expectation for checking that an element is present on the DOM of a page
     * and visible. Visibility means that the element is not only displayed but also
     * has a height and width that is greater than 0.
     */
    public static WebElement waitForElementVisible(WebDriver driver, By locator, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        logger.info("Waited for element visibility: {}", locator);
        return element;
    }

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
     * An expectation for checking that there is at least one element present on a
     * web page.
     */
    public static List<WebElement> waitForPresenceOfElementsLocated(WebDriver driver, By locator, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        List<WebElement> elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
        logger.info("Waited for presence of elements: {}", locator);
        return elements;
    }

    /**
     * An expectation for checking that all elements present on the web page that
     * match the locator are visible. Visibility means that the elements are not
     * only displayed but also have a height and width that is greater than 0.
     */
    public static List<WebElement> waitForVisibilityOfElementsLocated(WebDriver driver, By locator, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        try {
            List<WebElement> elements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
            logger.info("Waited for visibility of elements: {}", locator);
            return elements;
        } catch (Exception e) {
            logger.warn("No elements found for visibility check: {}", locator);
            return List.of(); // return empty arraylist
        }
    }

    /**
     * An expectation for checking an element is visible and enabled such that you
     * can click it.
     */
    public static void clickWhenReady(WebDriver driver, By locator, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
        logger.info("Waited for element to be clickable and clicked: {}", locator);
    }

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
            title = driver.getTitle(); // Still return the current title
        }
        return title;
    }

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
            title = driver.getTitle(); // Still return the current title

        }
        return title;
    }

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

    public static Alert waitForJSAlert(WebDriver driver, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        logger.info("Waited for JS alert");
        return alert;
    }

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

    public static String getAlertText(WebDriver driver, int timeOut) {
        Alert alert = waitForJSAlert(driver, timeOut);
        String text = alert.getText();
        alert.accept();
        logger.info("Got alert text: {}", text);
        return text;
    }

    public static void acceptAlert(WebDriver driver, int timeOut) {
        waitForJSAlert(driver, timeOut).accept();
        logger.info("Accepted JS alert");
    }

    public static void dismissAlert(WebDriver driver, int timeOut) {
        waitForJSAlert(driver, timeOut).dismiss();
        logger.info("Dismissed JS alert");
    }

    public static void alertSendKeys(WebDriver driver, int timeOut, String value) {
        Alert alert = waitForJSAlert(driver, timeOut);
        alert.sendKeys(value);
        alert.accept();
        logger.info("Sent keys to JS alert: {}", value);
    }

    public static void waitForFrameByLocator(WebDriver driver, By frameLocator, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameLocator));
        logger.info("Waited for frame by locator: {}", frameLocator);
    }

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

    public static void waitForFrameByIndex(WebDriver driver, int frameIndex, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameIndex));
        logger.info("Waited for frame by index: {}", frameIndex);
    }

    public static void waitForFrameByIndex(WebDriver driver, String frameIDOrName, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameIDOrName));
        logger.info("Waited for frame by ID or name: {}", frameIDOrName);
    }

    public static void waitForFrameByIndex(WebDriver driver, WebElement frameElement, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameElement));
        logger.info("Waited for frame by element: {}", frameElement);
    }

    public static boolean waitForWindowsToBe(WebDriver driver, int totalWindows, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        boolean result = wait.until(ExpectedConditions.numberOfWindowsToBe(totalWindows));
        logger.info("Waited for number of windows to be: {}", totalWindows);
        return result;
    }

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

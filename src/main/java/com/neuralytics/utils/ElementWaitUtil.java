package com.neuralytics.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;

import java.util.List;

/**
 * Utility class for waiting on WebElement-related conditions in Selenium WebDriver.
 */
public class ElementWaitUtil extends BaseWaitUtil {
    private static final Logger logger = LoggerUtil.getLogger(ElementWaitUtil.class);

    public static WebElement waitForElementPresence(WebDriver driver, By locator, int timeoutSeconds) {
        return waitForCondition(driver, timeoutSeconds,
                ExpectedConditions.presenceOfElementLocated(locator),
                "element presence: " + locator);
    }

    public static WebElement waitForElementVisible(WebDriver driver, By locator, int timeoutSeconds) {
        return waitForCondition(driver, timeoutSeconds,
                ExpectedConditions.visibilityOfElementLocated(locator),
                "element visibility: " + locator);
    }

    public static WebElement waitForElementVisible(WebDriver driver, By locator, int timeoutSeconds,
                                                   int pollingIntervalSeconds) {
        return createFluentWait(driver, timeoutSeconds, pollingIntervalSeconds, NoSuchElementException.class)
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static List<WebElement> waitForPresenceOfElements(WebDriver driver, By locator, int timeoutSeconds) {
        return waitForCondition(driver, timeoutSeconds,
                ExpectedConditions.presenceOfAllElementsLocatedBy(locator),
                "presence of elements: " + locator);
    }

    public static List<WebElement> waitForVisibilityOfElements(WebDriver driver, By locator, int timeoutSeconds) {
        return waitForCondition(driver, timeoutSeconds,
                ExpectedConditions.visibilityOfAllElementsLocatedBy(locator),
                "visibility of elements: " + locator);
    }

    public static void clickWhenReady(WebDriver driver, By locator, int timeoutSeconds) {
        WebElement element = waitForCondition(driver, timeoutSeconds,
                ExpectedConditions.elementToBeClickable(locator),
                "element clickability: " + locator);
        element.click();
        logger.info("Clicked element: {}", locator);
    }
}
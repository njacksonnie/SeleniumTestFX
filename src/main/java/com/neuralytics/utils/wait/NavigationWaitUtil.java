package com.neuralytics.utils.wait;

import com.neuralytics.exceptions.WaitTimeoutException;
import com.neuralytics.utils.LoggerUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;

import java.util.function.Function;

/**
 * Utility class for waiting on navigation-related conditions (title, URL, frames, windows) in Selenium WebDriver.
 */
public class NavigationWaitUtil extends BaseWaitUtil {
    private static final Logger logger = LoggerUtil.getLogger(NavigationWaitUtil.class);

    public static String waitForTitleContains(WebDriver driver, String titleFraction, int timeoutSeconds) {
        return waitForTextCondition(driver, titleFraction, timeoutSeconds,
                t -> driver.getTitle().contains(t), "title containing", "title");
    }

    public static String waitForTitleToBe(WebDriver driver, String titleValue, int timeoutSeconds) {
        return waitForTextCondition(driver, titleValue, timeoutSeconds,
                t -> driver.getTitle().equals(t), "title to be", "title");
    }

    public static String waitForUrlContains(WebDriver driver, String urlFraction, int timeoutSeconds) {
        return waitForTextCondition(driver, urlFraction, timeoutSeconds,
                u -> driver.getCurrentUrl().contains(u), "URL containing", "url");
    }

    public static String waitForUrlToBe(WebDriver driver, String urlValue, int timeoutSeconds) {
        return waitForTextCondition(driver, urlValue, timeoutSeconds,
                u -> driver.getCurrentUrl().equals(u), "URL to be", "url");
    }

    private static String waitForTextCondition(WebDriver driver, String expectedValue, int timeoutSeconds,
                                               Function<String, Boolean> condition, String description,
                                               String valueType) {
        try {
            createWebDriverWait(driver, timeoutSeconds).until(d -> condition.apply(expectedValue));
            String actualValue = valueType.equalsIgnoreCase("title") ? driver.getTitle() : driver.getCurrentUrl();
            logger.info("Wait succeeded for {} '{}', actual: '{}'", description, expectedValue, actualValue);
            return actualValue;
        } catch (TimeoutException e) {
            String actualValue = valueType.equalsIgnoreCase("title") ? driver.getTitle() : driver.getCurrentUrl();
            logger.warn("Wait failed for {} '{}', actual: '{}'", description, expectedValue, actualValue);
            return actualValue;
        }
    }

    public static void waitForFrameByLocator(WebDriver driver, By frameLocator, int timeoutSeconds) {
        waitForCondition(driver, timeoutSeconds,
                ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameLocator),
                "frame by locator: " + frameLocator);
    }

    public static void waitForFrameByLocator(WebDriver driver, By frameLocator, int timeoutSeconds,
                                             int pollingIntervalSeconds) {
        createFluentWait(driver, timeoutSeconds, pollingIntervalSeconds)
                .until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameLocator));
        logger.info("Switched to frame by locator: {}", frameLocator);
    }

    public static void waitForFrameByIndex(WebDriver driver, int frameIndex, int timeoutSeconds) {
        waitForCondition(driver, timeoutSeconds,
                ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameIndex),
                "frame by index: " + frameIndex);
    }

    public static void waitForFrameByIdOrName(WebDriver driver, String frameIdOrName, int timeoutSeconds) {
        waitForCondition(driver, timeoutSeconds,
                ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameIdOrName),
                "frame by ID or name: " + frameIdOrName);
    }

    public static void waitForFrameByElement(WebDriver driver, WebElement frameElement, int timeoutSeconds) {
        waitForCondition(driver, timeoutSeconds,
                ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameElement),
                "frame by element: " + frameElement);
    }

    public static boolean waitForNumberOfWindows(WebDriver driver, int expectedWindows, int timeoutSeconds) {
        return waitForCondition(driver, timeoutSeconds,
                ExpectedConditions.numberOfWindowsToBe(expectedWindows),
                "number of windows: " + expectedWindows);
    }

    public static void waitForPageLoaded(WebDriver driver, int timeoutSeconds) {
        boolean isLoaded = waitForCondition(driver, timeoutSeconds,
                d -> "complete".equals(((JavascriptExecutor) d).executeScript("return document.readyState")),
                "page load");
        if (!isLoaded) {
            throw new WaitTimeoutException("Page did not load completely");
        }
    }
}
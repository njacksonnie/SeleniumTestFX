package com.neuralytics.utils.wait;

import com.neuralytics.exceptions.WaitTimeoutException;
import com.neuralytics.utils.LoggerUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.TimeoutException;

import org.slf4j.Logger;

import java.time.Duration;

/**
 * Base utility class providing common wait creation and condition-checking logic for Selenium WebDriver.
 */
abstract class BaseWaitUtil {
    protected static final Logger logger = LoggerUtil.getLogger(BaseWaitUtil.class);

    protected static WebDriverWait createWebDriverWait(WebDriver driver, int timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    }

    protected static FluentWait<WebDriver> createFluentWait(WebDriver driver, int timeoutSeconds,
                                                            int pollingIntervalSeconds,
                                                            Class<? extends Throwable>... ignoredExceptions) {
        FluentWait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeoutSeconds))
                .pollingEvery(Duration.ofSeconds(pollingIntervalSeconds))
                .withMessage("Wait condition not met");
        for (Class<? extends Throwable> exception : ignoredExceptions) {
            wait.ignoring(exception);
        }
        return wait;
    }

    protected static <T> T waitForCondition(WebDriver driver, int timeoutSeconds,
                                            ExpectedCondition<T> condition, String description) {
        try {
            T result = createWebDriverWait(driver, timeoutSeconds).until(condition);
            logger.info("Wait succeeded for {}: {}", description, result);
            return result;
        } catch (TimeoutException e) {
            throw new WaitTimeoutException("Timed out waiting for " + description, e);
        }
    }
}
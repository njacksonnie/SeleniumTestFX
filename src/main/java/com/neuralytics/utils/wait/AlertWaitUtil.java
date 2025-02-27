package com.neuralytics.utils.wait;

import com.neuralytics.utils.LoggerUtil;
import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;

/**
 * Utility class for waiting on JavaScript alert-related conditions in Selenium
 * WebDriver.
 */
public class AlertWaitUtil extends BaseWaitUtil {
    private static final Logger logger = LoggerUtil.getLogger(AlertWaitUtil.class);

    public static Alert waitForAlert(WebDriver driver, int timeoutSeconds) {
        return waitForCondition(driver, timeoutSeconds,
                ExpectedConditions.alertIsPresent(),
                "JS alert");
    }

    public static Alert waitForAlert(WebDriver driver, int timeoutSeconds, int pollingIntervalSeconds) {
        return createFluentWait(driver, timeoutSeconds, pollingIntervalSeconds, NoAlertPresentException.class)
                .until(ExpectedConditions.alertIsPresent());
    }

    public static String getAlertTextAndAccept(WebDriver driver, int timeoutSeconds) {
        Alert alert = waitForAlert(driver, timeoutSeconds);
        String text = alert.getText();
        alert.accept();
        logger.info("Accepted alert with text: {}", text);
        return text;
    }

    public static void acceptAlert(WebDriver driver, int timeoutSeconds) {
        waitForAlert(driver, timeoutSeconds).accept();
        logger.info("Accepted JS alert");
    }

    public static void dismissAlert(WebDriver driver, int timeoutSeconds) {
        waitForAlert(driver, timeoutSeconds).dismiss();
        logger.info("Dismissed JS alert");
    }

    public static void sendKeysToAlert(WebDriver driver, int timeoutSeconds, String value) {
        Alert alert = waitForAlert(driver, timeoutSeconds);
        alert.sendKeys(value);
        alert.accept();
        logger.info("Sent keys to alert: {}", value);
    }
}
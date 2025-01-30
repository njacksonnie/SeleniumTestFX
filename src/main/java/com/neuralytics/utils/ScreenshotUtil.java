package com.neuralytics.utils;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class ScreenshotUtil {

    // Logger instance
    private static final Logger logger = LoggerUtil.getLogger(ScreenshotUtil.class);

    // Constants for configuration
    private static final String SCREENSHOT_DIR = "./screenshots/";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd-HHmmss");

    // Static block to initialize the screenshot directory
    static {
        createScreenshotDirectory();
    }

    /**
     * Private constructor to prevent instantiation (Utility Class Pattern).
     */
    private ScreenshotUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated.");
    }

    /**
     * Creates the screenshot directory if it doesn't exist.
     */
    private static void createScreenshotDirectory() {
        File directory = new File(SCREENSHOT_DIR);
        if (!directory.exists() && !directory.mkdirs()) {
            logger.error("Failed to create screenshots directory: {}", SCREENSHOT_DIR);
        } else {
            logger.trace("Screenshot directory initialized: {}", SCREENSHOT_DIR);
        }
    }

    /**
     * Captures a full-page screenshot and saves it with a timestamped filename.
     *
     * @param driver   WebDriver instance
     * @param testName Name of the test case
     * @return Absolute path of the saved screenshot
     */
    public static synchronized String captureFullPageScreenshot(WebDriver driver, String testName) {
        Objects.requireNonNull(driver, "WebDriver cannot be null.");
        Objects.requireNonNull(testName, "Test name cannot be null.");

        try {
            logger.trace("Capturing full-page screenshot for test: {}", testName);

            // Ensure the driver supports screenshot capture
            if (!(driver instanceof TakesScreenshot)) {
                logger.error("WebDriver does not support screenshot capture: {}", driver.getClass().getName());
                ReportManager.getInstance().logFail("WebDriver does not support screenshot capture.");
                return null;
            }

            // Capture the screenshot
            File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String screenshotPath = saveScreenshot(screenshotFile, testName, "full_page");

            // Log success
            logger.info("Full-page screenshot captured for test {}: {}", testName, screenshotPath);
            ReportManager.getInstance().logInfo("Full-page screenshot captured: " + screenshotPath);

            return screenshotPath;
        } catch (Exception e) {
            String errorMessage = "Failed to capture full-page screenshot for test: " + testName;
            logger.error(errorMessage, e);
            ReportManager.getInstance().logFail(errorMessage + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Captures a screenshot of a specific element.
     *
     * @param driver   WebDriver instance
     * @param element  WebElement to capture
     * @param testName Name of the test case
     * @return Absolute path of the saved screenshot
     */
    public static synchronized String captureElementScreenshot(WebDriver driver, WebElement element, String testName) {
        Objects.requireNonNull(element, "WebElement cannot be null.");
        try {
            logger.trace("Capturing screenshot of element in test: {}", testName);
            File screenshotFile = element.getScreenshotAs(OutputType.FILE);
            String screenshotPath = saveScreenshot(screenshotFile, testName, "element");
            ReportManager.getInstance().logInfo("Element screenshot captured: " + screenshotPath);
            return screenshotPath;
        } catch (Exception e) {
            String errorMessage = "Failed to capture element screenshot for test: " + testName;
            logger.error(errorMessage, e);
            ReportManager.getInstance().logFail(errorMessage);
            return null;
        }
    }

    /**
     * Captures a screenshot and returns it as a Base64 encoded string.
     *
     * @param driver WebDriver instance
     * @return Base64 encoded screenshot
     */
    public static synchronized String captureScreenshotAsBase64(WebDriver driver) {
        Objects.requireNonNull(driver, "WebDriver cannot be null.");
        try {
            logger.trace("Capturing screenshot as Base64");
            String base64Screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
            ReportManager.getInstance().logInfo("Screenshot captured as Base64.");
            return base64Screenshot;
        } catch (Exception e) {
            String errorMessage = "Failed to capture screenshot as Base64";
            logger.error(errorMessage, e);
            ReportManager.getInstance().logFail(errorMessage);
            return null;
        }
    }

    /**
     * General method to capture and save a screenshot.
     *
     * @param driver   WebDriver instance
     * @param testName Name of the test case
     * @param type     Type of screenshot (full_page, element, etc.)
     * @return Absolute path of the saved screenshot
     */
    private static synchronized String captureScreenshot(WebDriver driver, String testName, String type) {
        Objects.requireNonNull(driver, "WebDriver cannot be null.");
        Objects.requireNonNull(testName, "Test name cannot be null.");
        try {
            logger.trace("Capturing {} screenshot for test: {}", type, testName);
            File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String screenshotPath = saveScreenshot(screenshotFile, testName, type);
            ReportManager.getInstance().logInfo(type + " screenshot captured: " + screenshotPath);
            return screenshotPath;
        } catch (Exception e) {
            String errorMessage = "Failed to capture " + type + " screenshot for test: " + testName;
            logger.error(errorMessage, e);
            ReportManager.getInstance().logFail(errorMessage);
            return null;
        }
    }

    /**
     * Saves the captured screenshot with a timestamped filename.
     *
     * @param screenshotFile Screenshot file to save
     * @param testName       Name of the test case
     * @param type           Type of screenshot (full_page, element, etc.)
     * @return Absolute path of the saved screenshot
     */
    private static synchronized String saveScreenshot(File screenshotFile, String testName, String type) {
        Objects.requireNonNull(screenshotFile, "Screenshot file cannot be null.");
        Objects.requireNonNull(testName, "Test name cannot be null.");
        Objects.requireNonNull(type, "Screenshot type cannot be null.");
        try {
            String timestamp = DATE_FORMAT.format(new Date());
            String fileName = String.format("%s_%s_%s.png", testName, type, timestamp);
            File destFile = new File(SCREENSHOT_DIR + fileName);
            FileUtils.copyFile(screenshotFile, destFile);
            logger.info("Screenshot saved: {}", destFile.getAbsolutePath());
            return destFile.getAbsolutePath();
        } catch (IOException e) {
            String errorMessage = "Failed to save screenshot for test: " + testName;
            logger.error(errorMessage, e);
            ReportManager.getInstance().logFail(errorMessage);
            return null;
        }
    }
}
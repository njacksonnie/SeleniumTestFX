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

    private static final Logger logger = LoggerUtil.getLogger(ScreenshotUtil.class);
    private static final String SCREENSHOT_DIR = "./screenshots/";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd-HHmmss");

    static {
        try {
            createScreenshotDirectory();
        } catch (Exception e) {
            logger.error("Failed to initialize screenshot directory: {}", e.getMessage(), e);
        }
    }

    private ScreenshotUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated.");
    }

    /**
     * Creates the screenshot directory if it doesn't exist with enhanced logging
     */
    private static void createScreenshotDirectory() {
        File directory = new File(SCREENSHOT_DIR);
        logger.trace("Initializing screenshot directory at: {}", directory.getAbsolutePath());

        if (directory.exists()) {
            logger.debug("Screenshot directory already exists: {}", directory.getAbsolutePath());
            return;
        }

        if (directory.mkdirs()) {
            logger.info("Created screenshot directory at: {}", directory.getAbsolutePath());
        } else {
            logger.error("FAILED to create screenshot directory at: {}", directory.getAbsolutePath());
        }
    }

    /**
     * Captures a full-page screenshot using WebDriver
     * @param driver    WebDriver instance
     * @param testName  Name of the test for screenshot naming
     * @return          Absolute path of the saved screenshot, or null if failed
     */
    public static String captureFullPageScreenshot(WebDriver driver, String testName) {
        Objects.requireNonNull(driver, "WebDriver cannot be null.");
        Objects.requireNonNull(testName, "Test name cannot be null.");

        try {
            if (!(driver instanceof TakesScreenshot)) {
                logger.error("Driver does not support taking screenshots: {}", driver.getClass().getName());
                return null;
            }

            File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            return saveScreenshot(screenshotFile, testName, "fullpage");
        } catch (Exception e) {
            String errorMessage = String.format("Failed to capture full page screenshot for test '%s': %s",
                    testName, e.getMessage());
            logger.error(errorMessage, e);
            ReportManager.getInstance().logFail(errorMessage);
            return null;
        }
    }

    /**
     * Captures a screenshot of a specific WebElement
     * @param element   WebElement to capture
     * @param testName  Name of the test for screenshot naming
     * @return          Absolute path of the saved screenshot, or null if failed
     */
    public static String captureElementScreenshot(WebElement element, String testName) {
        Objects.requireNonNull(element, "WebElement cannot be null.");
        Objects.requireNonNull(testName, "Test name cannot be null.");

        try {
            File screenshotFile = element.getScreenshotAs(OutputType.FILE);
            return saveScreenshot(screenshotFile, testName, "element");
        } catch (Exception e) {
            String errorMessage = String.format("Failed to capture element screenshot for test '%s': %s",
                    testName, e.getMessage());
            logger.error(errorMessage, e);
            ReportManager.getInstance().logFail(errorMessage);
            return null;
        }
    }

    /**
     * Enhanced save method with redundant directory checks and better path handling
     */
    private static synchronized String saveScreenshot(File screenshotFile, String testName, String type) {
        Objects.requireNonNull(screenshotFile, "Screenshot file cannot be null.");
        Objects.requireNonNull(testName, "Test name cannot be null.");
        Objects.requireNonNull(type, "Screenshot type cannot be null.");

        try {
            // Re-check directory existence before saving
            createScreenshotDirectory();

            String timestamp = DATE_FORMAT.format(new Date());
            String sanitizedTestName = testName.replaceAll("[^a-zA-Z0-9_-]", "_");
            String fileName = String.format("%s_%s_%s.png", sanitizedTestName, type, timestamp);

            File directory = new File(SCREENSHOT_DIR);
            File destFile = new File(directory, fileName);

            FileUtils.copyFile(screenshotFile, destFile);
            logger.info("Successfully saved screenshot to: {}", destFile.getAbsolutePath());
            return destFile.getAbsolutePath();
        } catch (IOException e) {
            String errorMessage = String.format("Failed to save screenshot for test '%s': %s", testName, e.getMessage());
            logger.error(errorMessage, e);
            ReportManager.getInstance().logFail(errorMessage);
            return null;
        }
    }
}
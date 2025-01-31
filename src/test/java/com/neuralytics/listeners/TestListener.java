package com.neuralytics.listeners;

import com.neuralytics.utils.LoggerUtil;
import com.neuralytics.utils.ReportManager;
import com.neuralytics.utils.ScreenshotUtil;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.util.Objects;

public class TestListener implements ITestListener {
    // Logger instance
    private static final Logger logger = LoggerUtil.getLogger(TestListener.class);

    /**
     * Called when the test suite starts.
     *
     * @param context TestNG context
     */
    @Override
    public synchronized void onStart(ITestContext context) {
        Objects.requireNonNull(context, "ITestContext cannot be null.");
        logJenkinsInfo();
        ReportManager.getInstance().logInfo("Test Suite Started: " + context.getName());
        logger.info("Test Suite Started: {}", context.getName());
    }

    /**
     * Called when a test starts.
     *
     * @param result TestNG result
     */
    @Override
    public synchronized void onTestStart(ITestResult result) {
        Objects.requireNonNull(result, "ITestResult cannot be null.");
        String testName = getTestName(result);
        ReportManager.getInstance().startTest(testName);
        ReportManager.getInstance().logInfo("Test Started: " + testName);
        logger.info("Test Started: {}", testName);
    }

    /**
     * Called when a test passes.
     *
     * @param result TestNG result
     */
    @Override
    public synchronized void onTestSuccess(ITestResult result) {
        Objects.requireNonNull(result, "ITestResult cannot be null.");
        String testName = getTestName(result);
        ReportManager.getInstance().logPass("Test Passed: " + testName);
        logger.info("Test Passed: {}", testName);
    }

    /**
     * Called when a test fails.
     *
     * @param result TestNG result
     */
    @Override
    public synchronized void onTestFailure(ITestResult result) {
        Objects.requireNonNull(result, "ITestResult cannot be null.");
        String testName = getTestName(result);
        Throwable throwable = result.getThrowable();

        // Log failure details
        ReportManager.getInstance().logFail("Test Failed: " + testName);
        if (throwable != null) {
            ReportManager.getInstance().logFail(throwable.getMessage());
            logger.error("Test Failed: {}", testName, throwable);
        }

        // Capture Screenshot on Failure
        captureScreenshotOnFailure(result, testName);
    }

    /**
     * Called when a test is skipped.
     *
     * @param result TestNG result
     */
    @Override
    public synchronized void onTestSkipped(ITestResult result) {
        Objects.requireNonNull(result, "ITestResult cannot be null.");
        String testName = getTestName(result);
        ReportManager.getInstance().logInfo("Test Skipped: " + testName);
        logger.warn("Test Skipped: {}", testName);
    }

    /**
     * Called when the test suite finishes.
     *
     * @param context TestNG context
     */
    @Override
    public synchronized void onFinish(ITestContext context) {
        Objects.requireNonNull(context, "ITestContext cannot be null.");
        ReportManager.getInstance().logInfo("Test Suite Finished: " + context.getName());
        ReportManager.getInstance().tearDown(); // Flush ExtentReports and close log file
        logger.info("Test Suite Finished: {}", context.getName());
        logJenkinsInfo();
    }

    /**
     * Extracts the test name from the TestNG result.
     *
     * @param result TestNG result
     * @return Test name
     */
    private String getTestName(ITestResult result) {
        return Objects.requireNonNull(result.getMethod(), "TestMethod cannot be null.").getMethodName();
    }

    /**
     * Uses Reflection to get WebDriver instance from the test class.
     *
     * @param result TestNG result instance
     * @return WebDriver instance, or null if unavailable
     */
    private WebDriver getDriverFromTestInstance(ITestResult result) {
        Object testClass = result.getInstance();
        try {
            Method getDriverMethod = testClass.getClass().getDeclaredMethod("getDriver");
            getDriverMethod.setAccessible(true); // Allow private method access
            return (WebDriver) getDriverMethod.invoke(testClass);
        } catch (NoSuchMethodException e) {
            logger.error("No 'getDriver' method found in test class: {}", testClass.getClass().getName(), e);
        } catch (Exception e) {
            logger.error("Failed to retrieve WebDriver instance for test: {}", getTestName(result), e);
        }
        return null;
    }

    /**
     * Logs Jenkins-specific environment variables.
     */
    private void logJenkinsInfo() {
        String jobName = System.getenv("JOB_NAME");
        String buildNumber = System.getenv("BUILD_NUMBER");
        if (jobName != null && buildNumber != null) {
            ReportManager.getInstance().logInfo("Jenkins Job: " + jobName + ", Build Number: " + buildNumber);
            logger.info("Jenkins Job: {}, Build Number: {}", jobName, buildNumber);
        }
    }

    /**
     * Captures a screenshot on test failure.
     *
     * @param result   TestNG result
     * @param testName Name of the test
     */
    private void captureScreenshotOnFailure(ITestResult result, String testName) {
        WebDriver driver = getDriverFromTestInstance(result);
        if (driver != null) {
            String screenshotPath = ScreenshotUtil.captureFullPageScreenshot(driver, testName);
            if (screenshotPath != null) {
                ReportManager.getInstance().logFail("Screenshot Captured: " + screenshotPath);
                logger.info("Screenshot captured for failed test {}: {}", testName, screenshotPath);
            } else {
                logger.warn("Failed to capture screenshot for test: {}", testName);
            }
        }
    }
}
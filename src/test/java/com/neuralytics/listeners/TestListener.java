package com.neuralytics.listeners;

import com.neuralytics.factories.ReportFactory;
import com.neuralytics.factories.ScreenshotFactory;
import com.neuralytics.utils.LoggerUtil;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.util.Objects;

public class TestListener implements ITestListener {
    private static final Logger logger = LoggerUtil.getLogger(TestListener.class);

    @Override
    public void onStart(ITestContext context) {
        Objects.requireNonNull(context, "ITestContext cannot be null.");
        logJenkinsInfo();
        ReportFactory.getInstance().logInfo("Test Suite Started: " + context.getName());
        logger.info("Test Suite Started: {}", context.getName());
    }

    @Override
    public void onTestStart(ITestResult result) {
        Objects.requireNonNull(result, "ITestResult cannot be null.");
        String testName = getFullTestName(result);
        ReportFactory.getInstance().startTest(testName);
        ReportFactory.getInstance().logInfo("Test Started: " + testName);
        logger.info("Test Started: {}", testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        Objects.requireNonNull(result, "ITestResult cannot be null.");
        String testName = getFullTestName(result);
        ReportFactory.getInstance().logPass("Test Passed: " + testName);
        logger.info("Test Passed: {}", testName);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        Objects.requireNonNull(result, "ITestResult cannot be null.");
        String testName = getFullTestName(result);
        Throwable throwable = result.getThrowable();
        ReportFactory.getInstance().logFail("Test Failed: " + testName);
        if (throwable != null) {
            ReportFactory.getInstance().logFail("Error: " + throwable.getMessage());
            logger.error("Test Failed: {}", testName, throwable);
        }
        captureScreenshotOnFailure(result, testName);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        Objects.requireNonNull(result, "ITestResult cannot be null.");
        String testName = getFullTestName(result);
        ReportFactory.getInstance().logInfo("Test Skipped: " + testName);
        logger.warn("Test Skipped: {}", testName);
    }

    @Override
    public void onFinish(ITestContext context) {
        Objects.requireNonNull(context, "ITestContext cannot be null.");
        ReportFactory.getInstance().logInfo("Test Suite Finished: " + context.getName());
        ReportFactory.getInstance().tearDown(); // Perform suite-level cleanup
        logger.info("Test Suite Finished: {}", context.getName());
        logJenkinsInfo();
    }

    private String getFullTestName(ITestResult result) {
        String className = result.getTestClass() != null ? result.getTestClass().getName() : "UnknownClass";
        String methodName = Objects.requireNonNull(result.getMethod(), "TestMethod cannot be null.").getMethodName();
        return className + "." + methodName;
    }

    private WebDriver getDriverFromTestInstance(ITestResult result) {
        Object testInstance = result.getInstance();
        if (testInstance == null) {
            logger.warn("Test instance is null. Cannot retrieve WebDriver.");
            return null;
        }
        try {
            // Traverse the class hierarchy to find the 'getDriver' method
            Class<?> clazz = testInstance.getClass();
            while (clazz != null) {
                try {
                    Method getDriverMethod = clazz.getDeclaredMethod("getDriver");
                    getDriverMethod.setAccessible(true);
                    return (WebDriver) getDriverMethod.invoke(testInstance);
                } catch (NoSuchMethodException e) {
                    clazz = clazz.getSuperclass(); // Move to the superclass
                }
            }
            logger.warn("No 'getDriver' method found in test class hierarchy: {}", testInstance.getClass().getName());
        } catch (Exception e) {
            logger.error("Failed to retrieve WebDriver instance for test: {}", getFullTestName(result), e);
        }
        return null;
    }

    private void logJenkinsInfo() {
        String jobName = System.getenv("JOB_NAME");
        String buildNumber = System.getenv("BUILD_NUMBER");
        if (jobName != null && buildNumber != null) {
            String jenkinsInfo = "Jenkins Job: " + jobName + ", Build Number: " + buildNumber;
            ReportFactory.getInstance().logInfo(jenkinsInfo);
            logger.info(jenkinsInfo);
        } else {
            logger.info("Not running in Jenkins. Skipping Jenkins info logging.");
        }
    }

    private void captureScreenshotOnFailure(ITestResult result, String testName) {
        WebDriver driver = getDriverFromTestInstance(result);
        if (driver == null) {
            logger.warn("WebDriver is null. Cannot capture screenshot for test: {}", testName);
            return;
        }
        if (!(driver instanceof TakesScreenshot)) {
            logger.error("Driver does not support taking screenshots: {}", driver.getClass().getName());
            return;
        }
        String screenshotPath = ScreenshotFactory.captureFullPageScreenshot(driver, testName);
        if (screenshotPath != null) {
            ReportFactory.getInstance().logFail("Screenshot Captured: " + screenshotPath);
            logger.info("Screenshot captured for failed test {}: {}", testName, screenshotPath);
        } else {
            logger.warn("Failed to capture screenshot for test: {}", testName);
        }
    }
}
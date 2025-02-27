package com.neuralytics.listeners;

import com.neuralytics.factories.ReportFactory;
import com.neuralytics.factories.ScreenshotFactory;
import com.neuralytics.utils.CiCdDetector;
import com.neuralytics.utils.LoggerUtil;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * A TestNG listener for managing test lifecycle events with logging, reporting,
 * and screenshot capture.
 * Implements {@link ITestListener} to handle events such as test suite start,
 * test execution (start, success,
 * failure, skipped), and suite finish. Integrates with {@link ReportFactory}
 * for test and suite-level reporting,
 * {@link ScreenshotFactory} for capturing screenshots on failure, and
 * {@link CiCdDetector} for logging CI/CD
 * environment details. Uses SLF4J via {@link LoggerUtil} for consistent
 * logging.
 *
 * <p>
 * Usage:
 * 
 * <pre>
 *     &lt;listeners&gt;
 *         &lt;listener class-name="com.neuralytics.listeners.TestListener"/&gt;
 *     &lt;/listeners&gt;
 * </pre>
 * 
 * Include this in your TestNG XML configuration to enable the listener for all
 * tests.
 *
 * <p>
 * This listener assumes test classes may implement {@code WebDriverProvider} to
 * provide WebDriver instances
 * for screenshot capture on failure. Suite-level events are logged using
 * {@link ReportFactory#logSuiteInfo(String)}.
 *
 * @see ITestListener
 * @see ReportFactory
 * @see ScreenshotFactory
 * @see CiCdDetector
 */
public class TestListener implements ITestListener {

    /**
     * Logger instance for recording test lifecycle events, errors, and CI/CD
     * information.
     */
    private static final Logger logger = LoggerUtil.getLogger(TestListener.class);

    /**
     * Called when the test suite starts.
     * Logs CI/CD information and suite start event at the suite level using
     * {@link ReportFactory#logSuiteInfo(String)}.
     *
     * @param context the TestNG test context
     * @throws NullPointerException if the context is null
     */
    @Override
    public void onStart(ITestContext context) {
        validateContext(context);
        logCiCdInfo();
        String message = String.format("Test Suite Started: %s", context.getName());
        ReportFactory.getInstance().logSuiteInfo(message);
        logger.info(message);
    }

    /**
     * Called when an individual test starts.
     * Initiates reporting for the test and logs the start event.
     *
     * @param result the TestNG test result
     * @throws NullPointerException if the result is null
     */
    @Override
    public void onTestStart(ITestResult result) {
        validateResult(result);
        String testName = getFullTestName(result);
        ReportFactory.getInstance().startTest(testName);
        String message = String.format("Test Started: %s", testName);
        logAndReport(message, logger::info);
    }

    /**
     * Called when a test passes.
     * Logs and reports the success event at the test level.
     *
     * @param result the TestNG test result
     * @throws NullPointerException if the result is null
     */
    @Override
    public void onTestSuccess(ITestResult result) {
        validateResult(result);
        String testName = getFullTestName(result);
        String message = String.format("Test Passed: %s", testName);
        logAndReport(message, ReportFactory.getInstance()::logPass, logger::info);
    }

    /**
     * Called when a test fails.
     * Logs and reports the failure, including any throwable details, and captures a
     * screenshot if possible.
     *
     * @param result the TestNG test result
     * @throws NullPointerException if the result is null
     */
    @Override
    public void onTestFailure(ITestResult result) {
        validateResult(result);
        String testName = getFullTestName(result);
        Throwable throwable = result.getThrowable();
        String message = String.format("Test Failed: %s", testName);
        logAndReport(message, ReportFactory.getInstance()::logFail, logger::error);
        if (throwable != null) {
            String errorMessage = String.format("Error: %s", throwable.getMessage());
            logAndReport(errorMessage, ReportFactory.getInstance()::logFail, logger::error);
            logger.error("Test Failed: {}", testName, throwable);
        }
        captureScreenshotOnFailure(result, testName);
    }

    /**
     * Called when a test is skipped.
     * Logs and reports the skipped event at the test level.
     *
     * @param result the TestNG test result
     * @throws NullPointerException if the result is null
     */
    @Override
    public void onTestSkipped(ITestResult result) {
        validateResult(result);
        String testName = getFullTestName(result);
        String message = String.format("Test Skipped: %s", testName);
        logAndReport(message, logger::warn);
    }

    /**
     * Called when the test suite finishes.
     * Logs suite finish event and CI/CD information at the suite level, then
     * finalizes reporting.
     *
     * @param context the TestNG test context
     * @throws NullPointerException if the context is null
     */
    @Override
    public void onFinish(ITestContext context) {
        validateContext(context);
        String message = String.format("Test Suite Finished: %s", context.getName());
        ReportFactory.getInstance().logSuiteInfo(message);
        logger.info(message);
        ReportFactory.getInstance().tearDown();
        logCiCdInfo();
    }

    /**
     * Validates that the provided {@link ITestContext} is not null.
     *
     * @param context the TestNG test context to validate
     * @throws NullPointerException if the context is null
     */
    private void validateContext(ITestContext context) {
        Objects.requireNonNull(context, "ITestContext cannot be null.");
    }

    /**
     * Validates that the provided {@link ITestResult} is not null.
     *
     * @param result the TestNG test result to validate
     * @throws NullPointerException if the result is null
     */
    private void validateResult(ITestResult result) {
        Objects.requireNonNull(result, "ITestResult cannot be null.");
    }

    /**
     * Constructs the full test name from the class and method names.
     * Combines the test class name and method name (e.g., "MyTestClass.testMethod")
     * for unique identification.
     *
     * @param result the TestNG test result
     * @return the full test name in the format "className.methodName"
     * @throws NullPointerException if the test method is null
     */
    private String getFullTestName(ITestResult result) {
        String className = result.getTestClass() != null ? result.getTestClass().getName() : "UnknownClass";
        String methodName = Objects.requireNonNull(result.getMethod(), "TestMethod cannot be null.").getMethodName();
        return String.format("%s.%s", className, methodName);
    }

    /**
     * Retrieves the WebDriver instance from the test class if it implements
     * {@code WebDriverProvider}.
     * Returns null if the instance is unavailable or does not support WebDriver
     * provision.
     *
     * @param result the TestNG test result containing the test instance
     * @return the {@link WebDriver} instance, or null if not available
     */
    private WebDriver getDriverFromTestInstance(ITestResult result) {
        Object testInstance = result.getInstance();
        if (testInstance == null) {
            logger.warn("Test instance is null. Cannot retrieve WebDriver.");
            return null;
        }
        if (testInstance instanceof WebDriverProvider) {
            try {
                return ((WebDriverProvider) testInstance).getWebDriver();
            } catch (Exception e) {
                logger.error("Failed to retrieve WebDriver from test instance: {}", testInstance.getClass().getName(),
                        e);
                return null;
            }
        } else {
            logger.warn("Test class {} does not implement WebDriverProvider.", testInstance.getClass().getName());
            return null;
        }
    }

    /**
     * Logs CI/CD environment information if detected.
     * Uses {@link CiCdDetector} to retrieve details and logs them at the suite
     * level via
     * {@link ReportFactory#logSuiteInfo(String)}.
     */
    private void logCiCdInfo() {
        Map<String, String> ciCdInfo = CiCdDetector.getEnvironmentInfo();
        if (!ciCdInfo.isEmpty()) {
            StringBuilder infoBuilder = new StringBuilder("CI/CD Info: ");
            ciCdInfo.forEach((key, value) -> infoBuilder.append(key).append(": ").append(value).append(", "));
            String info = infoBuilder.toString().replaceAll(", $", "");
            ReportFactory.getInstance().logSuiteInfo(info);
            logger.info(info);
        } else {
            logger.info("No CI/CD environment detected. Skipping CI/CD info logging.");
        }
    }

    /**
     * Captures a screenshot on test failure if a compatible WebDriver is available.
     * Uses {@link ScreenshotFactory} to save the screenshot and logs the result at
     * the test level.
     *
     * @param result   the TestNG test result
     * @param testName the full test name for screenshot naming
     */
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
            String message = String.format("Screenshot Captured: %s", screenshotPath);
            logAndReport(message, ReportFactory.getInstance()::logFail, logger::info);
        } else {
            logger.warn("Failed to capture screenshot for test: {}", testName);
        }
    }

    /**
     * Logs a message to both the report and SLF4J logger at the specified level.
     * Uses {@link ReportFactory#logInfo(String)} for reporting test-level info
     * events.
     *
     * @param message      the message to log
     * @param loggerMethod the SLF4J logging method (e.g., {@code logger::info})
     */
    private void logAndReport(String message, Consumer<String> loggerMethod) {
        ReportFactory.getInstance().logInfo(message);
        loggerMethod.accept(message);
    }

    /**
     * Logs a message to both the report and SLF4J logger with custom reporting and
     * logging methods.
     * Allows flexibility in reporting (e.g., pass, fail) while maintaining SLF4J
     * logging consistency.
     *
     * @param message      the message to log
     * @param reportMethod the reporting method (e.g.,
     *                     {@code ReportFactory.getInstance()::logFail})
     * @param loggerMethod the SLF4J logging method (e.g., {@code logger::error})
     */
    private void logAndReport(String message, Consumer<String> reportMethod, Consumer<String> loggerMethod) {
        reportMethod.accept(message);
        loggerMethod.accept(message);
    }
}
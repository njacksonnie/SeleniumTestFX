package com.neuralytics.tests;

import com.neuralytics.factories.DriverFactory;
import com.neuralytics.factories.ReportFactory;
import com.neuralytics.utils.ConfigLoader;
import com.neuralytics.utils.LoggerUtil;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.util.Objects;

/**
 * A base class for TestNG tests providing WebDriver setup, tearDown, and
 * reporting integration.
 * Manages the test lifecycle at suite, class, and method levels, initializing
 * the WebDriver via
 * {@link DriverFactory}, configuring properties with {@link ConfigLoader}, and
 * handling reporting
 * through {@link ReportFactory}. This class is annotated with
 * {@code @Listeners} to integrate with
 * {@link com.neuralytics.listeners.TestListener} for event logging and
 * screenshot capture.
 * The WebDriver is preserved on test failure to allow screenshot capture by the
 * listener, and
 * configuration is loaded from properties defined in {@link ConfigLoader}.
 *
 * @see DriverFactory
 * @see ReportFactory
 * @see ConfigLoader
 */
@Listeners(com.neuralytics.listeners.TestListener.class)
public class BaseTest {

    /**
     * Logger instance for recording test lifecycle events and errors.
     */
    private static final Logger logger = LoggerUtil.getLogger(BaseTest.class);

    /**
     * The WebDriver instance used for browser automation, accessible to subclasses.
     */
    protected WebDriver driver;

    /**
     * The name of the current test method, set during setup for logging and
     * reporting.
     */
    private String testName;

    /**
     * Sets up the test suite by loading configuration properties and initializing
     * the report log file.
     * Executed once before the entire test suite, ensuring properties and reporting
     * are ready.
     *
     * @throws Exception if configuration loading or log file initialization fails
     */
    @BeforeSuite(alwaysRun = true)
    public void setUpSuite() throws Exception {
        ConfigLoader.loadProperties();
        ReportFactory.getInstance().initializeLogFile();
        logger.info("Test Suite Setup Completed.");
    }

    /**
     * Tears down the test suite by finalizing the report and closing resources.
     * Executed once after the entire test suite completes, flushing the report
     * output.
     */
    @AfterSuite(alwaysRun = true)
    public void tearDownSuite() {
        ReportFactory.getInstance().tearDown();
        logger.info("Test Suite TearDown Completed.");
    }

    /**
     * Sets up the test class before any test methods run.
     * Provides a hook for class-level initialization, currently logging the event.
     */
    @BeforeClass(alwaysRun = true)
    public void setUpClass() {
        logger.info("Test Class Setup Completed.");
    }

    /**
     * Tears down the test class after all test methods have run.
     * Provides a hook for class-level cleanup, currently logging the event.
     */
    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        logger.info("Test Class TearDown Completed.");
    }

    /**
     * Sets up each test method by initializing the WebDriver and navigating to the
     * application URL.
     * Initializes the WebDriver via {@link DriverFactory}, sets the test name,
     * starts reporting,
     * and navigates to the URL specified in the configuration properties.
     *
     * @param result the TestNG test result containing method information
     * @throws RuntimeException if WebDriver initialization or navigation fails
     */
    @BeforeMethod(alwaysRun = true)
    public void setUp(ITestResult result) {
        Objects.requireNonNull(result, "ITestResult cannot be null");
        try {
            driver = DriverFactory.initDriver();
            testName = result.getMethod().getMethodName();
            ReportFactory.getInstance().startTest(testName);
            logger.info("Test Setup Completed for: {}", testName);
            final String appUrl = ConfigLoader.loadProperties().getProperty("url");
            DriverFactory.navigateToUrl(appUrl);
        } catch (Exception e) {
            logger.error("Error during test setup for: {}", testName, e);
            throw new RuntimeException("Test setup failed", e);
        }
    }

    /**
     * Tears down each test method by cleaning up the WebDriver and ending
     * reporting.
     * Quits the WebDriver unless the test failed (to preserve it for screenshots),
     * then ends the
     * test in {@link ReportFactory}. Handles exceptions gracefully to avoid masking
     * test results.
     *
     * @param result the TestNG test result indicating the test outcome
     */
    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        Objects.requireNonNull(result, "ITestResult cannot be null");
        try {
            if (result.getStatus() == ITestResult.FAILURE) {
                logger.warn("Test failed: {}. Skipping driver cleanup for screenshot capture.", testName);
                return;
            }
            if (driver != null) {
                DriverFactory.quitDriver();
            }
        } catch (Exception e) {
            logger.error("Error during test tearDown for: {}", testName, e);
        } finally {
            ReportFactory.getInstance().endTest();
            logger.info("Test TearDown Completed for: {}", testName);
        }
    }

    /**
     * Retrieves the WebDriver instance for use in test methods or listeners.
     * Provides access to the initialized WebDriver for subclasses or external
     * components.
     *
     * @return the current {@link WebDriver} instance, or null if not yet
     *         initialized
     */
    public WebDriver getDriver() {
        return this.driver;
    }
}
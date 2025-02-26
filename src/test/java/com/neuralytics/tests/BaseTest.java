package com.neuralytics.tests;

import com.neuralytics.factories.DriverFactory;
import com.neuralytics.factories.ReportFactory;
import com.neuralytics.utils.ConfigLoader;
import com.neuralytics.utils.LoggerUtil;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.*;

@Listeners(com.neuralytics.listeners.TestListener.class) // Attach the TestListener for reporting and logging
public class BaseTest {
    private static final Logger logger = LoggerUtil.getLogger(BaseTest.class);
    protected WebDriver driver; // WebDriver instance for browser automation
    private String testName;

    @BeforeSuite(alwaysRun = true)
    public void setUpSuite() {
        ConfigLoader.loadProperties();
        ReportFactory.getInstance().initializeLogFile(); // Initialize log file here
        logger.info("Test Suite Setup Completed.");
    }

    @AfterSuite(alwaysRun = true)
    public void tearDownSuite() {
        ReportFactory.getInstance().tearDown(); // Flush the report and close the log file
        logger.info("Test Suite Teardown Completed.");
    }

    @BeforeClass(alwaysRun = true)
    public void setUpClass() {
        logger.info("Test Class Setup Completed.");
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        logger.info("Test Class Teardown Completed.");
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp(ITestResult result) {
        try {
            driver = DriverFactory.initDriver();
            testName = result.getMethod().getMethodName();
            ReportFactory.getInstance().startTest(testName);
            logger.info("Test Setup Completed for: " + testName);
            final String appUrl = ConfigLoader.loadProperties().getProperty("url");
            DriverFactory.navigateToUrl(appUrl);

        } catch (Exception e) {
            logger.error("Error during test setup for: " + testName, e);
            // Consider throwing a runtime exception to halt execution if setup fails
            throw new RuntimeException("Test setup failed", e);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        try {
            if (result.getStatus() == ITestResult.FAILURE) {
                // Do not quit the driver if the test failed
                logger.warn("Test failed: " + testName + ". Skipping driver cleanup for screenshot capture.");
                return;
            }
            if (driver != null) {
                DriverFactory.quitDriver();
            }
        } catch (Exception e) {
            logger.error("Error during test teardown for: " + testName, e);
            // Log the error, but don't re-throw as we don't want to mask the original test
            // failure
        } finally {
            ReportFactory.getInstance().endTest();
            logger.info("Test TearDown Completed for: " + testName);
        }
    }

    public WebDriver getDriver() {
        return this.driver;
    }
}

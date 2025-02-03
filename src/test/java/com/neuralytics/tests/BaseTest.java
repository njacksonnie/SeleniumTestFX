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

    @BeforeSuite(alwaysRun = true)
    public void setUpSuite() {
        ConfigLoader.loadProperties();
        logger.info("Test Suite Setup Completed.");
    }

    @AfterSuite(alwaysRun = true)
    public void tearDownSuite() {
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
    public void setUp() {
        driver = DriverFactory.initDriver();
        ReportFactory.getInstance().startTest(getTestName());
        logger.info("Test Setup Completed.");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            // Do not quit the driver if the test failed
            logger.warn("Test failed. Skipping driver cleanup for screenshot capture.");
            return;
        }
        if (driver != null) {
            DriverFactory.quitDriver();
        }
        ReportFactory.getInstance().endTest();
        logger.info("Test TearDown Completed.");
    }

    private String getTestName() {
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }

    public WebDriver getDriver() {
        return this.driver;
    }
}
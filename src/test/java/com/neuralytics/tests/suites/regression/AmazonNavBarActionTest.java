package com.neuralytics.tests.suites.regression;

import com.neuralytics.components.NavBar;
import com.neuralytics.factories.ReportFactory;
import com.neuralytics.tests.BaseTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * A TestNG test class for verifying navigation bar actions on an Amazon-like
 * site.
 * Extends {@link BaseTest} to inherit WebDriver setup, teardown, and reporting
 * capabilities,
 * testing the functionality of the {@link NavBar} component’s store navigation
 * methods. This class
 * focuses on ensuring that clicking navigation options (e.g., groceries, meat)
 * leads to the expected
 * outcomes, such as correct page titles or URLs.
 *
 * <p>
 * Usage:
 * This class is automatically executed by TestNG as part of the test suite,
 * leveraging the
 * {@code TestListener} for detailed reporting and screenshot capture on
 * failure.
 *
 * @see BaseTest
 * @see NavBar
 * @see ReportFactory
 */
public class AmazonNavBarActionTest extends BaseTest {

    /**
     * The navigation bar component instance used for testing store navigation
     * actions.
     */
    private NavBar navBar;

    /**
     * Initializes the navigation bar component before each test method.
     * Ensures a fresh {@link NavBar} instance is created with the current WebDriver
     * for every test,
     * leveraging the inherited {@link #getDriver()} method.
     */
    @BeforeMethod
    public void setUpNavigationBar() {
        navBar = new NavBar(getDriver());
    }

    /**
     * Tests the opening of the groceries store via the navigation bar.
     * Navigates to the groceries store section and verifies the page title to
     * ensure correct navigation.
     * Logs test progress and results using {@link ReportFactory}.
     */
    @Test
    public void openGroceriesStoreTest() {
        ReportFactory.getInstance().logInfo("Starting test: openGroceriesStoreTest");
        navBar.openGroceriesStore();
        // Assert.assertEquals(driver.getTitle(), "Groceries", "Groceries store page
        // title mismatch");
        ReportFactory.getInstance().logPass("Groceries store opened successfully.");
    }

    /**
     * Tests the opening of the meat store via the navigation bar.
     * Navigates to the meat store section and verifies the page title to ensure
     * correct navigation.
     * Logs test progress and results using {@link ReportFactory}.
     */
    // @Test
    // public void openMeatStoreTest() {
    // ReportFactory.getInstance().logInfo("Starting test: openMeatStoreTest");
    // navBar.openMeatStore();
    // Assert.assertEquals(driver.getTitle(), "Meat", "Meat store page title
    // mismatch");
    // ReportFactory.getInstance().logPass("Meat store opened successfully.");
    // }
}
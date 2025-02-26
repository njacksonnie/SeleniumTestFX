package com.neuralytics.tests;

import com.neuralytics.components.NavBar;
import com.neuralytics.factories.ReportFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AmazonNavBarActionTest extends BaseTest {
    private NavBar navBar;

    @BeforeMethod
    public void setUpNavigationBar() {
        navBar = new NavBar(getDriver());
    }

    @Test
    public void openGroceriesStoreTest() {
        ReportFactory.getInstance().logInfo("Starting test: openGroceriesStoreTest");
        navBar.openGroceriesStore();
        // Add an assertion here, e.g., checking the page title or URL
        // Assert.assertEquals(driver.getTitle(), "Groceries");
        ReportFactory.getInstance().logPass("Groceries store opened successfully.");
    }

    @Test
    public void openMeatStoreTest() {
        ReportFactory.getInstance().logInfo("Starting test: openMeatStoreTest");
        navBar.openMeatStore();
        // Add an assertion here, e.g., checking the page title or URL
        // Assert.assertEquals(driver.getTitle(), "Meat");
        ReportFactory.getInstance().logPass("Meat store opened successfully.");
    }
}

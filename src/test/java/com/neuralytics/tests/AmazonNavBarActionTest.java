package com.neuralytics.tests;

import com.neuralytics.components.NavBar;
import com.neuralytics.factories.ReportFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AmazonNavBarActionTest extends BaseTest {
    private NavBar navBar;

    // Initialize reportFactory using the singleton instance
    private final ReportFactory reportFactory = ReportFactory.getInstance();

    @BeforeMethod
    public void setUpNavigationBar() {
        navBar = new NavBar(getDriver());
    }

    @Test
    public void openGroceriesStoreTest() throws InterruptedException {
        reportFactory.logInfo("Starting test: openGroceriesStoreTest");
        navBar.openGroceriesStore();
        reportFactory.logPass("Product search completed successfully.");
    }

    @Test
    public void openMeatStoreTest() throws InterruptedException {
        reportFactory.logInfo("Starting test: openMeatStoreTest");
        navBar.openMeatStore();
        reportFactory.logPass("Product search completed successfully.");
    }
}
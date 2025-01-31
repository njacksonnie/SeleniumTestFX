package com.neuralytics.tests;

import com.neuralytics.components.AmazonNavigationBar;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class AmazonNavigationTest extends BaseTest {
    private AmazonNavigationBar navBar;

    @BeforeMethod
    public void setUpNavigationBar() {
        navBar = new AmazonNavigationBar(getDriver());
    }

    @Test
    public void testSearchFunctionality() {
        report.logInfo("Starting test: testSearchFunctionality");
        report.logInfo("Searching for product: Laptop");
        navBar.searchForProduct("Laptop");
        report.logPass("Product search completed successfully.");
    }

    @Test
    public void testNavigateToCart() {
        report.logInfo("Starting test: testNavigateToCart");
        report.logInfo("Navigating to the cart page.");
        navBar.navigateToCart();
        report.logPass("Navigation to cart completed successfully.");
    }

    @Test
    public void testOpenAllCategoriesMenu() {
        report.logInfo("Starting test: testOpenAllCategoriesMenu");
        report.logInfo("Opening the 'All Categories' menu.");
        navBar.openAllCategoriesMenu();
        report.logPass("'All Categories' menu opened successfully.");
    }

    @Test
    public void testAccountAndListsMenu() {
        report.logInfo("Starting test: testAccountAndListsMenu");
        report.logInfo("Navigating to the 'Account & Lists' menu.");
        navBar.navigateToAccountAndLists();
        report.logPass("Navigation to 'Account & Lists' menu completed successfully.");
    }

    @Test
    public void testNavigationBarVisibility() {
        report.logInfo("Starting test: testNavigationBarVisibility");

        SoftAssert softAssert = new SoftAssert();

        report.logInfo("Checking if the search box is displayed.");
        softAssert.assertTrue(navBar.isSearchBoxDisplayed(), "Search box is not displayed!");

        report.logInfo("Checking if the cart icon is displayed.");
        softAssert.assertTrue(navBar.isCartIconDisplayed(), "Cart icon is not displayed!");

        report.logInfo("Checking if the 'Account & Lists' menu is displayed.");
        softAssert.assertTrue(navBar.isAccountListMenuDisplayed(), "'Account & Lists' menu is not displayed!");

        softAssert.assertAll();
        report.logPass("Navigation bar visibility checks completed successfully.");
    }
}

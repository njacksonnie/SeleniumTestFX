package com.neuralytics.tests;

import com.neuralytics.components.AmazonNavigationBar;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AmazonNavigationTest extends BaseTest {

    @Test
    public void testSearchFunctionality() {
        report.logInfo("Starting test: testSearchFunctionality");
        try {
            AmazonNavigationBar navBar = new AmazonNavigationBar(getDriver());
            report.logInfo("Searching for product: Laptop");
            navBar.searchForProduct("Laptop");
            report.logPass("Product search completed successfully.");
        } catch (Exception e) {
            report.logFail("Error during testSearchFunctionality: " + e.getMessage());
            Assert.fail("Test failed due to an exception: " + e.getMessage());
        }
    }

    @Test
    public void testNavigateToCart() {
        report.logInfo("Starting test: testNavigateToCart");
        try {
            AmazonNavigationBar navBar = new AmazonNavigationBar(getDriver());
            report.logInfo("Navigating to the cart page.");
            navBar.navigateToCart();
            report.logPass("Navigation to cart completed successfully.");
        } catch (Exception e) {
            report.logFail("Error during testNavigateToCart: " + e.getMessage());
            Assert.fail("Test failed due to an exception: " + e.getMessage());
        }
    }

    @Test
    public void testOpenAllCategoriesMenu() {
        report.logInfo("Starting test: testOpenAllCategoriesMenu");
        try {
            AmazonNavigationBar navBar = new AmazonNavigationBar(getDriver());
            report.logInfo("Opening the 'All Categories' menu.");
            navBar.openAllCategoriesMenu();
            report.logPass("'All Categories' menu opened successfully.");
        } catch (Exception e) {
            report.logFail("Error during testOpenAllCategoriesMenu: " + e.getMessage());
            Assert.fail("Test failed due to an exception: " + e.getMessage());
        }
    }

    @Test
    public void testAccountAndListsMenu() {
        report.logInfo("Starting test: testAccountAndListsMenu");
        try {
            AmazonNavigationBar navBar = new AmazonNavigationBar(getDriver());
            report.logInfo("Navigating to the 'Account & Lists' menu.");
            navBar.navigateToAccountAndLists();
            report.logPass("Navigation to 'Account & Lists' menu completed successfully.");
        } catch (Exception e) {
            report.logFail("Error during testAccountAndListsMenu: " + e.getMessage());
            Assert.fail("Test failed due to an exception: " + e.getMessage());
        }
    }

    @Test
    public void testNavigationBarVisibility() {
        report.logInfo("Starting test: testNavigationBarVisibility");
        try {
            AmazonNavigationBar navBar = new AmazonNavigationBar(getDriver());
            report.logInfo("Checking if the search box is displayed.");
            boolean isSearchBoxDisplayed = navBar.isSearchBoxDisplayed();
            Assert.assertTrue(isSearchBoxDisplayed, "Search box is not displayed!");
            report.logInfo("Search box is displayed.");

            report.logInfo("Checking if the cart icon is displayed.");
            boolean isCartIconDisplayed = navBar.isCartIconDisplayed();
            Assert.assertTrue(isCartIconDisplayed, "Cart icon is not displayed!");
            report.logInfo("Cart icon is displayed.");

            report.logInfo("Checking if the 'Account & Lists' menu is displayed.");
            boolean isAccountListMenuDisplayed = navBar.isAccountListMenuDisplayed();
            Assert.assertTrue(isAccountListMenuDisplayed, "'Account & Lists' menu is not displayed!");
            report.logInfo("'Account & Lists' menu is displayed.");

            report.logPass("Navigation bar visibility checks completed successfully.");
        } catch (Exception e) {
            report.logFail("Error during testNavigationBarVisibility: " + e.getMessage());
            Assert.fail("Test failed due to an exception: " + e.getMessage());
        }
    }

    @Test
    public void testThatRequiresRestart() {
        report.logInfo("Starting test: testThatRequiresRestart");
        try {
            AmazonNavigationBar navBar = new AmazonNavigationBar(getDriver());
            report.logInfo("Performing actions that require a fresh browser.");
            navBar.searchForProduct("Restart Test");

            // Mark the browser for restart after this test
            markBrowserForRestart();
            report.logInfo("Marked browser for restart.");
            report.logPass("Actions requiring browser restart completed successfully.");
        } catch (Exception e) {
            report.logFail("Error during testThatRequiresRestart: " + e.getMessage());
            Assert.fail("Test failed due to an exception: " + e.getMessage());
        }
    }
}
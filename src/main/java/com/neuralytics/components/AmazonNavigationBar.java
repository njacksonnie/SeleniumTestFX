package com.neuralytics.components;

import com.neuralytics.utils.SeleniumWrapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AmazonNavigationBar {

    // WebDriver and SeleniumWrapper instances
    private final WebDriver driver;
    private final SeleniumWrapper selenium;

    // Locators for navigation bar components
    private static final By SEARCH_BOX = By.id("twotabsearchtextbox");
    private static final By SEARCH_BUTTON = By.id("nav-search-submit-button");
    private static final By ACCOUNT_LIST_MENU = By.id("nav-link-accountList");
    private static final By CART_ICON = By.id("nav-cart");
    private static final By ALL_CATEGORIES_MENU = By.id("nav-hamburger-menu");

    /**
     * Constructor to initialize the navigation bar.
     *
     * @param driver WebDriver instance
     */
    public AmazonNavigationBar(WebDriver driver) {
        this.driver = driver;
        this.selenium = new SeleniumWrapper(driver);
    }

    /**
     * Searches for a product on Amazon.
     *
     * @param productName The name of the product to search for
     */
    public void searchForProduct(String productName) {
        selenium.type(SEARCH_BOX, productName);
        selenium.click(SEARCH_BUTTON);
    }

    /**
     * Navigates to the "Account & Lists" menu.
     */
    public void navigateToAccountAndLists() {
        selenium.click(ACCOUNT_LIST_MENU);
    }

    /**
     * Navigates to the shopping cart page.
     */
    public void navigateToCart() {
        selenium.click(CART_ICON);
    }

    /**
     * Opens the "All Categories" menu (hamburger menu).
     */
    public void openAllCategoriesMenu() {
        selenium.click(ALL_CATEGORIES_MENU);
    }

    /**
     * Checks if the search box is displayed.
     *
     * @return true if the search box is displayed, false otherwise
     */
    public boolean isSearchBoxDisplayed() {
        return selenium.isDisplayed(SEARCH_BOX);
    }

    /**
     * Checks if the cart icon is displayed.
     *
     * @return true if the cart icon is displayed, false otherwise
     */
    public boolean isCartIconDisplayed() {
        return selenium.isDisplayed(CART_ICON);
    }

    /**
     * Checks if the "Account & Lists" menu is displayed.
     *
     * @return true if the "Account & Lists" menu is displayed, false otherwise
     */
    public boolean isAccountListMenuDisplayed() {
        return selenium.isDisplayed(ACCOUNT_LIST_MENU);
    }
}

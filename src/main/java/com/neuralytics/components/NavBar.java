package com.neuralytics.components;

import com.neuralytics.utils.ElementUtil;
import com.neuralytics.utils.JavaScriptUtil;
import com.neuralytics.utils.LoggerUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;

public class NavBar {

    // WebDriver and ElementUtil instances
    private final WebDriver driver;
    private final ElementUtil eleUtil;
    private final JavaScriptUtil jsUtil;
    private static final Logger logger = LoggerUtil.getLogger(NavBar.class);

    public NavBar(WebDriver driver) {
        this.driver = driver;
        this.eleUtil = new ElementUtil(driver);
        this.jsUtil = new JavaScriptUtil(driver);
    }

    // Locators for navigation bar components, organized by category
    private static class Locators {
        static final By SEARCH_BOX = By.id("twotabsearchtextbox");
        static final By SEARCH_BUTTON = By.id("nav-search-submit-button");
        static final By ACCOUNT_LIST_MENU = By.id("nav-link-accountList");
        static final By CART_ICON = By.id("nav-cart");
        static final By ALL_CATEGORIES_MENU = By.id("nav-hamburger-menu");
        static final By FRESH_MENU = By.xpath("//a[@id='nav-link-groceries']");
        static final By GROCERIES_STORE = By.cssSelector(".f3-cgi-flyout-store-box-left");
        static final By MEAT_STORE = By.cssSelector(".f3-cgi-flyout-store-box-right");
        static final By BEST_SELLERS = By.xpath("//a[@data-csa-c-content-id='nav_cs_bestsellers']");
        static final By MOBILES = By.xpath("//a[@data-csa-c-content-id='nav_cs_mobiles']");
        static final By TODAYS_DEALS = By.xpath("//a[@data-csa-c-content-id='nav_cs_gb']");
        static final By ELECTRONICS = By.xpath("//a[@data-csa-c-content-id='nav_cs_electronics']");
        static final By CUSTOMER_SERVICE = By.xpath("//a[@data-csa-c-content-id='nav_cs_help']");
        static final By AMAZON_PAY = By.xpath("//a[@data-csa-c-content-id='nav_cs_apay']");
        static final By HOME_KITCHEN = By.xpath("//a[@data-csa-c-content-id='nav_cs_home']");
        static final By NEW_RELEASES = By.xpath("//a[@data-csa-c-content-id='nav_cs_newreleases']");
        static final By FASHION = By.xpath("//a[@data-csa-c-content-id='nav_cs_fashion']");
        static final By COMPUTERS = By.xpath("//a[@data-csa-c-content-id='nav_cs_pc']");
        static final By CAR_MOTORBIKE = By.xpath("//a[@data-csa-c-content-id='nav_cs_automotive']");
        static final By PRIME = By.xpath("//a[@data-csa-c-content-id='nav_cs_primelink_nonmember']");
    }

    // Unused locators
    private static final By MX_PLAYER = By.xpath("//a[normalize-space()='MX Player']");
    private static final By SELL = By.xpath("//a[normalize-space()='Sell']");

    public void openGroceriesStore() {
        logger.info("Opening Groceries Store");
        try {
            eleUtil.handleParentSubMenu(Locators.FRESH_MENU, Locators.GROCERIES_STORE);
        } catch (Exception e) {
            logger.error("Error opening Groceries Store", e);
            // Consider re-throwing a custom exception or handling the error appropriately
        }
    }

    public void openMeatStore() {
        logger.info("Opening Meat Store");
        try {
            eleUtil.handleParentSubMenu(Locators.FRESH_MENU, Locators.MEAT_STORE);
        } catch (Exception e) {
            logger.error("Error opening Meat Store", e);
            // Consider re-throwing a custom exception or handling the error appropriately

        }
    }
}

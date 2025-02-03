package com.neuralytics.components;


import com.neuralytics.utils.ElementUtil;
import com.neuralytics.utils.JavaScriptUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class NavBar {

    // WebDriver and ElementUtil instances
    private final WebDriver driver;
    private final ElementUtil eleUtil;
    private final JavaScriptUtil jsUtil;

    public NavBar(WebDriver driver) {
        this.driver = driver;
        this.eleUtil = new ElementUtil(driver);
        this.jsUtil = new JavaScriptUtil(driver);

    }

    // Locators for navigation bar components
    private static final By SEARCH_BOX = By.id("twotabsearchtextbox");
    private static final By SEARCH_BUTTON = By.id("nav-search-submit-button");
    private static final By ACCOUNT_LIST_MENU = By.id("nav-link-accountList");
    private static final By CART_ICON = By.id("nav-cart");
    private static final By ALL_CATEGORIES_MENU = By.id("nav-hamburger-menu");
    private static final By FRESH_MENU = By.xpath("//a[@id='nav-link-groceries']");
    private static final By MX_PLAYER = By.xpath("//a[normalize-space()='MX Player']");
    private static final By SELL = By.xpath("//a[normalize-space()='Sell']");
    private static final By GROCERIES_STORE = By.cssSelector(".f3-cgi-flyout-store-box-left");
    private static final By MEAT_STORE = By.cssSelector(".f3-cgi-flyout-store-box-right");
    private static final By BEST_SELLERS = By.xpath("//a[@data-csa-c-content-id='nav_cs_bestsellers']");
    private static final By MOBILES = By.xpath("//a[@data-csa-c-content-id='nav_cs_mobiles']");
    private static final By TODAYS_DEALS = By.xpath("//a[@data-csa-c-content-id='nav_cs_gb']");
    private static final By Electronics = By.xpath("//a[@data-csa-c-content-id='nav_cs_electronics']");
    private static final By CUSTOMER_SERVICE = By.xpath("//a[@data-csa-c-content-id='nav_cs_help']");
    private static final By AMAZON_PAY = By.xpath("//a[@data-csa-c-content-id='nav_cs_apay']");
    private static final By HOME_KITCHEN = By.xpath("//a[@data-csa-c-content-id='nav_cs_home']");
    private static final By NEW_RELEASES = By.xpath("//a[@data-csa-c-content-id='nav_cs_newreleases']");
    private static final By FASHION = By.xpath("//a[@data-csa-c-content-id='nav_cs_fashion']");
    private static final By COMPUTERS = By.xpath("//a[@data-csa-c-content-id='nav_cs_pc']");
    private static final By CAR_MOTORBIKE = By.xpath("//a[@data-csa-c-content-id='nav_cs_automotive']");
    private static final By PRIME = By.xpath("//a[@data-csa-c-content-id='nav_cs_primelink_nonmember']");





    public void openGroceriesStore() throws InterruptedException {
        eleUtil.handleParentSubMenu(FRESH_MENU, GROCERIES_STORE);
    }

    public void openMeatStore() throws InterruptedException {
        eleUtil.handleParentSubMenu(FRESH_MENU, MEAT_STORE);
    }



}

package com.neuralytics.pages;

import com.neuralytics.utils.LoggerUtil;
import com.neuralytics.utils.element.ElementUtil;
import com.neuralytics.utils.element.JavaScriptUtil;
import com.neuralytics.utils.wait.NavigationWaitUtil;

import org.checkerframework.checker.units.qual.s;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;

public class HomePage {

    private final WebDriver driver;
    private final ElementUtil elementUtil;
    private final JavaScriptUtil jsUtil;
    private static final Logger logger = LoggerUtil.getLogger(NavBar.class);

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.elementUtil = new ElementUtil(driver);
        this.jsUtil = new JavaScriptUtil(driver);
    }

    private static final By LOGO_IMAGE = By
            .xpath("//div[@class='header_header_left_container__Qzy8S']/descendant::img");
    private static final By MEN_MENU = By
            .xpath("//div[@class='header_header_left_container__Qzy8S']/descendant::a[text()='Men']");
    private static final By WOMEN_MENU = By
            .xpath("//div[@class='header_header_left_container__Qzy8S']/descendant::a[text()='Women']");
    private static final By MOBILE_COVER_MENU = By
            .xpath("//div[@class='header_header_left_container__Qzy8S']/descendant::a[text()='Mobile Covers']");

    private static final By SEARCH_INPUT_FIELD = By
            .xpath("//form[@class='header_search_container__2QbV9']/descendant::input");
    private static final By LOGIN_BUTTON = By.xpath("//a[contains(@href, 'login')]");
    private static final By WISHLIST_BUTTON = By.xpath("//a[contains(@href, 'wishlist')]");
    private static final By CART_BUTTON = By.xpath("//a[contains(@href, 'cart')]");

    private static final By SEARCH_HEADING = By.xpath("//div[@class='lg:flex items-baseline hidden  gap-2']/child::h1");

    public void searchFunctionality(String searchTerm) {
        logger.info("Performing search functionality");
        try {
            elementUtil.doSendKeys(SEARCH_INPUT_FIELD, searchTerm);
            elementUtil.doPressKey(CART_BUTTON, Keys.ENTER);
            NavigationWaitUtil.waitForPageLoaded(driver, 10);
            String searchResult = elementUtil.doGetText(SEARCH_HEADING);
            System.out.println(searchResult);
        } catch (Exception e) {
            logger.error("Error performing search functionality", e);
        }
    }

}

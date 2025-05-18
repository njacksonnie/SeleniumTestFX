package com.neuralytics.tests.suites.smoke;

import org.testng.annotations.BeforeMethod;

import com.neuralytics.pages.HomePage;
import com.neuralytics.tests.BaseTest;
import org.testng.annotations.Test;

public class NavigationBarTest extends BaseTest {

    private HomePage homePage;

    @BeforeMethod
    public void setupHomePage() {
        homePage = new HomePage(driver);
    }

    @Test
    public void searchFunctionalityTest() {
        homePage.searchFunctionality("Shirt");
    }

}

package com.neuralytics.utils;

import com.neuralytics.exceptions.ElementException;
import com.neuralytics.exceptions.FrameworkException;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * A utility class for interacting with web elements using Selenium WebDriver.
 * Provides a comprehensive set of methods for common operations such as
 * clicking, sending keys,
 * retrieving text and attributes, handling dropdowns, and performing advanced
 * actions like drag-and-drop
 * and menu navigation. This class is designed to simplify element interactions
 * in a Selenium-based
 * testing framework, with built-in logging and exception handling.
 *
 * <p>
 * Usage example:
 * 
 * <pre>
 * WebDriver driver = DriverFactory.getDriver();
 * ElementUtil elementUtil = new ElementUtil(driver);
 * elementUtil.doSendKeys(By.id("username"), "testuser");
 * elementUtil.doClick(By.id("loginButton"));
 * String text = elementUtil.doGetText(By.id("welcomeMessage"));
 * </pre>
 *
 * <p>
 * All methods operate on elements identified by {@link By} locators and throw
 * {@link ElementException}
 * if elements are not found, ensuring robust error handling.
 *
 * @see WebDriver
 * @see Actions
 * @see ElementWaitUtil
 * @see NavigationWaitUtil
 */
public class ElementUtil {

    private final WebDriver driver;
    private final Actions act;
    private static final Logger logger = LoggerUtil.getLogger(ElementUtil.class);

    public ElementUtil(WebDriver driver) {
        if (driver == null) {
            throw new FrameworkException("WebDriver cannot be null");
        }
        this.act = new Actions(driver);
        this.driver = driver;
    }

    private void nullCheck(String value) {
        if (value == null) {
            logger.error("Value is null");
            throw new ElementException("VALUE IS NULL");
        }
    }

    private Select getSelect(By locator) {
        return new Select(getElement(locator));
    }

    public void doSendKeys(By locator, String value) {
        nullCheck(value);
        WebElement element = getElement(locator);
        element.clear();
        element.sendKeys(value);
        logger.info("Sent keys '{}' to element: {}", value, locator);
    }

    public void doSendKeys(By locator, String value, int timeOut) {
        nullCheck(value);
        WebElement element = ElementWaitUtil.waitForElementVisible(driver, locator, timeOut);
        element.clear();
        element.sendKeys(value);
        logger.info("Sent keys '{}' to element: {} with timeout: {}", value, locator, timeOut);
    }

    public WebElement getElement(By locator) {
        try {
            return driver.findElement(locator);
        } catch (NoSuchElementException e) {
            logger.warn("Element not found: {}", locator);
            throw new ElementException("Element not found: " + locator);
        }
    }

    public List<WebElement> getElements(By locator) {
        return driver.findElements(locator);
    }

    public void doClick(By locator) {
        WebElement element = getElement(locator);
        element.click();
        logger.info("Clicked on element: {}", locator);
    }

    public void doClick(By locator, int timeOut) {
        WebElement element = ElementWaitUtil.waitForElementVisible(driver, locator, timeOut);
        element.click();
        logger.info("Clicked on element: {} with timeout: {}", locator, timeOut);
    }

    // New overload using clickWhenReady for better reliability
    public void doClickWhenReady(By locator, int timeOut) {
        ElementWaitUtil.clickWhenReady(driver, locator, timeOut);
        logger.info("Clicked on element when ready: {} with timeout: {}", locator, timeOut);
    }

    public String doGetText(By locator) {
        WebElement element = getElement(locator);
        String text = element.getText();
        logger.info("Got text from element: {}: '{}'", locator, text);
        return text;
    }

    // New overload with wait for visibility
    public String doGetText(By locator, int timeOut) {
        WebElement element = ElementWaitUtil.waitForElementVisible(driver, locator, timeOut);
        String text = element.getText();
        logger.info("Got text from element: {} with timeout {}: '{}'", locator, timeOut, text);
        return text;
    }

    public String doGetAttribute(By locator, String attrName) {
        WebElement element = getElement(locator);
        String attributeValue = element.getDomAttribute(attrName);
        logger.info("Got attribute '{}' from element: {}: '{}'", attrName, locator, attributeValue);
        return attributeValue;
    }

    // New overload with wait for visibility
    public String doGetAttribute(By locator, String attrName, int timeOut) {
        WebElement element = ElementWaitUtil.waitForElementVisible(driver, locator, timeOut);
        String attributeValue = element.getDomAttribute(attrName);
        logger.info("Got attribute '{}' from element: {} with timeout {}: '{}'", attrName, locator, timeOut,
                attributeValue);
        return attributeValue;
    }

    public boolean doIsDisplayed(By locator) {
        try {
            boolean flag = getElement(locator).isDisplayed();
            logger.info("Element is displayed: {}", locator);
            return flag;
        } catch (ElementException e) {
            logger.warn("Element is not displayed: {}", locator);
            return false;
        }
    }

    public boolean isElementDisplayed(By locator) {
        List<WebElement> elements = driver.findElements(locator);
        boolean displayed = !elements.isEmpty() && elements.get(0).isDisplayed();
        logger.info("Element {} is displayed: {}", locator, displayed);
        return displayed;
    }

    public int getElementsCount(By locator) {
        int count = getElements(locator).size();
        logger.info("Found {} elements for locator: {}", count, locator);
        return count;
    }

    public List<String> getTextList(By locator) {
        List<WebElement> eleList = getElements(locator);
        List<String> eleTextList = new ArrayList<>();
        for (WebElement e : eleList) {
            String text = e.getText();
            if (!text.isEmpty()) {
                eleTextList.add(text);
            }
        }
        logger.info("Got text list for elements: {}: {}", locator, eleTextList);
        return eleTextList;
    }

    // New overload with wait for visibility of all elements
    public List<String> getTextList(By locator, int timeOut) {
        List<WebElement> eleList = ElementWaitUtil.waitForVisibilityOfElements(driver, locator, timeOut);
        List<String> eleTextList = new ArrayList<>();
        for (WebElement e : eleList) {
            String text = e.getText();
            if (!text.isEmpty()) {
                eleTextList.add(text);
            }
        }
        logger.info("Got text list for elements: {} with timeout {}: {}", locator, timeOut, eleTextList);
        return eleTextList;
    }

    public List<String> getAttributeList(By locator, String attrName) {
        List<WebElement> imagesList = getElements(locator);
        List<String> attrList = new ArrayList<>();
        for (WebElement e : imagesList) {
            String attrVal = e.getDomAttribute(attrName);
            if (attrVal != null && !attrVal.isEmpty()) {
                attrList.add(attrVal);
            }
        }
        logger.info("Got attribute list for elements: {}: {}", locator, attrList);
        return attrList;
    }

    // New overload with wait for visibility of all elements
    public List<String> getAttributeList(By locator, String attrName, int timeOut) {
        List<WebElement> imagesList = ElementWaitUtil.waitForVisibilityOfElements(driver, locator, timeOut);
        List<String> attrList = new ArrayList<>();
        for (WebElement e : imagesList) {
            String attrVal = e.getDomAttribute(attrName);
            if (attrVal != null && !attrVal.isEmpty()) {
                attrList.add(attrVal);
            }
        }
        logger.info("Got attribute list for elements: {} with timeout {}: {}", locator, timeOut, attrList);
        return attrList;
    }

    public void doSelectByIndex(By locator, int index) {
        getSelect(locator).selectByIndex(index);
        logger.info("Selected option by index {} for element: {}", index, locator);
    }

    // New overload with wait
    public void doSelectByIndex(By locator, int index, int timeOut) {
        WebElement element = ElementWaitUtil.waitForElementVisible(driver, locator, timeOut);
        new Select(element).selectByIndex(index);
        logger.info("Selected option by index {} for element: {} with timeout: {}", index, locator, timeOut);
    }

    public void doSelectByText(By locator, String visibleText) {
        getSelect(locator).selectByVisibleText(visibleText);
        logger.info("Selected option by visible text '{}' for element: {}", visibleText, locator);
    }

    public void doSelectByText(By locator, String visibleText, int timeOut) {
        WebElement element = ElementWaitUtil.waitForElementVisible(driver, locator, timeOut);
        new Select(element).selectByVisibleText(visibleText);
        logger.info("Selected option by visible text '{}' for element: {} with timeout: {}", visibleText, locator,
                timeOut);
    }

    public void doSelectByValue(By locator, String value) {
        getSelect(locator).selectByValue(value);
        logger.info("Selected option by value '{}' for element: {}", value, locator);
    }

    public void doSelectByValue(By locator, String value, int timeOut) {
        WebElement element = ElementWaitUtil.waitForElementVisible(driver, locator, timeOut);
        new Select(element).selectByValue(value);
        logger.info("Selected option by value '{}' for element: {} with timeout: {}", value, locator, timeOut);
    }

    public int getDropDownOptionsCount(By locator) {
        int count = getSelect(locator).getOptions().size();
        logger.info("Got dropdown options count for element: {}: {}", locator, count);
        return count;
    }

    public List<String> getDropdownOptions(By locator) {
        List<WebElement> optionsList = getSelect(locator).getOptions();
        List<String> optionsTextList = new ArrayList<>();
        for (WebElement e : optionsList) {
            optionsTextList.add(e.getText());
        }
        logger.info("Got dropdown options text list for element: {}: {}", locator, optionsTextList);
        return optionsTextList;
    }

    public void selectFromDropdown(By locator, String optionText) {
        List<WebElement> optionsList = getSelect(locator).getOptions();
        for (WebElement e : optionsList) {
            if (e.getText().equals(optionText.trim())) {
                e.click();
                logger.info("Selected option '{}' from dropdown: {}", optionText, locator);
                break;
            }
        }
    }

    public void selectFromDropdownAlt(By locator, String optionText) {
        List<WebElement> optionsList = getElements(locator);
        for (WebElement e : optionsList) {
            if (e.getText().equals(optionText)) {
                e.click();
                logger.info("Selected option '{}' from dropdown (without Select class): {}", optionText, locator);
                break;
            }
        }
    }

    public void searchAndSelect(By searchField, String searchKey, By suggestions, String value) {
        doSendKeys(searchField, searchKey);
        List<WebElement> suggList = ElementWaitUtil.waitForVisibilityOfElements(driver, suggestions, 10);
        logger.info("Search suggestions count: {}", suggList.size());
        for (WebElement e : suggList) {
            String text = e.getText();
            logger.info("Suggestion: {}", text);
            if (text.contains(value)) {
                e.click();
                logger.info("Selected suggestion: {}", text);
                break;
            }
        }
    }

    public void handleParentSubMenu(By parentLocator, By childLocator) {
        WebElement parent = getElement(parentLocator);
        act.moveToElement(parent).perform();
        logger.info("Moved to parent element: {}", parentLocator);
        doClick(childLocator);
        logger.info("Clicked on child element: {}", childLocator);
    }

    // New overload with waits
    public void handleParentSubMenu(By parentLocator, By childLocator, int timeOut) {
        WebElement parent = ElementWaitUtil.waitForElementVisible(driver, parentLocator, timeOut);
        act.moveToElement(parent).perform();
        logger.info("Moved to parent element: {} with timeout: {}", parentLocator, timeOut);
        ElementWaitUtil.clickWhenReady(driver, childLocator, timeOut);
        logger.info("Clicked on child element: {} with timeout: {}", childLocator, timeOut);
    }

    public void doDragAndDrop(By sourceLocator, By targetLocator) {
        act.dragAndDrop(getElement(sourceLocator), getElement(targetLocator)).perform();
        logger.info("Dragged element {} to element {}", sourceLocator, targetLocator);
    }

    // New overload with waits
    public void doDragAndDrop(By sourceLocator, By targetLocator, int timeOut) {
        WebElement source = ElementWaitUtil.waitForElementVisible(driver, sourceLocator, timeOut);
        WebElement target = ElementWaitUtil.waitForElementVisible(driver, targetLocator, timeOut);
        act.dragAndDrop(source, target).perform();
        logger.info("Dragged element {} to element {} with timeout: {}", sourceLocator, targetLocator, timeOut);
    }

    public void doActionsSendKeys(By locator, String value) {
        act.sendKeys(getElement(locator), value).perform();
        logger.info("Sent keys '{}' to element using Actions: {}", value, locator);
    }

    public void doActionsClick(By locator) {
        act.click(getElement(locator)).perform();
        logger.info("Clicked on element using Actions: {}", locator);
    }

    public void doActionSendKeysSlow(By locator, String value, long pauseTime) {
        char[] ch = value.toCharArray();
        for (char c : ch) {
            act.sendKeys(getElement(locator), String.valueOf(c)).pause(pauseTime).perform();
        }
        logger.info("Sent keys '{}' to element using Actions with pause: {} ms", value, pauseTime);
    }

    public void doActionSendKeysSlow(By locator, String value) {
        char[] ch = value.toCharArray();
        for (char c : ch) {
            act.sendKeys(getElement(locator), String.valueOf(c)).pause(500).perform();
        }
        logger.info("Sent keys '{}' to element using Actions with default pause", value);
    }

    public void navigate4LevelMenuClick(By level1, String level2, String level3, String level4) {
        ElementWaitUtil.clickWhenReady(driver, level1, 10);
        WebElement lvl2 = ElementWaitUtil.waitForElementVisible(driver, By.linkText(level2), 10);
        act.moveToElement(lvl2).perform();
        WebElement lvl3 = ElementWaitUtil.waitForElementVisible(driver, By.linkText(level3), 10);
        act.moveToElement(lvl3).perform();
        ElementWaitUtil.clickWhenReady(driver, By.linkText(level4), 10);
        logger.info("Navigated through 4-level menu using click: {}, {}, {}, {}", level1, level2, level3, level4);
    }

    public void navigate4LevelMenuClick(By level1, By level2, By level3, By level4) {
        ElementWaitUtil.clickWhenReady(driver, level1, 10);
        WebElement lvl2 = ElementWaitUtil.waitForElementVisible(driver, level2, 10);
        act.moveToElement(lvl2).perform();
        WebElement lvl3 = ElementWaitUtil.waitForElementVisible(driver, level3, 10);
        act.moveToElement(lvl3).perform();
        ElementWaitUtil.clickWhenReady(driver, level4, 10);
        logger.info("Navigated through 4-level menu using click: {}, {}, {}, {}", level1, level2, level3, level4);
    }

    public void navigate4LevelMenuHover(By level1, By level2, By level3, By level4) {
        WebElement lvl1 = ElementWaitUtil.waitForElementVisible(driver, level1, 10);
        act.moveToElement(lvl1).perform();
        WebElement lvl2 = ElementWaitUtil.waitForElementVisible(driver, level2, 10);
        act.moveToElement(lvl2).perform();
        WebElement lvl3 = ElementWaitUtil.waitForElementVisible(driver, level3, 10);
        act.moveToElement(lvl3).perform();
        ElementWaitUtil.clickWhenReady(driver, level4, 10);
        logger.info("Navigated through 4-level menu using mouse hover: {}, {}, {}, {}", level1, level2, level3, level4);
    }
}
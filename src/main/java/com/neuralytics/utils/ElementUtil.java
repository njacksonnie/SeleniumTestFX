package com.neuralytics.utils;

import com.neuralytics.exceptions.ElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ElementUtil {

    private final WebDriver driver;
    private final JavaScriptUtil jsUtil;
    private static final Logger logger = LoggerUtil.getLogger(ElementUtil.class);

    public ElementUtil(WebDriver driver) {
        this.driver = driver;
        this.jsUtil = new JavaScriptUtil(driver);
    }

    private void nullCheck(String value) {
        if (value == null) {
            logger.error("Value is null");
            throw new ElementException("VALUE IS NULL");
        }
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
        WebElement element = WaitUtil.waitForElementVisible(driver, locator, timeOut);
        element.clear();
        element.sendKeys(value);
        logger.info("Sent keys '{}' to element: {} with timeout: {}", value, locator, timeOut);
    }

    // Removed redundant doSendKeys overload

    public WebElement getElement(By locator) {
        List<WebElement> elements = driver.findElements(locator);
        if (elements.isEmpty()) {
            logger.warn("Element not found: {}", locator);
            throw new ElementException("Element not found: " + locator);
        }
        if (elements.size() > 1) {
            logger.warn("Multiple elements found for: {}, returning the first one", locator);
        }
        return elements.get(0);
    }

    public void doClick(By locator) {
        WebElement element = getElement(locator);
        element.click();
        logger.info("Clicked on element: {}", locator);
    }

    public void doClick(By locator, int timeOut) {
        WebElement element = WaitUtil.waitForElementVisible(driver, locator, timeOut);
        element.click();
        logger.info("Clicked on element: {} with timeout: {}", locator, timeOut);
    }

    public String doGetText(By locator) {
        WebElement element = getElement(locator);
        String text = element.getText();
        logger.info("Got text from element: {}: '{}'", locator, text);
        return text;
    }

    public String doGetAttribute(By locator, String attrName) {
        WebElement element = getElement(locator);
        String attributeValue = element.getDomAttribute(attrName);
        logger.info("Got attribute '{}' from element: {}: '{}'", attrName, locator, attributeValue);
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

    // Simplified isElementDisplayed
    public boolean isElementDisplayed(By locator) {
        List<WebElement> elements = driver.findElements(locator);
        boolean displayed = !elements.isEmpty() && elements.get(0).isDisplayed();
        logger.info("Element {} is displayed: {}", locator, displayed);
        return displayed;
    }

    // Removed isElementDisplayed(By locator, int expectedElementCount) - not
    // generally useful

    public List<WebElement> getElements(By locator) {
        return driver.findElements(locator);
    }

    public int getElementsCount(By locator) {
        int count = getElements(locator).size();
        logger.info("Found {} elements for locator: {}", count, locator);
        return count;
    }

    public List<String> getElementsTextList(By locator) {
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

    public List<String> getElementAttributeList(By locator, String attrName) {
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

    // ********************** Select drop down utils **************//

    public void doSelectByIndex(By locator, int index) {
        Select select = new Select(getElement(locator));
        select.selectByIndex(index);
        logger.info("Selected option by index {} for element: {}", index, locator);
    }

    public void doSelectByVisibleText(By locator, String visibleText) {
        Select select = new Select(getElement(locator));
        select.selectByVisibleText(visibleText);
        logger.info("Selected option by visible text '{}' for element: {}", visibleText, locator);
    }

    public void doSelectByValue(By locator, String value) {
        Select select = new Select(getElement(locator));
        select.selectByValue(value);
        logger.info("Selected option by value '{}' for element: {}", value, locator);
    }

    public int getDropDownOptionsCount(By locator) {
        Select select = new Select(getElement(locator)); // Changed to getElement
        int count = select.getOptions().size();
        logger.info("Got dropdown options count for element: {}: {}", locator, count);
        return count;
    }

    public List<String> getDropDownOptionsTextList(By locator) {
        Select select = new Select(getElement(locator)); // Changed to getElement

        List<WebElement> optionsList = select.getOptions();
        List<String> optionsTextList = new ArrayList<>();

        for (WebElement e : optionsList) {
            String text = e.getText();
            optionsTextList.add(text);
        }
        logger.info("Got dropdown options text list for element: {}: {}", locator, optionsTextList);
        return optionsTextList;
    }

    public void selectValueFromDropDown(By locator, String optionText) {
        Select select = new Select(getElement(locator));
        List<WebElement> optionsList = select.getOptions();

        for (WebElement e : optionsList) {
            String text = e.getText();
            if (text.equals(optionText.trim())) {
                e.click();
                logger.info("Selected option '{}' from dropdown: {}", optionText, locator);
                break;
            }
        }
    }

    public void selectValueFromDropDownWithoutSelectClass(By locator, String optionText) {
        List<WebElement> optionsList = getElements(locator);
        for (WebElement e : optionsList) {
            String text = e.getText();
            if (text.equals(optionText)) {
                e.click();
                logger.info("Selected option '{}' from dropdown (without Select class): {}", optionText, locator);
                break;
            }
        }
    }

    public void doSearch(By searchField, String searchKey, By suggestions, String value) throws InterruptedException {
        doSendKeys(searchField, searchKey);
        Thread.sleep(3000);
        List<WebElement> suggList = getElements(suggestions);
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

    // *****************Actions utils********************//

    public void handleParentSubMenu(By parentLocator, By childLocator) {
        Actions act = new Actions(driver);
        WebElement parent = getElement(parentLocator);
        act.moveToElement(parent).perform();
        logger.info("Moved to parent element: {}", parentLocator);
        // WaitUtil.waitForElementVisible(driver, childLocator, 2); // Wait for a short
        // time for the child to become visible.
        doClick(childLocator);
        logger.info("Clicked on child element: {}", childLocator);
    }

    public void doDragAndDrop(By sourceLocator, By targetLocator) {
        Actions act = new Actions(driver);
        act.dragAndDrop(getElement(sourceLocator), getElement(targetLocator)).perform();
        logger.info("Dragged element {} to element {}", sourceLocator, targetLocator);
    }

    public void doActionsSendKeys(By locator, String value) {
        Actions act = new Actions(driver);
        act.sendKeys(getElement(locator), value).perform();
        logger.info("Sent keys '{}' to element using Actions: {}", value, locator);
    }

    public void doActionsClick(By locator) {
        Actions act = new Actions(driver);
        act.click(getElement(locator)).perform();
        logger.info("Clicked on element using Actions: {}", locator);
    }

    /**
     * This method is used to enter the value in the text field with a pause.
     */
    public void doActionsSendKeysWithPause(By locator, String value, long pauseTime) {
        Actions act = new Actions(driver);
        char[] ch = value.toCharArray();
        for (char c : ch) {
            act.sendKeys(getElement(locator), String.valueOf(c)).pause(pauseTime).perform();
        }
        logger.info("Sent keys '{}' to element using Actions with pause: {} ms", value, pauseTime);
    }

    /**
     * This method is used to enter the value in the text field with a pause of 500
     * ms (by default).
     */
    public void doActionsSendKeysWithPause(By locator, String value) {
        Actions act = new Actions(driver);
        char[] ch = value.toCharArray();
        for (char c : ch) {
            act.sendKeys(getElement(locator), String.valueOf(c)).pause(500).perform();
        }
        logger.info("Sent keys '{}' to element using Actions with default pause", value);
    }

    public void level4MenuSubMenuHandlingUsingClick(By level1, String level2, String level3, String level4)
            throws InterruptedException {
        doClick(level1);
        Thread.sleep(1000);

        Actions act = new Actions(driver);
        act.moveToElement(getElement(By.linkText(level2))).perform();
        Thread.sleep(1000);
        act.moveToElement(getElement(By.linkText(level3))).perform();
        Thread.sleep(1000);
        doClick(By.linkText(level4));
        logger.info("Navigated through 4-level menu using click: {}, {}, {}, {}", level1, level2, level3, level4);
    }

    public void level4MenuSubMenuHandlingUsingClick(By level1, By level2, By level3, By level4)
            throws InterruptedException {
        doClick(level1);
        Thread.sleep(1000);

        Actions act = new Actions(driver);
        act.moveToElement(getElement(level2)).perform();
        Thread.sleep(1000);
        act.moveToElement(getElement(level3)).perform();
        Thread.sleep(1000);
        doClick(level4);
        logger.info("Navigated through 4-level menu using click: {}, {}, {}, {}", level1, level2, level3, level4);
    }

    public void level4MenuSubMenuHandlingUsingMouseHover(By level1, By level2, By level3, By level4)
            throws InterruptedException {
        Actions act = new Actions(driver);

        act.moveToElement(getElement(level1)).perform();
        Thread.sleep(1000);

        act.moveToElement(getElement(level2)).perform();
        Thread.sleep(1000);
        act.moveToElement(getElement(level3)).perform();
        Thread.sleep(1000);
        doClick(level4);
        logger.info("Navigated through 4-level menu using mouse hover: {}, {}, {}, {}", level1, level2, level3, level4);
    }

    // Removed all wait methods - these will be moved to a separate WaitUtil class

    // Removed naveenClick method
}

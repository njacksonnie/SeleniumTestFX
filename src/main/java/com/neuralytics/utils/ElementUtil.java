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
 * @see WaitUtil
 */
public class ElementUtil {

    /**
     * The WebDriver instance used for all element interactions.
     */
    private final WebDriver driver;

    /**
     * The Actions instance for performing complex interactions like mouse hovers
     * and drag-and-drop.
     */
    private final Actions act;

    /**
     * Logger instance for tracing element operations and logging errors.
     */
    private static final Logger logger = LoggerUtil.getLogger(ElementUtil.class);

    /**
     * Constructs an ElementUtil instance with the specified WebDriver.
     *
     * @param driver the WebDriver instance to use for element interactions
     * @throws FrameworkException if the provided WebDriver is null
     */
    public ElementUtil(WebDriver driver) {
        if (driver == null) {
            throw new FrameworkException("WebDriver cannot be null");
        }
        this.act = new Actions(driver);
        this.driver = driver;
    }

    /**
     * Checks if a value is null, throwing an exception if so.
     *
     * @param value the value to check
     * @throws ElementException if the value is null
     */
    private void nullCheck(String value) {
        if (value == null) {
            logger.error("Value is null");
            throw new ElementException("VALUE IS NULL");
        }
    }

    /**
     * Creates a Select instance for interacting with dropdown elements.
     *
     * @param locator the locator of the dropdown element
     * @return a {@link Select} instance for the specified element
     * @throws ElementException if the element is not found
     */
    private Select getSelect(By locator) {
        return new Select(getElement(locator));
    }

    /**
     * Sends the specified value to an element after clearing its current content.
     * Useful for text fields and similar input elements.
     *
     * @param locator the locator of the target element
     * @param value   the value to send to the element
     * @throws ElementException if the value is null or the element is not found
     */
    public void doSendKeys(By locator, String value) {
        nullCheck(value);
        WebElement element = getElement(locator);
        element.clear();
        element.sendKeys(value);
        logger.info("Sent keys '{}' to element: {}", value, locator);
    }

    /**
     * Sends the specified value to an element with a timeout for visibility,
     * clearing its current content first.
     * Waits for the element to be visible before performing the action, using
     * {@link WaitUtil}.
     *
     * @param locator the locator of the target element
     * @param value   the value to send to the element
     * @param timeOut the maximum time (in seconds) to wait for element visibility
     * @throws ElementException if the value is null or the element is not found
     *                          within the timeout
     * @see WaitUtil#waitForElementVisible(WebDriver, By, int)
     */
    public void doSendKeys(By locator, String value, int timeOut) {
        nullCheck(value);
        WebElement element = WaitUtil.waitForElementVisible(driver, locator, timeOut);
        element.clear();
        element.sendKeys(value);
        logger.info("Sent keys '{}' to element: {} with timeout: {}", value, locator, timeOut);
    }

    /**
     * Retrieves a single web element using the specified locator.
     *
     * @param locator the locator of the target element
     * @return the found {@link WebElement}
     * @throws ElementException if the element is not found
     */
    public WebElement getElement(By locator) {
        try {
            return driver.findElement(locator);
        } catch (NoSuchElementException e) {
            logger.warn("Element not found: {}", locator);
            throw new ElementException("Element not found: " + locator);
        }
    }

    /**
     * Retrieves a list of web elements matching the specified locator.
     * Returns an empty list if no elements are found.
     *
     * @param locator the locator to find elements
     * @return a list of matching {@link WebElement} instances
     */
    public List<WebElement> getElements(By locator) {
        return driver.findElements(locator);
    }

    /**
     * Clicks on an element identified by the specified locator.
     *
     * @param locator the locator of the target element
     * @throws ElementException if the element is not found
     */
    public void doClick(By locator) {
        WebElement element = getElement(locator);
        element.click();
        logger.info("Clicked on element: {}", locator);
    }

    /**
     * Clicks on an element with a timeout for visibility.
     * Waits for the element to be visible before clicking, using {@link WaitUtil}.
     *
     * @param locator the locator of the target element
     * @param timeOut the maximum time (in seconds) to wait for element visibility
     * @throws ElementException if the element is not found within the timeout
     * @see WaitUtil#waitForElementVisible(WebDriver, By, int)
     */
    public void doClick(By locator, int timeOut) {
        WebElement element = WaitUtil.waitForElementVisible(driver, locator, timeOut);
        element.click();
        logger.info("Clicked on element: {} with timeout: {}", locator, timeOut);
    }

    /**
     * Retrieves the text content of an element.
     *
     * @param locator the locator of the target element
     * @return the text content of the element
     * @throws ElementException if the element is not found
     */
    public String doGetText(By locator) {
        WebElement element = getElement(locator);
        String text = element.getText();
        logger.info("Got text from element: {}: '{}'", locator, text);
        return text;
    }

    /**
     * Retrieves the value of a specified DOM attribute from an element.
     *
     * @param locator  the locator of the target element
     * @param attrName the name of the attribute to retrieve (e.g., "href", "value")
     * @return the attribute value, or null if the attribute does not exist
     * @throws ElementException if the element is not found
     */
    public String doGetAttribute(By locator, String attrName) {
        WebElement element = getElement(locator);
        String attributeValue = element.getDomAttribute(attrName);
        logger.info("Got attribute '{}' from element: {}: '{}'", attrName, locator, attributeValue);
        return attributeValue;
    }

    /**
     * Checks if an element is displayed on the page.
     * Returns false if the element is not found, suppressing the exception.
     *
     * @param locator the locator of the target element
     * @return true if the element is found and displayed, false otherwise
     */
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

    /**
     * Checks if an element is displayed using a simplified approach.
     * Verifies if at least one element exists and is displayed, without throwing
     * exceptions.
     *
     * @param locator the locator of the target element
     * @return true if at least one matching element is found and displayed, false
     *         otherwise
     */
    public boolean isElementDisplayed(By locator) {
        List<WebElement> elements = driver.findElements(locator);
        boolean displayed = !elements.isEmpty() && elements.get(0).isDisplayed();
        logger.info("Element {} is displayed: {}", locator, displayed);
        return displayed;
    }

    /**
     * Returns the count of elements matching the specified locator.
     *
     * @param locator the locator to count elements
     * @return the number of matching elements (0 if none found)
     */
    public int getElementsCount(By locator) {
        int count = getElements(locator).size();
        logger.info("Found {} elements for locator: {}", count, locator);
        return count;
    }

    /**
     * Retrieves a list of non-empty text contents from elements matching the
     * locator.
     *
     * @param locator the locator to find elements
     * @return a list of non-empty text contents from the matching elements
     */
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

    /**
     * Retrieves a list of non-empty attribute values from elements matching the
     * locator.
     *
     * @param locator  the locator to find elements
     * @param attrName the name of the attribute to retrieve (e.g., "src", "id")
     * @return a list of non-empty attribute values from the matching elements
     */
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

    /**
     * Selects an option from a dropdown by its index using the {@link Select}
     * class.
     *
     * @param locator the locator of the dropdown element (must be a &lt;select&gt;
     *                tag)
     * @param index   the zero-based index of the option to select
     * @throws ElementException if the element is not found or is not a selectable
     *                          dropdown
     */
    public void doSelectByIndex(By locator, int index) {
        getSelect(locator).selectByIndex(index);
        logger.info("Selected option by index {} for element: {}", index, locator);
    }

    /**
     * Selects an option from a dropdown by its visible text using the
     * {@link Select} class.
     *
     * @param locator     the locator of the dropdown element (must be a
     *                    &lt;select&gt; tag)
     * @param visibleText the visible text of the option to select
     * @throws ElementException if the element is not found or is not a selectable
     *                          dropdown
     */
    public void doSelectByText(By locator, String visibleText) {
        getSelect(locator).selectByVisibleText(visibleText);
        logger.info("Selected option by visible text '{}' for element: {}", visibleText, locator);
    }

    /**
     * Selects an option from a dropdown by its value attribute using the
     * {@link Select} class.
     *
     * @param locator the locator of the dropdown element (must be a &lt;select&gt;
     *                tag)
     * @param value   the value attribute of the option to select
     * @throws ElementException if the element is not found or is not a selectable
     *                          dropdown
     */
    public void doSelectByValue(By locator, String value) {
        getSelect(locator).selectByValue(value);
        logger.info("Selected option by value '{}' for element: {}", value, locator);
    }

    /**
     * Returns the number of options in a dropdown.
     *
     * @param locator the locator of the dropdown element (must be a &lt;select&gt;
     *                tag)
     * @return the number of options in the dropdown
     * @throws ElementException if the element is not found or is not a selectable
     *                          dropdown
     */
    public int getDropDownOptionsCount(By locator) {
        int count = getSelect(locator).getOptions().size();
        logger.info("Got dropdown options count for element: {}: {}", locator, count);
        return count;
    }

    /**
     * Retrieves a list of all option texts from a dropdown.
     *
     * @param locator the locator of the dropdown element (must be a &lt;select&gt;
     *                tag)
     * @return a list of option texts (empty or not)
     * @throws ElementException if the element is not found or is not a selectable
     *                          dropdown
     */
    public List<String> getDropdownOptions(By locator) {
        List<WebElement> optionsList = getSelect(locator).getOptions();
        List<String> optionsTextList = new ArrayList<>();

        for (WebElement e : optionsList) {
            optionsTextList.add(e.getText());
        }
        logger.info("Got dropdown options text list for element: {}: {}", locator, optionsTextList);
        return optionsTextList;
    }

    /**
     * Selects an option from a dropdown by matching its trimmed text.
     * Uses direct clicking instead of {@link Select} methods for flexibility.
     *
     * @param locator    the locator of the dropdown element (must be a
     *                   &lt;select&gt; tag)
     * @param optionText the text of the option to select (trimmed for comparison)
     * @throws ElementException if the element is not found or is not a selectable
     *                          dropdown
     */
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

    /**
     * Selects an option from a non-&lt;select&gt; dropdown by matching its text.
     * Useful for custom dropdowns not compatible with the {@link Select} class.
     *
     * @param locator    the locator of the dropdown elements (e.g., list items)
     * @param optionText the exact text of the option to select
     */
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

    /**
     * Searches for a value in a search field and selects a suggestion containing
     * the specified value.
     * Enters the search key, waits 3 seconds for suggestions, and clicks the first
     * match.
     *
     * @param searchField the locator of the search input field
     * @param searchKey   the value to enter in the search field
     * @param suggestions the locator for suggestion elements
     * @param value       the value to match within suggestion text
     * @throws InterruptedException if the thread sleep is interrupted
     * @throws ElementException     if the search field or suggestions are not found
     */
    public void searchAndSelect(By searchField, String searchKey, By suggestions, String value)
            throws InterruptedException {
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

    /**
     * Handles a parent-child menu by hovering over the parent and clicking the
     * child.
     * Uses {@link Actions} to simulate mouse hover before clicking.
     *
     * @param parentLocator the locator of the parent menu element
     * @param childLocator  the locator of the child menu element
     * @throws ElementException if either element is not found
     */
    public void handleParentSubMenu(By parentLocator, By childLocator) {
        WebElement parent = getElement(parentLocator);
        act.moveToElement(parent).perform();
        logger.info("Moved to parent element: {}", parentLocator);
        doClick(childLocator);
        logger.info("Clicked on child element: {}", childLocator);
    }

    /**
     * Performs a drag-and-drop operation between two elements.
     * Uses {@link Actions} to simulate dragging the source element to the target.
     *
     * @param sourceLocator the locator of the source element
     * @param targetLocator the locator of the target element
     * @throws ElementException if either element is not found
     */
    public void doDragAndDrop(By sourceLocator, By targetLocator) {
        act.dragAndDrop(getElement(sourceLocator), getElement(targetLocator)).perform();
        logger.info("Dragged element {} to element {}", sourceLocator, targetLocator);
    }

    /**
     * Sends keys to an element using the {@link Actions} class.
     * Useful for scenarios requiring precise keyboard simulation.
     *
     * @param locator the locator of the target element
     * @param value   the value to send
     * @throws ElementException if the element is not found
     */
    public void doActionsSendKeys(By locator, String value) {
        act.sendKeys(getElement(locator), value).perform();
        logger.info("Sent keys '{}' to element using Actions: {}", value, locator);
    }

    /**
     * Clicks an element using the {@link Actions} class.
     * Useful for scenarios requiring precise mouse simulation.
     *
     * @param locator the locator of the target element
     * @throws ElementException if the element is not found
     */
    public void doActionsClick(By locator) {
        act.click(getElement(locator)).perform();
        logger.info("Clicked on element using Actions: {}", locator);
    }

    /**
     * Sends keys to an element with a specified pause between each character using
     * {@link Actions}.
     * Simulates slow typing for scenarios requiring deliberate input pacing.
     *
     * @param locator   the locator of the target element
     * @param value     the value to send
     * @param pauseTime the pause time (in milliseconds) between each character
     * @throws ElementException if the element is not found
     */
    public void doActionSendKeysSlow(By locator, String value, long pauseTime) {
        char[] ch = value.toCharArray();
        for (char c : ch) {
            act.sendKeys(getElement(locator), String.valueOf(c)).pause(pauseTime).perform();
        }
        logger.info("Sent keys '{}' to element using Actions with pause: {} ms", value, pauseTime);
    }

    /**
     * Sends keys to an element with a default 500ms pause between each character
     * using {@link Actions}.
     * Simulates slow typing with a standard delay.
     *
     * @param locator the locator of the target element
     * @param value   the value to send
     * @throws ElementException if the element is not found
     */
    public void doActionSendKeysSlow(By locator, String value) {
        char[] ch = value.toCharArray();
        for (char c : ch) {
            act.sendKeys(getElement(locator), String.valueOf(c)).pause(500).perform();
        }
        logger.info("Sent keys '{}' to element using Actions with default pause", value);
    }

    /**
     * Navigates a 4-level menu by clicking the first level and hovering over
     * subsequent levels using link text.
     * Uses a 1-second delay between actions to ensure menu visibility.
     *
     * @param level1 the locator of the first-level menu
     * @param level2 the link text of the second-level menu
     * @param level3 the link text of the third-level menu
     * @param level4 the link text of the fourth-level menu
     * @throws InterruptedException if the thread sleep is interrupted
     * @throws ElementException     if any element is not found
     */
    public void navigate4LevelMenuClick(By level1, String level2, String level3, String level4)
            throws InterruptedException {
        doClick(level1);
        Thread.sleep(1000);
        act.moveToElement(getElement(By.linkText(level2))).perform();
        Thread.sleep(1000);
        act.moveToElement(getElement(By.linkText(level3))).perform();
        Thread.sleep(1000);
        doClick(By.linkText(level4));
        logger.info("Navigated through 4-level menu using click: {}, {}, {}, {}", level1, level2, level3, level4);
    }

    /**
     * Navigates a 4-level menu by clicking the first level and hovering over
     * subsequent levels using locators.
     * Uses a 1-second delay between actions to ensure menu visibility.
     *
     * @param level1 the locator of the first-level menu
     * @param level2 the locator of the second-level menu
     * @param level3 the locator of the third-level menu
     * @param level4 the locator of the fourth-level menu
     * @throws InterruptedException if the thread sleep is interrupted
     * @throws ElementException     if any element is not found
     */
    public void navigate4LevelMenuClick(By level1, By level2, By level3, By level4)
            throws InterruptedException {
        doClick(level1);
        Thread.sleep(1000);
        act.moveToElement(getElement(level2)).perform();
        Thread.sleep(1000);
        act.moveToElement(getElement(level3)).perform();
        Thread.sleep(1000);
        doClick(level4);
        logger.info("Navigated through 4-level menu using click: {}, {}, {}, {}", level1, level2, level3, level4);
    }

    /**
     * Navigates a 4-level menu by hovering over all levels and clicking the final
     * level.
     * Uses a 1-second delay between actions to ensure menu visibility.
     *
     * @param level1 the locator of the first-level menu
     * @param level2 the locator of the second-level menu
     * @param level3 the locator of the third-level menu
     * @param level4 the locator of the fourth-level menu
     * @throws InterruptedException if the thread sleep is interrupted
     * @throws ElementException     if any element is not found
     */
    public void navigate4LevelMenuHover(By level1, By level2, By level3, By level4)
            throws InterruptedException {
        act.moveToElement(getElement(level1)).perform();
        Thread.sleep(1000);
        act.moveToElement(getElement(level2)).perform();
        Thread.sleep(1000);
        act.moveToElement(getElement(level3)).perform();
        Thread.sleep(1000);
        doClick(level4);
        logger.info("Navigated through 4-level menu using mouse hover: {}, {}, {}, {}", level1, level2, level3, level4);
    }
}
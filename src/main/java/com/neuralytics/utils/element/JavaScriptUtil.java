package com.neuralytics.utils.element;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * A utility class for executing JavaScript operations in Selenium WebDriver.
 * Provides methods to perform actions such as flashing elements, scrolling,
 * clicking, retrieving page data,
 * and generating alerts using JavaScript. This class leverages the
 * {@link JavascriptExecutor} interface
 * to offer functionality beyond native WebDriver methods, enhancing flexibility
 * in a testing framework.
 *
 * <p>
 * Usage example:
 * 
 * <pre>
 * WebDriver driver = DriverFactory.getDriver();
 * JavaScriptUtil jsUtil = new JavaScriptUtil(driver);
 * jsUtil.flash(driver.findElement(By.id("button")), "red", 5);
 * jsUtil.scrollPageDown();
 * String title = jsUtil.getTitleByJS();
 * </pre>
 *
 * <p>
 * All methods execute JavaScript via the WebDriver’s {@link JavascriptExecutor}
 * capability, requiring
 * a compatible driver implementation (e.g., ChromeDriver, FirefoxDriver).
 *
 * @see JavascriptExecutor
 * @see WebDriver
 */
public class JavaScriptUtil {

    /**
     * The {@link JavascriptExecutor} instance used for executing JavaScript
     * commands.
     */
    private final JavascriptExecutor jsExecutor;

    /**
     * Constructs a JavaScriptUtil instance with the given WebDriver.
     * Verifies that the driver supports JavaScript execution before initialization.
     *
     * @param driver the WebDriver instance to use for JavaScript execution
     * @throws IllegalArgumentException if the driver does not implement
     *                                  {@link JavascriptExecutor}
     */
    public JavaScriptUtil(WebDriver driver) {
        if (!(driver instanceof JavascriptExecutor)) {
            throw new IllegalArgumentException("Driver does not support JavaScript execution");
        }
        this.jsExecutor = (JavascriptExecutor) driver;
    }

    /**
     * Flashes an element by toggling its background color between a specified color
     * and its original color.
     * Useful for visually highlighting elements during test execution.
     *
     * @param element    the {@link WebElement} to flash
     * @param flashColor the CSS color to flash (e.g., "rgb(0,200,0)", "red")
     * @param times      the number of flash cycles (each cycle includes on and off
     *                   states)
     * @throws NullPointerException if the element is null
     */
    public void flash(WebElement element, String flashColor, int times) {
        Objects.requireNonNull(element, "Element cannot be null");
        String originalColor = element.getCssValue("backgroundColor");
        for (int i = 0; i < times; i++) {
            changeColor(flashColor, element);
            changeColor(originalColor, element);
        }
    }

    /**
     * Changes the background color of an element using JavaScript.
     * Includes a 20ms delay to make the color change perceptible.
     *
     * @param color   the CSS color to apply (e.g., "blue", "#FF0000")
     * @param element the {@link WebElement} to modify
     */
    private void changeColor(String color, WebElement element) {
        executeScript("arguments[0].style.backgroundColor = arguments[1];", element, color);
        try {
            TimeUnit.MILLISECONDS.sleep(20);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Retrieves the page title using JavaScript.
     * Returns an empty string if the script returns null or fails.
     *
     * @return the current page title
     */
    public String getTitleByJS() {
        return Objects.toString(executeScript("return document.title;"), "");
    }

    /**
     * Refreshes the browser using JavaScript.
     * Equivalent to a manual page reload.
     */
    public void refreshBrowserByJS() {
        executeScript("history.go(0);");
    }

    /**
     * Navigates to the previous page in the browser’s history using JavaScript.
     * Equivalent to clicking the back button.
     */
    public void navigateToBackPage() {
        executeScript("history.go(-1);");
    }

    /**
     * Navigates to the next page in the browser’s history using JavaScript.
     * Equivalent to clicking the forward button.
     */
    public void navigateToForwardPage() {
        executeScript("history.go(1);");
    }

    /**
     * Generates a JavaScript alert with the specified message.
     * The alert must be handled separately.
     *
     * @param message the message to display in the alert
     */
    public void generateAlert(String message) {
        executeScript("alert(arguments[0]);", message);
    }

    /**
     * Retrieves the inner text of the entire page using JavaScript.
     * Returns an empty string if the script returns null or fails.
     *
     * @return the inner text of the page
     */
    public String getPageInnerText() {
        return Objects.toString(executeScript("return document.documentElement.innerText;"), "");
    }

    /**
     * Clicks an element using JavaScript.
     * Bypasses WebDriver’s native click method, useful for elements not clickable
     * via standard means.
     *
     * @param element the {@link WebElement} to click
     * @throws NullPointerException if the element is null
     */
    public void clickElementByJS(WebElement element) {
        Objects.requireNonNull(element, "Element cannot be null");
        executeScript("arguments[0].click();", element);
    }

    /**
     * Sends keys to an element identified by its ID using JavaScript.
     * Directly sets the element’s value attribute, bypassing WebDriver’s native
     * sendKeys method.
     *
     * @param id    the ID of the target element
     * @param value the value to set
     * @throws NullPointerException if the ID or value is null
     */
    public void sendKeysUsingWithId(String id, String value) {
        Objects.requireNonNull(id, "ID cannot be null");
        Objects.requireNonNull(value, "Value cannot be null");
        executeScript("let el = document.getElementById(arguments[0]); if(el) el.value = arguments[1];", id, value);
    }

    /**
     * Scrolls the page to the bottom using JavaScript.
     */
    public void scrollPageDown() {
        executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    /**
     * Scrolls the page down by a specified pixel height using JavaScript.
     *
     * @param height the number of pixels to scroll down
     */
    public void scrollPageDown(int height) {
        executeScript("window.scrollTo(0, arguments[0]);", height);
    }

    /**
     * Scrolls the page to the top using JavaScript.
     */
    public void scrollToTop() {
        executeScript("window.scrollTo(0, 0);");
    }

    /**
     * Scrolls an element into view using JavaScript.
     * Ensures the element is visible within the viewport, aligning it to the top.
     *
     * @param element the {@link WebElement} to scroll into view
     * @throws NullPointerException if the element is null
     */
    public void scrollIntoView(WebElement element) {
        Objects.requireNonNull(element, "Element cannot be null");
        executeScript("arguments[0].scrollIntoView(true);", element);
    }

    /**
     * Draws a border around an element using JavaScript.
     * Useful for visually highlighting elements during test execution.
     *
     * @param element     the {@link WebElement} to border
     * @param borderStyle the CSS border style (e.g., "2px solid red")
     * @throws NullPointerException if the element or borderStyle is null
     */
    public void drawBorder(WebElement element, String borderStyle) {
        Objects.requireNonNull(element, "Element cannot be null");
        Objects.requireNonNull(borderStyle, "Border style cannot be null");
        executeScript("arguments[0].style.border = arguments[1];", element, borderStyle);
    }

    /**
     * Executes a JavaScript script with the provided arguments.
     * Serves as a helper method for all JavaScript operations in this class.
     *
     * @param script the JavaScript code to execute
     * @param args   the arguments to pass to the script (e.g., WebElements,
     *               strings)
     * @return the result of the script execution, or null if no result
     */
    private Object executeScript(String script, Object... args) {
        return jsExecutor.executeScript(script, args);
    }
}
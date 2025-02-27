package com.neuralytics.interfaces;

import org.openqa.selenium.MutableCapabilities;

/**
 * Defines a contract for providing browser-specific options for Selenium
 * WebDriver.
 * Implementations of this interface supply configured
 * {@link MutableCapabilities} instances (e.g.,
 * ChromeOptions, FirefoxOptions) tailored to specific browsers, enabling
 * flexible configuration of
 * WebDriver instances in a testing framework.
 *
 * <p>
 * This interface is typically used with a factory or manager class (e.g.,
 * {@code OptionsManager})
 * to abstract the creation of browser capabilities, allowing for easy extension
 * to support additional
 * browsers.
 *
 * <p>
 * Example implementation:
 * 
 * <pre>
 * public class ChromeOptionsProvider implements BrowserOptionsProvider {
 *     private final Properties props;
 *
 *     public ChromeOptionsProvider(Properties props) {
 *         this.props = props;
 *     }
 *
 *     &#64;Override
 *     public MutableCapabilities getOptions() {
 *         ChromeOptions options = new ChromeOptions();
 *         if (Boolean.parseBoolean(props.getProperty("headless"))) {
 *             options.addArguments("--headless");
 *         }
 *         return options;
 *     }
 * }
 * </pre>
 *
 * @see org.openqa.selenium.MutableCapabilities
 * @see org.openqa.selenium.chrome.ChromeOptions
 * @see org.openqa.selenium.firefox.FirefoxOptions
 */
public interface BrowserOptionsProvider {

    /**
     * Retrieves the browser-specific capabilities for configuring a Selenium
     * WebDriver instance.
     * Implementations must return a configured {@link MutableCapabilities} object
     * tailored to the
     * target browser (e.g., ChromeOptions for Chrome, FirefoxOptions for Firefox).
     * The returned
     * capabilities may incorporate settings such as headless mode, browser
     * arguments, or custom
     * preferences based on external configuration (e.g., properties or environment
     * variables).
     *
     * @return a configured {@link MutableCapabilities} instance for the specific
     *         browser
     */
    MutableCapabilities getOptions();
}
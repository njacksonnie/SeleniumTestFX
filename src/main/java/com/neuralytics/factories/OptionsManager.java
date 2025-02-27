package com.neuralytics.factories;

import com.neuralytics.exceptions.BrowserException;
import com.neuralytics.providers.ChromeOptionsProvider;
import com.neuralytics.providers.EdgeOptionsProvider;
import com.neuralytics.providers.FirefoxOptionsProvider;
import com.neuralytics.providers.SafariOptionsProvider;
import com.neuralytics.utils.LoggerUtil;
import org.openqa.selenium.MutableCapabilities;
import org.slf4j.Logger;

import java.util.Properties;

/**
 * Manages browser-specific options for Selenium WebDriver instances.
 * This class acts as a factory to provide {@link MutableCapabilities}
 * implementations (e.g., ChromeOptions,
 * FirefoxOptions) based on the specified browser type and configuration
 * properties. It delegates option
 * creation to specialized provider classes, allowing for flexible and
 * extensible browser configuration.
 *
 * <p>
 * Designed to work with {@link DriverFactory} to initialize WebDriver
 * instances, this class uses
 * properties to customize browser behavior (e.g., headless mode, window size,
 * or additional capabilities).
 *
 * <p>
 * Usage example:
 * 
 * <pre>
 * Properties props = new Properties();
 * props.setProperty("headless", "true");
 * OptionsManager optionsManager = new OptionsManager(props);
 * MutableCapabilities chromeOptions = optionsManager.getBrowserOptions("chrome");
 * </pre>
 *
 * <p>
 * Supported browsers include Chrome, Firefox, Edge, and Safari, with
 * extensibility for additional types
 * via custom providers.
 */
public class OptionsManager {

    /**
     * Configuration properties used to customize browser options.
     * These properties are passed to provider classes to tailor the returned
     * capabilities.
     */
    private final Properties prop;

    /**
     * Logger instance for recording option retrieval events and errors.
     */
    private static final Logger logger = LoggerUtil.getLogger(OptionsManager.class);

    /**
     * Constructs an OptionsManager instance with the specified configuration
     * properties.
     *
     * @param prop the properties containing browser configuration settings
     * @throws NullPointerException if the provided properties object is null
     */
    public OptionsManager(final Properties prop) {
        if (prop == null) {
            throw new NullPointerException("Properties cannot be null");
        }
        this.prop = prop;
    }

    /**
     * Retrieves browser-specific capabilities based on the provided browser name.
     * This method uses a switch-based approach to delegate option creation to the
     * appropriate provider
     * class (e.g., {@link ChromeOptionsProvider} for "chrome"). The returned
     * {@link MutableCapabilities}
     * instance is configured according to the properties passed to this manager.
     *
     * <p>
     * Supported browser names (case-insensitive) are:
     * <ul>
     * <li>chrome</li>
     * <li>firefox</li>
     * <li>edge</li>
     * <li>safari</li>
     * </ul>
     *
     * @param browser the name of the browser for which options are required (e.g.,
     *                "chrome", "firefox")
     * @return a configured {@link MutableCapabilities} instance for the specified
     *         browser
     * @throws BrowserException     if the browser name is unsupported or invalid
     * @throws NullPointerException if the browser parameter is null
     * @see ChromeOptionsProvider#getOptions()
     * @see FirefoxOptionsProvider#getOptions()
     * @see EdgeOptionsProvider#getOptions()
     * @see SafariOptionsProvider#getOptions()
     */
    public MutableCapabilities getBrowserOptions(final String browser) {
        if (browser == null) {
            logger.error("Browser name cannot be null");
            throw new NullPointerException("Browser name cannot be null");
        }

        logger.info("Getting browser options for: {}", browser);
        return switch (browser.toLowerCase().trim()) {
            case "chrome" -> new ChromeOptionsProvider(prop).getOptions();
            case "firefox" -> new FirefoxOptionsProvider(prop).getOptions();
            case "edge" -> new EdgeOptionsProvider(prop).getOptions();
            case "safari" -> new SafariOptionsProvider(prop).getOptions();
            default -> {
                logger.error("Unsupported Browser: {}", browser);
                throw new BrowserException("Unsupported Browser: " + browser);
            }
        };
    }
}
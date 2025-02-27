package com.neuralytics.providers;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.safari.SafariOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Provides Safari-specific options for Selenium WebDriver configuration.
 * Implements {@link BrowserOptionsProvider} to supply a configured
 * {@link SafariOptions} instance
 * based on properties, supporting basic settings (e.g., private browsing),
 * limited advanced features
 * (e.g., user agent), and remote execution capabilities (e.g., Selenoid
 * integration). This class is
 * typically used with a factory or manager (e.g., {@code OptionsManager}) to
 * initialize Safari WebDriver
 * instances.
 *
 * <p>
 * Configuration is driven by a {@link Properties} object, with keys such as:
 * <ul>
 * <li>{@code incognito}: Enables private browsing (e.g., "true").</li>
 * <li>{@code advanced_mode}: Enables advanced options (e.g., "true").</li>
 * <li>{@code remote}: Triggers remote-specific settings (e.g., "true").</li>
 * <li>{@code user_agent}: Overrides the default user agent (e.g.,
 * "Mozilla/5.0...").</li>
 * </ul>
 *
 * <p>
 * Usage example:
 * 
 * <pre>
 * Properties props = new Properties();
 * props.setProperty("incognito", "true");
 * SafariOptionsProvider provider = new SafariOptionsProvider(props);
 * MutableCapabilities options = provider.getOptions();
 * </pre>
 *
 * <p>
 * Note: Safari’s Selenium support is limited compared to Chromium-based
 * browsers. Features like
 * headless mode, mobile emulation, and extensive performance optimizations are
 * not natively supported
 * and may require external workarounds (e.g., WebDriver commands or system
 * settings).
 *
 * @see BrowserOptionsProvider
 * @see SafariOptions
 */
public class SafariOptionsProvider implements BrowserOptionsProvider {

    /**
     * Configuration properties used to customize Safari options.
     */
    private final Properties prop;

    /**
     * Constructs a SafariOptionsProvider with the specified configuration
     * properties.
     *
     * @param prop the properties defining Safari-specific settings
     * @throws NullPointerException if prop is null
     */
    public SafariOptionsProvider(final Properties prop) {
        if (prop == null) {
            throw new NullPointerException("Properties cannot be null");
        }
        this.prop = prop;
    }

    /**
     * Retrieves configured Safari options based on the provided properties.
     * Applies basic options (e.g., private browsing), advanced options (if
     * {@code advanced_mode} is true),
     * and remote-specific capabilities (if {@code remote} is true). The returned
     * {@link SafariOptions}
     * instance is suitable for both local and remote WebDriver initialization,
     * though some features are
     * limited by Safari’s Selenium support.
     *
     * @return a configured {@link SafariOptions} instance
     * @see #applyBasicOptions(SafariOptions)
     * @see #applyAdvancedOptions(SafariOptions)
     * @see #applyRemoteOptions(SafariOptions)
     */
    @Override
    public MutableCapabilities getOptions() {
        SafariOptions options = new SafariOptions();

        applyBasicOptions(options);

        if (Boolean.parseBoolean(prop.getProperty("advanced_mode", "false"))) {
            applyAdvancedOptions(options);
        }

        if (Boolean.parseBoolean(prop.getProperty("remote"))) {
            applyRemoteOptions(options);
        }

        return options;
    }

    /**
     * Applies basic Safari options that are included in all configurations.
     * Configures private browsing; headless mode is not supported by Safari via
     * Selenium and is ignored.
     *
     * @param options the {@link SafariOptions} instance to configure
     */
    private void applyBasicOptions(SafariOptions options) {
        if (Boolean.parseBoolean(prop.getProperty("headless"))) {
            // Headless mode not supported; could log a warning via LoggerUtil if integrated
        }

        if (Boolean.parseBoolean(prop.getProperty("incognito"))) {
            options.setCapability("safari:usePrivateBrowsing", true);
        }
    }

    /**
     * Applies advanced Safari options when {@code advanced_mode} is enabled.
     * Configures limited performance, security, and debugging settings based on
     * properties. Many advanced
     * features (e.g., GPU disable, mobile emulation) are not natively supported by
     * Safari’s Selenium
     * implementation and are either ignored or require external workarounds.
     *
     * @param options the {@link SafariOptions} instance to configure
     */
    private void applyAdvancedOptions(SafariOptions options) {
        if (Boolean.parseBoolean(prop.getProperty("disable_gpu"))) {
            // No direct GPU disable support; ignored
        }
        if (Boolean.parseBoolean(prop.getProperty("disable_extensions"))) {
            options.setCapability("safari:extensions", false);
        }
        if (Boolean.parseBoolean(prop.getProperty("no_sandbox"))) {
            // No sandbox toggle support; ignored
        }

        String windowSize = prop.getProperty("window_size");
        if (windowSize != null && !windowSize.isBlank()) {
            // Window size not supported directly; use WebDriver commands instead
        }

        if (Boolean.parseBoolean(prop.getProperty("ignore_ssl_errors"))) {
            options.setCapability("safari:ignoreFraudWarning", true);
        }
        if (Boolean.parseBoolean(prop.getProperty("disable_web_security"))) {
            // No direct web security disable support; ignored
        }

        String userAgent = prop.getProperty("user_agent");
        if (userAgent != null && !userAgent.isBlank()) {
            options.setCapability("safari:userAgent", userAgent);
        }

        String proxyServer = prop.getProperty("proxy_server");
        if (proxyServer != null && !proxyServer.isBlank()) {
            // Proxy not supported directly; use system settings or WebDriver Proxy class
        }

        if (Boolean.parseBoolean(prop.getProperty("enable_verbose_logging"))) {
            options.setCapability("safari:diagnose", true);
        }

        String mobileEmulation = prop.getProperty("mobile_emulation");
        if (mobileEmulation != null && !mobileEmulation.isBlank()) {
            // No native mobile emulation; approximate with user agent and window size
            // externally
        }
    }

    /**
     * Applies remote-specific Safari options when {@code remote} is enabled.
     * Configures capabilities for remote execution (e.g., Selenoid) including
     * browser version,
     * Selenoid options, and limited performance logging (if enabled with
     * {@code advanced_mode}).
     *
     * @param options the {@link SafariOptions} instance to configure
     */
    private void applyRemoteOptions(SafariOptions options) {
        options.setCapability("browserName", "safari");
        options.setBrowserVersion(prop.getProperty("browser_version", "latest"));
        options.setCapability("selenoid:options", getSelenoidOptions());

        if (Boolean.parseBoolean(prop.getProperty("enable_performance_logging")) &&
                Boolean.parseBoolean(prop.getProperty("advanced_mode", "false"))) {
            options.setCapability("safari:performanceLogging", getPerfLoggingPrefs());
        }
    }

    /**
     * Builds Selenoid-specific options for remote Safari execution.
     * Includes settings like screen resolution, VNC, video recording, and test
     * name.
     *
     * @return a map of Selenoid options
     */
    private Map<String, Object> getSelenoidOptions() {
        Map<String, Object> selenoidOptions = new HashMap<>();
        selenoidOptions.put("screenResolution", prop.getProperty("screen_resolution", "1280x1024x24"));
        selenoidOptions.put("enableVNC", true);
        selenoidOptions.put("enableVideo", Boolean.parseBoolean(prop.getProperty("enable_video", "false")));
        selenoidOptions.put("name", prop.getProperty("test_name", "default_test"));
        return selenoidOptions;
    }

    /**
     * Builds performance logging preferences for Safari.
     * Enables network and page performance logging for remote execution, though
     * Safari’s support may be limited.
     *
     * @return a map of performance logging preferences
     */
    private Map<String, Object> getPerfLoggingPrefs() {
        Map<String, Object> perfLoggingPrefs = new HashMap<>();
        perfLoggingPrefs.put("enableNetwork", true);
        perfLoggingPrefs.put("enablePage", true);
        return perfLoggingPrefs;
    }
}
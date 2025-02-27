package com.neuralytics.providers;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Provides Firefox-specific options for Selenium WebDriver configuration.
 * Implements {@link BrowserOptionsProvider} to supply a configured
 * {@link FirefoxOptions} instance
 * based on properties, supporting basic settings (e.g., headless mode),
 * advanced features (e.g., user agent),
 * and remote execution capabilities (e.g., Selenoid integration). This class is
 * typically used with a
 * factory or manager (e.g., {@code OptionsManager}) to initialize Firefox
 * WebDriver instances.
 *
 * <p>
 * Configuration is driven by a {@link Properties} object, with keys such as:
 * <ul>
 * <li>{@code headless}: Enables headless mode (e.g., "true").</li>
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
 * props.setProperty("headless", "true");
 * FirefoxOptionsProvider provider = new FirefoxOptionsProvider(props);
 * MutableCapabilities options = provider.getOptions();
 * </pre>
 *
 * <p>
 * Note: Unlike Chromium-based browsers, Firefox does not natively support
 * mobile emulation.
 * Such functionality can be approximated using window size and user agent
 * settings.
 *
 * @see BrowserOptionsProvider
 * @see FirefoxOptions
 */
public class FirefoxOptionsProvider implements BrowserOptionsProvider {

    /**
     * Configuration properties used to customize Firefox options.
     */
    private final Properties prop;

    /**
     * Constructs a FirefoxOptionsProvider with the specified configuration
     * properties.
     *
     * @param prop the properties defining Firefox-specific settings
     * @throws NullPointerException if prop is null
     */
    public FirefoxOptionsProvider(final Properties prop) {
        if (prop == null) {
            throw new NullPointerException("Properties cannot be null");
        }
        this.prop = prop;
    }

    /**
     * Retrieves configured Firefox options based on the provided properties.
     * Applies basic options (e.g., headless, private browsing), advanced options
     * (if {@code advanced_mode}
     * is true), and remote-specific capabilities (if {@code remote} is true). The
     * returned
     * {@link FirefoxOptions} instance is suitable for both local and remote
     * WebDriver initialization.
     *
     * @return a configured {@link FirefoxOptions} instance
     * @see #applyBasicOptions(FirefoxOptions)
     * @see #applyAdvancedOptions(FirefoxOptions)
     * @see #applyRemoteOptions(FirefoxOptions)
     */
    @Override
    public MutableCapabilities getOptions() {
        FirefoxOptions options = new FirefoxOptions();

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
     * Applies basic Firefox options that are included in all configurations.
     * Configures settings such as headless mode and private browsing based on
     * properties.
     *
     * @param options the {@link FirefoxOptions} instance to configure
     */
    private void applyBasicOptions(FirefoxOptions options) {
        if (Boolean.parseBoolean(prop.getProperty("headless"))) {
            options.addArguments("--headless");
        }
        if (Boolean.parseBoolean(prop.getProperty("incognito"))) {
            options.addArguments("-private");
        }
    }

    /**
     * Applies advanced Firefox options when {@code advanced_mode} is enabled.
     * Configures performance optimizations, window size, security settings, user
     * agent, proxy,
     * and verbose logging based on properties. Note that Firefox does not support
     * Chromium-style
     * mobile emulation; such functionality can be approximated with window size and
     * user agent.
     *
     * @param options the {@link FirefoxOptions} instance to configure
     */
    private void applyAdvancedOptions(FirefoxOptions options) {
        if (Boolean.parseBoolean(prop.getProperty("disable_gpu"))) {
            options.addArguments("--disable-gpu");
        }
        if (Boolean.parseBoolean(prop.getProperty("disable_extensions"))) {
            options.addPreference("extensions.enabledScopes", 0);
        }
        if (Boolean.parseBoolean(prop.getProperty("no_sandbox"))) {
            options.addArguments("--no-sandbox");
        }

        String windowSize = prop.getProperty("window_size");
        if (windowSize != null && !windowSize.isBlank()) {
            options.addArguments("--window-size=" + windowSize);
        }

        if (Boolean.parseBoolean(prop.getProperty("ignore_ssl_errors"))) {
            options.addPreference("security.enterprise_roots.enabled", true);
            options.addPreference("security.ssl.errorReporting.enabled", false);
        }
        if (Boolean.parseBoolean(prop.getProperty("disable_web_security"))) {
            options.addPreference("security.fileuri.strict_origin_policy", false);
        }

        String userAgent = prop.getProperty("user_agent");
        if (userAgent != null && !userAgent.isBlank()) {
            options.addPreference("general.useragent.override", userAgent);
        }

        String proxyServer = prop.getProperty("proxy_server");
        if (proxyServer != null && !proxyServer.isBlank()) {
            options.addArguments("--proxy-server=" + proxyServer);
        }

        if (Boolean.parseBoolean(prop.getProperty("enable_verbose_logging"))) {
            options.setCapability("moz:firefoxOptions", getLoggingPrefs());
        }
    }

    /**
     * Applies remote-specific Firefox options when {@code remote} is enabled.
     * Configures capabilities for remote execution (e.g., Selenoid) including
     * browser version,
     * Selenoid options, and performance logging (if enabled with
     * {@code advanced_mode}).
     *
     * @param options the {@link FirefoxOptions} instance to configure
     */
    private void applyRemoteOptions(FirefoxOptions options) {
        options.setCapability("browserName", "firefox");
        options.setBrowserVersion(prop.getProperty("browser_version", "latest"));
        options.setCapability("selenoid:options", getSelenoidOptions());

        if (Boolean.parseBoolean(prop.getProperty("enable_performance_logging")) &&
                Boolean.parseBoolean(prop.getProperty("advanced_mode", "false"))) {
            options.setCapability("moz:performanceLogging", getPerfLoggingPrefs());
        }
    }

    /**
     * Builds Selenoid-specific options for remote Firefox execution.
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
     * Builds logging preferences for verbose Firefox logging.
     * Enables detailed browser logging; performance logging is handled separately.
     *
     * @return a map of logging preferences
     */
    private Map<String, Object> getLoggingPrefs() {
        Map<String, Object> loggingPrefs = new HashMap<>();
        loggingPrefs.put("log", "ALL");
        return loggingPrefs;
    }

    /**
     * Builds performance logging preferences for Firefox.
     * Enables network and page performance logging for remote execution.
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
package com.neuralytics.providers;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.edge.EdgeOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Provides Microsoft Edge-specific options for Selenium WebDriver
 * configuration.
 * Implements {@link BrowserOptionsProvider} to supply a configured
 * {@link EdgeOptions} instance
 * based on properties, supporting basic settings (e.g., headless mode),
 * advanced features (e.g., mobile
 * emulation), and remote execution capabilities (e.g., Selenoid integration).
 * This class is typically
 * used with a factory or manager (e.g., {@code OptionsManager}) to initialize
 * Edge WebDriver instances.
 *
 * <p>
 * Configuration is driven by a {@link Properties} object, with keys such as:
 * <ul>
 * <li>{@code headless}: Enables headless mode (e.g., "true").</li>
 * <li>{@code advanced_mode}: Enables advanced options (e.g., "true").</li>
 * <li>{@code remote}: Triggers remote-specific settings (e.g., "true").</li>
 * <li>{@code mobile_emulation}: Specifies a device name for emulation (e.g.,
 * "Pixel 2").</li>
 * </ul>
 *
 * <p>
 * Usage example:
 * 
 * <pre>
 * Properties props = new Properties();
 * props.setProperty("headless", "true");
 * EdgeOptionsProvider provider = new EdgeOptionsProvider(props);
 * MutableCapabilities options = provider.getOptions();
 * </pre>
 *
 * @see BrowserOptionsProvider
 * @see EdgeOptions
 */
public class EdgeOptionsProvider implements BrowserOptionsProvider {

    /**
     * Configuration properties used to customize Edge options.
     */
    private final Properties prop;

    /**
     * Constructs an EdgeOptionsProvider with the specified configuration
     * properties.
     *
     * @param prop the properties defining Edge-specific settings
     * @throws NullPointerException if prop is null
     */
    public EdgeOptionsProvider(final Properties prop) {
        if (prop == null) {
            throw new NullPointerException("Properties cannot be null");
        }
        this.prop = prop;
    }

    /**
     * Retrieves configured Edge options based on the provided properties.
     * Applies basic options (e.g., headless, inPrivate), advanced options (if
     * {@code advanced_mode} is true),
     * and remote-specific capabilities (if {@code remote} is true). The returned
     * {@link EdgeOptions}
     * instance is suitable for both local and remote WebDriver initialization.
     *
     * @return a configured {@link EdgeOptions} instance
     * @see #applyBasicOptions(EdgeOptions)
     * @see #applyAdvancedOptions(EdgeOptions)
     * @see #applyRemoteOptions(EdgeOptions)
     */
    @Override
    public MutableCapabilities getOptions() {
        EdgeOptions options = new EdgeOptions();

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
     * Applies basic Edge options that are included in all configurations.
     * Configures settings such as headless mode and inPrivate (Edge’s incognito)
     * browsing based on properties.
     *
     * @param options the {@link EdgeOptions} instance to configure
     */
    private void applyBasicOptions(EdgeOptions options) {
        if (Boolean.parseBoolean(prop.getProperty("headless"))) {
            options.addArguments("--headless=new");
        }
        if (Boolean.parseBoolean(prop.getProperty("incognito"))) {
            options.addArguments("--inPrivate");
        }
    }

    /**
     * Applies advanced Edge options when {@code advanced_mode} is enabled.
     * Configures performance optimizations, window size, security settings, user
     * agent, proxy,
     * verbose logging, and mobile emulation based on properties.
     *
     * @param options the {@link EdgeOptions} instance to configure
     */
    private void applyAdvancedOptions(EdgeOptions options) {
        if (Boolean.parseBoolean(prop.getProperty("disable_gpu"))) {
            options.addArguments("--disable-gpu");
        }
        if (Boolean.parseBoolean(prop.getProperty("disable_extensions"))) {
            options.addArguments("--disable-extensions");
        }
        if (Boolean.parseBoolean(prop.getProperty("no_sandbox"))) {
            options.addArguments("--no-sandbox");
        }

        String windowSize = prop.getProperty("window_size");
        if (windowSize != null && !windowSize.isBlank()) {
            options.addArguments("--window-size=" + windowSize);
        }

        if (Boolean.parseBoolean(prop.getProperty("ignore_ssl_errors"))) {
            options.addArguments("--ignore-certificate-errors");
        }
        if (Boolean.parseBoolean(prop.getProperty("disable_web_security"))) {
            options.addArguments("--disable-web-security");
        }

        String userAgent = prop.getProperty("user_agent");
        if (userAgent != null && !userAgent.isBlank()) {
            options.addArguments("--user-agent=" + userAgent);
        }

        String proxyServer = prop.getProperty("proxy_server");
        if (proxyServer != null && !proxyServer.isBlank()) {
            options.addArguments("--proxy-server=" + proxyServer);
        }

        if (Boolean.parseBoolean(prop.getProperty("enable_verbose_logging"))) {
            options.setCapability("ms:loggingPrefs", getLoggingPrefs());
        }

        String mobileEmulation = prop.getProperty("mobile_emulation");
        if (mobileEmulation != null && !mobileEmulation.isBlank()) {
            options.setExperimentalOption("mobileEmulation", getMobileEmulationOptions(mobileEmulation));
        }
    }

    /**
     * Applies remote-specific Edge options when {@code remote} is enabled.
     * Configures capabilities for remote execution (e.g., Selenoid) including
     * browser version,
     * Selenoid options, and performance logging (if enabled with
     * {@code advanced_mode}).
     *
     * @param options the {@link EdgeOptions} instance to configure
     */
    private void applyRemoteOptions(EdgeOptions options) {
        options.setCapability("browserName", "MicrosoftEdge");
        options.setBrowserVersion(prop.getProperty("browser_version", "latest"));
        options.setCapability("selenoid:options", getSelenoidOptions());

        if (Boolean.parseBoolean(prop.getProperty("enable_performance_logging")) &&
                Boolean.parseBoolean(prop.getProperty("advanced_mode", "false"))) {
            options.setCapability("ms:perfLoggingPrefs", getPerfLoggingPrefs());
        }
    }

    /**
     * Builds Selenoid-specific options for remote Edge execution.
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
     * Builds logging preferences for verbose Edge logging.
     * Enables detailed logging for browser and performance categories.
     *
     * @return a map of logging preferences
     */
    private Map<String, String> getLoggingPrefs() {
        Map<String, String> loggingPrefs = new HashMap<>();
        loggingPrefs.put("browser", "ALL");
        loggingPrefs.put("performance", "ALL");
        return loggingPrefs;
    }

    /**
     * Builds mobile emulation options for Edge.
     * Configures the device name for mobile emulation (e.g., "Pixel 2").
     *
     * @param deviceName the name of the device to emulate
     * @return a map of mobile emulation options
     */
    private Map<String, Object> getMobileEmulationOptions(String deviceName) {
        Map<String, Object> mobileEmulation = new HashMap<>();
        mobileEmulation.put("deviceName", deviceName);
        return mobileEmulation;
    }

    /**
     * Builds performance logging preferences for Edge.
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
package com.neuralytics.factories;

import com.neuralytics.providers.ChromeOptionsProvider;
import com.neuralytics.providers.EdgeOptionsProvider;
import com.neuralytics.providers.FirefoxOptionsProvider;
import com.neuralytics.utils.LoggerUtil;
import org.openqa.selenium.MutableCapabilities;
import org.slf4j.Logger;

import java.util.Properties;

public class OptionsManager {

    private final Properties prop;
    private static final Logger logger = LoggerUtil.getLogger(OptionsManager.class);

    public OptionsManager(final Properties prop) {
        this.prop = prop;
    }

    /**
     * Returns browser-specific capabilities based on the browser string.
     *
     * @param browser the browser for which options are required.
     * @return the MutableCapabilities instance for the specified browser.
     */
    public MutableCapabilities getBrowserOptions(final String browser) {
        logger.info("Getting browser options for: {}", browser);
        return switch (browser.toLowerCase().trim()) {
            case "chrome" -> new ChromeOptionsProvider(prop).getOptions();
            case "firefox" -> new FirefoxOptionsProvider(prop).getOptions();
            case "edge" -> new EdgeOptionsProvider(prop).getOptions();
            default -> {
                logger.error("Unsupported Browser: {}", browser);
                throw new IllegalArgumentException("Unsupported Browser: " + browser);
            }
        };
    }
}

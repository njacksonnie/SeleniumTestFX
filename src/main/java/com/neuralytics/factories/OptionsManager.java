package com.neuralytics.factories;

import java.util.Properties;

import com.neuralytics.providers.ChromeOptionsProvider;
import com.neuralytics.providers.EdgeOptionsProvider;
import com.neuralytics.providers.FirefoxOptionsProvider;
import org.openqa.selenium.MutableCapabilities;

public class OptionsManager {

    private final Properties prop;

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
        return switch (browser.toLowerCase().trim()) {
            case "chrome" -> new ChromeOptionsProvider(prop).getOptions();
            case "firefox" -> new FirefoxOptionsProvider(prop).getOptions();
            case "edge" -> new EdgeOptionsProvider(prop).getOptions();
            default -> throw new IllegalArgumentException("Unsupported Browser: " + browser);
        };
    }
}

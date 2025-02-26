package com.neuralytics.providers;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import com.neuralytics.interfaces.BrowserOptionsProvider;

public class ChromeOptionsProvider implements BrowserOptionsProvider {

    private final Properties prop;

    public ChromeOptionsProvider(final Properties prop) {
        this.prop = prop;
    }

    @Override
    public MutableCapabilities getOptions() {
        ChromeOptions options = new ChromeOptions();

        // Add arguments based on configuration properties.
        if (Boolean.parseBoolean(prop.getProperty("headless"))) {
            options.addArguments("--headless");
        }
        if (Boolean.parseBoolean(prop.getProperty("incognito"))) {
            options.addArguments("--incognito");
        }

        // Remote-specific capabilities.
        if (Boolean.parseBoolean(prop.getProperty("remote"))) {
            options.setCapability("browserName", "chrome");
            options.setBrowserVersion(prop.getProperty("browser_version", "latest"));
            options.setCapability("selenoid:options", getSelenoidIdOptions());
        }

        return options;
    }

    private Map<String, Object> getSelenoidIdOptions() {
        Map<String, Object> selenoidOptions = new HashMap<>();
        selenoidOptions.put("screenResolution", "1280x1024x24");
        selenoidOptions.put("enableVNC", true);
        selenoidOptions.put("name", prop.getProperty("test_name", "default_test"));
        return selenoidOptions;
    }

}

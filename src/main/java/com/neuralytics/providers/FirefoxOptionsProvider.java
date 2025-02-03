package com.neuralytics.providers;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import com.neuralytics.interfaces.BrowserOptionsProvider;

public class FirefoxOptionsProvider implements BrowserOptionsProvider {

    private final Properties prop;

    public FirefoxOptionsProvider(final Properties prop) {
        this.prop = prop;
    }

    @Override
    public MutableCapabilities getOptions() {
        FirefoxOptions options = new FirefoxOptions();

        if (Boolean.parseBoolean(prop.getProperty("headless"))) {
            options.addArguments("--headless");
        }
        if (Boolean.parseBoolean(prop.getProperty("incognito"))) {
            options.addArguments("--incognito");
        }

        if (Boolean.parseBoolean(prop.getProperty("remote"))) {
            options.setCapability("browserName", "firefox");
            options.setBrowserVersion(prop.getProperty("browser_version", "latest"));
            options.setCapability("selenoid:options", getSelenoidOptions());
        }

        return options;
    }

    private Map<String, Object> getSelenoidOptions() {
        Map<String, Object> selenoidOptions = new HashMap<>();
        selenoidOptions.put("screenResolution", "1280x1024x24");
        selenoidOptions.put("enableVNC", true);
        selenoidOptions.put("name", prop.getProperty("test_name", "default_test"));
        return selenoidOptions;
    }

    @Override
    public WebDriver createDriver() {
        return new FirefoxDriver((FirefoxOptions) getOptions());
    }
}

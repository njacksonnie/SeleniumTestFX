package com.neuralytics.providers;

import java.util.Properties;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import com.neuralytics.interfaces.BrowserOptionsProvider;

public class EdgeOptionsProvider implements BrowserOptionsProvider {

    private final Properties prop;

    public EdgeOptionsProvider(final Properties prop) {
        this.prop = prop;
    }

    @Override
    public MutableCapabilities getOptions() {
        EdgeOptions options = new EdgeOptions();

        if (Boolean.parseBoolean(prop.getProperty("headless"))) {
            options.addArguments("--headless");
        }
        if (Boolean.parseBoolean(prop.getProperty("incognito"))) {
            options.addArguments("--inPrivate");
        }

        // You can add remote-specific configurations here if needed.
        return options;
    }

    @Override
    public WebDriver createDriver() {
        return new EdgeDriver((EdgeOptions) getOptions());
    }
}

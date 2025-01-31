package com.neuralytics.factories;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.MutableCapabilities;

public class WebDriverFactory {

    public static WebDriver createDriver(String browser, boolean headless) {
        BrowserOptionsProvider<? extends MutableCapabilities> optionsProvider =
                BrowserOptionsFactory.getOptionsProvider(browser);
        MutableCapabilities options = optionsProvider.getOptions(headless);
        return initializeDriver(browser, options);
    }

    private static WebDriver initializeDriver(String browser, MutableCapabilities options) {
        return switch (browser.toLowerCase()) {
            case "chrome" -> new ChromeDriver((ChromeOptions) options);
            case "firefox" -> new FirefoxDriver((FirefoxOptions) options);
            case "edge" -> new EdgeDriver((EdgeOptions) options);
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        };
    }
}
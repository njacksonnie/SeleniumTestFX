package com.neuralytics.factories;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeOptions;

public class BrowserOptionsFactory {
    public static BrowserOptionsProvider getOptionsProvider(String browser) {
        return switch (browser.toLowerCase()) {
            case "chrome" -> new ChromeOptionsProvider();
            case "firefox" -> new FirefoxOptionsProvider();
            case "edge" -> new EdgeOptionsProvider();
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        };
    }

    public interface BrowserOptionsProvider {
        MutableCapabilities getOptions(boolean headless);
    }

    private static class ChromeOptionsProvider implements BrowserOptionsProvider {
        @Override
        public MutableCapabilities getOptions(boolean headless) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments(
                    "--window-size=1920,1080",
                    "--remote-allow-origins=*",
                    "--disable-infobars",
                    "--disable-notifications"
            );
            if (headless) options.addArguments("--headless=new");
            return options;
        }
    }

    private static class FirefoxOptionsProvider implements BrowserOptionsProvider {
        @Override
        public MutableCapabilities getOptions(boolean headless) {
            FirefoxOptions options = new FirefoxOptions();
            options.addArguments("--window-size=1920,1080");
            if (headless) options.addArguments("-headless");
            options.addPreference("browser.cache.disk.enable", false);
            options.addPreference("browser.cache.memory.enable", false);
            return options;
        }
    }

    private static class EdgeOptionsProvider implements BrowserOptionsProvider {
        @Override
        public MutableCapabilities getOptions(boolean headless) {
            EdgeOptions options = new EdgeOptions();
            options.addArguments(
                    "--window-size=1920,1080",
                    "--remote-allow-origins=*",
                    "--disable-infobars",
                    "--disable-notifications"
            );
            if (headless) options.addArguments("--headless=new");
            return options;
        }
    }
}
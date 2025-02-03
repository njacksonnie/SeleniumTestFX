package com.neuralytics.interfaces;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;

public interface BrowserOptionsProvider {
    MutableCapabilities getOptions();
    WebDriver createDriver();
}

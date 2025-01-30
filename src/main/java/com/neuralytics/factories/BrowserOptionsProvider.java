package com.neuralytics.factories;

import org.openqa.selenium.MutableCapabilities;

public interface BrowserOptionsProvider<T extends MutableCapabilities> {
    T getOptions(boolean headless);
}
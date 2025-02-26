package com.neuralytics.interfaces;

import org.openqa.selenium.MutableCapabilities;

public interface BrowserOptionsProvider {
    MutableCapabilities getOptions();
}

# Selenium Test FX

Selenium Test FX is a robust, extensible testing framework built on Selenium WebDriver and TestNG, designed to streamline automated browser testing. Whether you're testing web applications on local or CI/CD environments, this framework offers powerful features like dynamic WebDriver management, detailed reporting with ExtentReports, and screenshot capture on failure. Its modular design makes it ideal for both small projects and large-scale test suites.

## Table of Contents

- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)

## Features

- **Dynamic WebDriver Management:** Seamlessly initialize and manage browsers (Chrome, Firefox, Edge, Safari) with local or remote execution via DriverFactory.
- **Comprehensive Reporting:** Generate HTML reports with ExtentReports and parallel file logs using ReportFactory.
- **Screenshot Capture:** Automatically capture screenshots on test failure with ScreenshotFactory.
- **Flexible Element Interactions:** Simplify Selenium operations with ElementUtil (e.g., clicks, dropdowns, drag-and-drop).
- **Robust Wait Handling:** Manage dynamic page loading with WaitUtil using WebDriverWait and FluentWait.
- **JavaScript Utilities:** Execute custom JavaScript actions with JavaScriptUtil (e.g., scrolling, alerts).
- **CI/CD Integration:** Detect and log CI/CD environment details with CiCdDetector.
- **Extensible Design:** Easily add new browser providers or customize reporting.

## Installation

1.  **Clone the Repository:**

    ```bash
    git clone https://github.com/njacksonnie/SeleniumTestFX.git
    ```

2.  **Install Dependencies:**

    ```bash
    mvn clean install
    ```

## Usage

```java
package com.neuralytics.tests;

import com.neuralytics.components.NavBar;
import com.neuralytics.factories.ReportFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class NavBarTest extends BaseTest {
    private NavBar navBar;

    @BeforeMethod
    public void setUpNavBar() {
        navBar = new NavBar(getDriver());
    }

    @Test
    public void testGroceriesStore() {
        ReportFactory.getInstance().logInfo("Opening groceries store");
        navBar.openGroceriesStore();
        Assert.assertEquals(driver.getTitle(), "Groceries", "Groceries page not loaded");
        ReportFactory.getInstance().logPass("Groceries store opened successfully");
    }
}
```

## Project Structure

```
SeleniumTestFX/
├── .gitignore
├── Jenkinsfile
├── pom.xml
├── sonar-project.properties
├── logs/
├── screenshots/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── neuralytics/
│   │   │           ├── components/
│   │   │           │   └── NavBar.java
│   │   │           ├── constants/
│   │   │           │   └── AppConstants.java
│   │   │           ├── enums/
│   │   │           │   └── BrowserType.java
│   │   │           ├── errors/
│   │   │           │   └── AppError.java
│   │   │           ├── exceptions/
│   │   │           │   ├── BrowserException.java
│   │   │           │   ├── ElementException.java
│   │   │           │   └── FrameworkException.java
│   │   │           ├── factories/
│   │   │           │   ├── DriverFactory.java
│   │   │           │   ├── OptionsManager.java
│   │   │           │   ├── ReportFactory.java
│   │   │           │   └── ScreenshotFactory.java
│   │   │           ├── interfaces/
│   │   │           │   └── BrowserOptionsProvider.java
│   │   │           ├── providers/
│   │   │           │   ├── ChromeOptionsProvider.java
│   │   │           │   ├── EdgeOptionsProvider.java
│   │   │           │   ├── FirefoxOptionsProvider.java
│   │   │           │   └── SafariOptionsProvider.java
│   │   │           └── utils/
│   │   │               ├── CiCdDetector.java
│   │   │               ├── ConfigLoader.java
│   │   │               ├── ElementUtil.java
│   │   │               ├── JavaScriptUtil.java
│   │   │               ├── LoggerUtil.java
│   │   │               ├── TimeUtil.java
│   │   │               └── WaitUtil.java
│   │   └── resources/
│   │       ├── log4j.properties
│   │       ├── log4j2.xml
│   │       └── logback.xml
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── neuralytics/
│       │           ├── listeners/
│       │           │   ├── TestListener.java
│       │           │   └── WebDriverProvider.java
│       │           └── tests/
│       │               ├── AmazonNavBarActionTest.java
│       │               └── BaseTest.java
│       └── resources/
│           ├── config/
│           │   └── qa.properties
│           └── testng/
│               ├── runChrome.xml
│               └── testng.xml
├── target/
└── test-output/
```

## Contributing

We welcome contributions to [Project Name]! Here’s how to get involved:

1.  **Fork the Repository:** Clone it to your local machine.
2.  **Create a Branch:** Use a descriptive name (e.g., `feature/add-edge-support`).
3.  **Make Changes:** Follow Java coding standards and add tests where applicable.
4.  **Run Tests:** Ensure all tests pass with `mvn test`.
5.  **Submit a Pull Request:** Include a clear description of your changes and reference any issues.

**Code Style:**

- Use Java 17 conventions.
- Follow existing Javadoc patterns

## License

This project is licensed under the Apache 2.0 License.

package com.neuralytics.factories;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.neuralytics.utils.LoggerUtil;
import org.slf4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.function.BiConsumer;

public class ReportFactory {
    private static volatile ReportFactory instance;
    private final ExtentReports extent;
    private static final ThreadLocal<ExtentTest> currentTest = ThreadLocal.withInitial(() -> null);
    private static final Logger logger = LoggerUtil.getLogger(ReportFactory.class);
    private static FileWriter logFileWriter;
    private static final Object logLock = new Object();

    private ReportFactory() {
        logger.trace("Initializing ExtentReports...");
        extent = new ExtentReports();
        extent.attachReporter(new ExtentSparkReporter("test-output/extent-report.html"));
        initializeLogFile();
        logger.trace("ExtentReports initialized successfully.");
    }

    public static ReportFactory getInstance() {
        if (instance == null) {
            synchronized (ReportFactory.class) {
                if (instance == null) {
                    logger.trace("Creating ReportFactory instance...");
                    instance = new ReportFactory();
                    logger.trace("ReportFactory instance created.");
                }
            }
        }
        return instance;
    }

    private void initializeLogFile() {
        try {
            String logFilePath = "logs/test-execution.log";
            synchronized (logLock) {
                logFileWriter = new FileWriter(logFilePath, true);
                logToFile("--- Test Execution Log ---");
                logToFile("Start Time: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                logger.trace("Log file initialized at: {}", logFilePath);
            }
        } catch (IOException e) {
            logger.error("Error initializing log file: {}", e.getMessage(), e);
        }
    }

    public void startTest(String testName) {
        logger.trace("Starting test: {}", testName);
        ExtentTest test = extent.createTest(testName);
        currentTest.set(test);
        logToFile("Starting test: " + testName);
        logger.trace("Test '{}' started successfully.", testName);
    }

    public void logInfo(String message) {
        logMessage(message, "INFO", ExtentTest::info);
    }

    public void logPass(String message) {
        logMessage(message, "PASS", ExtentTest::pass);
    }

    public void logFail(String message) {
        logMessage(message, "FAIL", ExtentTest::fail);
    }

    public void endTest() {
        logger.trace("Ending test...");
        currentTest.remove();
        logToFile("Ending test.");
        logger.trace("Test ended successfully.");
    }

    public synchronized void tearDown() {
        logger.trace("Flushing ExtentReports...");
        if (extent != null) {
            extent.flush();
            logToFile("ExtentReports flushed successfully.");
            logger.trace("ExtentReports flushed successfully.");
        }
        closeLogFile();
    }

    private Optional<ExtentTest> getCurrentTest() {
        return Optional.ofNullable(currentTest.get());
    }

    private synchronized void logToFile(String message) {
        try {
            if (logFileWriter != null) {
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                logFileWriter.write(timestamp + " - " + message + "\n");
                logFileWriter.flush();
            }
        } catch (IOException e) {
            logger.error("Error writing to log file: {}", e.getMessage(), e);
        }
    }

    private synchronized void closeLogFile() {
        try {
            if (logFileWriter != null) {
                logFileWriter.write("End Time: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n");
                logFileWriter.close();
                logFileWriter = null; // Prevent reuse after closing
                logger.trace("Log file closed successfully.");
            }
        } catch (IOException e) {
            logger.error("Error closing log file: {}", e.getMessage(), e);
        }
    }

    private void logMessage(String message, String level, BiConsumer<ExtentTest, String> logMethod) {
        logger.trace("Logging {}: {}", level, message);
        getCurrentTest().ifPresentOrElse(
                test -> {
                    logMethod.accept(test, message);
                    logToFile("[" + level + "] " + message);
                },
                () -> logger.warn("Attempted to log {} but no active test found.", level)
        );
    }
}
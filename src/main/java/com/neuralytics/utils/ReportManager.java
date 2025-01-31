package com.neuralytics.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.slf4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class ReportManager {
    private static volatile ReportManager instance;
    private final ExtentReports extent;
    private static final ThreadLocal<ExtentTest> currentTest = new ThreadLocal<>();
    private static final Logger logger = LoggerUtil.getLogger(ReportManager.class);

    // FileWriter for log file
    private static FileWriter logFileWriter;

    private ReportManager() {
        logger.trace("Initializing ExtentReports...");
        extent = new ExtentReports();
        extent.attachReporter(new ExtentSparkReporter("test-output/extent-report.html"));
        initializeLogFile();
        logger.trace("ExtentReports initialized successfully.");
    }

    public static ReportManager getInstance() {
        if (instance == null) {
            synchronized (ReportManager.class) {
                if (instance == null) {
                    logger.trace("Creating ReportManager instance...");
                    instance = new ReportManager();
                    logger.trace("ReportManager instance created.");
                }
            }
        }
        return instance;
    }

    /**
     * Initialize the log file writer.
     */
    private void initializeLogFile() {
        try {
            String logFilePath = "logs/test-execution.log";
            logFileWriter = new FileWriter(logFilePath, true); // Append mode
            logFileWriter.write("\n--- Test Execution Log ---\n");
            logFileWriter.write("Start Time: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n");
            logger.trace("Log file initialized at: logs/test-execution.log");
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

    public void tearDown() {
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

    /**
     * Write a message to the log file.
     */
    private void logToFile(String message) {
        try {
            if (logFileWriter != null) {
                logFileWriter.write(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " - " + message + "\n");
                logFileWriter.flush();
            }
        } catch (IOException e) {
            logger.error("Error writing to log file: {}", e.getMessage(), e);
        }
    }

    /**
     * Close the log file writer.
     */
    private void closeLogFile() {
        try {
            if (logFileWriter != null) {
                logFileWriter.write("End Time: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n");
                logFileWriter.close();
                logger.trace("Log file closed successfully.");
            }
        } catch (IOException e) {
            logger.error("Error closing log file: {}", e.getMessage(), e);
        }
    }

    /**
     * Log a message with the specified log level and ExtentTest method.
     */
    private void logMessage(String message, String level, java.util.function.BiConsumer<ExtentTest, String> logMethod) {
        logger.trace("Logging {}: {}", level, message);
        getCurrentTest().ifPresentOrElse(
                test -> {
                    logMethod.accept(test, message);
                    logToFile("[" + level + "] " + message);
                },
                () -> logger.warn("Attempted to log {}, but no active test found.", level)
        );
    }
}
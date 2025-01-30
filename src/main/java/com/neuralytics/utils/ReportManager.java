package com.neuralytics.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.slf4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        logger.trace("Logging INFO: {}", message);
        getCurrentTest().ifPresentOrElse(
                test -> {
                    test.info(message);
                    logToFile("[INFO] " + message);
                },
                () -> logger.warn("Attempted to log INFO, but no active test found.")
        );
    }

    public void logPass(String message) {
        logger.trace("Logging PASS: {}", message);
        getCurrentTest().ifPresentOrElse(
                test -> {
                    test.pass(message);
                    logToFile("[PASS] " + message);
                },
                () -> logger.warn("Attempted to log PASS, but no active test found.")
        );
    }

    public void logFail(String message) {
        logger.trace("Logging FAIL: {}", message);
        getCurrentTest().ifPresentOrElse(
                test -> {
                    test.fail(message);
                    logToFile("[FAIL] " + message);
                },
                () -> logger.warn("Attempted to log FAIL, but no active test found.")
        );
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

    private java.util.Optional<ExtentTest> getCurrentTest() {
        ExtentTest test = currentTest.get();
        return test != null ? java.util.Optional.of(test) : java.util.Optional.empty();
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
}
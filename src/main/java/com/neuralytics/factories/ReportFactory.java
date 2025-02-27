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
import java.util.function.BiConsumer;

/**
 * A singleton factory for managing test reporting with ExtentReports and
 * file-based logging.
 * Provides thread-safe methods to initialize, log, and finalize test cases,
 * generating an HTML report
 * at "test-output/extent-report.html" and a parallel log file at
 * "logs/test-execution.log". This class
 * is designed for use in a Selenium-TestNG testing framework to track test
 * execution details at both
 * test and suite levels.
 *
 * <p>
 * Key features:
 * <ul>
 * <li>Singleton pattern for a single reporting instance across the
 * application.</li>
 * <li>Thread-local test management via {@link ThreadLocal} for parallel
 * execution.</li>
 * <li>Dual logging to ExtentReports and a timestamped log file.</li>
 * <li>Suite-level logging for events outside individual tests.</li>
 * </ul>
 *
 * <p>
 * Usage example:
 * 
 * <pre>
 * ReportFactory report = ReportFactory.getInstance();
 * report.initializeLogFile();
 * report.startTest("MyTest");
 * report.logInfo("Test step executed");
 * report.logPass("Test passed");
 * report.endTest();
 * report.tearDown();
 * </pre>
 *
 * <p>
 * The {@link #initializeLogFile()} method must be called before logging
 * (typically in a test suite
 * setup phase), and suite-level messages can be logged via
 * {@link #logSuiteInfo(String)}.
 *
 * @see ExtentReports
 * @see ExtentTest
 * @see LoggerUtil
 */
public class ReportFactory {

    /**
     * Singleton instance of ReportFactory, marked volatile for thread-safe
     * double-checked locking.
     */
    private static volatile ReportFactory instance;

    /**
     * The ExtentReports instance responsible for generating the HTML test report.
     */
    private final ExtentReports extent;

    /**
     * Thread-local storage for the current test instance, ensuring thread safety in
     * parallel execution.
     */
    private static final ThreadLocal<ExtentTest> currentTest = ThreadLocal.withInitial(() -> null);

    /**
     * Logger instance for tracing reporting operations and logging errors.
     */
    private static final Logger logger = LoggerUtil.getLogger(ReportFactory.class);

    /**
     * File writer for appending test execution details to
     * "logs/test-execution.log".
     */
    private static FileWriter logFileWriter;

    /**
     * Synchronization lock for thread-safe file logging operations.
     */
    private static final Object logLock = new Object();

    /**
     * Private constructor to enforce singleton pattern and initialize
     * ExtentReports.
     * Configures the reporting engine with an {@link ExtentSparkReporter} targeting
     * "test-output/extent-report.html".
     */
    private ReportFactory() {
        logger.trace("Initializing ExtentReports...");
        extent = new ExtentReports();
        extent.attachReporter(new ExtentSparkReporter("test-output/extent-report.html"));
        logger.trace("ExtentReports initialized successfully.");
    }

    /**
     * Retrieves the singleton instance of ReportFactory, creating it if necessary.
     * Uses double-checked locking for thread-safe lazy initialization.
     *
     * @return the singleton {@code ReportFactory} instance
     */
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

    /**
     * Initializes the log file for test execution details.
     * Creates or appends to "logs/test-execution.log" with a header and start
     * timestamp. This method
     * should be called once before logging begins, typically during suite setup
     * (e.g., in
     * {@code BaseTest#setUpSuite()}).
     *
     * @throws IOException if the log file cannot be created or written to
     */
    public void initializeLogFile() throws IOException {
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
            throw new IOException("Failed to initialize log file", e);
        }
    }

    /**
     * Starts a new test case with the specified name.
     * Creates an {@link ExtentTest} instance and associates it with the current
     * thread for reporting.
     *
     * @param testName the name of the test case to start
     * @throws IllegalArgumentException if testName is null or blank
     */
    public void startTest(String testName) {
        if (testName == null || testName.isBlank()) {
            logger.error("Test name cannot be null or blank");
            throw new IllegalArgumentException("Test name cannot be null or blank");
        }
        logger.trace("Starting test: {}", testName);
        ExtentTest test = extent.createTest(testName);
        currentTest.set(test);
        logToFile("Starting test: " + testName);
        logger.trace("Test '{}' started successfully.", testName);
    }

    /**
     * Logs suite-level information to the file log without associating it with a
     * specific test.
     * Useful for recording events or metadata at the suite level (e.g., suite
     * start/end, environment info).
     *
     * @param message the suite-level message to log
     * @throws IllegalArgumentException if message is null
     */
    public void logSuiteInfo(String message) {
        if (message == null) {
            logger.warn("Attempted to log null suite info message");
            throw new IllegalArgumentException("Suite info message cannot be null");
        }
        logger.trace("Logging suite info: {}", message);
        logToFile("[SUITE-INFO] " + message);
    }

    /**
     * Logs an informational message for the current test.
     * Records the message in both ExtentReports and the log file at the "INFO"
     * level.
     *
     * @param message the informational message to log
     * @throws IllegalArgumentException if message is null
     * @throws IllegalStateException    if no test is currently active
     */
    public void logInfo(String message) {
        logMessage(message, "INFO", ExtentTest::info);
    }

    /**
     * Logs a pass status message for the current test.
     * Records the message in both ExtentReports and the log file at the "PASS"
     * level.
     *
     * @param message the pass message to log
     * @throws IllegalArgumentException if message is null
     * @throws IllegalStateException    if no test is currently active
     */
    public void logPass(String message) {
        logMessage(message, "PASS", ExtentTest::pass);
    }

    /**
     * Logs a failure message for the current test.
     * Records the message in both ExtentReports and the log file at the "FAIL"
     * level.
     *
     * @param message the failure message to log
     * @throws IllegalArgumentException if message is null
     * @throws IllegalStateException    if no test is currently active
     */
    public void logFail(String message) {
        logMessage(message, "FAIL", ExtentTest::fail);
    }

    /**
     * Ends the current test case and clears it from thread-local storage.
     * Safe to call even if no test is active (no-op in that case).
     */
    public void endTest() {
        logger.trace("Ending test...");
        currentTest.remove();
        logToFile("Ending test.");
        logger.trace("Test ended successfully.");
    }

    /**
     * Finalizes the reporting process by flushing ExtentReports and closing the log
     * file.
     * This thread-safe method should be called after all tests are complete (e.g.,
     * in suite teardown).
     */
    public synchronized void tearDown() {
        logger.trace("Flushing ExtentReports...");
        if (extent != null) {
            extent.flush();
            logToFile("ExtentReports flushed successfully.");
            logger.trace("ExtentReports flushed successfully.");
        }
        closeLogFile();
    }

    /**
     * Retrieves the current test instance for the calling thread.
     *
     * @return the active {@link ExtentTest} instance, or null if no test is active
     */
    private ExtentTest getCurrentTest() {
        return currentTest.get();
    }

    /**
     * Appends a message to the log file with a timestamp.
     * Thread-safe method for writing to the file log without ExtentReports
     * integration.
     *
     * @param message the message to write to the log file
     */
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

    /**
     * Closes the log file and records the end timestamp.
     * Thread-safe method that nullifies the file writer to prevent reuse after
     * closure.
     */
    private synchronized void closeLogFile() {
        try {
            if (logFileWriter != null) {
                logFileWriter
                        .write("End Time: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n");
                logFileWriter.close();
                logFileWriter = null;
                logger.trace("Log file closed successfully.");
            }
        } catch (IOException e) {
            logger.error("Error closing log file: {}", e.getMessage(), e);
        }
    }

    /**
     * Logs a message to both ExtentReports and the log file with the specified
     * level and method.
     * Ensures an active test exists for ExtentReports logging, throwing an
     * exception otherwise.
     *
     * @param message   the message to log
     * @param level     the log level (e.g., "INFO", "PASS", "FAIL")
     * @param logMethod the ExtentTest logging method (e.g.,
     *                  {@code ExtentTest::info})
     * @throws IllegalArgumentException if message is null
     * @throws IllegalStateException    if no test is active
     */
    private void logMessage(String message, String level, BiConsumer<ExtentTest, String> logMethod) {
        if (message == null) {
            logger.warn("Attempted to log null message at level: {}", level);
            throw new IllegalArgumentException("Message cannot be null");
        }
        logger.trace("Logging {}: {}", level, message);
        ExtentTest test = getCurrentTest();
        if (test != null) {
            logMethod.accept(test, message);
            logToFile("[" + level + "] " + message);
        } else {
            logger.warn("Attempted to log {} but no active test found.", level);
            throw new IllegalStateException("No active test found for logging " + level);
        }
    }
}
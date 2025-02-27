package com.neuralytics.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class for obtaining SLF4J loggers in a standardized manner.
 * Provides a static method to retrieve logger instances for classes within a
 * testing framework,
 * leveraging the SLF4J {@link LoggerFactory} for consistent logging
 * configuration. This class is
 * designed as a non-instantiable utility to centralize logger creation and
 * usage.
 *
 * <p>
 * Usage example:
 * 
 * <pre>
 * public class MyTestClass {
 *     private static final Logger logger = LoggerUtil.getLogger(MyTestClass.class);
 *
 *     public void testMethod() {
 *         logger.info("Test method executed");
 *     }
 * }
 * </pre>
 *
 * <p>
 * This utility ensures that all loggers are created with the same SLF4J
 * implementation,
 * facilitating unified logging across the application.
 *
 * @see Logger
 * @see LoggerFactory
 */
public class LoggerUtil {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private LoggerUtil() {
        // Intentionally empty to enforce non-instantiability
    }

    /**
     * Retrieves an SLF4J logger instance for the specified class.
     * The logger is created using {@link LoggerFactory#getLogger(Class)},
     * associating it with the
     * provided class’s name for contextual logging.
     *
     * @param clazz the class for which to create a logger (used as the logger name)
     * @return an SLF4J {@link Logger} instance for the specified class
     * @throws NullPointerException if the provided class is null
     * @see LoggerFactory#getLogger(Class)
     */
    public static Logger getLogger(Class<?> clazz) {
        if (clazz == null) {
            throw new NullPointerException("Class cannot be null");
        }
        return LoggerFactory.getLogger(clazz);
    }
}
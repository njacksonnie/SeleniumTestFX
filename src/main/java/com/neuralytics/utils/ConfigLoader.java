package com.neuralytics.utils;

import com.neuralytics.constants.AppConstants;
import com.neuralytics.exceptions.FrameworkException;
import org.slf4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * A utility class for loading and caching environment-specific configuration
 * properties.
 * This class provides a thread-safe method to load properties from a file based
 * on the "env" system
 * property (e.g., "qa", "prod"), caching the result to avoid redundant file
 * reads. It is designed for
 * use in a testing framework to supply configuration settings such as browser
 * options or test URLs.
 *
 * <p>
 * The environment is determined by the system property "env", defaulting to
 * "qa" if unspecified.
 * Supported environments map to file paths defined in {@link AppConstants}
 * (e.g.,
 * {@code CONFIG_QA_FILE_PATH}). Properties are loaded once and cached for
 * subsequent calls.
 *
 * <p>
 * Usage example:
 * 
 * <pre>
 * // Set environment via system property (e.g., -Denv=prod)
 * Properties config = ConfigLoader.loadProperties();
 * String browser = config.getProperty("browser", "chrome");
 * </pre>
 *
 * @see AppConstants
 * @see FrameworkException
 */
public class ConfigLoader {

    /**
     * Cached instance of loaded properties, initialized on first call to
     * {@link #loadProperties()}.
     * This ensures properties are loaded only once and reused across the
     * application.
     */
    private static Properties prop;

    /**
     * Logger instance for tracing property loading and logging errors.
     */
    private static final Logger logger = LoggerUtil.getLogger(ConfigLoader.class);

    /**
     * Loads configuration properties from an environment-specific file.
     * Determines the file path using the "env" system property (defaulting to "qa")
     * and loads the
     * properties into a cached {@link Properties} instance. Subsequent calls return
     * the cached
     * instance without reloading the file.
     *
     * <p>
     * Supported environments include "qa", "stage", "dev", "uat", and "prod", each
     * mapped to a
     * corresponding constant in {@link AppConstants}.
     *
     * @return the loaded and cached {@link Properties} object
     * @throws FrameworkException if the configuration file path is invalid,
     *                            missing, or cannot be read
     * @see #getConfigPath()
     */
    public static Properties loadProperties() {
        if (prop == null) {
            prop = new Properties();
            final String configPath = getConfigPath();
            logger.info("Loading properties from: {}", configPath);
            if (configPath == null || configPath.isBlank()) {
                throw new FrameworkException("Configuration file path is null or empty.");
            }
            try (FileInputStream ip = new FileInputStream(configPath)) {
                prop.load(ip);
            } catch (IOException e) {
                logger.error("Error loading properties file from path: {}", configPath, e);
                throw new FrameworkException("Error loading properties file from path: " + configPath, e);
            }
        }
        return prop;
    }

    /**
     * Determines the configuration file path based on the "env" system property.
     * Retrieves the environment name (defaulting to "qa" if not set) and maps it to
     * a file path
     * defined in {@link AppConstants}. Supported environments are "qa", "stage",
     * "dev", "uat", and
     * "prod".
     *
     * @return the path to the configuration file for the specified environment
     * @throws FrameworkException if an invalid environment name is specified
     * @see AppConstants#CONFIG_QA_FILE_PATH
     * @see AppConstants#CONFIG_FILE_PATH
     */
    private static String getConfigPath() {
        final String envName = System.getProperty("env", "qa").toLowerCase();
        logger.info("Loading configuration for environment: {}", envName);
        return switch (envName) {
            case "qa" -> AppConstants.CONFIG_QA_FILE_PATH;
            case "stage" -> AppConstants.CONFIG_STAGE_FILE_PATH;
            case "dev" -> AppConstants.CONFIG_DEV_FILE_PATH;
            case "uat" -> AppConstants.CONFIG_UAT_FILE_PATH;
            case "prod" -> AppConstants.CONFIG_FILE_PATH;
            default -> {
                logger.error("Invalid environment name: {}", envName);
                throw new FrameworkException("Invalid environment name: " + envName);
            }
        };
    }
}
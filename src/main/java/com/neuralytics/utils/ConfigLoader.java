package com.neuralytics.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.neuralytics.constants.AppConstants;
import com.neuralytics.exceptions.FrameworkException;

public class ConfigLoader {

    // Cached properties instance to avoid reloading the file multiple times.
    private static Properties prop;

    /**
     * Loads the configuration properties based on the environment.
     * The environment is determined by the system property "env" (defaults to "qa").
     *
     * @return the loaded Properties object.
     * @throws FrameworkException if the properties file cannot be loaded.
     */
    public static Properties loadProperties() {
        if (prop == null) {
            prop = new Properties();
            final String configPath = getConfigPath();
            try (FileInputStream ip = new FileInputStream(configPath)) {
                prop.load(ip);
            } catch (IOException e) {
                throw new FrameworkException("Error loading properties file from path: " + configPath, e);
            }
        }
        return prop;
    }

    /**
     * Determines the configuration file path based on the "env" system property.
     *
     * @return the path to the appropriate configuration file.
     * @throws FrameworkException if an invalid environment is specified.
     */
    private static String getConfigPath() {
        final String envName = System.getProperty("env", "qa").toLowerCase();
        return switch (envName) {
            case "qa"    -> AppConstants.CONFIG_QA_FILE_PATH;
            case "stage" -> AppConstants.CONFIG_STAGE_FILE_PATH;
            case "dev"   -> AppConstants.CONFIG_DEV_FILE_PATH;
            case "uat"   -> AppConstants.CONFIG_UAT_FILE_PATH;
            case "prod"  -> AppConstants.CONFIG_FILE_PATH;
            default      -> throw new FrameworkException("Invalid environment name: " + envName);
        };
    }
}

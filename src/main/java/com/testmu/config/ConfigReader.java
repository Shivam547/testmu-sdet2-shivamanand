package com.testmu.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Configuration Reader - Singleton pattern for loading and accessing configuration properties.
 * Supports environment-specific configuration overrides and system property injection.
 * 
 * Priority order (highest to lowest):
 * 1. System Properties (-Dproperty=value)
 * 2. Environment-specific config (config-{env}.properties)
 * 3. Base config (config.properties)
 * 4. Default values
 */
public class ConfigReader {
    private static final Logger logger = LogManager.getLogger(ConfigReader.class);
    private static Properties properties;
    private static final String BASE_CONFIG_PATH = "config/config.properties";
    
    // Private constructor to prevent instantiation
    private ConfigReader() {}
    
    // Static block for initialization
    static {
        loadProperties();
    }
    
    /**
     * Load properties from base config and environment-specific config
     */
    private static void loadProperties() {
        properties = new Properties();
        String env = System.getProperty("env", "staging");
        
        try {
            // Load base configuration
            logger.info("Loading base configuration from: {}", BASE_CONFIG_PATH);
            properties.load(new FileInputStream(BASE_CONFIG_PATH));
            
            // Load environment-specific configuration (overrides base)
            String envConfigPath = String.format("config/config-%s.properties", env);
            try (FileInputStream envConfig = new FileInputStream(envConfigPath)) {
                logger.info("Loading environment configuration from: {}", envConfigPath);
                properties.load(envConfig);
            } catch (IOException e) {
                logger.warn("Environment-specific config not found: {}. Using base config only.", envConfigPath);
            }
            
            logger.info("Configuration loaded successfully for environment: {}", env);
        } catch (IOException e) {
            logger.error("Failed to load base configuration from: {}", BASE_CONFIG_PATH);
            throw new RuntimeException("Failed to load configuration", e);
        }
    }
    
    /**
     * Reload configuration (useful for tests that need fresh config)
     */
    public static void reload() {
        loadProperties();
    }
    
    /**
     * Get property value with system property override support
     * @param key Property key
     * @return Property value or null if not found
     */
    public static String get(String key) {
        // System property takes highest priority
        String systemProperty = System.getProperty(key);
        if (systemProperty != null) {
            return systemProperty;
        }
        return properties.getProperty(key);
    }
    
    /**
     * Get property value with default fallback
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Property value or default value
     */
    public static String get(String key, String defaultValue) {
        String value = get(key);
        return value != null ? value : defaultValue;
    }
    
    /**
     * Get integer property value
     * @param key Property key
     * @return Integer value
     * @throws NumberFormatException if value cannot be parsed
     */
    public static int getInt(String key) {
        String value = get(key);
        if (value == null) {
            throw new RuntimeException("Property not found: " + key);
        }
        return Integer.parseInt(value);
    }
    
    /**
     * Get integer property value with default fallback
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Integer value or default value
     */
    public static int getInt(String key, int defaultValue) {
        String value = get(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer value for {}: {}. Using default: {}", key, value, defaultValue);
            return defaultValue;
        }
    }
    
    /**
     * Get boolean property value
     * @param key Property key
     * @return Boolean value (true if "true", false otherwise)
     */
    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }
    
    /**
     * Get boolean property value with default fallback
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Boolean value or default value
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }
    
    /**
     * Get long property value with default fallback
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Long value or default value
     */
    public static long getLong(String key, long defaultValue) {
        String value = get(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            logger.warn("Invalid long value for {}: {}. Using default: {}", key, value, defaultValue);
            return defaultValue;
        }
    }
    
    // ==================== Convenience Methods ====================
    
    /**
     * Get the base URL for the current environment
     * @return Base URL
     */
    public static String getBaseUrl() {
        String env = get("environment", "staging");
        return get("base.url." + env, get("base.url", ""));
    }
    
    /**
     * Get the API base URL
     * @return API base URL
     */
    public static String getApiBaseUrl() {
        return get("api.base.url", "");
    }
    
    /**
     * Get the configured browser name
     * @return Browser name (chrome, firefox, edge, safari)
     */
    public static String getBrowser() {
        return get("browser", "chrome").toLowerCase();
    }
    
    /**
     * Check if headless mode is enabled
     * @return true if headless mode is enabled
     */
    public static boolean isHeadless() {
        return getBoolean("headless", false);
    }
    
    /**
     * Get implicit wait timeout in seconds
     * @return Implicit wait timeout
     */
    public static int getImplicitWait() {
        return getInt("implicit.wait", 10);
    }
    
    /**
     * Get explicit wait timeout in seconds
     * @return Explicit wait timeout
     */
    public static int getExplicitWait() {
        return getInt("explicit.wait", 20);
    }
    
    /**
     * Get page load timeout in seconds
     * @return Page load timeout
     */
    public static int getPageLoadTimeout() {
        return getInt("page.load.timeout", 30);
    }
    
    /**
     * Get retry count for failed tests
     * @return Retry count
     */
    public static int getRetryCount() {
        return getInt("retry.count", 2);
    }
    
    /**
     * Check if screenshots should be captured on failure
     * @return true if screenshot on failure is enabled
     */
    public static boolean captureScreenshotOnFailure() {
        return getBoolean("screenshot.on.failure", true);
    }
    
    /**
     * Get screenshot directory path
     * @return Screenshot directory path
     */
    public static String getScreenshotPath() {
        return get("screenshot.path", "./screenshots");
    }
    
    /**
     * Get report directory path
     * @return Report directory path
     */
    public static String getReportPath() {
        return get("report.path", "./reports");
    }
    
    /**
     * Get current environment name
     * @return Environment name
     */
    public static String getEnvironment() {
        return get("environment", "staging");
    }
}

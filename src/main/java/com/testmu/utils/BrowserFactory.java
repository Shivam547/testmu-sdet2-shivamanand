package com.testmu.utils;

import com.testmu.config.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Browser Factory - Creates and manages WebDriver instances.
 * Uses ThreadLocal for thread-safe parallel execution.
 * Supports Chrome, Firefox, Edge, and Safari browsers.
 */
public class BrowserFactory {
    private static final Logger logger = LogManager.getLogger(BrowserFactory.class);
    
    // ThreadLocal ensures each thread gets its own WebDriver instance
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<String> browserNameThreadLocal = new ThreadLocal<>();
    
    /**
     * Create WebDriver using default browser from configuration
     * @return WebDriver instance
     */
    public static WebDriver createDriver() {
        return createDriver(ConfigReader.getBrowser());
    }
    
    /**
     * Create WebDriver for specified browser
     * @param browserName Browser name (chrome, firefox, edge, safari)
     * @return WebDriver instance
     */
    public static WebDriver createDriver(String browserName) {
        logger.info("Creating WebDriver for browser: {}", browserName);
        
        WebDriver driver;
        boolean headless = ConfigReader.isHeadless();
        
        switch (browserName.toLowerCase()) {
            case "chrome" -> {
                driver = createChromeDriver(headless);
            }
            case "firefox" -> {
                driver = createFirefoxDriver(headless);
            }
            case "edge" -> {
                driver = createEdgeDriver(headless);
            }
            case "safari" -> {
                driver = createSafariDriver();
            }
            default -> {
                logger.warn("Unknown browser: {}. Defaulting to Chrome.", browserName);
                driver = createChromeDriver(headless);
            }
        }
        
        configureDriver(driver);
        driverThreadLocal.set(driver);
        browserNameThreadLocal.set(browserName);
        
        logger.info("WebDriver created successfully for browser: {}", browserName);
        return driver;
    }
    
    /**
     * Create Chrome WebDriver
     */
    private static WebDriver createChromeDriver(boolean headless) {
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        
        if (headless) {
            options.addArguments("--headless=new");
        }
        
        // Common Chrome options
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-infobars");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        
        // Disable automation flags
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);
        
        // Set download preferences
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", System.getProperty("user.dir") + "/downloads");
        prefs.put("download.prompt_for_download", false);
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);
        
        return new ChromeDriver(options);
    }
    
    /**
     * Create Firefox WebDriver
     */
    private static WebDriver createFirefoxDriver(boolean headless) {
        WebDriverManager.firefoxdriver().setup();
        
        FirefoxOptions options = new FirefoxOptions();
        
        if (headless) {
            options.addArguments("--headless");
        }
        
        // Firefox preferences
        options.addPreference("dom.webnotifications.enabled", false);
        options.addPreference("dom.push.enabled", false);
        
        return new FirefoxDriver(options);
    }
    
    /**
     * Create Edge WebDriver
     */
    private static WebDriver createEdgeDriver(boolean headless) {
        WebDriverManager.edgedriver().setup();
        
        EdgeOptions options = new EdgeOptions();
        
        if (headless) {
            options.addArguments("--headless=new");
        }
        
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--remote-allow-origins=*");
        
        return new EdgeDriver(options);
    }
    
    /**
     * Create Safari WebDriver (macOS only, no headless support)
     */
    private static WebDriver createSafariDriver() {
        // Safari doesn't need WebDriverManager - uses built-in safaridriver
        logger.info("Creating Safari driver - headless mode not supported");
        return new SafariDriver();
    }
    
    /**
     * Configure WebDriver with timeouts
     */
    private static void configureDriver(WebDriver driver) {
        // Maximize window
        driver.manage().window().maximize();
        
        // Set timeouts
        driver.manage().timeouts().implicitlyWait(
            Duration.ofSeconds(ConfigReader.getImplicitWait())
        );
        driver.manage().timeouts().pageLoadTimeout(
            Duration.ofSeconds(ConfigReader.getPageLoadTimeout())
        );
        driver.manage().timeouts().scriptTimeout(
            Duration.ofSeconds(ConfigReader.getExplicitWait())
        );
        
        // Delete all cookies for clean state
        driver.manage().deleteAllCookies();
    }
    
    /**
     * Get the WebDriver instance for current thread
     * @return WebDriver instance or null if not initialized
     */
    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }
    
    /**
     * Get the browser name for current thread
     * @return Browser name
     */
    public static String getBrowserName() {
        return browserNameThreadLocal.get();
    }
    
    /**
     * Quit WebDriver and remove from ThreadLocal
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                logger.info("Quitting WebDriver for browser: {}", browserNameThreadLocal.get());
                driver.quit();
            } catch (Exception e) {
                logger.error("Error while quitting WebDriver: {}", e.getMessage());
            } finally {
                driverThreadLocal.remove();
                browserNameThreadLocal.remove();
            }
        }
    }
    
    /**
     * Check if WebDriver is active
     * @return true if driver exists and session is active
     */
    public static boolean isDriverActive() {
        WebDriver driver = driverThreadLocal.get();
        if (driver == null) {
            return false;
        }
        try {
            driver.getTitle();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

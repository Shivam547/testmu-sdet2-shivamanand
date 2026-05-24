package com.testmu.tests;

import com.testmu.config.ConfigReader;
import com.testmu.listeners.TestListener;
import com.testmu.utils.BrowserFactory;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.*;

/**
 * Base Test - Abstract base class for all UI tests.
 * Handles WebDriver lifecycle, configuration, and common setup/teardown.
 */
@Listeners(TestListener.class)
public abstract class BaseTest {
    
    protected WebDriver driver;
    protected final Logger logger = LogManager.getLogger(this.getClass());
    
    /**
     * Setup method - runs before each test method
     * Initializes WebDriver with specified or default browser
     * 
     * @param browser Browser name (optional, from testng.xml or system property)
     * @param context Test context
     */
    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser"})
    public void setUp(@Optional("") String browser, ITestContext context) {
        // Determine browser: parameter > system property > config
        String browserToUse = determineBrowser(browser);
        
        logger.info("Setting up test with browser: {}", browserToUse);
        logger.info("Environment: {}", ConfigReader.getEnvironment());
        logger.info("Base URL: {}", ConfigReader.getBaseUrl());
        
        // Create WebDriver
        driver = BrowserFactory.createDriver(browserToUse);
        
        // Add test parameters to Allure report
        Allure.parameter("Browser", browserToUse);
        Allure.parameter("Environment", ConfigReader.getEnvironment());
        Allure.parameter("Base URL", ConfigReader.getBaseUrl());
        Allure.parameter("Headless", String.valueOf(ConfigReader.isHeadless()));
    }
    
    /**
     * Teardown method - runs after each test method
     * Quits WebDriver and cleans up
     */
    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        logger.info("Tearing down test - closing browser");
        BrowserFactory.quitDriver();
    }
    
    /**
     * Suite setup - runs once before all tests in suite
     */
    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        logger.info("╔══════════════════════════════════════════════════════════════╗");
        logger.info("║         INITIALIZING TEST SUITE                              ║");
        logger.info("╠══════════════════════════════════════════════════════════════╣");
        logger.info("║  Environment: {}                                             ", ConfigReader.getEnvironment());
        logger.info("║  Browser: {}                                                 ", ConfigReader.getBrowser());
        logger.info("║  Headless: {}                                                ", ConfigReader.isHeadless());
        logger.info("║  Base URL: {}                                                ", ConfigReader.getBaseUrl());
        logger.info("║  API URL: {}                                                 ", ConfigReader.getApiBaseUrl());
        logger.info("╚══════════════════════════════════════════════════════════════╝");
    }
    
    /**
     * Suite teardown - runs once after all tests in suite
     */
    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        logger.info("╔══════════════════════════════════════════════════════════════╗");
        logger.info("║         TEST SUITE COMPLETED                                 ║");
        logger.info("╚══════════════════════════════════════════════════════════════╝");
    }
    
    /**
     * Class setup - runs once before all tests in class
     */
    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        logger.info("Starting test class: {}", this.getClass().getSimpleName());
    }
    
    /**
     * Class teardown - runs once after all tests in class
     */
    @AfterClass(alwaysRun = true)
    public void afterClass() {
        logger.info("Completed test class: {}", this.getClass().getSimpleName());
    }
    
    // ==================== Helper Methods ====================
    
    /**
     * Determine which browser to use
     * Priority: testng parameter > system property > config file
     */
    private String determineBrowser(String browserParam) {
        if (browserParam != null && !browserParam.isEmpty()) {
            return browserParam;
        }
        
        String systemBrowser = System.getProperty("browser");
        if (systemBrowser != null && !systemBrowser.isEmpty()) {
            return systemBrowser;
        }
        
        return ConfigReader.getBrowser();
    }
    
    /**
     * Navigate to base URL
     */
    protected void navigateToBaseUrl() {
        String baseUrl = ConfigReader.getBaseUrl();
        logger.info("Navigating to base URL: {}", baseUrl);
        driver.get(baseUrl);
    }
    
    /**
     * Navigate to specific path on base URL
     * @param path Path to append to base URL
     */
    protected void navigateTo(String path) {
        String url = ConfigReader.getBaseUrl() + path;
        logger.info("Navigating to: {}", url);
        driver.get(url);
    }
    
    /**
     * Get current URL
     * @return Current URL
     */
    protected String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
    
    /**
     * Get page title
     * @return Page title
     */
    protected String getPageTitle() {
        return driver.getTitle();
    }
    
    /**
     * Refresh the current page
     */
    protected void refreshPage() {
        driver.navigate().refresh();
    }
    
    /**
     * Navigate back
     */
    protected void navigateBack() {
        driver.navigate().back();
    }
}

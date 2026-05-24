package com.testmu.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Secure Area Page - Page Object for the authenticated dashboard/secure area.
 * This represents the page after successful login.
 * URL: https://the-internet.herokuapp.com/secure
 */
public class SecureAreaPage extends BasePage {
    
    // ==================== Locators ====================
    
    @FindBy(css = "h2")
    private WebElement pageHeader;
    
    @FindBy(css = "h4.subheader")
    private WebElement welcomeMessage;
    
    @FindBy(css = "a[href='/logout']")
    private WebElement logoutButton;
    
    @FindBy(id = "flash")
    private WebElement flashMessage;
    
    // By locators
    private final By logoutLocator = By.cssSelector("a[href='/logout']");
    private final By welcomeLocator = By.cssSelector("h4.subheader");
    private final By headerLocator = By.cssSelector("h2");
    
    /**
     * Constructor
     * @param driver WebDriver instance
     */
    public SecureAreaPage(WebDriver driver) {
        super(driver);
        logger.info("Secure Area Page initialized");
    }
    
    // ==================== Page Actions ====================
    
    /**
     * Get the welcome message text
     * @return Welcome message
     */
    @Step("Get welcome message")
    public String getWelcomeMessage() {
        waitUtils.waitForVisible(welcomeLocator);
        String message = getText(welcomeMessage);
        logger.debug("Welcome message: {}", message);
        return message;
    }
    
    /**
     * Get page header text
     * @return Header text
     */
    @Step("Get page header")
    public String getPageHeader() {
        return getText(pageHeader);
    }
    
    /**
     * Get flash message text (success/error notification)
     * @return Flash message text
     */
    @Step("Get flash message")
    public String getFlashMessage() {
        waitUtils.waitForVisible(flashMessage);
        return getText(flashMessage);
    }
    
    /**
     * Click logout button
     * @return LoginPage after logout
     */
    @Step("Click logout")
    public LoginPage logout() {
        logger.info("Logging out");
        click(logoutButton);
        return new LoginPage(driver);
    }
    
    /**
     * Check if logout button is displayed
     * @return true if displayed
     */
    public boolean isLogoutButtonDisplayed() {
        return isDisplayed(logoutButton);
    }
    
    /**
     * Check if logout button is enabled
     * @return true if enabled
     */
    public boolean isLogoutButtonEnabled() {
        try {
            return logoutButton.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if user is logged in (secure area is displayed)
     * @return true if logged in
     */
    @Step("Check if user is logged in")
    public boolean isLoggedIn() {
        try {
            waitUtils.waitForVisible(logoutLocator, 5);
            boolean headerVisible = isDisplayed(pageHeader);
            boolean logoutVisible = isDisplayed(logoutButton);
            boolean urlCorrect = getCurrentUrl().contains("/secure");
            
            boolean loggedIn = headerVisible && logoutVisible && urlCorrect;
            logger.debug("User logged in: {}", loggedIn);
            return loggedIn;
        } catch (Exception e) {
            logger.debug("User not logged in: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Verify success message is displayed after login
     * @return true if success message is displayed
     */
    @Step("Check for success message")
    public boolean isSuccessMessageDisplayed() {
        try {
            String flashClass = getAttribute(flashMessage, "class");
            return flashClass != null && flashClass.contains("success");
        } catch (Exception e) {
            return false;
        }
    }
    
    // ==================== Page Validation ====================
    
    /**
     * Verify secure area page is loaded
     * @return true if page is properly loaded
     */
    @Override
    public boolean isLoaded() {
        try {
            waitUtils.waitForPageLoad();
            waitUtils.waitForVisible(logoutLocator, 10);
            
            boolean headerVisible = isDisplayed(pageHeader);
            boolean logoutVisible = isDisplayed(logoutButton);
            boolean urlCorrect = getCurrentUrl().contains("/secure");
            
            boolean loaded = headerVisible && logoutVisible && urlCorrect;
            logger.debug("Secure area page loaded: {}", loaded);
            return loaded;
        } catch (Exception e) {
            logger.error("Secure area page not loaded: {}", e.getMessage());
            return false;
        }
    }
}

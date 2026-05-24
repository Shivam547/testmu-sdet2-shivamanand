package com.testmu.pages;

import com.testmu.config.ConfigReader;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Login Page - Page Object for the login functionality.
 * Uses The Internet Herokuapp for demonstration.
 * URL: https://the-internet.herokuapp.com/login
 */
public class LoginPage extends BasePage {
    
    // Page URL path
    private static final String LOGIN_PATH = "/login";
    
    // ==================== Locators ====================
    // Using @FindBy for PageFactory initialization
    
    @FindBy(id = "username")
    private WebElement usernameInput;
    
    @FindBy(id = "password")
    private WebElement passwordInput;
    
    @FindBy(css = "button[type='submit']")
    private WebElement loginButton;
    
    @FindBy(id = "flash")
    private WebElement flashMessage;
    
    @FindBy(css = "h2")
    private WebElement pageHeader;
    
    // Alternative locators using By (for demonstration)
    private final By usernameLocator = By.id("username");
    private final By passwordLocator = By.id("password");
    private final By loginButtonLocator = By.cssSelector("button[type='submit']");
    private final By flashMessageLocator = By.id("flash");
    
    /**
     * Constructor
     * @param driver WebDriver instance
     */
    public LoginPage(WebDriver driver) {
        super(driver);
        logger.info("Login Page initialized");
    }
    
    // ==================== Page Actions ====================
    
    /**
     * Open the login page
     * @return LoginPage for chaining
     */
    @Step("Open login page")
    public LoginPage open() {
        String url = ConfigReader.getBaseUrl() + LOGIN_PATH;
        navigateTo(url);
        logger.info("Opened login page: {}", url);
        return this;
    }
    
    /**
     * Enter username
     * @param username Username to enter
     * @return LoginPage for chaining
     */
    @Step("Enter username: {username}")
    public LoginPage enterUsername(String username) {
        type(usernameInput, username);
        logger.debug("Entered username: {}", username);
        return this;
    }
    
    /**
     * Enter password
     * @param password Password to enter
     * @return LoginPage for chaining
     */
    @Step("Enter password")
    public LoginPage enterPassword(String password) {
        type(passwordInput, password);
        logger.debug("Entered password: ****");
        return this;
    }
    
    /**
     * Click login button
     */
    @Step("Click login button")
    public void clickLogin() {
        click(loginButton);
        logger.info("Clicked login button");
    }
    
    /**
     * Perform login with credentials
     * @param username Username
     * @param password Password
     * @return SecureAreaPage on success
     */
    @Step("Login with username: {username}")
    public SecureAreaPage loginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
        logger.info("Performed login for user: {}", username);
        return new SecureAreaPage(driver);
    }
    
    /**
     * Attempt login with invalid credentials (stays on login page)
     * @param username Username
     * @param password Password
     * @return LoginPage for validation
     */
    @Step("Attempt login with invalid credentials")
    public LoginPage loginWithInvalidCredentials(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
        logger.info("Attempted login with invalid credentials");
        return this;
    }
    
    /**
     * Get flash/error message text
     * @return Message text
     */
    @Step("Get flash message")
    public String getFlashMessage() {
        waitUtils.waitForVisible(flashMessageLocator);
        String message = getText(flashMessage);
        logger.debug("Flash message: {}", message);
        return message;
    }
    
    /**
     * Check if error message is displayed
     * @return true if error message is displayed
     */
    @Step("Check if error is displayed")
    public boolean isErrorDisplayed() {
        try {
            waitUtils.waitForVisible(flashMessageLocator, 5);
            String flashClass = getAttribute(flashMessage, "class");
            return flashClass != null && flashClass.contains("error");
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if success message is displayed
     * @return true if success message is displayed
     */
    @Step("Check if success message is displayed")
    public boolean isSuccessDisplayed() {
        try {
            waitUtils.waitForVisible(flashMessageLocator, 5);
            String flashClass = getAttribute(flashMessage, "class");
            return flashClass != null && flashClass.contains("success");
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get page header text
     * @return Header text
     */
    public String getPageHeader() {
        return getText(pageHeader);
    }
    
    /**
     * Clear login form
     * @return LoginPage for chaining
     */
    @Step("Clear login form")
    public LoginPage clearForm() {
        clear(usernameLocator);
        clear(passwordLocator);
        return this;
    }
    
    /**
     * Check if username field is displayed
     * @return true if displayed
     */
    public boolean isUsernameFieldDisplayed() {
        return isDisplayed(usernameInput);
    }
    
    /**
     * Check if password field is displayed
     * @return true if displayed
     */
    public boolean isPasswordFieldDisplayed() {
        return isDisplayed(passwordInput);
    }
    
    /**
     * Check if login button is enabled
     * @return true if enabled
     */
    public boolean isLoginButtonEnabled() {
        return usernameInput.isEnabled();
    }
    
    // ==================== Page Validation ====================
    
    /**
     * Verify login page is loaded
     * @return true if page is properly loaded
     */
    @Override
    public boolean isLoaded() {
        try {
            waitUtils.waitForPageLoad();
            boolean usernameVisible = isDisplayed(usernameInput);
            boolean passwordVisible = isDisplayed(passwordInput);
            boolean loginBtnVisible = isDisplayed(loginButton);
            boolean urlCorrect = getCurrentUrl().contains("/login");
            
            boolean loaded = usernameVisible && passwordVisible && loginBtnVisible && urlCorrect;
            logger.debug("Login page loaded: {}", loaded);
            return loaded;
        } catch (Exception e) {
            logger.error("Login page not loaded: {}", e.getMessage());
            return false;
        }
    }
}

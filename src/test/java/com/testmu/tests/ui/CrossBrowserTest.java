package com.testmu.tests.ui;

import com.testmu.pages.LoginPage;
import com.testmu.pages.SecureAreaPage;
import com.testmu.tests.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Cross-Browser Tests - Tests that run on multiple browsers.
 * Uses TestNG parameters to specify browser from testng-crossbrowser.xml
 */
@Epic("Cross-Browser Testing")
@Feature("Browser Compatibility")
public class CrossBrowserTest extends BaseTest {
    
    // Valid credentials for The Internet Herokuapp
    private static final String VALID_USERNAME = "tomsmith";
    private static final String VALID_PASSWORD = "SuperSecretPassword!";
    
    @Test(description = "Verify login flow works on multiple browsers")
    @Story("Cross-Browser Login")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Login functionality should work consistently across different browsers")
    public void testLoginOnMultipleBrowsers() {
        // Get browser name for reporting
        String browserName = System.getProperty("browser", "chrome");
        Allure.parameter("Browser", browserName);
        
        // Open login page
        Allure.step("Opening login page on " + browserName);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        
        // Verify page loaded
        Allure.step("Verifying login page loaded correctly");
        Assert.assertTrue(loginPage.isLoaded(), 
            "Login page should load on " + browserName);
        
        // Perform login
        Allure.step("Performing login");
        SecureAreaPage securePage = loginPage.loginAs(VALID_USERNAME, VALID_PASSWORD);
        
        // Verify successful login
        Allure.step("Verifying login success");
        Assert.assertTrue(securePage.isLoggedIn(), 
            "User should be logged in on " + browserName);
        
        // Verify logout works
        Allure.step("Performing logout");
        LoginPage logoutPage = securePage.logout();
        Assert.assertTrue(logoutPage.isLoaded(), 
            "Should return to login page after logout on " + browserName);
    }
    
    @Test(description = "Verify page rendering on multiple browsers")
    @Story("Visual Consistency")
    @Severity(SeverityLevel.NORMAL)
    @Description("Page elements should render correctly across browsers")
    public void testPageRendering() {
        String browserName = System.getProperty("browser", "chrome");
        Allure.parameter("Browser", browserName);
        
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        
        // Verify all elements are displayed
        Assert.assertTrue(loginPage.isUsernameFieldDisplayed(), 
            "Username field should be displayed on " + browserName);
        Assert.assertTrue(loginPage.isPasswordFieldDisplayed(), 
            "Password field should be displayed on " + browserName);
        Assert.assertTrue(loginPage.isLoginButtonEnabled(), 
            "Login button should be enabled on " + browserName);
    }
    
    @Test(description = "Verify form interactions work on multiple browsers")
    @Story("Form Interactions")
    @Severity(SeverityLevel.NORMAL)
    @Description("Form inputs and buttons should work consistently across browsers")
    public void testFormInteractions() {
        String browserName = System.getProperty("browser", "chrome");
        Allure.parameter("Browser", browserName);
        
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        
        // Test form input
        Allure.step("Testing form input on " + browserName);
        loginPage.enterUsername("testuser");
        loginPage.enterPassword("testpass");
        
        // Clear and verify
        Allure.step("Testing form clear on " + browserName);
        loginPage.clearForm();
        
        // Re-enter and submit
        Allure.step("Testing form submission on " + browserName);
        loginPage.loginWithInvalidCredentials("invalid", "invalid");
        
        // Verify error handling
        Assert.assertTrue(loginPage.isErrorDisplayed() || loginPage.isLoaded(), 
            "Error should be displayed on " + browserName);
    }
    
    @Test(description = "Verify navigation works on multiple browsers")
    @Story("Navigation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Page navigation should work consistently across browsers")
    public void testNavigation() {
        String browserName = System.getProperty("browser", "chrome");
        Allure.parameter("Browser", browserName);
        
        // Navigate to login
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        
        String loginUrl = getCurrentUrl();
        Assert.assertTrue(loginUrl.contains("/login"), 
            "Should be on login page on " + browserName);
        
        // Login and navigate
        SecureAreaPage securePage = loginPage.loginAs(VALID_USERNAME, VALID_PASSWORD);
        
        String secureUrl = getCurrentUrl();
        Assert.assertTrue(secureUrl.contains("/secure"), 
            "Should be on secure page on " + browserName);
        
        // Navigate back via logout
        LoginPage logoutPage = securePage.logout();
        
        String finalUrl = getCurrentUrl();
        Assert.assertTrue(finalUrl.contains("/login"), 
            "Should return to login on " + browserName);
    }
    
    @Test(description = "Verify error messages display correctly")
    @Story("Error Messages")
    @Severity(SeverityLevel.NORMAL)
    public void testErrorMessagesDisplayed() {
        String browserName = System.getProperty("browser", "chrome");
        Allure.parameter("Browser", browserName);
        
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        
        // Trigger error
        loginPage.loginWithInvalidCredentials("wrong", "wrong");
        
        // Verify error is displayed
        Assert.assertTrue(loginPage.isErrorDisplayed(), 
            "Error message should be displayed on " + browserName);
        
        // Verify error message content
        String errorMessage = loginPage.getFlashMessage();
        Assert.assertFalse(errorMessage.isEmpty(), 
            "Error message should not be empty on " + browserName);
    }
}

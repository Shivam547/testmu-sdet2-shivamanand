package com.testmu.tests.ui;

import com.testmu.listeners.RetryAnalyzer;
import com.testmu.pages.LoginPage;
import com.testmu.pages.SecureAreaPage;
import com.testmu.tests.BaseTest;
import com.testmu.utils.JsonUtils;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

/**
 * Login Tests - UI tests for login functionality.
 * Tests valid login, invalid login, and data-driven login scenarios.
 */
@Epic("Authentication")
@Feature("Login")
@Listeners({
  io.qameta.allure.testng.AllureTestNg.class,
  com.testmu.listeners.TestListener.class
})
public class LoginTest extends BaseTest {
    
    private LoginPage loginPage;
    
    // Valid credentials for The Internet Herokuapp
    private static final String VALID_USERNAME = "tomsmith";
    private static final String VALID_PASSWORD = "SuperSecretPassword!";
    
    @BeforeMethod
    public void initLoginPage() {
        loginPage = new LoginPage(driver);
        loginPage.open();
    }
    
    @Test(description = "Verify successful login with valid credentials")
    @Story("User Login")
    @Severity(SeverityLevel.BLOCKER)
    @Description("User should be able to login with valid username and password")
    public void testValidLogin() {
        // Verify login page loaded
        Assert.assertTrue(loginPage.isLoaded(), "Login page should be loaded");
        
        // Perform login
        SecureAreaPage securePage = loginPage.loginAs(VALID_USERNAME, VALID_PASSWORD);
        
        // Verify successful login
        Assert.assertTrue(securePage.isLoggedIn(), "User should be logged in");
        Assert.assertTrue(securePage.isSuccessMessageDisplayed(), "Success message should be displayed");
        
        String welcomeMessage = securePage.getWelcomeMessage();
        Assert.assertTrue(welcomeMessage.toLowerCase().contains("secure area"), 
            "Welcome message should mention secure area");
    }
    
    @Test(description = "Verify error message for invalid username")
    @Story("User Login")
    @Severity(SeverityLevel.CRITICAL)
    @Description("System should show error message when login with invalid username")
    public void testInvalidUsername() {
        loginPage.loginWithInvalidCredentials("invalid_user", VALID_PASSWORD);
        
        // Verify error message
        Assert.assertTrue(loginPage.isErrorDisplayed(), "Error message should be displayed");
        
        String errorMessage = loginPage.getFlashMessage();
        Assert.assertTrue(errorMessage.toLowerCase().contains("invalid"), 
            "Error should mention invalid credentials");
    }
    
    @Test(description = "Verify error message for invalid password")
    @Story("User Login")
    @Severity(SeverityLevel.CRITICAL)
    @Description("System should show error message when login with invalid password")
    public void testInvalidPassword() {
        loginPage.loginWithInvalidCredentials(VALID_USERNAME, "wrong_password");
        
        // Verify error message
        Assert.assertTrue(loginPage.isErrorDisplayed(), "Error message should be displayed");
        
        String errorMessage = loginPage.getFlashMessage();
        Assert.assertTrue(errorMessage.toLowerCase().contains("invalid"), 
            "Error should mention invalid credentials");
    }
    
    @Test(description = "Verify error for empty username")
    @Story("User Login")
    @Severity(SeverityLevel.NORMAL)
    @Description("System should show error when username is empty")
    public void testEmptyUsername() {
        loginPage.loginWithInvalidCredentials("", VALID_PASSWORD);
        
        // Should show error or stay on login page
        Assert.assertTrue(loginPage.isErrorDisplayed() || loginPage.isLoaded(), 
            "Should show error or stay on login page");
    }
    
    @Test(description = "Verify error for empty password")
    @Story("User Login")
    @Severity(SeverityLevel.NORMAL)
    @Description("System should show error when password is empty")
    public void testEmptyPassword() {
        loginPage.loginWithInvalidCredentials(VALID_USERNAME, "");
        
        // Should show error or stay on login page
        Assert.assertTrue(loginPage.isErrorDisplayed() || loginPage.isLoaded(), 
            "Should show error or stay on login page");
    }
    
    @Test(description = "Verify error for empty credentials")
    @Story("User Login")
    @Severity(SeverityLevel.NORMAL)
    @Description("System should show error when both fields are empty")
    public void testEmptyCredentials() {
        loginPage.clickLogin();
        
        // Should show error or stay on login page
        Assert.assertTrue(loginPage.isErrorDisplayed() || loginPage.isLoaded(), 
            "Should show error or stay on login page");
    }
    
    @Test(description = "Verify successful logout")
    @Story("User Logout")
    @Severity(SeverityLevel.BLOCKER)
    @Description("User should be able to logout successfully")
    public void testLogout() {
        // Login first
        SecureAreaPage securePage = loginPage.loginAs(VALID_USERNAME, VALID_PASSWORD);
        Assert.assertTrue(securePage.isLoggedIn(), "User should be logged in");
        
        // Logout
        LoginPage logoutPage = securePage.logout();
        
        // Verify logout
        Assert.assertTrue(logoutPage.isLoaded(), "Should return to login page");
        
        // Verify logged out message
        String flashMessage = logoutPage.getFlashMessage();
        Assert.assertTrue(flashMessage.toLowerCase().contains("logged out"), 
            "Should show logged out message");
    }
    
    @Test(description = "Data-driven login test", 
          dataProvider = "loginDataProvider",
          retryAnalyzer = RetryAnalyzer.class)
    @Story("User Login")
    @Severity(SeverityLevel.NORMAL)
    @Description("Parameterized test with multiple credential sets")
    public void testDataDrivenLogin(String username, String password, boolean shouldPass, String description) {
        Allure.description("Test case: " + description);
        Allure.parameter("Username", username);
        Allure.parameter("Should Pass", String.valueOf(shouldPass));
        
        if (shouldPass) {
            SecureAreaPage securePage = loginPage.loginAs(username, password);
            Assert.assertTrue(securePage.isLoggedIn(), "Login should succeed for: " + description);
        } else {
            loginPage.loginWithInvalidCredentials(username, password);
            Assert.assertTrue(loginPage.isErrorDisplayed() || loginPage.isLoaded(), 
                "Login should fail for: " + description);
        }
    }
    
    /**
     * Data provider for login test data
     */
    @DataProvider(name = "loginDataProvider")
    public Object[][] loginData() {
        return new Object[][] {
            {VALID_USERNAME, VALID_PASSWORD, true, "Valid credentials"},
            {"invalid_user", "invalid_pass", false, "Invalid username and password"},
            {VALID_USERNAME, "wrong_password", false, "Valid username, invalid password"},
            {"wrong_user", VALID_PASSWORD, false, "Invalid username, valid password"},
            {"", VALID_PASSWORD, false, "Empty username"},
            {VALID_USERNAME, "", false, "Empty password"},
            {"  ", "  ", false, "Whitespace only credentials"}
        };
    }
    
    /**
     * Data provider that loads from JSON file
     */
    @DataProvider(name = "jsonLoginDataProvider")
    public Object[][] jsonLoginData() {
        try {
            List<Map<String, Object>> testData = JsonUtils.readJsonArray(
                "testdata/login-data.json", 
                (Class<Map<String, Object>>) (Class<?>) Map.class
            );
            
            Object[][] data = new Object[testData.size()][4];
            for (int i = 0; i < testData.size(); i++) {
                Map<String, Object> row = testData.get(i);
                data[i][0] = row.get("username");
                data[i][1] = row.get("password");
                data[i][2] = row.get("shouldPass");
                data[i][3] = row.get("description");
            }
            return data;
        } catch (Exception e) {
            logger.warn("Could not load JSON test data, using default data provider");
            return loginData();
        }
    }
    
    @Test(description = "Verify login page elements are displayed")
    @Story("Login Page")
    @Severity(SeverityLevel.MINOR)
    @Description("All login form elements should be visible")
    public void testLoginPageElements() {
        Assert.assertTrue(loginPage.isUsernameFieldDisplayed(), "Username field should be displayed");
        Assert.assertTrue(loginPage.isPasswordFieldDisplayed(), "Password field should be displayed");
        Assert.assertTrue(loginPage.isLoginButtonEnabled(), "Login button should be enabled");
    }
    
    @Test(description = "Verify page header")
    @Story("Login Page")
    @Severity(SeverityLevel.MINOR)
    public void testLoginPageHeader() {
        String header = loginPage.getPageHeader();
        Assert.assertTrue(header.toLowerCase().contains("login"), 
            "Page header should contain 'login'");
    }
}

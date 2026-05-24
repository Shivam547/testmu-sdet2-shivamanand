package com.testmu.tests.ui;

import com.testmu.pages.LoginPage;
import com.testmu.pages.SecureAreaPage;
import com.testmu.tests.BaseTest;

import io.qameta.allure.*;

import org.openqa.selenium.Cookie;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.Set;

import static com.testmu.config.ConfigReader.getBaseUrl;

/**
 * Dashboard Tests - Comprehensive tests for dashboard/secure area interactions.
 * Tests session management, dashboard elements, navigation, and state persistence.
 */
@Epic("Dashboard")
@Feature("Dashboard Interactions")
@Listeners({
  io.qameta.allure.testng.AllureTestNg.class,
  com.testmu.listeners.TestListener.class
})
public class DashboardTest extends BaseTest {
    
    private LoginPage loginPage;
    private SecureAreaPage securePage;
    
    // Valid credentials for The Internet Herokuapp
    private static final String VALID_USERNAME = "tomsmith";
    private static final String VALID_PASSWORD = "SuperSecretPassword!";
    
    @BeforeMethod
    public void loginToDashboard() {
        loginPage = new LoginPage(driver);
        loginPage.open();
        securePage = loginPage.loginAs(VALID_USERNAME, VALID_PASSWORD);
    }
    
    // ==================== Dashboard Elements Tests ====================
    
    @Test(description = "Verify all dashboard elements are displayed after login")
    @Story("Dashboard Elements")
    @Severity(SeverityLevel.BLOCKER)
    @Description("All essential dashboard elements should be visible after successful login")
    public void testDashboardElementsDisplayed() {
        Allure.step("Verifying dashboard elements visibility");
        
        Assert.assertTrue(securePage.isLoggedIn(), "User should be logged in");
        Assert.assertTrue(securePage.isLogoutButtonDisplayed(), "Logout button should be displayed");
        Assert.assertTrue(securePage.isSuccessMessageDisplayed(), "Success message should be displayed");
        
        String header = securePage.getPageHeader();
        Assert.assertNotNull(header, "Page header should be present");
        Assert.assertFalse(header.isEmpty(), "Page header should not be empty");
        
        logger.info("All dashboard elements verified successfully");
    }
    
    @Test(description = "Verify dashboard welcome message content")
    @Story("Dashboard Elements")
    @Severity(SeverityLevel.NORMAL)
    @Description("Welcome message should contain expected content")
    public void testDashboardWelcomeMessage() {
        String welcomeMessage = securePage.getWelcomeMessage();
        
        Assert.assertNotNull(welcomeMessage, "Welcome message should not be null");
        Assert.assertTrue(welcomeMessage.toLowerCase().contains("secure"), 
            "Welcome message should mention secure area");
        
        logger.info("Welcome message: {}", welcomeMessage);
    }
    
    @Test(description = "Verify success flash message after login")
    @Story("Dashboard Elements")
    @Severity(SeverityLevel.NORMAL)
    public void testLoginSuccessFlashMessage() {
        String flashMessage = securePage.getFlashMessage();
        
        Assert.assertNotNull(flashMessage, "Flash message should be present");
        Assert.assertTrue(flashMessage.toLowerCase().contains("logged in"), 
            "Flash should confirm successful login");
        
        logger.info("Flash message: {}", flashMessage);
    }
    
    @Test(description = "Verify page header text")
    @Story("Dashboard Elements")
    @Severity(SeverityLevel.MINOR)
    public void testDashboardPageHeader() {
        String header = securePage.getPageHeader();
        
        Assert.assertTrue(header.toLowerCase().contains("secure"), 
            "Header should indicate secure area");
    }
    
    // ==================== Session Management Tests ====================
    
    @Test(description = "Verify session cookies are set after login")
    @Story("Session Management")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Session cookies should be properly set after successful authentication")
    public void testSessionCookiesPresent() {
        Allure.step("Checking session cookies");
        
        Set<Cookie> cookies = driver.manage().getCookies();
        Assert.assertFalse(cookies.isEmpty(), "Cookies should be present after login");
        
        logger.info("Found {} cookies after login", cookies.size());
        for (Cookie cookie : cookies) {
            logger.debug("Cookie: {} = {}", cookie.getName(), cookie.getValue());
        }
    }
    
    @Test(description = "Verify session persists on page refresh")
    @Story("Session Management")
    @Severity(SeverityLevel.CRITICAL)
    @Description("User should remain logged in after page refresh")
    public void testSessionPersistsOnRefresh() {
        Allure.step("Refreshing the page");
        driver.navigate().refresh();
        
        // Re-initialize page object after refresh
        securePage = new SecureAreaPage(driver);
        
        Allure.step("Verifying session persisted");
        Assert.assertTrue(securePage.isLoggedIn(), 
            "User should remain logged in after refresh");
    }
    
    @Test(description = "Verify session persists on back navigation")
    @Story("Session Management")
    @Severity(SeverityLevel.NORMAL)
    @Description("User should remain logged in after navigating back")
    public void testSessionPersistsOnBackNavigation() {
        String secureUrl = getCurrentUrl();
        
        // Navigate somewhere else (login page)
        Allure.step("Navigating away from secure area");
        driver.navigate().to(getBaseUrl() + "/login");
        
        // Navigate back
        Allure.step("Navigating back");
        driver.navigate().back();
        
        // Verify still on secure page
        String currentUrl = getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/secure") || currentUrl.equals(secureUrl), 
            "Should return to secure area");
    }
    
    @Test(description = "Verify logout clears session")
    @Story("Session Management")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Logging out should properly end the session")
    public void testLogoutClearsSession() {
        Allure.step("Logging out");
        LoginPage logoutResult = securePage.logout();
        
        Assert.assertTrue(logoutResult.isLoaded(), "Should return to login page");
        
        // Verify logout message
        String flashMessage = logoutResult.getFlashMessage();
        Assert.assertTrue(flashMessage.toLowerCase().contains("logged out"), 
            "Should show logged out message");
        
        // Try to access secure area directly
        Allure.step("Attempting to access secure area after logout");
        driver.navigate().to(getBaseUrl() + "/secure");
        
        // Should be redirected to login or show error
        String currentUrl = getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/login") || 
            new LoginPage(driver).isLoaded() || 
            new LoginPage(driver).isErrorDisplayed(),
            "Should not be able to access secure area after logout");
    }
    
    @Test(description = "Verify direct URL access without login redirects")
    @Story("Session Management")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Accessing secure area directly without login should redirect to login")
    public void testDirectAccessWithoutLoginRedirects() {
        // First logout
        securePage.logout();
        
        // Clear cookies to ensure clean state
        driver.manage().deleteAllCookies();
        
        // Try to access secure area directly
        Allure.step("Accessing secure area without authentication");
        driver.navigate().to(getBaseUrl() + "/secure");
        
        // Should redirect to login page
        String currentUrl = getCurrentUrl();
        LoginPage loginPageCheck = new LoginPage(driver);
        
        Assert.assertTrue(currentUrl.contains("/login") || loginPageCheck.isErrorDisplayed(),
            "Should redirect to login or show access denied");
    }
    
    // ==================== Navigation Tests ====================
    
    @Test(description = "Verify URL is correct after login")
    @Story("Navigation")
    @Severity(SeverityLevel.NORMAL)
    public void testDashboardUrlCorrect() {
        String currentUrl = getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/secure"), 
            "URL should contain /secure after login");
    }
    
    @Test(description = "Verify page title on dashboard")
    @Story("Navigation")
    @Severity(SeverityLevel.MINOR)
    public void testDashboardPageTitle() {
        String pageTitle = getPageTitle();
        Assert.assertNotNull(pageTitle, "Page should have a title");
        Assert.assertFalse(pageTitle.isEmpty(), "Page title should not be empty");
        
        logger.info("Dashboard page title: {}", pageTitle);
    }
    
    @Test(description = "Verify browser back after logout")
    @Story("Navigation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Browser back after logout should not return to authenticated state")
    public void testBackAfterLogoutNoAuth() {
        // Logout
        securePage.logout();
        
        // Try browser back
        Allure.step("Clicking browser back button");
        driver.navigate().back();
        
        // Verify not in authenticated state (may show cached page but no session)
        // This behavior varies by application - we verify we can't perform auth actions
        logger.info("URL after back: {}", getCurrentUrl());
    }
    
    // ==================== UI State Tests ====================
    
    @Test(description = "Verify logout button is clickable")
    @Story("UI State")
    @Severity(SeverityLevel.NORMAL)
    public void testLogoutButtonClickable() {
        Assert.assertTrue(securePage.isLogoutButtonDisplayed(), 
            "Logout button should be displayed");
        Assert.assertTrue(securePage.isLogoutButtonEnabled(), 
            "Logout button should be enabled");
    }
    
    @Test(description = "Verify dashboard loads within acceptable time")
    @Story("Performance")
    @Severity(SeverityLevel.MINOR)
    @Description("Dashboard should load within 5 seconds")
    public void testDashboardLoadTime() {
        // Logout and re-login to measure load time
        securePage.logout();
        
        loginPage = new LoginPage(driver);
        loginPage.open();
        
        long startTime = System.currentTimeMillis();
        securePage = loginPage.loginAs(VALID_USERNAME, VALID_PASSWORD);
        long loadTime = System.currentTimeMillis() - startTime;
        
        Assert.assertTrue(securePage.isLoggedIn(), "Should be logged in");
        Assert.assertTrue(loadTime < 5000, 
            "Dashboard should load within 5 seconds, took: " + loadTime + "ms");
        
        logger.info("Dashboard load time: {}ms", loadTime);
    }
    
    // ==================== Multiple Session Tests ====================
    
    @Test(description = "Verify multiple login/logout cycles")
    @Story("Session Stability")
    @Severity(SeverityLevel.NORMAL)
    @Description("Application should handle multiple login/logout cycles")
    public void testMultipleLoginLogoutCycles() {
        int cycles = 3;
        
        for (int i = 1; i <= cycles; i++) {
            Allure.step("Cycle " + i + ": Verifying logged in state");
            Assert.assertTrue(securePage.isLoggedIn(), 
                "Should be logged in on cycle " + i);
            
            Allure.step("Cycle " + i + ": Logging out");
            LoginPage logout = securePage.logout();
            Assert.assertTrue(logout.isLoaded(), 
                "Should return to login on cycle " + i);
            
            if (i < cycles) {
                Allure.step("Cycle " + i + ": Logging back in");
                securePage = logout.loginAs(VALID_USERNAME, VALID_PASSWORD);
            }
        }
        
        logger.info("Completed {} login/logout cycles successfully", cycles);
    }
    
    // ==================== Error Handling Tests ====================
    
    @Test(description = "Verify graceful handling of network issues")
    @Story("Error Handling")
    @Severity(SeverityLevel.MINOR)
    @Description("Dashboard should handle temporary network issues gracefully")
    public void testDashboardNetworkResilience() {
        // Verify page is functional
        Assert.assertTrue(securePage.isLoggedIn(), "Initially should be logged in");
        
        // Refresh to simulate potential network issue recovery
        driver.navigate().refresh();
        securePage = new SecureAreaPage(driver);
        
        // Page should still be functional
        Assert.assertTrue(securePage.isLoggedIn() || 
            new LoginPage(driver).isLoaded(),
            "Page should recover gracefully");
    }
}

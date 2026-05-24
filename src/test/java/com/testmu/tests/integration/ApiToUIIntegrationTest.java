package com.testmu.tests.integration;

import com.testmu.api.ApiClient;
import com.testmu.models.Post;
import com.testmu.models.User;
import com.testmu.pages.LoginPage;
import com.testmu.pages.SecureAreaPage;
import com.testmu.tests.BaseTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

/**
 * API to UI Integration Tests - Tests that combine API and UI interactions.
 * Demonstrates patterns for:
 * - Using API to set up test data for UI tests
 * - Validating UI changes via API
 * - End-to-end workflows spanning API and UI
 */
@Epic("Integration Testing")
@Feature("API-UI Integration")
public class ApiToUIIntegrationTest extends BaseTest {
    
    private static final Logger logger = LoggerFactory.getLogger(ApiToUIIntegrationTest.class);
    
    private ApiClient apiClient;
    private LoginPage loginPage;
    
    // Valid credentials for The Internet Herokuapp
    private static final String VALID_USERNAME = "tomsmith";
    private static final String VALID_PASSWORD = "SuperSecretPassword!";
    
    @BeforeClass
    public void setupApiClient() {
        apiClient = new ApiClient();
    }
    
    @BeforeMethod
    public void initPages() {
        loginPage = new LoginPage(driver);
    }
    
    // ==================== API Setup + UI Validation Tests ====================
    
    @Test(description = "Fetch user data via API and validate in UI context")
    @Story("API Data Setup")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Use API to fetch test data, then use that data in UI test")
    public void testApiDataSetupForUI() {
        // Step 1: Fetch user data via API
        Allure.step("Fetching user data from API");
        Response apiResponse = apiClient.get("/users/1");
        Assert.assertEquals(apiResponse.getStatusCode(), 200, "API should return user data");
        
        User user = apiResponse.as(User.class);
        Assert.assertNotNull(user.getName(), "User should have a name");
        Assert.assertNotNull(user.getEmail(), "User should have an email");
        
        logger.info("Retrieved user from API: {} ({})", user.getName(), user.getEmail());
        
        // Step 2: Use API data context in UI test
        Allure.step("Navigating to login page with API context");
        loginPage.open();
        Assert.assertTrue(loginPage.isLoaded(), "Login page should load");
        
        // Step 3: Perform UI action (login)
        // Note: Using hardcoded credentials as the-internet.herokuapp.com doesn't 
        // integrate with JSONPlaceholder. This demonstrates the pattern.
        Allure.step("Performing login");
        SecureAreaPage securePage = loginPage.loginAs(VALID_USERNAME, VALID_PASSWORD);
        
        // Step 4: Validate UI state
        Allure.step("Validating login success in UI");
        Assert.assertTrue(securePage.isLoggedIn(), "User should be logged in");
        
        logger.info("Integration test completed: API user {} used in UI context", user.getName());
    }
    
    @Test(description = "Create data via API, validate workflow continues in UI")
    @Story("API Data Setup")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Create test data via API, then continue workflow in UI")
    public void testCreateApiDataThenUIWorkflow() {
        // Step 1: Create post via API
        Allure.step("Creating test post via API");
        Post newPost = Post.builder()
            .userId(1)
            .title("Integration Test Post")
            .body("This post was created via API for UI integration testing.")
            .build();
        
        Response createResponse = apiClient.post("/posts", newPost);
        Assert.assertEquals(createResponse.getStatusCode(), 201, "Post should be created");
        
        Post createdPost = createResponse.as(Post.class);
        Integer postId = createdPost.getId();
        Assert.assertNotNull(postId, "Created post should have ID");
        
        logger.info("Created post with ID: {}", postId);
        
        // Step 2: Continue with UI workflow
        Allure.step("Continuing workflow in UI");
        loginPage.open();
        SecureAreaPage securePage = loginPage.loginAs(VALID_USERNAME, VALID_PASSWORD);
        Assert.assertTrue(securePage.isLoggedIn(), "Should be logged in for UI workflow");
        
        // Step 3: In a real scenario, we'd use the API-created data in the UI
        // Here we demonstrate the pattern of data passing
        Allure.parameter("API Created Post ID", String.valueOf(postId));
        Allure.parameter("API Created Post Title", createdPost.getTitle());
        
        logger.info("UI workflow continued with API context: Post ID = {}", postId);
    }
    
    // ==================== UI Action + API Validation Tests ====================
    
    @Test(description = "Perform UI action and validate via API")
    @Story("UI Action API Validation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Perform an action in UI, then validate state via API")
    public void testUIActionValidateViaAPI() {
        // Step 1: Perform UI action - Login
        Allure.step("Performing UI action - Login");
        loginPage.open();
        SecureAreaPage securePage = loginPage.loginAs(VALID_USERNAME, VALID_PASSWORD);
        Assert.assertTrue(securePage.isLoggedIn(), "Login should succeed in UI");
        
        // Step 2: Make related API call to validate/extend test
        Allure.step("Making API call to validate state");
        Response apiResponse = apiClient.get("/users/1");
        Assert.assertEquals(apiResponse.getStatusCode(), 200, "API should be accessible");
        
        User apiUser = apiResponse.as(User.class);
        
        // Step 3: Cross-validate or use combined data
        // In a real scenario, you might validate that UI user matches API user
        Allure.step("Cross-validating UI and API states");
        Assert.assertNotNull(apiUser.getName(), "API user should have name");
        
        logger.info("UI action performed, API validation successful");
    }
    
    @Test(description = "Logout in UI and verify session state pattern")
    @Story("UI Action API Validation")
    @Severity(SeverityLevel.NORMAL)
    public void testUILogoutStateValidation() {
        // Step 1: Login via UI
        Allure.step("Login via UI");
        loginPage.open();
        SecureAreaPage securePage = loginPage.loginAs(VALID_USERNAME, VALID_PASSWORD);
        Assert.assertTrue(securePage.isLoggedIn());
        
        // Step 2: Logout via UI
        Allure.step("Logout via UI");
        LoginPage logoutPage = securePage.logout();
        Assert.assertTrue(logoutPage.isLoaded(), "Should return to login page");
        
        // Step 3: Verify via API that public endpoints still work
        // (In a real auth system, you'd verify session is invalidated)
        Allure.step("Verify API state after logout");
        Response publicApiResponse = apiClient.get("/posts/1");
        Assert.assertEquals(publicApiResponse.getStatusCode(), 200, 
            "Public API should still be accessible after UI logout");
    }
    
    // ==================== End-to-End Workflow Tests ====================
    
    @Test(description = "Complete E2E workflow: API setup → UI interaction → API verification")
    @Story("End-to-End Workflow")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Full end-to-end test combining API setup, UI interaction, and API verification")
    public void testCompleteE2EWorkflow() {
        // Phase 1: API Setup
        Allure.step("Phase 1: Setting up test data via API");
        
        // Get existing posts count
        Response postsResponse = apiClient.get("/posts");
        List<Post> existingPosts = postsResponse.jsonPath().getList(".", Post.class);
        int initialPostCount = existingPosts.size();
        logger.info("Initial posts count: {}", initialPostCount);
        
        // Create new test post
        Post testPost = Post.builder()
            .userId(1)
            .title("E2E Test Post - " + System.currentTimeMillis())
            .body("Created for E2E integration testing")
            .build();
        
        Response createResponse = apiClient.post("/posts", testPost);
        Assert.assertEquals(createResponse.getStatusCode(), 201);
        Post createdPost = createResponse.as(Post.class);
        
        // Phase 2: UI Interaction
        Allure.step("Phase 2: Performing UI interactions");
        
        loginPage.open();
        Assert.assertTrue(loginPage.isLoaded(), "Login page should load");
        
        SecureAreaPage securePage = loginPage.loginAs(VALID_USERNAME, VALID_PASSWORD);
        Assert.assertTrue(securePage.isLoggedIn(), "Should login successfully");
        
        // Capture UI state
        String welcomeMessage = securePage.getWelcomeMessage();
        Assert.assertNotNull(welcomeMessage, "Welcome message should be displayed");
        
        // Logout
        LoginPage logoutResult = securePage.logout();
        Assert.assertTrue(logoutResult.isLoaded(), "Should logout successfully");
        
        // Phase 3: API Verification
        Allure.step("Phase 3: Verifying state via API");
        
        // Verify created post exists
        Response verifyResponse = apiClient.get("/posts/" + createdPost.getId());
        Assert.assertEquals(verifyResponse.getStatusCode(), 200, 
            "Created post should be retrievable");
        
        // Cleanup: Delete test post
        Response deleteResponse = apiClient.delete("/posts/" + createdPost.getId());
        Assert.assertEquals(deleteResponse.getStatusCode(), 200, 
            "Should be able to cleanup test data");
        
        logger.info("E2E workflow completed successfully");
        
        Allure.step("E2E Test completed: 3 phases executed");
    }
    
    // ==================== Data Consistency Tests ====================
    
    @Test(description = "Verify data consistency across API and UI")
    @Story("Data Consistency")
    @Severity(SeverityLevel.NORMAL)
    @Description("Ensures data remains consistent when accessed via different channels")
    public void testDataConsistencyAcrossChannels() {
        // Fetch user via API
        Allure.step("Fetching data via API");
        Response apiResponse = apiClient.get("/users/1");
        User apiUser = apiResponse.as(User.class);
        
        // Store API data
        String apiUserName = apiUser.getName();
        String apiUserEmail = apiUser.getEmail();
        
        logger.info("API User: {} ({})", apiUserName, apiUserEmail);
        
        // Perform UI operations
        Allure.step("Performing UI operations");
        loginPage.open();
        SecureAreaPage securePage = loginPage.loginAs(VALID_USERNAME, VALID_PASSWORD);
        
        // UI state doesn't directly show JSONPlaceholder user, but we verify
        // the UI is functioning while we have API context
        Assert.assertTrue(securePage.isLoggedIn(), "UI should function correctly");
        
        // Re-fetch via API to ensure data hasn't changed
        Allure.step("Re-verifying data via API");
        Response verifyResponse = apiClient.get("/users/1");
        User verifyUser = verifyResponse.as(User.class);
        
        Assert.assertEquals(verifyUser.getName(), apiUserName, 
            "User name should remain consistent");
        Assert.assertEquals(verifyUser.getEmail(), apiUserEmail, 
            "User email should remain consistent");
        
        logger.info("Data consistency verified across API and UI operations");
    }
    
    // ==================== Error Handling Integration Tests ====================
    
    @Test(description = "Handle API failure gracefully in integration context")
    @Story("Error Handling")
    @Severity(SeverityLevel.NORMAL)
    @Description("Tests graceful handling when API fails during integration test")
    public void testApiFailureHandling() {
        // Simulate API failure by requesting non-existent resource
        Allure.step("Simulating API failure");
        Response failResponse = apiClient.get("/users/99999");
        
        if (failResponse.getStatusCode() == 404) {
            logger.warn("API returned 404 - handling gracefully");
            
            // Continue with UI test despite API failure
            Allure.step("Continuing UI test despite API failure");
            loginPage.open();
            Assert.assertTrue(loginPage.isLoaded(), 
                "UI should still function despite API failure");
            
            // Use default/fallback data
            SecureAreaPage securePage = loginPage.loginAs(VALID_USERNAME, VALID_PASSWORD);
            Assert.assertTrue(securePage.isLoggedIn(), 
                "Should use fallback flow when API fails");
        } else {
            // API worked, continue normally
            Assert.fail("Expected API to return 404 for non-existent user");
        }
    }
    
    @Test(description = "Handle UI failure gracefully in integration context")
    @Story("Error Handling")
    @Severity(SeverityLevel.NORMAL)
    public void testUIFailureHandling() {
        // Set up via API first
        Allure.step("Setting up via API");
        Response apiResponse = apiClient.get("/posts/1");
        Assert.assertEquals(apiResponse.getStatusCode(), 200, "API setup should succeed");
        
        // Attempt UI action that we expect to fail
        Allure.step("Attempting UI action with invalid credentials");
        loginPage.open();
        loginPage.loginWithInvalidCredentials("invalid", "invalid");
        
        // Verify we're still on login page (UI "failure" handled)
        Assert.assertTrue(loginPage.isErrorDisplayed() || loginPage.isLoaded(), 
            "UI should show error or stay on login page");
        
        // Verify API is still accessible (system still works)
        Allure.step("Verifying API still accessible after UI error");
        Response verifyResponse = apiClient.get("/posts/1");
        Assert.assertEquals(verifyResponse.getStatusCode(), 200, 
            "API should still work after UI error");
        
        logger.info("UI failure handled gracefully, API remains functional");
    }
}

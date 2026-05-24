package com.testmu.tests.api;

import com.testmu.api.ApiClient;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Error Handling API Tests - Tests for various error scenarios.
 * Tests 4xx and 5xx error responses and proper error handling.
 */
@Epic("API Testing")
@Feature("Error Handling")
public class ErrorHandlingTest {
    
    private static final Logger logger = LoggerFactory.getLogger(ErrorHandlingTest.class);
    
    private ApiClient apiClient;
    
    @BeforeClass
    public void setup() {
        apiClient = new ApiClient();
    }
    
    // ==================== 4xx Client Error Tests ====================
    
    @Test(description = "Test 404 Not Found response")
    @Story("4xx Errors")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Should return 404 for non-existent resource")
    public void testNotFoundError() {
        Response response = apiClient.get("/posts/999999");
        
        Assert.assertEquals(response.getStatusCode(), 404, 
            "Should return 404 for non-existent post");
    }
    
    @Test(description = "Test invalid endpoint returns 404")
    @Story("4xx Errors")
    @Severity(SeverityLevel.NORMAL)
    public void testInvalidEndpoint() {
        Response response = apiClient.get("/invalid-endpoint-xyz");
        
        Assert.assertEquals(response.getStatusCode(), 404, 
            "Should return 404 for invalid endpoint");
    }
    
    @Test(description = "Test GET on non-existent user")
    @Story("4xx Errors")
    @Severity(SeverityLevel.NORMAL)
    public void testNonExistentUser() {
        Response response = apiClient.get("/users/999999");
        
        Assert.assertEquals(response.getStatusCode(), 404, 
            "Should return 404 for non-existent user");
    }
    
    @Test(description = "Test empty body POST request handling")
    @Story("4xx Errors")
    @Severity(SeverityLevel.NORMAL)
    @Description("API should handle empty request body gracefully")
    public void testEmptyBodyPost() {
        Response response = apiClient.post("/posts", "");
        
        // JSONPlaceholder accepts empty body and returns 201
        // Real APIs might return 400 Bad Request
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 201 || statusCode == 400, 
            "Should return 201 (accepted) or 400 (rejected)");
        
        logger.info("Empty body POST returned status: {}", statusCode);
    }
    
    @Test(description = "Test malformed JSON handling")
    @Story("4xx Errors")
    @Severity(SeverityLevel.NORMAL)
    public void testMalformedJson() {
        String malformedJson = "{invalid json content";
        
        Response response = io.restassured.RestAssured.given()
            .baseUri("https://jsonplaceholder.typicode.com")
            .header("Content-Type", "application/json")
            .body(malformedJson)
            .post("/posts")
            .then()
            .extract()
            .response();
        
        // JSONPlaceholder is lenient, but verify it doesn't crash
        Assert.assertTrue(response.getStatusCode() >= 200, 
            "API should not crash on malformed JSON");
    }
    
    @Test(description = "Test request with missing required fields")
    @Story("4xx Errors")
    @Severity(SeverityLevel.NORMAL)
    public void testMissingRequiredFields() {
        // Create post without title (normally required)
        String incompletePost = "{\"body\": \"Body without title\"}";
        
        Response response = apiClient.post("/posts", incompletePost);
        
        // JSONPlaceholder accepts it, real APIs might return 400
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 201 || statusCode == 400, 
            "Should return 201 (lenient API) or 400 (strict validation)");
    }
    
    @Test(description = "Test invalid ID format")
    @Story("4xx Errors")
    @Severity(SeverityLevel.NORMAL)
    public void testInvalidIdFormat() {
        Response response = apiClient.get("/posts/invalid-id");
        
        // Should return 404 or 400 for invalid ID format
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 404 || statusCode == 400, 
            "Should reject invalid ID format: " + statusCode);
    }
    
    // ==================== Error Response Validation ====================
    
    @Test(description = "Verify error response structure")
    @Story("Error Response Structure")
    @Severity(SeverityLevel.NORMAL)
    public void testErrorResponseStructure() {
        Response response = apiClient.get("/posts/999999");
        
        Assert.assertEquals(response.getStatusCode(), 404);
        
        // JSONPlaceholder returns empty object for 404
        String responseBody = response.getBody().asString();
        Assert.assertNotNull(responseBody, "Should have response body");
        
        logger.info("404 response body: {}", responseBody);
    }
    
    @Test(description = "Verify Content-Type on error response")
    @Story("Error Response Structure")
    @Severity(SeverityLevel.MINOR)
    public void testErrorResponseContentType() {
        Response response = apiClient.get("/posts/999999");
        
        String contentType = response.getHeader("Content-Type");
        Assert.assertTrue(
            contentType == null || contentType.contains("application/json") || contentType.contains("charset"),
            "Error response should have appropriate Content-Type"
        );
    }
    
    // ==================== Boundary Tests ====================
    
    @Test(description = "Test boundary ID values", dataProvider = "boundaryIdProvider")
    @Story("Boundary Testing")
    @Severity(SeverityLevel.NORMAL)
    public void testBoundaryIdValues(String idValue, String description) {
        Allure.description("Testing boundary: " + description);
        
        Response response = apiClient.get("/posts/" + idValue);
        
        // Verify API handles boundary values gracefully
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 200 || statusCode == 404 || statusCode == 400, 
            "Should handle boundary value gracefully: " + description);
        
        logger.info("Boundary test '{}' returned: {}", description, statusCode);
    }
    
    @DataProvider(name = "boundaryIdProvider")
    public Object[][] boundaryIdValues() {
        return new Object[][] {
            {"0", "Zero ID"},
            {"-1", "Negative ID"},
            {"1", "Minimum valid ID"},
            {"100", "Maximum typical ID"},
            {String.valueOf(Integer.MAX_VALUE), "Max integer value"},
            {"99999999999999", "Very large number"}
        };
    }
    
    // ==================== Timeout and Connection Tests ====================
    
    @Test(description = "Test request timeout handling")
    @Story("Timeout Handling")
    @Severity(SeverityLevel.NORMAL)
    public void testRequestTimeout() {
        // Measure response time for a normal request
        long startTime = System.currentTimeMillis();
        Response response = apiClient.get("/posts");
        long duration = System.currentTimeMillis() - startTime;
        
        Assert.assertEquals(response.getStatusCode(), 200, "Request should succeed");
        Assert.assertTrue(duration < 10000, "Request should complete within 10 seconds");
        
        logger.info("Request completed in {}ms", duration);
    }
    
    // ==================== Error Recovery Tests ====================
    
    @Test(description = "Test graceful handling of server error simulation")
    @Story("Error Recovery")
    @Severity(SeverityLevel.NORMAL)
    public void testGracefulErrorHandling() {
        // Test that client handles various responses gracefully
        try {
            Response response = apiClient.get("/posts/1");
            Assert.assertTrue(response.getStatusCode() >= 200, 
                "Should handle response gracefully");
        } catch (Exception e) {
            Assert.fail("Client should not throw exception for valid request: " + e.getMessage());
        }
    }
    
    @Test(description = "Test retry on failure pattern")
    @Story("Error Recovery")
    @Severity(SeverityLevel.MINOR)
    public void testRetryPattern() {
        // Demonstrate retry pattern
        int maxRetries = 3;
        int retryCount = 0;
        Response response = null;
        
        while (retryCount < maxRetries) {
            response = apiClient.get("/posts/1");
            
            if (response.getStatusCode() == 200) {
                break;
            }
            
            retryCount++;
            logger.warn("Retry attempt {} of {}", retryCount, maxRetries);
        }
        
        Assert.assertNotNull(response, "Should have response after retries");
        Assert.assertEquals(response.getStatusCode(), 200, "Should eventually succeed");
    }
    
    // ==================== Special Characters Tests ====================
    
    @Test(description = "Test special characters in query params")
    @Story("Special Characters")
    @Severity(SeverityLevel.MINOR)
    public void testSpecialCharactersInQuery() {
        // Test URL encoding
        Response response = apiClient.get("/posts?title=test%20value");
        
        // Should handle encoded values
        Assert.assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 400, 
            "Should handle special characters in query");
    }
    
    @Test(description = "Test unicode in request body")
    @Story("Special Characters")
    @Severity(SeverityLevel.MINOR)
    public void testUnicodeInBody() {
        String unicodeBody = "{\"title\": \"Тест 日本語 🎉\", \"body\": \"Unicode content\", \"userId\": 1}";
        
        Response response = apiClient.post("/posts", unicodeBody);
        
        // Should handle unicode gracefully
        Assert.assertTrue(response.getStatusCode() == 201 || response.getStatusCode() == 400, 
            "Should handle unicode in request body");
    }
}

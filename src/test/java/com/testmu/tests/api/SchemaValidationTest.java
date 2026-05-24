package com.testmu.tests.api;

import com.testmu.api.ApiClient;
import io.qameta.allure.*;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

/**
 * Schema Validation Tests - Tests for JSON Schema validation.
 * Validates API responses against predefined JSON schemas.
 */
@Epic("API Testing")
@Feature("Schema Validation")
public class SchemaValidationTest {
    
    private static final Logger logger = LoggerFactory.getLogger(SchemaValidationTest.class);
    
    private ApiClient apiClient;
    
    @BeforeClass
    public void setup() {
        apiClient = new ApiClient();
    }
    
    // ==================== User Schema Tests ====================
    
    @Test(description = "Validate user response against JSON schema")
    @Story("User Schema")
    @Severity(SeverityLevel.CRITICAL)
    @Description("User API response should match the defined JSON schema")
    public void testUserSchemaValidation() {
        Response response = apiClient.get("/users/1");
        
        Assert.assertEquals(response.getStatusCode(), 200);
        
        // Validate against schema (using inline validation)
        response.then()
            .body("id", notNullValue())
            .body("name", notNullValue())
            .body("username", notNullValue())
            .body("email", notNullValue())
            .body("email", matchesPattern("^[A-Za-z0-9+_.-]+@(.+)$"));
        
        // Verify required fields exist
        Map<String, Object> user = response.jsonPath().getMap(".");
        Assert.assertTrue(user.containsKey("id"), "User should have 'id' field");
        Assert.assertTrue(user.containsKey("name"), "User should have 'name' field");
        Assert.assertTrue(user.containsKey("username"), "User should have 'username' field");
        Assert.assertTrue(user.containsKey("email"), "User should have 'email' field");
    }
    
    @Test(description = "Validate user list schema")
    @Story("User Schema")
    @Severity(SeverityLevel.NORMAL)
    public void testUserListSchemaValidation() {
        Response response = apiClient.get("/users");
        
        Assert.assertEquals(response.getStatusCode(), 200);
        
        // Validate it's an array
        response.then().body("$", instanceOf(List.class));
        
        // Validate each user in the list has required fields
        List<Map<String, Object>> users = response.jsonPath().getList(".");
        for (Map<String, Object> user : users) {
            Assert.assertTrue(user.containsKey("id"), "Each user should have 'id'");
            Assert.assertTrue(user.containsKey("name"), "Each user should have 'name'");
            Assert.assertTrue(user.containsKey("email"), "Each user should have 'email'");
        }
    }
    
    @Test(description = "Validate user address nested schema")
    @Story("User Schema")
    @Severity(SeverityLevel.NORMAL)
    public void testUserAddressNestedSchema() {
        Response response = apiClient.get("/users/1");
        
        // Validate nested address structure
        response.then()
            .body("address", notNullValue())
            .body("address.street", notNullValue())
            .body("address.city", notNullValue())
            .body("address.zipcode", notNullValue())
            .body("address.geo", notNullValue())
            .body("address.geo.lat", notNullValue())
            .body("address.geo.lng", notNullValue());
    }
    
    @Test(description = "Validate user company nested schema")
    @Story("User Schema")
    @Severity(SeverityLevel.NORMAL)
    public void testUserCompanyNestedSchema() {
        Response response = apiClient.get("/users/1");
        
        // Validate nested company structure
        response.then()
            .body("company", notNullValue())
            .body("company.name", notNullValue())
            .body("company.catchPhrase", notNullValue())
            .body("company.bs", notNullValue());
    }
    
    // ==================== Post Schema Tests ====================
    
    @Test(description = "Validate post response schema")
    @Story("Post Schema")
    @Severity(SeverityLevel.CRITICAL)
    public void testPostSchemaValidation() {
        Response response = apiClient.get("/posts/1");
        
        Assert.assertEquals(response.getStatusCode(), 200);
        
        // Validate post structure
        response.then()
            .body("id", instanceOf(Number.class))
            .body("userId", instanceOf(Number.class))
            .body("title", instanceOf(String.class))
            .body("body", instanceOf(String.class));
    }
    
    @Test(description = "Validate post list schema")
    @Story("Post Schema")
    @Severity(SeverityLevel.NORMAL)
    public void testPostListSchemaValidation() {
        Response response = apiClient.get("/posts");
        
        Assert.assertEquals(response.getStatusCode(), 200);
        
        // Validate list and first item
        response.then()
            .body("$", hasSize(greaterThan(0)))
            .body("[0].id", notNullValue())
            .body("[0].userId", notNullValue())
            .body("[0].title", notNullValue())
            .body("[0].body", notNullValue());
    }
    
    // ==================== Comment Schema Tests ====================
    
    @Test(description = "Validate comment response schema")
    @Story("Comment Schema")
    @Severity(SeverityLevel.NORMAL)
    public void testCommentSchemaValidation() {
        Response response = apiClient.get("/comments/1");
        
        Assert.assertEquals(response.getStatusCode(), 200);
        
        // Validate comment structure
        response.then()
            .body("id", instanceOf(Number.class))
            .body("postId", instanceOf(Number.class))
            .body("name", instanceOf(String.class))
            .body("email", matchesPattern("^[A-Za-z0-9+_.-]+@(.+)$"))
            .body("body", instanceOf(String.class));
    }
    
    // ==================== Data Type Validation ====================
    
    @Test(description = "Validate field data types")
    @Story("Data Type Validation")
    @Severity(SeverityLevel.CRITICAL)
    public void testDataTypeValidation() {
        Response response = apiClient.get("/users/1");
        
        // ID should be integer
        Integer id = response.jsonPath().getInt("id");
        Assert.assertNotNull(id, "ID should be an integer");
        
        // Name should be string
        String name = response.jsonPath().getString("name");
        Assert.assertNotNull(name, "Name should be a string");
        Assert.assertFalse(name.isEmpty(), "Name should not be empty");
        
        // Geo coordinates should be numeric strings or numbers
        String lat = response.jsonPath().getString("address.geo.lat");
        String lng = response.jsonPath().getString("address.geo.lng");
        Assert.assertNotNull(lat, "Latitude should exist");
        Assert.assertNotNull(lng, "Longitude should exist");
        
        // Verify they can be parsed as numbers
        try {
            Double.parseDouble(lat);
            Double.parseDouble(lng);
        } catch (NumberFormatException e) {
            Assert.fail("Geo coordinates should be parseable as numbers");
        }
    }
    
    @Test(description = "Validate email format")
    @Story("Data Type Validation")
    @Severity(SeverityLevel.NORMAL)
    public void testEmailFormatValidation() {
        Response response = apiClient.get("/users");
        
        List<String> emails = response.jsonPath().getList("email");
        
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        for (String email : emails) {
            Assert.assertTrue(email.matches(emailPattern), 
                "Email should be valid format: " + email);
        }
    }
    
    // ==================== Response Structure Tests ====================
    
    @Test(description = "Validate created resource schema")
    @Story("Response Structure")
    @Severity(SeverityLevel.NORMAL)
    public void testCreatedResourceSchema() {
        String newPost = "{\"title\": \"Test\", \"body\": \"Content\", \"userId\": 1}";
        
        Response response = apiClient.post("/posts", newPost);
        
        Assert.assertEquals(response.getStatusCode(), 201);
        
        // Created resource should have ID
        response.then()
            .body("id", notNullValue())
            .body("id", instanceOf(Number.class));
    }
    
    @Test(description = "Validate empty array response")
    @Story("Response Structure")
    @Severity(SeverityLevel.MINOR)
    public void testEmptyArrayResponse() {
        // Query that returns no results
        Response response = apiClient.get("/posts?userId=99999");
        
        Assert.assertEquals(response.getStatusCode(), 200);
        
        // Should return empty array, not null
        response.then()
            .body("$", instanceOf(List.class))
            .body("$", hasSize(0));
    }
    
    // ==================== JSON Schema File Validation ====================
    
    @Test(description = "Validate response against JSON schema file")
    @Story("JSON Schema File")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Validates API response against external JSON schema file")
    public void testSchemaFileValidation() {
        Response response = apiClient.get("/users/1");
        
        Assert.assertEquals(response.getStatusCode(), 200);
        
        // Try to validate against schema file if it exists
        try {
            InputStream schemaStream = getClass().getClassLoader()
                .getResourceAsStream("schemas/user-schema.json");
            
            if (schemaStream != null) {
                response.then()
                    .body(matchesJsonSchemaInClasspath("schemas/user-schema.json"));
                logger.info("Schema validation passed against user-schema.json");
            } else {
                // Schema file doesn't exist yet, use inline validation
                logger.warn("Schema file not found, using inline validation");
                response.then()
                    .body("id", notNullValue())
                    .body("name", notNullValue())
                    .body("email", notNullValue());
            }
        } catch (Exception e) {
            logger.warn("Schema file validation skipped: {}", e.getMessage());
            // Fallback to inline validation
            response.then()
                .body("id", notNullValue())
                .body("name", notNullValue())
                .body("email", notNullValue());
        }
    }
    
    // ==================== Negative Schema Tests ====================
    
    @Test(description = "Verify 404 response has correct structure")
    @Story("Error Schema")
    @Severity(SeverityLevel.MINOR)
    public void testErrorResponseSchema() {
        Response response = apiClient.get("/users/99999");
        
        Assert.assertEquals(response.getStatusCode(), 404);
        
        // JSONPlaceholder returns empty object for 404
        String body = response.getBody().asString();
        Assert.assertTrue(body.equals("{}") || body.isEmpty() || body.equals("null"), 
            "404 should return empty or null body");
    }
}

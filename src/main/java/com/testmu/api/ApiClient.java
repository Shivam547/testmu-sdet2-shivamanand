package com.testmu.api;

import com.testmu.config.ConfigReader;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * API Client - REST API client for API testing.
 * Built on REST Assured with Allure integration for reporting.
 */
public class ApiClient {
    private static final Logger logger = LogManager.getLogger(ApiClient.class);
    
    private final RequestSpecification requestSpec;
    private ResponseSpecification responseSpec;
    private String authToken;
    private String baseUrl;
    
    /**
     * Constructor with default base URL from configuration
     */
    public ApiClient() {
        this(ConfigReader.getApiBaseUrl());
    }
    
    /**
     * Constructor with custom base URL
     * @param baseUrl API base URL
     */
    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
        
        this.requestSpec = new RequestSpecBuilder()
            .setBaseUri(baseUrl)
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .addFilter(new AllureRestAssured())  // Allure logging
            .log(LogDetail.ALL)
            .build();
        
        this.responseSpec = new ResponseSpecBuilder()
            .log(LogDetail.ALL)
            .build();
        
        logger.info("API Client initialized with base URL: {}", baseUrl);
    }
    
    // ==================== Configuration Methods ====================
    
    /**
     * Set authentication token for subsequent requests
     * @param token Bearer token
     * @return ApiClient for chaining
     */
    public ApiClient withAuth(String token) {
        this.authToken = token;
        logger.debug("Auth token set");
        return this;
    }
    
    /**
     * Clear authentication token
     * @return ApiClient for chaining
     */
    public ApiClient clearAuth() {
        this.authToken = null;
        return this;
    }
    
    /**
     * Set expected status code for response validation
     * @param statusCode Expected HTTP status code
     * @return ApiClient for chaining
     */
    public ApiClient expectStatusCode(int statusCode) {
        this.responseSpec = new ResponseSpecBuilder()
            .expectStatusCode(statusCode)
            .log(LogDetail.ALL)
            .build();
        return this;
    }
    
    /**
     * Reset response specification to default
     * @return ApiClient for chaining
     */
    public ApiClient resetResponseSpec() {
        this.responseSpec = new ResponseSpecBuilder()
            .log(LogDetail.ALL)
            .build();
        return this;
    }
    
    // ==================== Request Specification ====================
    
    /**
     * Get request specification with auth if set
     * @return RequestSpecification
     */
    private RequestSpecification getRequestSpec() {
        RequestSpecification spec = RestAssured.given().spec(requestSpec);
        
        if (authToken != null && !authToken.isEmpty()) {
            spec.header("Authorization", "Bearer " + authToken);
        }
        
        return spec;
    }
    
    // ==================== HTTP Methods ====================
    
    /**
     * GET request
     * @param endpoint API endpoint (relative to base URL)
     * @return Response
     */
    public Response get(String endpoint) {
        logger.info("GET request to: {}", endpoint);
        
        return getRequestSpec()
            .when()
            .get(endpoint)
            .then()
            .spec(responseSpec)
            .extract()
            .response();
    }
    
    /**
     * GET request with query parameters
     * @param endpoint API endpoint
     * @param queryParams Query parameters
     * @return Response
     */
    public Response get(String endpoint, Map<String, ?> queryParams) {
        logger.info("GET request to: {} with params: {}", endpoint, queryParams);
        
        return getRequestSpec()
            .queryParams(queryParams)
            .when()
            .get(endpoint)
            .then()
            .spec(responseSpec)
            .extract()
            .response();
    }
    
    /**
     * GET request with path parameters
     * @param endpoint API endpoint with placeholders
     * @param pathParams Path parameters
     * @return Response
     */
    public Response getWithPathParams(String endpoint, Map<String, ?> pathParams) {
        logger.info("GET request to: {} with path params: {}", endpoint, pathParams);
        
        return getRequestSpec()
            .pathParams(pathParams)
            .when()
            .get(endpoint)
            .then()
            .spec(responseSpec)
            .extract()
            .response();
    }
    
    /**
     * POST request with body
     * @param endpoint API endpoint
     * @param body Request body (POJO or Map)
     * @return Response
     */
    public Response post(String endpoint, Object body) {
        logger.info("POST request to: {}", endpoint);
        
        return getRequestSpec()
            .body(body)
            .when()
            .post(endpoint)
            .then()
            .spec(responseSpec)
            .extract()
            .response();
    }
    
    /**
     * POST request without body
     * @param endpoint API endpoint
     * @return Response
     */
    public Response post(String endpoint) {
        logger.info("POST request to: {} (no body)", endpoint);
        
        return getRequestSpec()
            .when()
            .post(endpoint)
            .then()
            .spec(responseSpec)
            .extract()
            .response();
    }
    
    /**
     * PUT request with body
     * @param endpoint API endpoint
     * @param body Request body
     * @return Response
     */
    public Response put(String endpoint, Object body) {
        logger.info("PUT request to: {}", endpoint);
        
        return getRequestSpec()
            .body(body)
            .when()
            .put(endpoint)
            .then()
            .spec(responseSpec)
            .extract()
            .response();
    }
    
    /**
     * PATCH request with body
     * @param endpoint API endpoint
     * @param body Request body (partial update)
     * @return Response
     */
    public Response patch(String endpoint, Object body) {
        logger.info("PATCH request to: {}", endpoint);
        
        return getRequestSpec()
            .body(body)
            .when()
            .patch(endpoint)
            .then()
            .spec(responseSpec)
            .extract()
            .response();
    }
    
    /**
     * DELETE request
     * @param endpoint API endpoint
     * @return Response
     */
    public Response delete(String endpoint) {
        logger.info("DELETE request to: {}", endpoint);
        
        return getRequestSpec()
            .when()
            .delete(endpoint)
            .then()
            .spec(responseSpec)
            .extract()
            .response();
    }
    
    /**
     * DELETE request with body
     * @param endpoint API endpoint
     * @param body Request body
     * @return Response
     */
    public Response delete(String endpoint, Object body) {
        logger.info("DELETE request to: {} with body", endpoint);
        
        return getRequestSpec()
            .body(body)
            .when()
            .delete(endpoint)
            .then()
            .spec(responseSpec)
            .extract()
            .response();
    }
    
    // ==================== Form Data ====================
    
    /**
     * POST request with form data
     * @param endpoint API endpoint
     * @param formParams Form parameters
     * @return Response
     */
    public Response postForm(String endpoint, Map<String, String> formParams) {
        logger.info("POST form request to: {}", endpoint);
        
        return getRequestSpec()
            .contentType(ContentType.URLENC)
            .formParams(formParams)
            .when()
            .post(endpoint)
            .then()
            .spec(responseSpec)
            .extract()
            .response();
    }
    
    // ==================== Utility Methods ====================
    
    /**
     * Get response time from response
     * @param response Response object
     * @return Response time in milliseconds
     */
    public long getResponseTime(Response response) {
        return response.getTime();
    }
    
    /**
     * Check if response was successful (2xx)
     * @param response Response object
     * @return true if status code is 2xx
     */
    public boolean isSuccessful(Response response) {
        int statusCode = response.getStatusCode();
        return statusCode >= 200 && statusCode < 300;
    }
    
    /**
     * Get base URL
     * @return Base URL
     */
    public String getBaseUrl() {
        return baseUrl;
    }
    
    /**
     * Extract value from response JSON path
     * @param response Response object
     * @param jsonPath JSON path expression
     * @param <T> Return type
     * @return Extracted value
     */
    public <T> T extractFromResponse(Response response, String jsonPath) {
        return response.jsonPath().get(jsonPath);
    }
}

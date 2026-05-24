package com.testmu.tests.api;

import com.testmu.api.ApiClient;
import com.testmu.models.Comment;
import com.testmu.models.Post;
import com.testmu.models.User;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * CRUD API Tests - Tests for Create, Read, Update, Delete operations.
 * Uses JSONPlaceholder API (https://jsonplaceholder.typicode.com)
 */
@Epic("API Testing")
@Feature("CRUD Operations")
public class CrudApiTest {
    
    private static final Logger logger = LoggerFactory.getLogger(CrudApiTest.class);
    
    private ApiClient apiClient;
    
    // API endpoints
    private static final String POSTS_ENDPOINT = "/posts";
    private static final String USERS_ENDPOINT = "/users";
    private static final String COMMENTS_ENDPOINT = "/comments";
    
    @BeforeClass
    public void setupApiClient() {
        apiClient = new ApiClient();
    }
    
    @BeforeMethod
    public void resetClient() {
        // Reset any auth or state between tests
        apiClient = new ApiClient();
    }
    
    // ==================== CREATE Tests ====================
    
    @Test(description = "Create a new post via POST request")
    @Story("Create Operations")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Should create a new post and return 201 status")
    public void testCreatePost() {
        Post newPost = Post.builder()
            .userId(1)
            .title("Test Post Title")
            .body("This is the body of the test post created via API automation.")
            .build();
        
        Response response = apiClient.post(POSTS_ENDPOINT, newPost);
        
        // Verify status code
        Assert.assertEquals(response.getStatusCode(), 201, "Should return 201 Created");
        
        // Verify response contains the created post
        Post createdPost = response.as(Post.class);
        Assert.assertNotNull(createdPost.getId(), "Created post should have an ID");
        Assert.assertEquals(createdPost.getTitle(), newPost.getTitle(), "Title should match");
        Assert.assertEquals(createdPost.getBody(), newPost.getBody(), "Body should match");
        Assert.assertEquals(createdPost.getUserId(), newPost.getUserId(), "UserId should match");
        
        logger.info("Created post with ID: {}", createdPost.getId());
    }
    
    @Test(description = "Create a new comment via POST request")
    @Story("Create Operations")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateComment() {
        Comment newComment = Comment.builder()
            .postId(1)
            .name("Test Comment")
            .email("test@example.com")
            .body("This is a test comment body.")
            .build();
        
        Response response = apiClient.post(COMMENTS_ENDPOINT, newComment);
        
        Assert.assertEquals(response.getStatusCode(), 201, "Should return 201 Created");
        
        Comment createdComment = response.as(Comment.class);
        Assert.assertNotNull(createdComment.getId(), "Created comment should have an ID");
        Assert.assertEquals(createdComment.getEmail(), newComment.getEmail(), "Email should match");
    }
    
    // ==================== READ Tests ====================
    
    @Test(description = "Get all posts via GET request")
    @Story("Read Operations")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Should retrieve list of all posts")
    public void testGetAllPosts() {
        Response response = apiClient.get(POSTS_ENDPOINT);
        
        Assert.assertEquals(response.getStatusCode(), 200, "Should return 200 OK");
        
        List<Post> posts = response.jsonPath().getList(".", Post.class);
        Assert.assertTrue(posts.size() > 0, "Should return at least one post");
        
        // Verify first post has required fields
        Post firstPost = posts.get(0);
        Assert.assertNotNull(firstPost.getId(), "Post should have ID");
        Assert.assertNotNull(firstPost.getTitle(), "Post should have title");
        Assert.assertNotNull(firstPost.getBody(), "Post should have body");
        
        logger.info("Retrieved {} posts", posts.size());
    }
    
    @Test(description = "Get a single post by ID")
    @Story("Read Operations")
    @Severity(SeverityLevel.BLOCKER)
    public void testGetPostById() {
        int postId = 1;
        
        Response response = apiClient.get(POSTS_ENDPOINT + "/" + postId);
        
        Assert.assertEquals(response.getStatusCode(), 200, "Should return 200 OK");
        
        Post post = response.as(Post.class);
        Assert.assertEquals(post.getId(), Integer.valueOf(postId), "Should return requested post");
        Assert.assertNotNull(post.getTitle(), "Post should have title");
    }
    
    @Test(description = "Get all users")
    @Story("Read Operations")
    @Severity(SeverityLevel.NORMAL)
    public void testGetAllUsers() {
        Response response = apiClient.get(USERS_ENDPOINT);
        
        Assert.assertEquals(response.getStatusCode(), 200, "Should return 200 OK");
        
        List<User> users = response.jsonPath().getList(".", User.class);
        Assert.assertTrue(users.size() > 0, "Should return at least one user");
        
        // Verify user structure
        User firstUser = users.get(0);
        Assert.assertNotNull(firstUser.getId(), "User should have ID");
        Assert.assertNotNull(firstUser.getName(), "User should have name");
        Assert.assertNotNull(firstUser.getEmail(), "User should have email");
        
        logger.info("Retrieved {} users", users.size());
    }
    
    @Test(description = "Get user by ID")
    @Story("Read Operations")
    @Severity(SeverityLevel.NORMAL)
    public void testGetUserById() {
        int userId = 1;
        
        Response response = apiClient.get(USERS_ENDPOINT + "/" + userId);
        
        Assert.assertEquals(response.getStatusCode(), 200, "Should return 200 OK");
        
        User user = response.as(User.class);
        Assert.assertEquals(user.getId(), Integer.valueOf(userId), "Should return requested user");
    }
    
    @Test(description = "Get comments for a specific post")
    @Story("Read Operations")
    @Severity(SeverityLevel.NORMAL)
    public void testGetCommentsForPost() {
        int postId = 1;
        
        Response response = apiClient.get(POSTS_ENDPOINT + "/" + postId + "/comments");
        
        Assert.assertEquals(response.getStatusCode(), 200, "Should return 200 OK");
        
        List<Comment> comments = response.jsonPath().getList(".", Comment.class);
        Assert.assertTrue(comments.size() > 0, "Post should have comments");
        
        // Verify all comments belong to the requested post
        for (Comment comment : comments) {
            Assert.assertEquals(comment.getPostId(), Integer.valueOf(postId), 
                "Comment should belong to requested post");
        }
    }
    
    @Test(description = "Filter posts by userId query parameter")
    @Story("Read Operations - Filtering")
    @Severity(SeverityLevel.NORMAL)
    public void testFilterPostsByUserId() {
        int userId = 1;
        
        Response response = apiClient.get(POSTS_ENDPOINT + "?userId=" + userId);
        
        Assert.assertEquals(response.getStatusCode(), 200, "Should return 200 OK");
        
        List<Post> posts = response.jsonPath().getList(".", Post.class);
        Assert.assertTrue(posts.size() > 0, "Should return posts for user");
        
        // Verify all posts belong to the requested user
        for (Post post : posts) {
            Assert.assertEquals(post.getUserId(), Integer.valueOf(userId), 
                "Post should belong to requested user");
        }
    }
    
    // ==================== UPDATE Tests ====================
    
    @Test(description = "Update a post via PUT request")
    @Story("Update Operations")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Should update an existing post completely")
    public void testUpdatePostPut() {
        int postId = 1;
        
        Post updatedPost = Post.builder()
            .id(postId)
            .userId(1)
            .title("Updated Post Title")
            .body("This is the updated body content.")
            .build();
        
        Response response = apiClient.put(POSTS_ENDPOINT + "/" + postId, updatedPost);
        
        Assert.assertEquals(response.getStatusCode(), 200, "Should return 200 OK");
        
        Post returnedPost = response.as(Post.class);
        Assert.assertEquals(returnedPost.getTitle(), updatedPost.getTitle(), "Title should be updated");
        Assert.assertEquals(returnedPost.getBody(), updatedPost.getBody(), "Body should be updated");
    }
    
    @Test(description = "Partial update via PATCH request")
    @Story("Update Operations")
    @Severity(SeverityLevel.NORMAL)
    @Description("Should partially update a post")
    public void testUpdatePostPatch() {
        int postId = 1;
        
        // Only updating the title
        String patchBody = "{\"title\": \"Patched Title Only\"}";
        
        Response response = apiClient.patch(POSTS_ENDPOINT + "/" + postId, patchBody);
        
        Assert.assertEquals(response.getStatusCode(), 200, "Should return 200 OK");
        
        Post returnedPost = response.as(Post.class);
        Assert.assertEquals(returnedPost.getTitle(), "Patched Title Only", "Title should be patched");
        // Body should remain (JSONPlaceholder simulates this)
    }
    
    // ==================== DELETE Tests ====================
    
    @Test(description = "Delete a post via DELETE request")
    @Story("Delete Operations")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Should delete an existing post")
    public void testDeletePost() {
        int postId = 1;
        
        Response response = apiClient.delete(POSTS_ENDPOINT + "/" + postId);
        
        // JSONPlaceholder returns 200 for successful delete
        Assert.assertEquals(response.getStatusCode(), 200, "Should return 200 OK");
    }
    
    @Test(description = "Delete a comment")
    @Story("Delete Operations")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteComment() {
        int commentId = 1;
        
        Response response = apiClient.delete(COMMENTS_ENDPOINT + "/" + commentId);
        
        Assert.assertEquals(response.getStatusCode(), 200, "Should return 200 OK");
    }
    
    // ==================== Response Validation Tests ====================
    
    @Test(description = "Verify response headers")
    @Story("Response Validation")
    @Severity(SeverityLevel.MINOR)
    public void testResponseHeaders() {
        Response response = apiClient.get(POSTS_ENDPOINT);
        
        String contentType = response.getHeader("Content-Type");
        Assert.assertTrue(contentType.contains("application/json"), 
            "Content-Type should be JSON");
    }
    
    @Test(description = "Verify response time is acceptable")
    @Story("Response Validation")
    @Severity(SeverityLevel.MINOR)
    public void testResponseTime() {
        Response response = apiClient.get(POSTS_ENDPOINT);
        
        long responseTime = response.getTime();
        Assert.assertTrue(responseTime < 5000, 
            "Response time should be less than 5 seconds: " + responseTime + "ms");
        
        logger.info("Response time: {}ms", responseTime);
    }
}

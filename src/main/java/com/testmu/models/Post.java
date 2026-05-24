package com.testmu.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

/**
 * Post Model - POJO for post data in API requests/responses.
 * Uses JSONPlaceholder API structure: https://jsonplaceholder.typicode.com/posts
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Post {
    
    private Integer id;
    
    private Integer userId;
    
    private String title;
    
    private String body;
    
    /**
     * Create a sample post for testing
     * @return Post with test data
     */
    public static Post createTestPost() {
        return Post.builder()
            .userId(1)
            .title("Test Post Title")
            .body("This is a test post body created for API testing purposes.")
            .build();
    }
    
    /**
     * Create a post with specific user ID
     * @param userId User ID
     * @param title Post title
     * @param body Post body
     * @return Post
     */
    public static Post create(int userId, String title, String body) {
        return Post.builder()
            .userId(userId)
            .title(title)
            .body(body)
            .build();
    }
}

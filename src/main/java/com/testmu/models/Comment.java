package com.testmu.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

/**
 * Comment Model - POJO for comment data in API requests/responses.
 * Uses JSONPlaceholder API structure: https://jsonplaceholder.typicode.com/comments
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Comment {
    
    private Integer id;
    
    private Integer postId;
    
    private String name;
    
    private String email;
    
    private String body;
    
    /**
     * Create a sample comment for testing
     * @return Comment with test data
     */
    public static Comment createTestComment() {
        return Comment.builder()
            .postId(1)
            .name("Test Comment Author")
            .email("comment@example.com")
            .body("This is a test comment for API testing.")
            .build();
    }
    
    /**
     * Create a comment with specific post ID
     * @param postId Post ID
     * @param name Comment author name
     * @param email Comment author email
     * @param body Comment body
     * @return Comment
     */
    public static Comment create(int postId, String name, String email, String body) {
        return Comment.builder()
            .postId(postId)
            .name(name)
            .email(email)
            .body(body)
            .build();
    }
}

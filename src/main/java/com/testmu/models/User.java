package com.testmu.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * User Model - POJO for user data in API requests/responses.
 * Uses JSONPlaceholder API structure: https://jsonplaceholder.typicode.com/users
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    
    private Integer id;
    
    private String name;
    
    private String username;
    
    private String email;
    
    private String phone;
    
    private String website;
    
    private Address address;
    
    private Company company;
    
    /**
     * Address nested object
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Address {
        private String street;
        private String suite;
        private String city;
        private String zipcode;
        private Geo geo;
    }
    
    /**
     * Geo coordinates nested object
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Geo {
        private String lat;
        private String lng;
    }
    
    /**
     * Company nested object
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Company {
        private String name;
        private String catchPhrase;
        private String bs;
    }
    
    /**
     * Create a sample user for testing
     * @return User with test data
     */
    public static User createTestUser() {
        return User.builder()
            .name("Test User")
            .username("testuser")
            .email("testuser@example.com")
            .phone("123-456-7890")
            .website("testuser.com")
            .address(Address.builder()
                .street("123 Test Street")
                .suite("Apt 1")
                .city("Test City")
                .zipcode("12345")
                .geo(Geo.builder()
                    .lat("0.0000")
                    .lng("0.0000")
                    .build())
                .build())
            .company(Company.builder()
                .name("Test Company")
                .catchPhrase("Testing made easy")
                .bs("quality assurance")
                .build())
            .build();
    }
    
    /**
     * Create user with minimal required fields
     * @param name User name
     * @param email User email
     * @return User with minimal data
     */
    public static User createMinimalUser(String name, String email) {
        return User.builder()
            .name(name)
            .email(email)
            .build();
    }
}

package com.sdet.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ApiTest {

    @BeforeClass
    public void setup() {
        // Set base URI
        RestAssured.baseURI = "https://api.example.com";
        
        // Configure logging if needed
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    public void testGetUserEndpoint() {
        given()
            .header("Authorization", "Bearer " + getAuthToken())
            .contentType(ContentType.JSON)
        .when()
            .get("/users/1")
        .then()
            .statusCode(200)
            .body("id", equalTo(1))
            .body("email", not(emptyString()))
            .time(lessThan(2000L)); // Response time < 2 seconds
    }

    @Test
    public void testCreateUser() {
        String requestBody = """
            {
                "name": "John Doe",
                "email": "john.doe@example.com",
                "role": "user"
            }
            """;

        given()
            .header("Authorization", "Bearer " + getAuthToken())
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/users")
        .then()
            .statusCode(201)
            .body("name", equalTo("John Doe"))
            .body("email", equalTo("john.doe@example.com"))
            .body("id", not(emptyString()));
    }

    @Test
    public void testUpdateUser() {
        String updateBody = """
            {
                "name": "John Updated",
                "role": "admin"
            }
            """;

        given()
            .header("Authorization", "Bearer " + getAuthToken())
            .contentType(ContentType.JSON)
            .body(updateBody)
        .when()
            .put("/users/1")
        .then()
            .statusCode(200)
            .body("name", equalTo("John Updated"))
            .body("role", equalTo("admin"));
    }

    private String getAuthToken() {
        // In a real implementation, this would handle OAuth or other authentication
        return "your-auth-token";
    }
}
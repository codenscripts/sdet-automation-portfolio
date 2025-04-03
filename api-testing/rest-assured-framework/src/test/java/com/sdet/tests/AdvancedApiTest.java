package com.sdet.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.json.JSONObject;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import java.util.HashMap;
import java.util.Map;

public class AdvancedApiTest {
    private RequestSpecification requestSpec;
    private String authToken;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://api.example.com";
        
        // Create request specification for reuse
        requestSpec = given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .log().all();  // Log all requests
    }

    @Test(description = "Test OAuth2 Authentication")
    public void testOAuth2Authentication() {
        // Get OAuth2 token
        authToken = given()
            .contentType(ContentType.JSON)
            .body(new HashMap<String, String>() {{
                put("client_id", "your-client-id");
                put("client_secret", "your-client-secret");
                put("grant_type", "client_credentials");
            }})
        .when()
            .post("/oauth/token")
        .then()
            .statusCode(200)
            .extract()
            .path("access_token");

        // Use token in subsequent requests
        requestSpec.header("Authorization", "Bearer " + authToken);
    }

    @Test(description = "Test File Upload API")
    public void testFileUpload() {
        given()
            .spec(requestSpec)
            .multiPart("file", new File("test-data.pdf"))
            .formParam("description", "Test file upload")
        .when()
            .post("/upload")
        .then()
            .statusCode(201)
            .body("fileName", notNullValue())
            .body("fileSize", greaterThan(0));
    }

    @Test(description = "Test Complex JSON Response")
    public void testComplexJsonResponse() {
        given()
            .spec(requestSpec)
        .when()
            .get("/users/1/details")
        .then()
            .statusCode(200)
            .body("user.profile.address.city", notNullValue())
            .body("user.orders.size()", greaterThanOrEqualTo(1))
            .body("user.orders[0].items.findAll { it.price > 10 }.size()", greaterThanOrEqualTo(1));
    }

    @Test(description = "Test API Performance")
    public void testApiPerformance() {
        Response response = given()
            .spec(requestSpec)
        .when()
            .get("/large-data")
        .then()
            .time(lessThan(2000L))  // Response time < 2 seconds
            .extract()
            .response();

        // Additional performance assertions
        long responseTime = response.getTime();
        System.out.println("Response time: " + responseTime + "ms");
    }

    @Test(description = "Test Batch Operations")
    public void testBatchOperations() {
        // Create multiple users in batch
        JSONObject[] users = new JSONObject[3];
        for (int i = 0; i < 3; i++) {
            users[i] = new JSONObject()
                .put("name", "User " + i)
                .put("email", "user" + i + "@example.com");
        }

        given()
            .spec(requestSpec)
            .body(users)
        .when()
            .post("/users/batch")
        .then()
            .statusCode(200)
            .body("size()", is(3))
            .body("findAll { it.id != null }.size()", is(3));
    }

    @Test(description = "Test API Error Handling")
    public void testErrorHandling() {
        given()
            .spec(requestSpec)
        .when()
            .get("/non-existent-endpoint")
        .then()
            .statusCode(404)
            .body("error", notNullValue())
            .body("message", containsString("not found"));

        given()
            .spec(requestSpec)
            .body(new HashMap<>())  // Empty body to trigger validation error
        .when()
            .post("/users")
        .then()
            .statusCode(400)
            .body("errors", notNullValue())
            .body("errors.size()", greaterThan(0));
    }

    @Test(description = "Test API Rate Limiting")
    public void testRateLimiting() {
        // Make multiple rapid requests to trigger rate limiting
        for (int i = 0; i < 10; i++) {
            Response response = given()
                .spec(requestSpec)
            .when()
                .get("/api/data")
            .then()
                .extract().response();

            // Check rate limit headers
            String remainingRequests = response.getHeader("X-RateLimit-Remaining");
            String resetTime = response.getHeader("X-RateLimit-Reset");
            
            System.out.println("Remaining requests: " + remainingRequests);
            System.out.println("Rate limit reset time: " + resetTime);
        }
    }
}
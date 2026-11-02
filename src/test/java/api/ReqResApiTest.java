package api;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * REST API tests against ReqRes (https://reqres.in) — a stable public
 * API built for practice. Covers the five basic HTTP operations:
 * read one, read many, create, update, delete.
 *
 * <p>These tests need no browser and run in seconds. They live in the
 * {@code api} package, separate from the Selenium UI tests in
 * {@code tests}.</p>
 */
@Epic("API")
@Feature("ReqRes user API")
class ReqResApiTest {

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "https://reqres.in";
        RestAssured.basePath = "/api";
    }

    @Test
    @Story("Read a single user")
    @DisplayName("Getting one user returns 200 with the expected name and email")
    void getSingleUserReturnsExpectedFields() {
        given()
            .pathParam("id", 2)
        .when()
            .get("/users/{id}")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("data.id", equalTo(2))
            .body("data.first_name", equalTo("Janet"))
            .body("data.last_name", equalTo("Weaver"))
            .body("data.email", equalTo("janet.weaver@reqres.in"));
    }

    @Test
    @Story("Read a page of users")
    @DisplayName("Getting the user list returns 200 with paginated data")
    void getUserListReturnsPaginatedData() {
        given()
            .queryParam("page", 2)
        .when()
            .get("/users")
        .then()
            .statusCode(200)
            .body("page", equalTo(2))
            .body("per_page", equalTo(6))
            .body("total", equalTo(12))
            .body("data.size()", equalTo(6))
            .body("data[0].id", notNullValue());
    }

    @Test
    @Story("Create a user")
    @DisplayName("Creating a user returns 201 with an id and a creation timestamp")
    void createUserReturnsIdAndTimestamp() {
        String body = "{\"name\": \"morpheus\", \"job\": \"leader\"}";

        given()
            .contentType(ContentType.JSON)
            .body(body)
        .when()
            .post("/users")
        .then()
            .statusCode(201)
            .body("name", equalTo("morpheus"))
            .body("job", equalTo("leader"))
            .body("id", notNullValue())
            .body("createdAt", notNullValue());
    }

    @Test
    @Story("Update a user")
    @DisplayName("Updating a user returns 200 with an updated timestamp")
    void updateUserReturnsUpdatedTimestamp() {
        String body = "{\"name\": \"morpheus\", \"job\": \"zion resident\"}";

        given()
            .contentType(ContentType.JSON)
            .body(body)
            .pathParam("id", 2)
        .when()
            .put("/users/{id}")
        .then()
            .statusCode(200)
            .body("name", equalTo("morpheus"))
            .body("job", equalTo("zion resident"))
            .body("updatedAt", notNullValue());
    }

    @Test
    @Story("Delete a user")
    @DisplayName("Deleting a user returns 204 with an empty body")
    void deleteUserReturnsNoContent() {
        given()
            .pathParam("id", 2)
        .when()
            .delete("/users/{id}")
        .then()
            .statusCode(204);
    }
}

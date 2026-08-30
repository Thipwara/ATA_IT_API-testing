package com.gorest.tests;

import com.gorest.base.BaseTest;
import com.gorest.config.ConfigManager;
import com.gorest.helpers.AssertionHelper;
import com.gorest.helpers.TestDataGenerator;
import com.gorest.models.User;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.*;

@Feature("Users Edge Cases & Security")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserEdgeCaseTest extends BaseTest {
    private static int defaultUserId;

    @BeforeAll
    static void createUserWithDefaultUser() {
        User defaultUser = TestDataGenerator.randomUser();
        Response defaultUserResponse = userApi.createUser(defaultUser);
        AssertionHelper.assertStatusCode(defaultUserResponse, 201);
        defaultUserId = defaultUserResponse.jsonPath().getInt("id");
    };

    @AfterAll
    static void cleanUpDefaultUser() {
        if (defaultUserId > 0) {
            userApi.deleteUser(defaultUserId);
            log.info("Cleanup — deleted default user ID={}", defaultUserId);
        }
    }

    // ─── Scenario 16 ────────────────────────────────────────────

    @Test
    @Order(16)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[Scenario 16] GET /users/{id} — non-existent ID should return 404")
    @Description("Fetch a user with an ID that does not exist in the system. Server must return 404 Not Found")
    void getUser_withNonExistentId_shouldReturn404() {
        int nonExistentId = 999_999_999;

        Response response = userApi.getUser(nonExistentId);

        AssertionHelper.assertStatusCode(response, 404);
        AssertionHelper.assertErrorMessage(response, "Resource not found");

        log.info("[Scenario 16] PASS — ID={} correctly returned 404", nonExistentId);
    }

    // ─── Scenario 17 ───────────────────────────────────────────

    @Test
    @Order(17)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Scenario 17] POST /users — invalid token should return 401")
    @Description("Attempt to create a user using an invalid Bearer token. Server must reject with 401 Unauthorized")
    void createUser_withInvalidToken_shouldReturn401() {
        User user = TestDataGenerator.randomUser();

        Response response = RestAssured.given()
                .baseUri(ConfigManager.getBaseUrl())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer invalid_token_12345")
                .body(user)
                .when()
                .post("/users");

        AssertionHelper.assertStatusCode(response, 401);
        AssertionHelper.assertErrorMessage(response, "Invalid token");

        log.info("[Scenario 17] PASS — invalid token correctly returned 401, body={}",
                response.body().asString());
    }

    // ─── Scenario 18 ───────────────────────────────────────────
    @Test
    @Order(18)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[Scenario 18] DELETE /users/{id} — non-existent ID should return 404")
    @Description("Delete a user with an ID that does not exist in the system. Server must return 404 Not Found")
    void deleteUser_withNonExistentId_shouldReturn404() {
        int nonExistentId = 999_999_999;
        Response response = userApi.deleteUser(nonExistentId);
        AssertionHelper.assertStatusCode(response, 404);
        AssertionHelper.assertErrorMessage(response, "Resource not found");
        log.info("[Scenario 18] PASS — ID={} correctly returned 404", nonExistentId);
    }

    // ─── Scenario 19 ───────────────────────────────────────────
    @Test
    @Order(19)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[Scenario 19] PUT /users/{id} — non-existent ID should return 404")
    @Description("Update a user with an ID that does not exist in the system. Server must return 404 Not Found")
    void updateUser_withNonExistentId_shouldReturn404() {
        int nonExistentId = 999_999_999;
        User user = TestDataGenerator.randomUser();
        Response response = userApi.updateUser(nonExistentId, user);
        AssertionHelper.assertStatusCode(response, 404);
        AssertionHelper.assertErrorMessage(response, "Resource not found");
        log.info("[Scenario 19] PASS — ID={} correctly returned 404", nonExistentId);
    }

    // ─── Scenario 20 ───────────────────────────────────────────
    @Test
    @Order(20)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[Scenario 20] POST /users — name is longer than 200 characters should return 422")
    @Description("Try to create a user with a name longer than 200 characters. Server must reject with 422 Unprocessable Entity")
    void createUser_withNameLongerThan200Characters_shouldReturn422() {
        User user = TestDataGenerator.randomUser();
        user.setName("a".repeat(201));
        Response response = userApi.createUser(user);
        AssertionHelper.assertStatusCode(response, 422);
        AssertionHelper.assertValidationError(response, "name", "is too long (maximum is 200 characters)");
        log.info("[Scenario 20] PASS — name too long correctly returned 422, body={}",
                response.body().asString());
    }

    // ─── Scenario 21 ───────────────────────────────────────────
    @Test
    @Order(21)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[Scenario 21] POST /users — email is longer than 200 characters should return 422")
    @Description("Try to create a user with an email longer than 200 characters. Server must reject with 422 Unprocessable Entity")
    void createUser_withEmailLongerThan200Characters_shouldReturn422() {
        User user = TestDataGenerator.randomUser();
        user.setEmail("a".repeat(201));
        Response response = userApi.createUser(user);
        AssertionHelper.assertStatusCode(response, 422);
        AssertionHelper.assertValidationError(response, "email", "is too long (maximum is 200 characters), is invalid");
        log.info("[Scenario 21] PASS — email too long correctly returned 422, body={}",
                response.body().asString());
    }

    // ─── Scenario 22 ───────────────────────────────────────────
    @Test
    @Order(22)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[Scenario 22] PUT /users — name is longer than 200 characters should return 422")
    @Description("Try to create a user with a name longer than 200 characters. Server must reject with 422 Unprocessable Entity")
    void updateUser_withNameLongerThan200Characters_shouldReturn422() {
        User user = TestDataGenerator.randomUser();
        user.setName("a".repeat(201));
        Response response = userApi.updateUser(defaultUserId, user);
        AssertionHelper.assertStatusCode(response, 422);
        AssertionHelper.assertValidationError(response, "name", "is too long (maximum is 200 characters)");
        log.info("[Scenario 22] PASS — name too long correctly returned 422, body={}",
                response.body().asString());
    }

    // ─── Scenario 23 ───────────────────────────────────────────
    @Test
    @Order(23)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[Scenario 23] PUT /users — email is longer than 200 characters should return 422")
    @Description("Try to create a user with an email longer than 200 characters. Server must reject with 422 Unprocessable Entity")
    void updateUser_withEmailLongerThan200Characters_shouldReturn422() {
        User user = TestDataGenerator.randomUser();
        user.setEmail("a".repeat(201));
        Response response = userApi.updateUser(defaultUserId, user);
        AssertionHelper.assertStatusCode(response, 422);
        AssertionHelper.assertValidationError(response, "email", "is too long (maximum is 200 characters), is invalid");
        log.info("[Scenario 23] PASS — email too long correctly returned 422, body={}",
                response.body().asString());
    }

    // ─── Scenario 24 ───────────────────────────────────────────
    @Test
    @Order(24)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[Scenario 24] POST /users — create user name with special character will return success")
    @Description("Try to create a user with an name with special character. Server must return 201 Created")
    void createUser_withNameWithSpecialCharacter_shouldReturnSuccess() {
        User user = TestDataGenerator.randomUser();
        user.setName("!@#$%^&*()");
        Response response = userApi.createUser(user);
        AssertionHelper.assertStatusCode(response, 201);

        User created = response.as(User.class);
        assertThat(created.getId()).as("Created user ID should not be null").isNotNull().isPositive();
        AssertionHelper.assertUserFields(created, user);
        log.info("[Scenario 24] PASS — create user name with special character correctly returned 201, body={}",
                response.body().asString());
    }

    // ─── Scenario 25 ───────────────────────────────────────────
    @Test
    @Order(25)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[Scenario 25] PUT /users — update user name with special character will return success")
    @Description("Try to update a user with an name with special character. Server must return 200 OK")
    void updateUser_withNameWithSpecialCharacter_shouldReturnSuccess() {
        User user = TestDataGenerator.randomUser();
        user.setName("!!!???");
        Response response = userApi.updateUser(defaultUserId, user);
        AssertionHelper.assertStatusCode(response, 200);
        log.info("[Scenario 25] PASS — update user name with special character correctly returned 200, body={}",
                response.body().asString());
    }

}

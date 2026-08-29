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
import org.junit.jupiter.api.*;

/**
 * Scenario 9–10: Edge Cases & Security
 *
 *  9.  GET  /users/{id} — non-existent ID → 404
 * 10.  POST /users — no token / invalid token → 401
 */
@Feature("Users Edge Cases & Security")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserEdgeCaseTest extends BaseTest {

    // ─── Scenario 9 ────────────────────────────────────────────

    @Test
    @Order(9)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[Scenario 9] GET /users/{id} — non-existent ID should return 404")
    @Description("Fetch a user with an ID that does not exist in the system. Server must return 404 Not Found")
    void getUser_withNonExistentId_shouldReturn404() {
        // ใช้ ID ที่มีโอกาสน้อยมากที่จะมีอยู่จริงในระบบ
        int nonExistentId = 999_999_999;

        Response response = userApi.getUser(nonExistentId);

        AssertionHelper.assertStatusCode(response, 404);

        log.info("[Scenario 9] PASS — ID={} correctly returned 404", nonExistentId);
    }

    // ─── Scenario 10 ───────────────────────────────────────────

    @Test
    @Order(10)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Scenario 10] POST /users — invalid token should return 401")
    @Description("Attempt to create a user using an invalid Bearer token. Server must reject with 401 Unauthorized")
    void createUser_withInvalidToken_shouldReturn401() {
        User payload = TestDataGenerator.randomUser();

        // ส่ง Request พร้อม Token ปลอม แทนที่จะใช้ userApi (ซึ่งมี token จริง)
        Response response = RestAssured.given()
                .baseUri(ConfigManager.getBaseUrl())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer invalid_token_12345")
                .body(payload)
                .when()
                .post("/users");

        AssertionHelper.assertStatusCode(response, 401);

        log.info("[Scenario 10] PASS — invalid token correctly returned 401, body={}",
                response.body().asString());
    }
}

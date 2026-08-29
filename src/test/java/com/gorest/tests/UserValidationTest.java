package com.gorest.tests;

import com.gorest.base.BaseTest;
import com.gorest.helpers.AssertionHelper;
import com.gorest.helpers.TestDataGenerator;
import com.gorest.models.User;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Scenario 6–8: Validation & Error Handling (422 Unprocessable Entity)
 *
 *  6. POST /users — duplicate email         → 422
 *  7. POST /users — missing required fields → 422
 *  8. PUT  /users/{id} — invalid field value → 422
 */
@Feature("Users Validation")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserValidationTest extends BaseTest {

    // ID ของ user ที่สร้างใน Scenario 6 เพื่อใช้ทดสอบ Scenario 8
    private static int helperUserId;
    private static String helperUserEmail;

    // ─── Scenario 6 ────────────────────────────────────────────

    @Test
    @Order(6)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Scenario 6] POST /users — duplicate email should return 422")
    @Description("Attempt to create two users with the same email. Second request must return 422 with 'already been taken' message")
    void createUser_withDuplicateEmail_shouldReturn422() {
        // สร้าง User คนแรกเพื่อเป็นตัวตั้ง (ใช้ email นี้ซ้ำใน request ถัดไป)
        User firstUser = TestDataGenerator.randomUser();
        Response firstResponse = userApi.createUser(firstUser);
        AssertionHelper.assertStatusCode(firstResponse, 201);
        helperUserId = firstResponse.jsonPath().getInt("id");
        helperUserEmail = firstUser.getEmail();

        // ลอง POST ซ้ำด้วย email เดิม
        User duplicateUser = new User(
                TestDataGenerator.randomName(),
                helperUserEmail,   // email ซ้ำ!
                "male",
                "active"
        );
        Response response = userApi.createUser(duplicateUser);

        AssertionHelper.assertStatusCode(response, 422);
        AssertionHelper.assertContainsMessage(response, "taken");

        log.info("[Scenario 6] PASS — duplicate email rejected with 422");
    }

    // ─── Scenario 7 ────────────────────────────────────────────

    @Test
    @Order(7)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Scenario 7] POST /users — missing required fields should return 422")
    @Description("Attempt to create a user without required fields (name, email). Server must return 422 with field-level error messages")
    void createUser_withMissingFields_shouldReturn422() {
        // ส่งเฉพาะ gender กับ status แต่ไม่ส่ง name และ email
        User incompleteUser = new User();
        incompleteUser.setGender("male");
        incompleteUser.setStatus("active");

        Response response = userApi.createUser(incompleteUser);

        AssertionHelper.assertStatusCode(response, 422);

        // ตรวจสอบว่า API แจ้งฟิลด์ที่ขาดไปอย่างถูกต้อง
        String body = response.body().asString();
        assertThat(body).as("Response should mention 'name' field error").containsIgnoringCase("name");
        assertThat(body).as("Response should mention 'email' field error").containsIgnoringCase("email");

        log.info("[Scenario 7] PASS — missing fields rejected with 422, body={}", body);
    }

    // ─── Scenario 8 ────────────────────────────────────────────

    @Test
    @Order(8)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[Scenario 8] PUT /users/{id} — invalid gender value should return 422")
    @Description("Update a user with an invalid 'gender' value (not 'male'/'female'). Server must reject with 422")
    void updateUser_withInvalidGender_shouldReturn422() {
        Assumptions.assumeTrue(helperUserId > 0, "Skipping — no helper user was created in Scenario 6");

        // ส่ง User object ที่มีค่า gender ไม่ถูกต้องผ่าน PUT
        User invalidPayload = new User(
                TestDataGenerator.randomName(),
                helperUserEmail,
                "unknown",   // gender ผิดกฎ (ต้องเป็น male หรือ female)
                "active"
        );

        Response response = userApi.updateUser(helperUserId, invalidPayload);

        AssertionHelper.assertStatusCode(response, 422);
        AssertionHelper.assertContainsMessage(response, "gender");

        log.info("[Scenario 8] PASS — invalid gender on PUT rejected with 422");

        // Cleanup helper user
        userApi.deleteUser(helperUserId);
        log.info("[Scenario 8] Cleanup — deleted helper user ID={}", helperUserId);
    }
}

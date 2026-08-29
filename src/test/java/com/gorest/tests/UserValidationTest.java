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

@Feature("Users Validation")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserValidationTest extends BaseTest {

    private static int helperUserId;
    private static String helperUserEmail;

    void createFirstUser() {
        User firstUser = TestDataGenerator.randomUser();
        Response firstResponse = userApi.createUser(firstUser);
        AssertionHelper.assertStatusCode(firstResponse, 201);
        helperUserId = firstResponse.jsonPath().getInt("id");
        helperUserEmail = firstUser.getEmail();
    };

    void cleanUpHelperUser() {
        userApi.deleteUser(helperUserId);
        log.info("Cleanup — deleted helper user ID={}", helperUserId);
    };

    // ─── Scenario 7 ────────────────────────────────────────────

    @Test
    @Order(7)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Scenario 7] POST /users — duplicate email should return 422")
    @Description("Attempt to create two users with the same email. Second request must return 422 with 'already been taken' message")
    void createUser_withDuplicateEmail_shouldReturn422() {
        createFirstUser();
        User duplicateUser = new User(
                TestDataGenerator.randomName(),
                helperUserEmail,
                "male",
                "active"
        );
        Response response = userApi.createUser(duplicateUser);

        AssertionHelper.assertStatusCode(response, 422);
        AssertionHelper.assertValidationError(response, "email", "has already been taken");

        log.info("[Scenario 7] PASS — duplicate email rejected with 422");
    }

    // ─── Scenario 8 ────────────────────────────────────────────

    @Test
    @Order(8)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Scenario 8] POST /users — missing required fields should return 422")
    @Description("Attempt to create a user without required fields (name, email, gender, status). Server must return 422 with field-level error messages")
    void createUser_withMissingFields_shouldReturn422() {
        User incompleteUser = new User();

        Response response = userApi.createUser(incompleteUser);

        AssertionHelper.assertStatusCode(response, 422);
        AssertionHelper.assertValidationError(response, "email", "can't be blank");
        AssertionHelper.assertValidationError(response, "name", "can't be blank");
        AssertionHelper.assertValidationError(response, "gender", "can't be blank, can be male of female");
        AssertionHelper.assertValidationError(response, "status", "can't be blank");

        log.info("[Scenario 8] PASS — missing fields rejected with 422, verified all field errors");
    }

      // ─── Scenario 9 ────────────────────────────────────────────

    @Test
    @Order(9)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Scenario 9] PUT /users — missing required fields should return 422")
    @Description("Attempt to update a user with missing required fields. Server must return 422 with field-level error messages")
    void updateUser_withMissingRequiredFields_shouldReturn422() {
        User incompleteUpdateUser = new User(
            "",
            "",
            "",
            ""
        );

        Response response = userApi.updateUser(helperUserId, incompleteUpdateUser);

        AssertionHelper.assertStatusCode(response, 422);
        AssertionHelper.assertValidationError(response, "email", "can't be blank");
        AssertionHelper.assertValidationError(response, "name", "can't be blank");
        AssertionHelper.assertValidationError(response, "gender", "can't be blank, can be male of female");
        AssertionHelper.assertValidationError(response, "status", "can't be blank");

        log.info("[Scenario 9] PASS — missing required fields on PUT rejected with 422");
    }

    // ─── Scenario 10 ────────────────────────────────────────────

    @Test
    @Order(10)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[Scenario 10] POST /users — invalid gender value should return 422")
    @Description("Attempt to create a user with an invalid 'gender' value (not 'male'/'female'). Server must reject with 422")
    void createUser_withInvalidGender_shouldReturn422() {
        User invalidPayload = new User(
                TestDataGenerator.randomName(),
                TestDataGenerator.uniqueEmail(),
                "unknown",
                "active"
        );

        Response response = userApi.createUser(invalidPayload);

        AssertionHelper.assertStatusCode(response, 422);
        AssertionHelper.assertValidationError(response, "gender", "can't be blank, can be male of female");

        log.info("[Scenario 10] PASS — invalid gender on POST rejected with 422");
    }
    // ─── Scenario 11 ────────────────────────────────────────────

    @Test
    @Order(11)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[Scenario 11] PUT /users/{id} — invalid gender value should return 422")
    @Description("Update a user with an invalid 'gender' value (not 'male'/'female'). Server must reject with 422")
    void updateUser_withInvalidGender_shouldReturn422() {
        Assumptions.assumeTrue(helperUserId > 0, "Skipping — no helper user was created in Scenario 6");

        User invalidPayload = new User(
                TestDataGenerator.randomName(),
                helperUserEmail,
                "unknown",
                "active"
        );

        Response response = userApi.updateUser(helperUserId, invalidPayload);

        AssertionHelper.assertStatusCode(response, 422);
        AssertionHelper.assertValidationError(response, "gender", "can't be blank, can be male of female");

        log.info("[Scenario 11] PASS — invalid gender on PUT rejected with 422");
    }

    // ─── Scenario 12 ────────────────────────────────────────────

    @Test
    @Order(12)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[Scenario 12] POST /users — invalid status value should return 422")
    @Description("Attempt to create a user with an invalid 'status' value (not 'active'/'inactive'). Server must reject with 422")
    void createUser_withInvalidStatus_shouldReturn422() {
        User invalidStatusPayload = new User(
                TestDataGenerator.randomName(),
                TestDataGenerator.uniqueEmail(),
                "male",
                "unknown"
        );

        Response response = userApi.createUser(invalidStatusPayload);

        AssertionHelper.assertStatusCode(response, 422);
        AssertionHelper.assertValidationError(response, "status", "can't be blank");

        log.info("[Scenario 12] PASS — invalid status on POST rejected with 422");
    }

    // ─── Scenario 13 ────────────────────────────────────────────

    @Test
    @Order(13)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[Scenario 13] PUT /users/{id} — invalid status value should return 422")
    @Description("Update a user with an invalid 'status' value (not 'active'/'inactive'). Server must reject with 422")
    void updateUser_withInvalidStatus_shouldReturn422() {
        Assumptions.assumeTrue(helperUserId > 0, "Skipping — no helper user was created in Scenario 6");

        User invalidStatusPayload = new User(
                TestDataGenerator.randomName(),
                helperUserEmail,
                "male",
                "invalidStatus"
        );

        Response response = userApi.updateUser(helperUserId, invalidStatusPayload);

        AssertionHelper.assertStatusCode(response, 422);
        AssertionHelper.assertValidationError(response, "status", "can't be blank");

        log.info("[Scenario 13] PASS — invalid status on PUT rejected with 422");
    }

    // ─── Scenario 14 ────────────────────────────────────────────

    @Test
    @Order(14)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Scenario 14] POST /users — invalid email format should return 422")
    @Description("Attempt to create a user with an invalid email format. Server must return 422 with field-level error messages")
    void createUser_withInvalidEmailFormat_shouldReturn422() {
        User invalidEmailPayload = new User(
                TestDataGenerator.randomName(),
                "invalidEmail",
                "male",
                "active"
        );

        Response response = userApi.createUser(invalidEmailPayload);

        AssertionHelper.assertStatusCode(response, 422);
        AssertionHelper.assertValidationError(response, "email", "is invalid");

        log.info("[Scenario 14] PASS — invalid email format on POST rejected with 422");
    }

    // ─── Scenario 15 ────────────────────────────────────────────

    @Test
    @Order(15)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Scenario 15] PUT /users — invalid email format should return 422")
    @Description("Attempt to update a user with an invalid email format. Server must return 422 with field-level error messages")
    void updateUser_withInvalidEmailFormat_shouldReturn422() {
        Assumptions.assumeTrue(helperUserId > 0, "Skipping — no helper user was created in Scenario 6");

        User invalidEmailPayload = new User(
                TestDataGenerator.randomName(),
                "invalidEmail",
                "male",
                "active"
        );

        Response response = userApi.updateUser(helperUserId, invalidEmailPayload);

        AssertionHelper.assertStatusCode(response, 422);
        AssertionHelper.assertValidationError(response, "email", "is invalid");

        log.info("[Scenario 15] PASS — invalid email format on PUT rejected with 422");
    }
}

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

@Feature("Users CRUD")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserCrudTest extends BaseTest {

    private static int createdUserId;
    private static User createdPayload;

    // ─── Scenario 1 ────────────────────────────────────────────

    @Test
    @Order(1)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Scenario 1] GET /users — should return 200 and non-empty list")
    @Description("Verify list-users endpoint returns HTTP 200 and at least one user in the response")
    void getUsers_shouldReturn200WithNonEmptyList() {
        Response response = userApi.listUsers();

        AssertionHelper.assertStatusCode(response, 200);

        User[] users = response.as(User[].class);
        assertThat(users)
                .as("User list should not be empty")
                .isNotEmpty();

        log.info("[Scenario 1] PASS — returned {} users", users.length);
    }

    // ─── Scenario 2 ────────────────────────────────────────────

    @Test
    @Order(2)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Scenario 2] POST /users — should create user and return 201")
    @Description("Create a new user with valid random data, verify 201 status and response fields match payload")
    void createUser_shouldReturn201WithMatchingFields() {
        createdPayload = TestDataGenerator.randomUser();

        Response response = userApi.createUser(createdPayload);

        AssertionHelper.assertStatusCode(response, 201);

        User created = response.as(User.class);
        assertThat(created.getId()).as("Created user ID should not be null").isNotNull().isPositive();

        AssertionHelper.assertUserFields(created, createdPayload);

        createdUserId = created.getId();
        log.info("[Scenario 2] PASS — created user ID={}", createdUserId);
    }

    // ─── Scenario 3 ────────────────────────────────────────────

    @Test
    @Order(3)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Scenario 3] GET /users — should return the created user in the list")
    @Description("Fetch the user list should return user in Scenario 2 by ID and verify all fields are persisted correctly")
    void getUserList_shouldReturn200WithCorrectData() {
        Assumptions.assumeTrue(createdUserId > 0, "Skipping — no user was created in Scenario 2");

        Response response = userApi.listAllUsers();

        AssertionHelper.assertStatusCode(response, 200);

        User[] users = response.as(User[].class);
        
        boolean userFound = false;
        for (User u : users) {
            if (u.getId() != null && u.getId().equals(createdUserId)) {
                userFound = true;
                AssertionHelper.assertUserFields(u, createdPayload);
                break;
            }
        }

        assertThat(userFound)
                .as("Created user ID " + createdUserId + " should be present in the user list")
                .isTrue();

        log.info("[Scenario 3] PASS — found user ID={} in the list", createdUserId);
    }

    // ─── Scenario 4 ────────────────────────────────────────────

    @Test
    @Order(4)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Scenario 4] GET /users/{id} — should return the created user")
    @Description("Fetch the user created in Scenario 2 by ID and verify all fields are persisted correctly")
    void getCreatedUser_shouldReturn200WithCorrectData() {
        Assumptions.assumeTrue(createdUserId > 0, "Skipping — no user was created in Scenario 2");

        Response response = userApi.getUser(createdUserId);

        AssertionHelper.assertStatusCode(response, 200);

        User fetched = response.as(User.class);
        assertThat(fetched.getId()).as("User ID should match").isEqualTo(createdUserId);
        AssertionHelper.assertUserFields(fetched, createdPayload);

        log.info("[Scenario 4] PASS — fetched user: {}", fetched);
    }

    // ─── Scenario 5 ────────────────────────────────────────────

    @Test
    @Order(5)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[Scenario 5] PUT /users/{id} — should update user and return 200")
    @Description("Update name and status of the created user, verify the response reflects the new values")
    void updateUser_shouldReturn200WithUpdatedFields() {
        Assumptions.assumeTrue(createdUserId > 0, "Skipping — no user was created in Scenario 2");

        User updatePayload = new User(
                TestDataGenerator.randomName(),
                createdPayload.getEmail(),
                "female",
                "inactive"
        );

        Response response = userApi.updateUser(createdUserId, updatePayload);

        AssertionHelper.assertStatusCode(response, 200);

        User updated = response.as(User.class);
        assertThat(updated.getName()).as("Name should be updated").isEqualTo(updatePayload.getName());
        assertThat(updated.getStatus()).as("Status should be 'inactive'").isEqualTo("inactive");
        assertThat(updated.getGender()).as("Gender should be 'female'").isEqualTo("female");

        log.info("[Scenario 5] PASS — updated user: {}", updated);
    }

    // ─── Scenario 6 ────────────────────────────────────────────

    @Test
    @Order(6)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Scenario 6] DELETE /users/{id} — should delete user and return 204")
    @Description("Delete the created user, verify 204 status. Then GET the same ID should return 404")
    void deleteUser_shouldReturn204AndThenNotFound() {
        Assumptions.assumeTrue(createdUserId > 0, "Skipping — no user was created in Scenario 2");

        Response deleteResponse = userApi.deleteUser(createdUserId);
        AssertionHelper.assertStatusCode(deleteResponse, 204);

        Response getAfterDelete = userApi.getUser(createdUserId);
        AssertionHelper.assertStatusCode(getAfterDelete, 404);

        log.info("[Scenario 6] PASS — user ID={} deleted, confirmed 404 on re-fetch", createdUserId);
    }
}

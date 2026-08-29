package com.gorest.helpers;

import com.gorest.models.User;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Reusable assertion helpers that provide descriptive failure messages
 * and reduce boilerplate in test classes.
 */
public final class AssertionHelper {

    private static final Logger log = LoggerFactory.getLogger(AssertionHelper.class);

    private AssertionHelper() { }

    /** Assert that the response has the expected HTTP status code. */
    public static void assertStatusCode(Response response, int expected) {
        int actual = response.statusCode();
        if (actual != expected) {
            log.error("Status code mismatch — expected={}, actual={}, body={}",
                    expected, actual, response.body().asString());
        }
        assertThat(actual)
                .as("HTTP Status Code")
                .isEqualTo(expected);
    }

    /** Assert that a returned User matches the expected field values. */
    public static void assertUserFields(User actual, User expected) {
        assertThat(actual.getName()).as("User name").isEqualTo(expected.getName());
        assertThat(actual.getEmail()).as("User email").isEqualTo(expected.getEmail());
        assertThat(actual.getGender()).as("User gender").isEqualTo(expected.getGender());
        assertThat(actual.getStatus()).as("User status").isEqualTo(expected.getStatus());
    }

    /** Assert that the response body contains a specific JSON field. */
    public static void assertFieldNotNull(Response response, String jsonPath) {
        Object value = response.jsonPath().get(jsonPath);
        assertThat(value)
                .as("JSON field '%s' should not be null", jsonPath)
                .isNotNull();
    }

    /** 
     * Reusable assertion for GoRest validation errors.
     * Expects a JSON array like: [{"field": "email", "message": "can't be blank"}]
     */
    public static void assertValidationError(Response response, String expectedField, String expectedMessage) {
        // ใช้ GPath ของ REST Assured เพื่อค้นหา object ใน array ที่มี field ตรงกัน
        String actualMessage = response.jsonPath().getString("find { it.field == '" + expectedField + "' }.message");
        
        assertThat(actualMessage)
                .as("Validation message for field '%s'", expectedField)
                .isNotNull()
                .isEqualTo(expectedMessage);
    }

    /** 
     * Reusable assertion for generic error messages.
     * Expects a JSON object like: {"message": "Resource not found"}
     */
    public static void assertErrorMessage(Response response, String expectedMessage) {
        String actualMessage = response.jsonPath().getString("message");
        
        assertThat(actualMessage)
                .as("Error message")
                .isNotNull()
                .isEqualTo(expectedMessage);
    }
}

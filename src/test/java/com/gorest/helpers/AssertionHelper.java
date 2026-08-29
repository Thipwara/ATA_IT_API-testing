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

    /** Assert that the response body contains an error message. */
    public static void assertContainsMessage(Response response, String expectedFragment) {
        String body = response.body().asString();
        assertThat(body)
                .as("Response body should contain '%s'", expectedFragment)
                .containsIgnoringCase(expectedFragment);
    }
}

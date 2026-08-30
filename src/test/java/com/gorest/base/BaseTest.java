package com.gorest.base;

import com.gorest.client.UserApiClient;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base test class — sets up shared REST Assured filters and the API client.
 * All test classes should extend this.
 */
public abstract class BaseTest {

    protected static final Logger log = LoggerFactory.getLogger(BaseTest.class);
    protected static UserApiClient userApi;

    @BeforeAll
    static void globalSetup() {
        // Log every request/response to stdout for debugging (use replace to avoid duplicates across test classes)
        RestAssured.replaceFiltersWith(
                new RequestLoggingFilter(),
                new ResponseLoggingFilter()
        );

        userApi = new UserApiClient();
        log.info("=== Test suite initialised ===");
    }
}

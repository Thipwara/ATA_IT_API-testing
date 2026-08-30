package com.gorest.base;

import com.gorest.client.UserApiClient;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseTest {

    protected static final Logger log = LoggerFactory.getLogger(BaseTest.class);
    protected static UserApiClient userApi;

    @BeforeAll
    static void globalSetup() {
    
        RestAssured.replaceFiltersWith(
                new RequestLoggingFilter(),
                new ResponseLoggingFilter()
        );

        userApi = new UserApiClient();
        log.info("=== Test suite initialised ===");
    }
}

package com.gorest.client;

import com.gorest.config.ConfigManager;
import com.gorest.models.User;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserApiClient {

    private static final Logger log = LoggerFactory.getLogger(UserApiClient.class);
    private static final String USERS_PATH = "/users";

    private final String baseUrl;
    private final String token;

    public UserApiClient() {
        this.baseUrl = ConfigManager.getBaseUrl();
        this.token   = ConfigManager.getApiToken();
        log.info("UserApiClient initialised — baseUrl={}", baseUrl);
    }

    private RequestSpecification baseSpec() {
        return RestAssured.given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
    }

    private RequestSpecification authSpec() {
        return baseSpec()
                .header("Authorization", "Bearer " + token);
    }

    public Response listUsers() {
        log.info("GET {}", USERS_PATH);
        return baseSpec()
                .when()
                .get(USERS_PATH);
    }

    public Response listAllUsers() {
        log.info("GET {}", USERS_PATH);
        return authSpec()
                .when()
                .get(USERS_PATH);
    }

    public Response getUser(int userId) {
        log.info("GET {}/{}", USERS_PATH, userId);
        return authSpec()
                .pathParam("id", userId)
                .when()
                .get(USERS_PATH + "/{id}");
    }

    public Response createUser(User user) {
        log.info("POST {} — payload={}", USERS_PATH, user);
        return authSpec()
                .body(user)
                .when()
                .post(USERS_PATH);
    }

    public Response updateUser(int userId, User user) {
        log.info("PUT {}/{} — payload={}", USERS_PATH, userId, user);
        return authSpec()
                .body(user)
                .pathParam("id", userId)
                .when()
                .put(USERS_PATH + "/{id}");
    }

    public Response deleteUser(int userId) {
        log.info("DELETE {}/{}", USERS_PATH, userId);
        return authSpec()
                .pathParam("id", userId)
                .when()
                .delete(USERS_PATH + "/{id}");
    }
}

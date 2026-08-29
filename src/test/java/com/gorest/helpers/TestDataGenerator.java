package com.gorest.helpers;

import com.github.javafaker.Faker;
import com.gorest.models.User;

/**
 * Generates randomised test data so every run uses unique values
 * and avoids 422 "email already taken" errors.
 */
public final class TestDataGenerator {

    private static final Faker faker = new Faker();

    private TestDataGenerator() { }

    /** Create a User with random name, unique email, and default active/male. */
    public static User randomUser() {
        return new User(
                faker.name().fullName(),
                faker.internet().emailAddress("test_" + System.nanoTime()),
                "male",
                "active"
        );
    }

    /** Create a User with a specific gender and status. */
    public static User randomUser(String gender, String status) {
        return new User(
                faker.name().fullName(),
                faker.internet().emailAddress("test_" + System.nanoTime()),
                gender,
                status
        );
    }

    /** Return a random email guaranteed to be unique. */
    public static String uniqueEmail() {
        return faker.internet().emailAddress("test_" + System.nanoTime());
    }

    /** Return a random full name. */
    public static String randomName() {
        return faker.name().fullName();
    }
}

package com.gorest.helpers;

import com.github.javafaker.Faker;
import com.gorest.models.User;

public final class TestDataGenerator {

    private static final Faker faker = new Faker();

    private TestDataGenerator() { }

    public static User randomUser() {
        return new User(
                faker.name().fullName(),
                faker.internet().emailAddress("test_" + System.nanoTime()),
                "male",
                "active"
        );
    }

    public static User randomUser(String gender, String status) {
        return new User(
                faker.name().fullName(),
                faker.internet().emailAddress("test_" + System.nanoTime()),
                gender,
                status
        );
    }

    public static String uniqueEmail() {
        return faker.internet().emailAddress("test_" + System.nanoTime());
    }
    public static String randomName() {
        return faker.name().fullName();
    }
}

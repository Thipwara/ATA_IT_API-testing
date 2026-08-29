package com.gorest.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Centralized configuration loader.
 * Priority: System property > .env file > default value.
 */
public final class ConfigManager {

    private static final Logger log = LoggerFactory.getLogger(ConfigManager.class);
    private static final Properties props = new Properties();

    static {
        loadEnvFile();
    }

    private ConfigManager() { }

    private static void loadEnvFile() {
        Path envPath = Path.of(".env");
        if (Files.exists(envPath)) {
            try (InputStream is = Files.newInputStream(envPath)) {
                props.load(is);
                log.info("Loaded .env file from project root");
            } catch (IOException e) {
                log.warn("Failed to load .env file: {}", e.getMessage());
            }
        } else {
            log.info(".env file not found — falling back to system properties / defaults");
        }
    }

    public static String get(String key) {
        // System property takes highest priority
        String value = System.getProperty(key);
        if (value != null) return value;

        // Then .env
        value = props.getProperty(key);
        if (value != null) return value;

        // Then environment variable
        return System.getenv(key);
    }

    public static String get(String key, String defaultValue) {
        String value = get(key);
        return value != null ? value : defaultValue;
    }

    public static String getBaseUrl() {
        return get("BASE_URL", "https://gorest.co.in/public/v2");
    }

    public static String getApiToken() {
        return get("GOREST_API_TOKEN", "");
    }
}

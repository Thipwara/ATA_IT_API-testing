package com.gorest.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * POJO representing a GoRest User resource.
 * Used for both request payloads and response deserialization.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

    private Integer id;
    private String name;
    private String email;
    private String gender;   // "male" | "female"
    private String status;   // "active" | "inactive"

    // ── Constructors ──────────────────────────────────────────

    public User() { }

    public User(String name, String email, String gender, String status) {
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.status = status;
    }

    // ── Getters / Setters ─────────────────────────────────────

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "User{id=%d, name='%s', email='%s', gender='%s', status='%s'}"
                .formatted(id, name, email, gender, status);
    }
}

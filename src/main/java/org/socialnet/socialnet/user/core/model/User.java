package org.socialnet.socialnet.user.core.model;

import java.time.Instant;

public class User {
    private String id;
    private String username;
    private String email;
    private int onboardingStep = 1;
    private Instant createdAt;
    private Instant updatedAt;

    private User(String id, String username, String email, int onboardingStep, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.onboardingStep = onboardingStep;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static User of(String id, String username, String email, int onboardingStep, Instant createdAt, Instant updatedAt) {
        return new User(id, username, email, onboardingStep, createdAt, updatedAt);
    }

    public String getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getOnboardingStep() {
        return onboardingStep;
    }

    public void setOnboardingStep(int onboardingStep) {
        this.onboardingStep = onboardingStep;
    }
}

package org.socialnet.socialnet.auth.core.model;

import java.util.Set;

public class AuthenticatedUser {
    private final String id;
    private final String email;
    private final String login;
    private final Set<String> roles;
    private AuthenticatedUser(String id, String email, String login, Set<String> roles) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.roles = roles;
    }

    public static AuthenticatedUser create(String id, String email, String login, Set<String> roles) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or blank.");
        }
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address.");
        }
        return new AuthenticatedUser(id, email, login, roles);
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public String getLogin() {
        return login;
    }
}

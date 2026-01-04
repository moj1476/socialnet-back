package org.socialnet.socialnet.user.core.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public class Profile {
    private String id;
    private String firstName;
    private String lastName;
    private String bio;
    private String avatarUrl;
    private String city;
    private LocalDate birthDate;
    private String workOrStudy;
    private List<String> interests;

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getWorkOrStudy() {
        return workOrStudy;
    }

    public void setWorkOrStudy(String workOrStudy) {
        this.workOrStudy = workOrStudy;
    }

    private Instant createdAt;
    private Instant updatedAt;

    private Profile(String id, String firstName, String lastName, String bio, String avatarUrl, List<String> interests, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.interests = interests;
    }

    public Profile() {

    }

    public static Profile of(String id, String firstName, String lastName, String bio, String avatarUrl, List<String> interests, Instant createdAt, Instant updatedAt) {
        return new Profile(id, firstName, lastName, bio, avatarUrl, interests, createdAt, updatedAt);
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getBio() {
        return bio;
    }

    public String getAvatarUrl() {
        return avatarUrl;
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

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}

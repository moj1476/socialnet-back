package org.socialnet.socialnet.user.core.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record CurrentProfile(
        String id,
        String firstName,
        String lastName,
        String bio,
        String avatarUrl,
        String city,
        LocalDate birthDate,
        String workOrStudy,
        List<String> interests,
        Integer likes,
        Integer friends,
        Integer posts,
        Boolean isOnline,
        String username,
        Instant createdAt,
        Instant updatedAt
) {
}

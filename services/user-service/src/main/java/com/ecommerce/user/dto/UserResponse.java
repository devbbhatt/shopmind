
package com.ecommerce.user.dto;

import com.ecommerce.user.entity.UserRole;
import java.time.Instant;

public class UserResponse {

    private Long id;
    private String fullName;
    private String email;
    private UserRole role;
    private boolean enabled;
    private Instant createdAt;

    public UserResponse(
            Long id,
            String fullName,
            String email,
            UserRole role,
            boolean enabled,
            Instant createdAt) {

        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.enabled = enabled;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}

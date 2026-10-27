package com.ecommerce.user.dto;

import java.time.LocalDateTime;

public class UserPreferenceResponse {

    private Long userId;
    private String preferredCurrency;
    private String preferredLanguage;
    private boolean emailNotifications;
    private boolean marketingEmails;
    private LocalDateTime updatedAt;

    public UserPreferenceResponse(
            Long userId,
            String preferredCurrency,
            String preferredLanguage,
            boolean emailNotifications,
            boolean marketingEmails,
            LocalDateTime updatedAt) {
        this.userId = userId;
        this.preferredCurrency = preferredCurrency;
        this.preferredLanguage = preferredLanguage;
        this.emailNotifications = emailNotifications;
        this.marketingEmails = marketingEmails;
        this.updatedAt = updatedAt;
    }

    public Long getUserId() {
        return userId;
    }

    public String getPreferredCurrency() {
        return preferredCurrency;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public boolean isEmailNotifications() {
        return emailNotifications;
    }

    public boolean isMarketingEmails() {
        return marketingEmails;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
package com.ecommerce.user.service;

import com.ecommerce.user.dto.UpdatePreferenceRequest;
import com.ecommerce.user.dto.UserPreferenceResponse;
import com.ecommerce.user.entity.User;
import com.ecommerce.user.entity.UserPreference;
import com.ecommerce.user.repository.UserPreferenceRepository;
import com.ecommerce.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class UserPreferenceService {

    private final UserPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    public UserPreferenceService(
            UserPreferenceRepository preferenceRepository,
            UserRepository userRepository) {
        this.preferenceRepository = preferenceRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserPreferenceResponse getPreferences(String email) {
        User user = findUser(email);

        UserPreference preference = preferenceRepository
                .findByUserId(user.getId())
                .orElse(null);

        if (preference == null) {
            return new UserPreferenceResponse(
                    user.getId(), "INR", "en",
                    true, false, null
            );
        }

        return toResponse(preference);
    }

    @Transactional
    public UserPreferenceResponse updatePreferences(
            String email,
            UpdatePreferenceRequest request) {

        User user = findUser(email);

        UserPreference preference = preferenceRepository
                .findByUserId(user.getId())
                .orElseGet(() -> {
                    UserPreference newPreference = new UserPreference();
                    newPreference.setUser(user);
                    return newPreference;
                });

        preference.setPreferredCurrency(
                request.getPreferredCurrency()
                        .trim().toUpperCase(Locale.ROOT));

        preference.setPreferredLanguage(
                request.getPreferredLanguage().trim());

        preference.setEmailNotifications(
                request.isEmailNotifications());

        preference.setMarketingEmails(
                request.isMarketingEmails());

        return toResponse(preferenceRepository.save(preference));
    }

    private User findUser(String email) {
        return userRepository.findByEmail(
                email.trim().toLowerCase(Locale.ROOT)
        ).orElseThrow(() ->
                new IllegalArgumentException("User not found"));
    }

    private UserPreferenceResponse toResponse(
            UserPreference preference) {

        return new UserPreferenceResponse(
                preference.getUser().getId(),
                preference.getPreferredCurrency(),
                preference.getPreferredLanguage(),
                preference.isEmailNotifications(),
                preference.isMarketingEmails(),
                preference.getUpdatedAt()
        );
    }
}
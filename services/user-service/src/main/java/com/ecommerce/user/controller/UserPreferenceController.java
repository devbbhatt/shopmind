package com.ecommerce.user.controller;

import com.ecommerce.user.dto.UpdatePreferenceRequest;
import com.ecommerce.user.dto.UserPreferenceResponse;
import com.ecommerce.user.service.UserPreferenceService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/me/preferences")
public class UserPreferenceController {

    private final UserPreferenceService preferenceService;

    public UserPreferenceController(
            UserPreferenceService preferenceService) {
        this.preferenceService = preferenceService;
    }

    @GetMapping
    public UserPreferenceResponse getPreferences(
            Authentication authentication) {

        return preferenceService.getPreferences(
                authentication.getName());
    }

    @PutMapping
    public UserPreferenceResponse updatePreferences(
            Authentication authentication,
            @Valid @RequestBody UpdatePreferenceRequest request) {

        return preferenceService.updatePreferences(
                authentication.getName(), request);
    }
}
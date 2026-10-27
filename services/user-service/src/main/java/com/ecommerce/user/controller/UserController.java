package com.ecommerce.user.controller;

import com.ecommerce.user.dto.CreateUserRequest;
import com.ecommerce.user.dto.UpdateUserRequest;
import com.ecommerce.user.dto.UserResponse;
import com.ecommerce.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.ecommerce.user.dto.ChangePasswordRequest;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Get the currently authenticated user's profile
    @GetMapping("/me")
    public UserResponse getMyProfile(Authentication authentication) {
        return userService.getUserByEmail(authentication.getName());
    }

    // Update only the currently authenticated user's profile
    @PutMapping("/me")
    public UserResponse updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateUserRequest request) {

        UserResponse currentUser =
                userService.getUserByEmail(authentication.getName());

        return userService.updateUser(currentUser.getId(), request);
    }

    // Public registration
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(
            @Valid @RequestBody CreateUserRequest request) {
        return userService.registerUser(request);
    }

    // Admin: fetch a specific user
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // Admin: list users
    @GetMapping
    public Page<UserResponse> getUsers(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return userService.getUsers(pageable);
    }

    // Admin: search by email
    @GetMapping("/by-email")
    public UserResponse getUserByEmail(@RequestParam String email) {
        return userService.getUserByEmail(email);
    }

    // Admin: enable or disable an account
    @PatchMapping("/{id}/status")
    public UserResponse updateUserStatus(
            @PathVariable Long id,
            @RequestParam boolean enabled) {
        return userService.updateUserStatus(id, enabled);
    }

    // Admin: update another user's profile
    @PutMapping("/{id}")
    public UserResponse updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        return userService.updateUser(id, request);
    }

    @PatchMapping("/me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {

        userService.changePassword(
                authentication.getName(), request);
    }
}
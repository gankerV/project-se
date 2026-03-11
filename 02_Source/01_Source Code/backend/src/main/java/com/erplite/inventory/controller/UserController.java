package com.erplite.inventory.controller;

import com.erplite.inventory.dto.common.ApiResponse;
import com.erplite.inventory.dto.common.PagedResponse;
import com.erplite.inventory.dto.user.*;
import com.erplite.inventory.entity.User.UserRole;
import com.erplite.inventory.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<PagedResponse<UserResponse>> listUsers(
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) Boolean isActive,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userService.listUsers(role, isActive, pageable));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(userService.getUserByJwtSub(jwt.getSubject()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<UserResponse> getUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable String id, @Valid @RequestBody UserUpdateRequest req) {
        return ResponseEntity.ok(userService.updateUser(id, req));
    }

    @PatchMapping("/{id}/password")
    @PreAuthorize("hasRole('Admin') or #id == authentication.token.claims['sub']")
    public ResponseEntity<ApiResponse> changePassword(
            @PathVariable String id,
            @Valid @RequestBody PasswordChangeRequest req,
            @AuthenticationPrincipal Jwt jwt) {
        boolean isAdmin = jwt.getClaim("realm_access") instanceof Map<?, ?> ra
            && ra.get("roles") instanceof List<?> roles && roles.contains("Admin");
        userService.changePassword(id, req, isAdmin);
        return ResponseEntity.ok(new ApiResponse("Password updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Void> deactivateUser(
            @PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        userService.deactivateUser(id, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/activity")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<?> getUserActivity(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserActivity(id));
    }
}

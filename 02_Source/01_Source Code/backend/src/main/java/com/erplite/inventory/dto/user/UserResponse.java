package com.erplite.inventory.dto.user;

import com.erplite.inventory.entity.User;
import com.erplite.inventory.entity.User.UserRole;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {

    private String userId;
    private String username;
    private String email;
    private UserRole role;
    private Boolean isActive;
    private LocalDateTime lastLogin;
    private LocalDateTime createdDate;

    public static UserResponse from(User user) {
        UserResponse dto = new UserResponse();
        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setIsActive(user.getIsActive());
        dto.setLastLogin(user.getLastLogin());
        dto.setCreatedDate(user.getCreatedDate());
        return dto;
    }
}

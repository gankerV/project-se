package com.erplite.inventory.dto.user;

import com.erplite.inventory.entity.User.UserRole;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserUpdateRequest {

    @NotBlank(message = "Username is required")
    @Size(max = 50)
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100)
    private String email;

    @NotNull(message = "Role is required")
    private UserRole role;

    private Boolean isActive;
}

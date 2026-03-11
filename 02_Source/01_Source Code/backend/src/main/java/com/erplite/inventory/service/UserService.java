package com.erplite.inventory.service;

import com.erplite.inventory.dto.common.PagedResponse;
import com.erplite.inventory.dto.user.*;
import com.erplite.inventory.entity.User;
import com.erplite.inventory.entity.User.UserRole;
import com.erplite.inventory.exception.BusinessException;
import com.erplite.inventory.exception.ResourceNotFoundException;
import com.erplite.inventory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public PagedResponse<UserResponse> listUsers(UserRole role, Boolean isActive, Pageable pageable) {
        Page<User> page;
        if (role != null && isActive != null) {
            page = userRepository.findByRoleAndIsActive(role, isActive, pageable);
        } else if (role != null) {
            page = userRepository.findByRole(role, pageable);
        } else if (isActive != null) {
            page = userRepository.findByIsActive(isActive, pageable);
        } else {
            page = userRepository.findAll(pageable);
        }
        return PagedResponse.from(page.map(UserResponse::from));
    }

    public UserResponse getUserByJwtSub(String sub) {
        return UserResponse.from(userRepository.findById(sub)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", sub)));
    }

    public UserResponse getUserById(String id) {
        return UserResponse.from(findUserOrThrow(id));
    }

    @Transactional
    public UserResponse createUser(UserCreateRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new BusinessException("Username already exists: " + req.getUsername());
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new BusinessException("Email already exists: " + req.getEmail());
        }
        User user = User.builder()
            .username(req.getUsername())
            .email(req.getEmail())
            .password(req.getPassword())
            .role(req.getRole())
            .isActive(true)
            .build();
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateUser(String id, UserUpdateRequest req) {
        User user = findUserOrThrow(id);
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setRole(req.getRole());
        if (req.getIsActive() != null) {
            user.setIsActive(req.getIsActive());
        }
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public void changePassword(String id, PasswordChangeRequest req, boolean isAdmin) {
        User user = findUserOrThrow(id);
        if (!isAdmin && req.getCurrentPassword() != null) {
            if (!req.getCurrentPassword().equals(user.getPassword())) {
                throw new BusinessException("Current password is incorrect");
            }
        }
        user.setPassword(req.getNewPassword());
        userRepository.save(user);
    }

    @Transactional
    public void deactivateUser(String id, String requestorSub) {
        User user = findUserOrThrow(id);
        if (id.equals(requestorSub)) {
            throw new BusinessException("Cannot deactivate your own account");
        }
        user.setIsActive(false);
        userRepository.save(user);
    }

    public List<?> getUserActivity(String id) {
        findUserOrThrow(id);
        return List.of();
    }

    private User findUserOrThrow(String id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }
}

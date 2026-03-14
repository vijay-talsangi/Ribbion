package com.mpj.ribbion.service;

import com.mpj.ribbion.dto.*;
import com.mpj.ribbion.entity.User;
import com.mpj.ribbion.exception.ResourceNotFoundException;
import com.mpj.ribbion.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", userDetails.getUsername()));
    }

    public UserProfileResponse getProfile(UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return AuthService.mapToProfileResponse(user);
    }

    public UserProfileResponse getProfileById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return AuthService.mapToProfileResponse(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(UserDetails userDetails, UpdateProfileRequest request) {
        User user = getCurrentUser(userDetails);

        if (request.getDisplayName() != null) {
            user.setDisplayName(request.getDisplayName());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        user = userRepository.save(user);
        return AuthService.mapToProfileResponse(user);
    }
}

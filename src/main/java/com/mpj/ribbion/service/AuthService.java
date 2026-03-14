package com.mpj.ribbion.service;

import com.mpj.ribbion.dto.*;
import com.mpj.ribbion.entity.User;
import com.mpj.ribbion.exception.DuplicateResourceException;
import com.mpj.ribbion.exception.ResourceNotFoundException;
import com.mpj.ribbion.repository.UserRepository;
import com.mpj.ribbion.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email is already registered");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .displayName(request.getDisplayName() != null ? request.getDisplayName() : request.getUsername())
                .build();

        user = userRepository.save(user);

        String token = tokenProvider.generateToken(user.getUsername());

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .user(mapToProfileResponse(user))
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsernameOrEmail(), request.getPassword()));

        String token = tokenProvider.generateToken(authentication);

        User user = userRepository.findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "usernameOrEmail", request.getUsernameOrEmail()));

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .user(mapToProfileResponse(user))
                .build();
    }

    public static UserProfileResponse mapToProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .reputation(user.getReputation())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public static UserSummary mapToUserSummary(User user) {
        return UserSummary.builder()
                .id(user.getId())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .avatarUrl(user.getAvatarUrl())
                .reputation(user.getReputation())
                .build();
    }
}

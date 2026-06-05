package com.exemplo.taskmanager.service;

import com.exemplo.taskmanager.dto.auth.AuthResponse;
import com.exemplo.taskmanager.dto.auth.LoginRequest;
import com.exemplo.taskmanager.dto.auth.RefreshTokenRequest;
import com.exemplo.taskmanager.dto.auth.RegisterRequest;
import com.exemplo.taskmanager.dto.user.ChangePasswordRequest;
import com.exemplo.taskmanager.dto.user.UpdateUserRequest;
import com.exemplo.taskmanager.dto.user.UserResponse;
import com.exemplo.taskmanager.exception.EmailAlreadyExistsException;
import com.exemplo.taskmanager.exception.InvalidPasswordException;
import com.exemplo.taskmanager.exception.InvalidTokenException;
import com.exemplo.taskmanager.exception.ResourceNotFoundException;
import com.exemplo.taskmanager.model.BlacklistedToken;
import com.exemplo.taskmanager.model.RefreshToken;
import com.exemplo.taskmanager.model.User;
import com.exemplo.taskmanager.repository.BlacklistedTokenRepository;
import com.exemplo.taskmanager.repository.RefreshTokenRepository;
import com.exemplo.taskmanager.repository.UserRepository;
import com.exemplo.taskmanager.security.JwtUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    // ─── Register ────────────────────────────────────────────

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registerd");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);
        return generateAuthResponse(user);
    }

    // ─── Login ───────────────────────────────────────────────
    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        refreshTokenRepository.deleteByUser(user);

        return generateAuthResponse(user);
    }

    // ─── Logout ──────────────────────────────────────────────
    @Transactional
    public void logout(String accessToken, User user) {
        blacklistedTokenRepository.save(
                BlacklistedToken.builder()
                        .token(accessToken)
                        .expiresAt(LocalDateTime.ofInstant(
                                jwtUtil.extractExpiration(accessToken).toInstant(),
                                ZoneId.systemDefault()))
                        .build()
        );

        refreshTokenRepository.deleteByUser(user);
    }

    // ─── See Profile ──────────────────────────────────────────

    public UserResponse getProfile(User user) {
        return toUserResponse(user);
    }

    // ─── Update Profile ───────────────────────────────────────

    public UserResponse updateProfile(User user, UpdateUserRequest request) {
        if (request.getName() != null) user.setName(request.getName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        userRepository.save(user);
        return toUserResponse(user);
    } 

    // ─── Change Password ────────────────────────────────────

    public void changePassword(User user, ChangePasswordRequest request) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Password atual incorreta");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    // ─── Delete Account ────────────────────────────────────────

    public void deleteAccount(User user, String accessToken) {
        logout(accessToken, user);
        userRepository.delete(user);
    }

    // ─── Refresh Token ───────────────────────────────────────

    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(request.getRefreshToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        if (refreshToken.isRevoked()) {
            throw new InvalidTokenException("Refresh token revoked");
        }

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Refresh token expired");
        }

        User user = refreshToken.getUser();

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        return generateAuthResponse(user);
    }

    // ─── Auxiliar Methods ──────────────────────────────────

    private AuthResponse generateAuthResponse(User user) {
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshTokenStr = jwtUtil.generateRefreshToken(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenStr)
                .user(user)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(
                accessToken,
                refreshTokenStr,
                user.getName(),
                user.getEmail()
        );
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}
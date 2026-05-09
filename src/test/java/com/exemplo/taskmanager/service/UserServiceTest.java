package com.exemplo.taskmanager.service;

import com.exemplo.taskmanager.dto.auth.AuthResponse;
import com.exemplo.taskmanager.dto.user.UserResponse;
import com.exemplo.taskmanager.dto.auth.LoginRequest;
import com.exemplo.taskmanager.dto.user.UpdateUserRequest;
import com.exemplo.taskmanager.dto.user.ChangePasswordRequest;
import com.exemplo.taskmanager.dto.auth.RegisterRequest;
import com.exemplo.taskmanager.dto.auth.RefreshTokenRequest;
import com.exemplo.taskmanager.model.RefreshToken;
import com.exemplo.taskmanager.model.User;
import com.exemplo.taskmanager.repository.BlacklistedTokenRepository;
import com.exemplo.taskmanager.repository.RefreshTokenRepository;
import com.exemplo.taskmanager.repository.UserRepository;
import com.exemplo.taskmanager.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.exemplo.taskmanager.model.BlacklistedToken;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private BlacklistedTokenRepository blacklistedTokenRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Alice")
                .email("alice@example.com")
                .password("hashed_password")
                .build();
    }

    // ─── Register ────────────────────────────────────────────

    // Should return auth response when email is new
    @Test
    void registerSuccess() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Alice");
        request.setEmail("alice@example.com");
        request.setPassword("Password123");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtUtil.generateAccessToken(any())).thenReturn("access_token");
        when(jwtUtil.generateRefreshToken(any())).thenReturn("refresh_token");

        AuthResponse response = userService.register(request);

        assertThat(response.getAccessToken()).isEqualTo("access_token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh_token");
        assertThat(response.getName()).isEqualTo("Alice");
        verify(userRepository).save(any(User.class));
    }

    // Should throw exception when email is already registered
    @Test
    void registerDuplicatedEmail() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Alice");
        request.setEmail("alice@example.com");
        request.setPassword("Password123");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email");

        verify(userRepository, never()).save(any());
    }

    // ─── Login ───────────────────────────────────────────────

    // Should return auth response when credentials are valid
    @Test
    void loginSuccess() {
        LoginRequest request = new LoginRequest();
        request.setEmail("alice@example.com");
        request.setPassword("password123");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(jwtUtil.generateAccessToken(any())).thenReturn("access_token");
        when(jwtUtil.generateRefreshToken(any())).thenReturn("refresh_token");

        AuthResponse response = userService.login(request);

        assertThat(response.getAccessToken()).isEqualTo("access_token");
        verify(refreshTokenRepository).deleteByUser(user);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    // Should throw exception when credentials are wrong
    @Test
    void loginWrongCredentials() {
        LoginRequest request = new LoginRequest();
        request.setEmail("alice@example.com");
        request.setPassword("password123");

        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager).authenticate(any());

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(BadCredentialsException.class);

        verify(userRepository, never()).findByEmail(any());
    }

    // ─── Logout ───────────────────────────────────────────────

    // Should blacklist the token and delete all refresh tokens for the user
    @Test
    void logoutSuccess() {
        String access_token = "FakeTestToken123";

        when(jwtUtil.extractExpiration(access_token)).thenReturn(new Date());

        userService.logout(access_token, user);

        verify(blacklistedTokenRepository).save(any(BlacklistedToken.class));
        verify(refreshTokenRepository).deleteByUser(user);
    }

    // ─── Get Profile ───────────────────────────────────────────────

    // Should return the user profile
    @Test
    void getProfileSuccess() {
        UserResponse response = userService.getProfile(user);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Alice");
        assertThat(response.getEmail()).isEqualTo("alice@example.com");
    }

    // ─── Update Profile ───────────────────────────────────────────────

    // Should update name and email and save the user
    @Test
    void updateProfileSuccess() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Teste");
        request.setEmail("teste@gmail.com");

        UserResponse response = userService.updateProfile(user, request);

        assertThat(response.getName()).isEqualTo("Teste");
        assertThat(response.getEmail()).isEqualTo("teste@gmail.com");

        verify(userRepository).save(any(User.class));
    }

    // Should update only the name and save the user
    @Test
    void updateProfileNameOnly() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Teste");

        UserResponse response = userService.updateProfile(user, request);

        assertThat(response.getName()).isEqualTo("Teste");
        assertThat(response.getEmail()).isEqualTo("alice@example.com");

        verify(userRepository).save(any(User.class));
    }

    // Should update only the email and save the user
    @Test
    void updateProfileEmailOnly() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("teste@gmail.com");

        UserResponse response = userService.updateProfile(user, request);

        assertThat(response.getEmail()).isEqualTo("teste@gmail.com");
        assertThat(response.getName()).isEqualTo("Alice");

        verify(userRepository).save(any(User.class));
    }

    // ─── Change Password ───────────────────────────────────────────────

    // Should encode and save the new password
    @Test
    void changePasswordSuccess() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("hashed_password");
        request.setNewPassword("ThisIsATest");

        when(passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(request.getNewPassword())).thenReturn("ThisIsATest");

        userService.changePassword(user, request);

        verify(userRepository).save(any(User.class));
    }

    // Should throw exception when current password is wrong
    @Test
    void ChangePasswordWrongCurrentPasswrod() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("WrongPassword");
        request.setNewPassword("ThisIsATest");

        when(passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> userService.changePassword(user, request)).isInstanceOf(RuntimeException.class).hasMessageContaining("Password");

        verify(userRepository, never()).save(any());
    }

    // ─── Delete Account ───────────────────────────────────────────────

    // Should blacklist the token, delete refresh tokens and delete the user
    @Test
    void deleteAccountSuccess() {
        String access_token = "FakeTestToken123";

        when(jwtUtil.extractExpiration(access_token)).thenReturn(new Date());

        userService.deleteAccount(user, access_token);

        verify(blacklistedTokenRepository).save(any(BlacklistedToken.class));
        verify(refreshTokenRepository).deleteByUser(user);

        verify(userRepository).delete(user);
    }

    // ─── Refresh Token ───────────────────────────────────────────────
    
    @Test
    void refreshSuccess() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("RefreshToken");
        refreshToken.setRevoked(false);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7));
        refreshToken.setUser(user);

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("RefreshToken");

        when(refreshTokenRepository.findByToken("RefreshToken")).thenReturn(Optional.of(refreshToken));

        userService.refresh(request);

        assertTrue(refreshToken.isRevoked());
        verify(refreshTokenRepository).save(refreshToken);
    }

    @Test
    void refreshGetTokenFailure() {
         RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("RefreshToken");

        when(refreshTokenRepository.findByToken("RefreshToken")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.refresh(request)).isInstanceOf(RuntimeException.class).hasMessageContaining("Invalid refresh token");
    }

    @Test
    void refreshTokenRevokedFailure() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("RefreshToken");
        refreshToken.setRevoked(true);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7));
        refreshToken.setUser(user);

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("RefreshToken");

        when(refreshTokenRepository.findByToken("RefreshToken")).thenReturn(Optional.of(refreshToken));

        assertThatThrownBy(() -> userService.refresh(request)).isInstanceOf(RuntimeException.class).hasMessageContaining("Refresh token revoked");
    }

    // Expires at, refresh token error
    @Test
    void refreshTokenExpiredFailure() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("RefreshToken");
        refreshToken.setRevoked(false);
        refreshToken.setExpiresAt(LocalDateTime.now().minusDays(1));
        refreshToken.setUser(user);

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("RefreshToken");

        when(refreshTokenRepository.findByToken("RefreshToken")).thenReturn(Optional.of(refreshToken));

        assertThatThrownBy(() -> userService.refresh(request)).isInstanceOf(RuntimeException.class).hasMessageContaining("Refresh token expired");
    }
}
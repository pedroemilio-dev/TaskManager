package com.exemplo.taskmanager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.exemplo.taskmanager.model.User;
import com.exemplo.taskmanager.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserDetailServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsServiceImpl;

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
    
    // Should return the user when found
    @Test
    void loadUserByUsernameSuccess() {
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));

        UserDetails result = userDetailsServiceImpl.loadUserByUsername("alice@example.com");

        assertThat(result.getUsername()).isEqualTo("alice@example.com");

        verify(userRepository).findByEmail("alice@example.com");
    }

    // Should throw exception when user is not found
    @Test
    void loadUserByUsernameNotFoundFailure() {
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsServiceImpl.loadUserByUsername("alice@example.com")).isInstanceOf(UsernameNotFoundException.class).hasMessageContaining("User not found");

        verify(userRepository).findByEmail("alice@example.com");
    }
}

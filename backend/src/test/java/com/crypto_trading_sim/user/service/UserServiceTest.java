package com.crypto_trading_sim.user.service;

import com.crypto_trading_sim.user.domain.model.AppRole;
import com.crypto_trading_sim.user.domain.model.User;
import com.crypto_trading_sim.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "password", "Test", "User", AppRole.USER);
    }

    @Test
    void save_whenCalled_shouldReturnSavedUser() {
        when(userRepository.insert(any(User.class))).thenReturn(user);

        User savedUser = userService.save(user);

        assertEquals(user, savedUser);
    }

    @Test
    void findByUsername_whenUserExists_shouldReturnUser() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.findByUsername("testuser");

        assertTrue(foundUser.isPresent());
        assertEquals(user, foundUser.get());
    }

    @Test
    void findByUsername_whenUserDoesntExist_shouldReturnEmpty() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        Optional<User> foundUser = userService.findByUsername("testuser");

        assertTrue(foundUser.isEmpty());
    }
}
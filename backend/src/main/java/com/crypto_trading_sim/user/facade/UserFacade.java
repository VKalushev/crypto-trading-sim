package com.crypto_trading_sim.user.facade;

import com.crypto_trading_sim.common.exception.AlreadyExistException;
import com.crypto_trading_sim.user.domain.dto.CreateUserRequest;
import com.crypto_trading_sim.user.domain.dto.UserDto;
import com.crypto_trading_sim.user.domain.mapper.UserMapper;
import com.crypto_trading_sim.user.domain.model.User;
import com.crypto_trading_sim.user.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserFacade {
    private final UserService userService;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;

    public UserFacade(UserService userService, PasswordEncoder encoder, UserMapper userMapper) {
        this.userService = userService;
        this.encoder = encoder;
        this.userMapper = userMapper;
    }

    public UserDto createUser(CreateUserRequest request) {
        if (userService.findByUsername(request.username()).isPresent()) {
            throw new AlreadyExistException("User with this username already exists!");
        }

        User user = new User(request.username(), encoder.encode(request.password()), request.firstName(), request.lastName(),request.role());
        return userMapper.toDto(userService.save(user));
    }
}
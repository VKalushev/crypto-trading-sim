package com.crypto_trading_sim.user.facade;

import com.crypto_trading_sim.common.exception.AlreadyExistException;
import com.crypto_trading_sim.common.exception.NotFoundException;
import com.crypto_trading_sim.user.domain.dto.CreateUserRequest;
import com.crypto_trading_sim.user.domain.dto.UserDto;
import com.crypto_trading_sim.user.domain.mapper.UserMapper;
import com.crypto_trading_sim.user.domain.model.AppRole;
import com.crypto_trading_sim.user.domain.model.User;
import com.crypto_trading_sim.user.service.UserService;
import com.crypto_trading_sim.wallet.domain.mapper.WalletMapper;
import com.crypto_trading_sim.wallet.domain.model.Wallet;
import com.crypto_trading_sim.wallet.service.WalletService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserFacade {
    private final UserService userService;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;
    private final WalletService walletService;
    private final WalletMapper walletMapper;

    public UserFacade(UserService userService, PasswordEncoder encoder, UserMapper userMapper, WalletService walletService, WalletMapper walletMapper) {
        this.userService = userService;
        this.encoder = encoder;
        this.userMapper = userMapper;
        this.walletService = walletService;
        this.walletMapper = walletMapper;
    }

    public UserDto createUser(CreateUserRequest request) {
        if (userService.findByUsername(request.username()).isPresent()) {
            throw new AlreadyExistException("User with this username already exists!");
        }

        User user = new User(request.username(), encoder.encode(request.password()), request.firstName(), request.lastName(), AppRole.USER);
        User savedUser = userService.save(user);
        Wallet wallet = walletService.createWallet(savedUser.getId());
        UserDto userDto = userMapper.toDto(savedUser);
        userDto.setWallet(walletMapper.toDto(wallet));
        return userDto;
    }

    public UserDto getUserByUsername(String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Wallet wallet = walletService.getWalletByUserId(user.getId());
        UserDto userDto = userMapper.toDto(user);
        userDto.setWallet(walletMapper.toDto(wallet));
        return userDto;
    }
}
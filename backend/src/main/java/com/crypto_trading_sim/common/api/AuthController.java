package com.crypto_trading_sim.common.api;

import com.crypto_trading_sim.common.facade.AuthenticationFacade;
import com.crypto_trading_sim.user.domain.dto.CreateUserRequest;
import com.crypto_trading_sim.user.domain.dto.LoginRequest;
import com.crypto_trading_sim.user.domain.dto.UserDto;
import com.crypto_trading_sim.user.facade.UserFacade;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationFacade authenticationFacade;
    private final UserFacade userFacade;

    public AuthController(AuthenticationFacade authenticationFacade, UserFacade userFacade) {
        this.authenticationFacade = authenticationFacade;
        this.userFacade = userFacade;
    }

    @PostMapping("/login")
    public ResponseEntity<String> userAuthentication(@RequestBody LoginRequest loginRequest) {
        return authenticationFacade.userAuthentication(loginRequest);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        UserDto user = userFacade.createUser(createUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}
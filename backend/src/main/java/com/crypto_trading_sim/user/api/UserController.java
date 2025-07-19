package com.crypto_trading_sim.user.api;

import com.crypto_trading_sim.security.jwt.JwtUtils;
import com.crypto_trading_sim.user.domain.dto.CreateUserRequest;
import com.crypto_trading_sim.user.domain.dto.LoginRequest;
import com.crypto_trading_sim.user.domain.dto.UserDto;
import com.crypto_trading_sim.user.facade.UserFacade;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class UserController {

    private final JwtUtils jwtUtils;

    private final AuthenticationManager authenticationManager;

    private final UserFacade userFacade;

    public UserController(JwtUtils jwtUtils, AuthenticationManager authenticationManager, UserFacade userFacade) {
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.userFacade = userFacade;
    }

    @PostMapping("/login")
    public ResponseEntity<String> userAuthentication(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String token = jwtUtils.generateTokenFromUserDetails(userDetails);
            return ResponseEntity.ok(token);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        UserDto user = userFacade.createUser(createUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}

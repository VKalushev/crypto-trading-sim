package com.crypto_trading_sim.user.api;

import com.crypto_trading_sim.security.user.CustomUserDetails;
import com.crypto_trading_sim.user.domain.dto.UserDto;
import com.crypto_trading_sim.user.facade.UserFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserFacade userFacade;

    public UserController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @GetMapping
    public ResponseEntity<UserDto> getUser(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(userFacade.getUserByUsername(user.getUsername()));
    }
}
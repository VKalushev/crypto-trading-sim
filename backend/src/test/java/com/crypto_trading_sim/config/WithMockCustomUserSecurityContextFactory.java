package com.crypto_trading_sim.config;

import com.crypto_trading_sim.security.user.CustomUserDetails;
import com.crypto_trading_sim.user.domain.model.AppRole;
import com.crypto_trading_sim.user.domain.model.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.UUID;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(customUser.username());
        user.setPassword("password");
        user.setRole(AppRole.USER);

        CustomUserDetails principal = CustomUserDetails.build(user);

        Authentication auth =
                new UsernamePasswordAuthenticationToken(principal, "password", principal.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }
}
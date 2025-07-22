package com.crypto_trading_sim.user.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(@Email String username,
                                @Size(min = 4, max = 120) String password,
                                String firstName,
                                String lastName) {
}
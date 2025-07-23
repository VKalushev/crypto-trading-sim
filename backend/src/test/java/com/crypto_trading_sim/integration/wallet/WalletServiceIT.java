package com.crypto_trading_sim.integration.wallet;

import com.crypto_trading_sim.integration.EmbeddedPostgresTest;
import com.crypto_trading_sim.user.domain.dto.CreateUserRequest;
import com.crypto_trading_sim.user.domain.dto.UserDto;
import com.crypto_trading_sim.user.domain.model.User;
import com.crypto_trading_sim.user.facade.UserFacade;
import com.crypto_trading_sim.user.service.UserService;
import com.crypto_trading_sim.wallet.domain.model.Wallet;
import com.crypto_trading_sim.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedPostgresTest
class WalletServiceIT {

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserFacade userFacade;

    private UserDto testUser;
    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        var request = new CreateUserRequest("walletuser@example.com", "password", "Wallet", "User");
        testUser = userFacade.createUser(request);
    }

    @Test
    void getWalletByUserId_whenUserExists_shouldReturnWallet() {
        User user = userService.findByUsername(testUser.getUsername()).get();

        Wallet wallet = walletService.getWalletByUserId(user.getId());

        assertThat(wallet).isNotNull();
        assertThat(wallet.getAssets()).isNotNull();
        assertThat(wallet.getAssets()).isEmpty();
        assertThat(wallet.getBalance()).isEqualByComparingTo("10000.00");
    }
}

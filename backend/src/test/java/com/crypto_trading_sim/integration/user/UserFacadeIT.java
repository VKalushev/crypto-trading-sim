package com.crypto_trading_sim.integration.user;

import com.crypto_trading_sim.integration.EmbeddedPostgresTest;
import com.crypto_trading_sim.user.domain.dto.CreateUserRequest;
import com.crypto_trading_sim.user.domain.dto.UserDto;
import com.crypto_trading_sim.user.facade.UserFacade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedPostgresTest
class UserFacadeIT {

    @Autowired
    private UserFacade userFacade;

    @Test
    void createUser_whenRequestIsValid_shouldCreateAndRetrieveUser() {
        var request = new CreateUserRequest("testuser@example.com", "password", "Test", "User");

        UserDto createdUser = userFacade.createUser(request);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getUsername()).isEqualTo(request.username());
        assertThat(createdUser.getFirstName()).isEqualTo(request.firstName());
        assertThat(createdUser.getLastName()).isEqualTo(request.lastName());
        assertThat(createdUser.getWallet()).isNotNull();
        assertThat(createdUser.getWallet().getBalance()).isEqualByComparingTo("10000.00");

        UserDto retrievedUser = userFacade.getUserByUsername(request.username());

        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getUsername()).isEqualTo(createdUser.getUsername());
        assertThat(retrievedUser.getWallet()).isNotNull();
    }
}
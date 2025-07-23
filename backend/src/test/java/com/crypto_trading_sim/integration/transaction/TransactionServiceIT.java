package com.crypto_trading_sim.integration.transaction;

import com.crypto_trading_sim.integration.EmbeddedPostgresTest;
import com.crypto_trading_sim.transaction.domain.dto.TransactionDto;
import com.crypto_trading_sim.transaction.domain.dto.TransactionRequestDto;
import com.crypto_trading_sim.transaction.service.TransactionService;
import com.crypto_trading_sim.user.domain.dto.CreateUserRequest;
import com.crypto_trading_sim.user.domain.dto.UserDto;
import com.crypto_trading_sim.user.domain.model.User;
import com.crypto_trading_sim.user.facade.UserFacade;
import com.crypto_trading_sim.user.service.UserService;
import com.crypto_trading_sim.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedPostgresTest
class TransactionServiceIT {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private WalletService walletService;

    private UserDto testUser;
    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        var request = new CreateUserRequest("transactionuser@example.com", "password", "Transaction", "User");
        testUser = userFacade.createUser(request);
    }

    @Test
    void buyAndSell_whenValidTransactions_shouldUpdateWallet() {
        var buyRequest = new TransactionRequestDto();
        buyRequest.setSymbol("BTC");
        buyRequest.setAmount(new BigDecimal("0.1"));
        buyRequest.setPrice(new BigDecimal("60000")); // 6000 cost

        User user = userService.findByUsername(testUser.getUsername()).get();
        TransactionDto buyTransaction = transactionService.buy(user.getId(), buyRequest);

        assertThat(buyTransaction).isNotNull();
        assertThat(buyTransaction.getSymbol()).isEqualTo("BTC");
        assertThat(buyTransaction.getAmount()).isEqualByComparingTo("0.1");

        var walletAfterBuy = walletService.getWalletByUserId(user.getId());
        assertThat(walletAfterBuy.getBalance()).isEqualByComparingTo("4000.00"); // 10000 - 6000
        assertThat(walletAfterBuy.getAssets()).hasSize(1);
        assertThat(walletAfterBuy.getAssets().get(0).getSymbol()).isEqualTo("BTC");
        assertThat(walletAfterBuy.getAssets().get(0).getAmount()).isEqualByComparingTo("0.1");

        var sellRequest = new TransactionRequestDto();
        sellRequest.setSymbol("BTC");
        sellRequest.setAmount(new BigDecimal("0.05"));
        sellRequest.setPrice(new BigDecimal("62000")); // 3100 income

        TransactionDto sellTransaction = transactionService.sell(user.getId(), sellRequest);

        assertThat(sellTransaction).isNotNull();
        assertThat(sellTransaction.getSymbol()).isEqualTo("BTC");
        assertThat(sellTransaction.getAmount()).isEqualByComparingTo("0.05");

        var walletAfterSell = walletService.getWalletByUserId(user.getId());
        assertThat(walletAfterSell.getBalance()).isEqualByComparingTo("7100.00"); // 4000 + 3100
        assertThat(walletAfterSell.getAssets().get(0).getAmount()).isEqualByComparingTo("0.05");
    }

    @Test
    void getTransactions_whenUserHasTransactions_shouldReturnTransactions() {
        User user = userService.findByUsername(testUser.getUsername()).get();

        var buyRequest = new TransactionRequestDto();
        buyRequest.setSymbol("ETH");
        buyRequest.setAmount(new BigDecimal("2"));
        buyRequest.setPrice(new BigDecimal("3000"));
        transactionService.buy(user.getId(), buyRequest);

        var sellRequest = new TransactionRequestDto();
        sellRequest.setSymbol("ETH");
        sellRequest.setAmount(new BigDecimal("1"));
        sellRequest.setPrice(new BigDecimal("3100"));
        transactionService.sell(user.getId(), sellRequest);

        List<TransactionDto> transactions = transactionService.getTransactions(user.getId());

        assertThat(transactions).isNotNull().hasSize(2);
    }
}
package com.crypto_trading_sim.wallet.api;

import com.crypto_trading_sim.config.WithMockCustomUser;
import com.crypto_trading_sim.wallet.domain.model.Wallet;
import com.crypto_trading_sim.wallet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    @Test
    @WithMockCustomUser
    void getWallet_whenWalletExists_shouldReturnWallet() throws Exception {
        Wallet wallet = new Wallet();
        wallet.setBalance(new BigDecimal("10000.00"));
        when(walletService.getWalletByUserId(any(UUID.class))).thenReturn(wallet);

        mockMvc.perform(get("/wallet"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(10000.00));
    }

    @Test
    @WithMockCustomUser
    void resetWallet_whenCalled_shouldReturnOk() throws Exception {
        mockMvc.perform(post("/wallet/reset"))
                .andExpect(status().isOk());
    }
}
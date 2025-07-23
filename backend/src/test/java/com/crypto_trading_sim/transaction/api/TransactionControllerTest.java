package com.crypto_trading_sim.transaction.api;

import com.crypto_trading_sim.config.WithMockCustomUser;
import com.crypto_trading_sim.transaction.domain.dto.TransactionDto;
import com.crypto_trading_sim.transaction.domain.dto.TransactionRequestDto;
import com.crypto_trading_sim.transaction.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockCustomUser
    void buy_whenValidRequest_shouldReturnOk() throws Exception {
        TransactionRequestDto request = new TransactionRequestDto();
        request.setSymbol("BTC");
        request.setAmount(new BigDecimal("0.1"));
        request.setPrice(new BigDecimal("50000"));

        when(transactionService.buy(any(UUID.class), any(TransactionRequestDto.class)))
                .thenReturn(new TransactionDto());

        mockMvc.perform(post("/transaction/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser
    void sell_whenValidRequest_shouldReturnOk() throws Exception {
        TransactionRequestDto sellRequest = new TransactionRequestDto();
        sellRequest.setSymbol("BTC");
        sellRequest.setAmount(new BigDecimal("0.1"));
        sellRequest.setPrice(new BigDecimal("52000"));

        when(transactionService.sell(any(UUID.class), any(TransactionRequestDto.class)))
                .thenReturn(new TransactionDto());

        mockMvc.perform(post("/transaction/sell")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sellRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser
    void getHistory_whenUserAuthenticated_shouldReturnOk() throws Exception {
        when(transactionService.getTransactions(any(UUID.class)))
                .thenReturn(Collections.singletonList(new TransactionDto()));

        mockMvc.perform(get("/transaction/history"))
                .andExpect(status().isOk());
    }
}
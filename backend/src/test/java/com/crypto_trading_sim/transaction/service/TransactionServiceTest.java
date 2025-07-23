package com.crypto_trading_sim.transaction.service;

import com.crypto_trading_sim.common.exception.InsufficientFundsException;
import com.crypto_trading_sim.common.exception.NotFoundException;
import com.crypto_trading_sim.transaction.domain.dto.TransactionDto;
import com.crypto_trading_sim.transaction.domain.dto.TransactionRequestDto;
import com.crypto_trading_sim.transaction.domain.mapper.TransactionMapper;
import com.crypto_trading_sim.transaction.domain.model.Transaction;
import com.crypto_trading_sim.transaction.domain.model.TransactionType;
import com.crypto_trading_sim.transaction.repository.TransactionRepository;
import com.crypto_trading_sim.wallet.domain.model.Asset;
import com.crypto_trading_sim.wallet.domain.model.Wallet;
import com.crypto_trading_sim.wallet.repository.AssetRepository;
import com.crypto_trading_sim.wallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private WalletRepository walletRepository;
    @Mock
    private AssetRepository assetRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionService transactionService;

    private UUID userId;
    private UUID walletId;
    private Wallet wallet;
    private TransactionRequestDto buyRequest;
    private TransactionRequestDto sellRequest;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        walletId = UUID.randomUUID();

        wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setUserId(userId);
        wallet.setBalance(new BigDecimal("10000"));

        buyRequest = new TransactionRequestDto();
        buyRequest.setSymbol("BTC");
        buyRequest.setAmount(new BigDecimal("0.1"));
        buyRequest.setPrice(new BigDecimal("50000"));

        sellRequest = new TransactionRequestDto();
        sellRequest.setSymbol("BTC");
        sellRequest.setAmount(new BigDecimal("0.05"));
        sellRequest.setPrice(new BigDecimal("52000"));
    }

    @Test
    void buy_whenSufficientFunds_shouldSucceed() {
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(assetRepository.findByWalletIdAndSymbol(walletId, "BTC")).thenReturn(Optional.empty());
        when(transactionRepository.insert(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));
        when(transactionMapper.toDto(any(Transaction.class))).thenAnswer(i -> {
            Transaction t = i.getArgument(0);
            TransactionDto dto = new TransactionDto();
            dto.setSymbol(t.getSymbol());
            dto.setAmount(t.getAmount());
            dto.setPrice(t.getPrice());
            dto.setType(t.getType());
            return dto;
        });

        TransactionDto result = transactionService.buy(userId, buyRequest);

        assertNotNull(result);
        assertEquals("BTC", result.getSymbol());
        assertEquals(TransactionType.BUY, result.getType());
        assertEquals(0, new BigDecimal("5000").compareTo(wallet.getBalance()));
    }

    @Test
    void buy_whenInsufficientFunds_shouldThrowException() {
        wallet.setBalance(new BigDecimal("4000"));
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        assertThrows(InsufficientFundsException.class, () -> transactionService.buy(userId, buyRequest));
    }

    @Test
    void sell_whenSufficientAssets_shouldSucceed() {
        Asset asset = new Asset(walletId, "BTC", new BigDecimal("0.2"));
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(assetRepository.findByWalletIdAndSymbol(walletId, "BTC")).thenReturn(Optional.of(asset));
        when(transactionRepository.insert(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));
        when(transactionMapper.toDto(any(Transaction.class))).thenAnswer(i -> {
            Transaction t = i.getArgument(0);
            TransactionDto dto = new TransactionDto();
            dto.setSymbol(t.getSymbol());
            dto.setAmount(t.getAmount());
            dto.setPrice(t.getPrice());
            dto.setType(t.getType());
            return dto;
        });

        TransactionDto result = transactionService.sell(userId, sellRequest);

        assertNotNull(result);
        assertEquals("BTC", result.getSymbol());
        assertEquals(TransactionType.SELL, result.getType());
        assertEquals(0, new BigDecimal("0.15").compareTo(asset.getAmount()));
        assertEquals(0, new BigDecimal("12600").compareTo(wallet.getBalance()));
    }

    @Test
    void sell_whenInsufficientAssets_shouldThrowException() {
        Asset asset = new Asset(walletId, "BTC", new BigDecimal("0.01"));
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(assetRepository.findByWalletIdAndSymbol(walletId, "BTC")).thenReturn(Optional.of(asset));
        assertThrows(InsufficientFundsException.class, () -> transactionService.sell(userId, sellRequest));
    }

    @Test
    void sell_whenAssetNotFound_shouldThrowException() {
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(assetRepository.findByWalletIdAndSymbol(walletId, "BTC")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> transactionService.sell(userId, sellRequest));
    }

    @Test
    void getTransactions_whenWalletExists_shouldReturnTransactions() {
        Transaction transaction = new Transaction();
        transaction.setSymbol("BTC");
        transaction.setAmount(new BigDecimal("0.1"));
        transaction.setPrice(new BigDecimal("50000"));
        transaction.setType(TransactionType.BUY);

        TransactionDto dto = new TransactionDto();
        dto.setSymbol("BTC");
        dto.setAmount(new BigDecimal("0.1"));
        dto.setPrice(new BigDecimal("50000"));
        dto.setType(TransactionType.BUY);


        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(transactionRepository.findAllByWalletId(walletId)).thenReturn(Collections.singletonList(transaction));
        when(transactionMapper.toDto(transaction)).thenReturn(dto);

        List<TransactionDto> result = transactionService.getTransactions(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("BTC", result.get(0).getSymbol());
    }

    @Test
    void getTransactions_whenWalletNotFound_shouldThrowException() {
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> transactionService.getTransactions(userId));
    }
}
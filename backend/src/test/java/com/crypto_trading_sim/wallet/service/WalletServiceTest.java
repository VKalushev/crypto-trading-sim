package com.crypto_trading_sim.wallet.service;

import com.crypto_trading_sim.common.exception.NotFoundException;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private WalletService walletService;

    private Wallet wallet;
    private UUID userId;
    private UUID walletId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        walletId = UUID.randomUUID();
        wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setUserId(userId);
        wallet.setBalance(new BigDecimal("10000.00"));
    }

    @Test
    void getWalletByUserId_whenWalletExists_shouldReturnWalletWithAssets() {
        Asset asset = new Asset(walletId, "BTC", new BigDecimal("0.5"));
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(assetRepository.findByWalletId(walletId)).thenReturn(Collections.singletonList(asset));

        Wallet foundWallet = walletService.getWalletByUserId(userId);

        assertNotNull(foundWallet);
        assertEquals(walletId, foundWallet.getId());
        assertEquals(1, foundWallet.getAssets().size());
        assertEquals("BTC", foundWallet.getAssets().get(0).getSymbol());
    }

    @Test
    void getWalletByUserId_whenWalletDoesntExist_shouldThrowNotFoundException() {
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> walletService.getWalletByUserId(userId));
    }

    @Test
    void createWallet_whenCalled_shouldReturnNewWallet() {
        when(walletRepository.insert(any(Wallet.class))).thenReturn(wallet);

        Wallet newWallet = walletService.createWallet(userId);

        assertNotNull(newWallet);
        assertEquals(userId, newWallet.getUserId());
        assertEquals(0, new BigDecimal("10000.00").compareTo(newWallet.getBalance()));
    }

    @Test
    void reset_whenWalletExists_shouldResetWallet() {
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        walletService.reset(userId);

        verify(walletRepository, times(1)).update(wallet);
        verify(assetRepository, times(1)).deleteAllByWalletId(walletId);
        assertEquals(0, new BigDecimal("10000.00").compareTo(wallet.getBalance()));
    }

    @Test
    void reset_whenWalletDoesntExist_shouldThrowNotFoundException() {
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> walletService.reset(userId));
    }

    @Test
    void findByUserId_whenWalletDoesntExist_shouldThrowNotFoundException() {
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> walletService.getWalletByUserId(userId));
    }
}
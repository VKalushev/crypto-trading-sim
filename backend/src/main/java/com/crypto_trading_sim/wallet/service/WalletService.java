package com.crypto_trading_sim.wallet.service;

import com.crypto_trading_sim.common.exception.NotFoundException;
import com.crypto_trading_sim.wallet.domain.model.Asset;
import com.crypto_trading_sim.wallet.domain.model.Wallet;
import com.crypto_trading_sim.wallet.repository.AssetRepository;
import com.crypto_trading_sim.wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final AssetRepository assetRepository;

    public WalletService(WalletRepository walletRepository, AssetRepository assetRepository) {
        this.walletRepository = walletRepository;
        this.assetRepository = assetRepository;
    }

    @Transactional(readOnly = true)
    public Wallet getWalletByUserId(UUID userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Wallet not found for user: " + userId));
        List<Asset> assets = assetRepository.findByWalletId(wallet.getId());
        wallet.setAssets(assets);
        return wallet;
    }

    @Transactional
    public Wallet createWallet(UUID userId) {
        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setBalance(new BigDecimal("10000.00"));
        return walletRepository.insert(wallet);
    }

    @Transactional
    public void reset(UUID userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Wallet not found"));
        wallet.setBalance(new BigDecimal("10000.00"));
        walletRepository.update(wallet);
        assetRepository.deleteAllByWalletId(wallet.getId());
    }
}
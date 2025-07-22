package com.crypto_trading_sim.transaction.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {



    private final WalletRepository walletRepository;
    private final AssetRepository assetRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public TransactionService(WalletRepository walletRepository, AssetRepository assetRepository, TransactionRepository transactionRepository, TransactionMapper transactionMapper) {
        this.walletRepository = walletRepository;
        this.assetRepository = assetRepository;
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    @Transactional
    public TransactionDto buy(UUID userId, TransactionRequestDto request) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        BigDecimal cost = request.getAmount().multiply(request.getPrice());
        if (wallet.getBalance().compareTo(cost) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        wallet.setBalance(wallet.getBalance().subtract(cost));
        walletRepository.update(wallet);

        Asset asset = assetRepository.findByWalletIdAndSymbol(wallet.getId(), request.getSymbol())
                .orElse(new Asset(wallet.getId(), request.getSymbol(), BigDecimal.ZERO));
        asset.setAmount(asset.getAmount().add(request.getAmount()));
        assetRepository.save(asset);

        Transaction transaction = new Transaction();
        transaction.setWalletId(wallet.getId());
        transaction.setSymbol(request.getSymbol());
        transaction.setAmount(request.getAmount());
        transaction.setPrice(request.getPrice());
        transaction.setType(TransactionType.BUY);
        return transactionMapper.toDto(transactionRepository.insert(transaction));
    }

    @Transactional
    public TransactionDto sell(UUID userId, TransactionRequestDto request) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        Asset asset = assetRepository.findByWalletIdAndSymbol(wallet.getId(), request.getSymbol())
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        if (asset.getAmount().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient asset amount");
        }

        asset.setAmount(asset.getAmount().subtract(request.getAmount()));
        assetRepository.save(asset);

        BigDecimal income = request.getAmount().multiply(request.getPrice());
        wallet.setBalance(wallet.getBalance().add(income));
        walletRepository.update(wallet);

        Transaction transaction = new Transaction();
        transaction.setWalletId(wallet.getId());
        transaction.setSymbol(request.getSymbol());
        transaction.setAmount(request.getAmount());
        transaction.setPrice(request.getPrice());
        transaction.setType(TransactionType.SELL);
        return transactionMapper.toDto(transactionRepository.insert(transaction));
    }

    public List<TransactionDto> getTransactions(UUID userId) {
        Wallet wallet = walletRepository.findByUserId(userId).orElseThrow(() -> new NotFoundException("Wallet not found"));

        return transactionRepository.findAllByWalletId(wallet.getId()).stream().map(transactionMapper::toDto).toList();
    }
}
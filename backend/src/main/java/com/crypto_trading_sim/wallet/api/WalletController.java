package com.crypto_trading_sim.wallet.api;

import com.crypto_trading_sim.security.user.CustomUserDetails;
import com.crypto_trading_sim.wallet.domain.model.Wallet;
import com.crypto_trading_sim.wallet.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping
    public ResponseEntity<Wallet> getWallet(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Wallet wallet = walletService.getWalletByUserId(userDetails.getId());
        return ResponseEntity.ok(wallet);
    }

    @PostMapping("/reset")
    public ResponseEntity<Void> resetWallet(@AuthenticationPrincipal CustomUserDetails userDetails) {
        walletService.reset(userDetails.getId());
        return ResponseEntity.ok().build();
    }
}
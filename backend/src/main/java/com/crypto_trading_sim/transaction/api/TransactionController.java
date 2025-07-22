package com.crypto_trading_sim.transaction.api;

import com.crypto_trading_sim.security.user.CustomUserDetails;
import com.crypto_trading_sim.transaction.domain.dto.TransactionDto;
import com.crypto_trading_sim.transaction.domain.dto.TransactionRequestDto;
import com.crypto_trading_sim.transaction.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/buy")
    public ResponseEntity<TransactionDto> buy(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody TransactionRequestDto request) {
        TransactionDto transaction = transactionService.buy(userDetails.getId(), request);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/sell")
    public ResponseEntity<TransactionDto> sell(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody TransactionRequestDto request) {
        TransactionDto transaction = transactionService.sell(userDetails.getId(), request);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("history")
    public ResponseEntity<List<TransactionDto>> getHistory(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(transactionService.getTransactions(userDetails.getId()));
    }
}
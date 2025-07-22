package com.crypto_trading_sim.transaction.domain.mapper;

import com.crypto_trading_sim.transaction.domain.dto.TransactionDto;
import com.crypto_trading_sim.transaction.domain.model.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    TransactionDto toDto(Transaction transaction);
}
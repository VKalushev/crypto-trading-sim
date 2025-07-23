package com.crypto_trading_sim.transaction.domain.mapper;

import com.crypto_trading_sim.transaction.domain.dto.TransactionDto;
import com.crypto_trading_sim.transaction.domain.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.SubclassExhaustiveStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public abstract class TransactionMapper {
    @Mapping(source = "created", target = "createdAt")
    public abstract TransactionDto toDto(Transaction transaction);
}
package com.crypto_trading_sim.wallet.domain.mapper;

import com.crypto_trading_sim.wallet.domain.dto.WalletDto;
import com.crypto_trading_sim.wallet.domain.model.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.SubclassExhaustiveStrategy;

@Mapper(componentModel = "spring",
        uses = AssetMapper.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public abstract class WalletMapper {
    public abstract WalletDto toDto(Wallet wallet);
}
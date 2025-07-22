package com.crypto_trading_sim.wallet.domain.mapper;

import com.crypto_trading_sim.wallet.domain.dto.AssetDto;
import com.crypto_trading_sim.wallet.domain.model.Asset;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.SubclassExhaustiveStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public abstract class AssetMapper {
    public abstract AssetDto toDto(Asset asset);
}
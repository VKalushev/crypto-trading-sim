package com.crypto_trading_sim.user.domain.mapper;

import com.crypto_trading_sim.user.domain.dto.UserDto;
import com.crypto_trading_sim.user.domain.model.User;
import com.crypto_trading_sim.wallet.domain.mapper.WalletMapper;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.SubclassExhaustiveStrategy;

@Mapper(componentModel = "spring",
        uses = WalletMapper.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public abstract class UserMapper {

    public abstract UserDto toDto(User user);
}
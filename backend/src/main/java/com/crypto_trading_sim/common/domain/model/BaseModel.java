package com.crypto_trading_sim.common.domain.model;


import java.util.UUID;

public class BaseModel<T extends BaseModel<T>> {

    private UUID id;

    public UUID getId() {
        return id;
    }

    public T setId(UUID id) {
        this.id = id;
        return (T) this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if(!(obj instanceof BaseModel))
            return false;

        BaseModel<T> other = (BaseModel<T>) obj;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

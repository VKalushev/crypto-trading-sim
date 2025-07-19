package com.crypto_trading_sim.user.domain.model;

import com.crypto_trading_sim.common.domain.model.BaseModel;

import java.time.Instant;

public class User extends BaseModel<User> {

    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private AppRole role;
    private Instant created;

    public User() {}

    public User(String username, String password, String firstName, String lastName, AppRole role) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public AppRole getRole() {
        return role;
    }

    public void setRole(AppRole role) {
        this.role = role;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }
}

package com.debd.kgp.dsmusic.model;

import java.io.Serializable;

public class UserCredential implements Serializable {
    private String username, password;
    private boolean rememberStatus, loginStatus;

    public UserCredential(){}

    public UserCredential(String username, String password, boolean rememberStatus, boolean loginStatus) {
        this.username = username;
        this.password = password;
        this.rememberStatus = rememberStatus;
        this.loginStatus = loginStatus;
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

    public boolean getRememberStatus() {
        return rememberStatus;
    }

    public void setRememberStatus(boolean rememberStatus) {
        this.rememberStatus = rememberStatus;
    }

    public boolean getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(boolean loginStatus) {
        this.loginStatus = loginStatus;
    }
}

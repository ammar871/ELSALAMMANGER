package com.elsalmcity.elsalammanger.notifecation;

public class Token {
 private String token;
 private boolean isServerToken;

    public Token(String tokenRefresh) {
    }

    public Token() {
    }

    public Token(String token, boolean isServerToken) {
        this.token = token;
        this.isServerToken = isServerToken;
    }

    public String getToken() {
        return token;
    }

    public boolean isIsservecToken() {
        return isServerToken;
    }

    public void setIsservecToken(boolean isServerToken) {
        this.isServerToken = isServerToken;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

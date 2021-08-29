package com.project.Objects.Entities;

public class AuthToken {
    private String token;
    private String authUser;
    private int userid;
    private int permission;

    public AuthToken(String token, String authUser, int userid, int permission) {
        this.token = token;
        this.authUser = authUser;
        this.userid = userid;
        this.permission = permission;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAuthUser() {
        return authUser;
    }

    public void setAuthUser(String authUser) {
        this.authUser = authUser;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }
}

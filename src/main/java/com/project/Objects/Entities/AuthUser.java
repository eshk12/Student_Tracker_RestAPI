package com.project.Objects.Entities;

public class AuthUser {
    private int authUser_id;
    private int authUser_permission;
    private int authUser_instituteId;
    private int authUser_departmentId;
    private String authUser_username;
    private String authUser_token;

    public AuthUser(int authUser_permission) {
        this.authUser_permission = authUser_permission;
    }

    public AuthUser(int authUser_id, int authUser_permission, int authUser_instituteId, int authUser_departmentId, String authUser_username, String authUser_token) {
        this.authUser_id = authUser_id;
        this.authUser_permission = authUser_permission;
        this.authUser_instituteId = authUser_instituteId;
        this.authUser_departmentId = authUser_departmentId;
        this.authUser_username = authUser_username;
        this.authUser_token = authUser_token;
    }

    public AuthUser(int authUser_id, int authUser_permission, int authUser_instituteId, String authUser_username, String authUser_token) {
        this.authUser_id = authUser_id;
        this.authUser_permission = authUser_permission;
        this.authUser_instituteId = authUser_instituteId;
        this.authUser_username = authUser_username;
        this.authUser_token = authUser_token;
    }

    public int getAuthUser_id() {
        return authUser_id;
    }

    public void setAuthUser_id(int authUser_id) {
        this.authUser_id = authUser_id;
    }

    public int getAuthUser_permission() {
        return authUser_permission;
    }

    public void setAuthUser_permission(int authUser_permission) {
        this.authUser_permission = authUser_permission;
    }

    public int getAuthUser_instituteId() {
        return authUser_instituteId;
    }

    public void setAuthUser_instituteId(int authUser_instituteId) {
        this.authUser_instituteId = authUser_instituteId;
    }

    public int getAuthUser_departmentId() {
        return authUser_departmentId;
    }

    public void setAuthUser_departmentId(int authUser_departmentId) {
        this.authUser_departmentId = authUser_departmentId;
    }

    public String getAuthUser_username() {
        return authUser_username;
    }

    public void setAuthUser_username(String authUser_username) {
        this.authUser_username = authUser_username;
    }

    public String getAuthUser_token() {
        return authUser_token;
    }

    public void setAuthUser_token(String authUser_token) {
        this.authUser_token = authUser_token;
    }

    @Override
    public String toString() {
        return "AuthUser{" +
                "authUser_id=" + authUser_id +
                ", authUser_permission=" + authUser_permission +
                ", authUser_instituteId=" + authUser_instituteId +
                ", authUser_departmentId=" + authUser_departmentId +
                ", authUser_username='" + authUser_username + '\'' +
                ", authUser_token='" + authUser_token + '\'' +
                '}';
    }
}

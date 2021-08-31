package com.project.Objects.Entities;

public class BasicResponseModel {
    private int errorCode;
    private String errorName;
    private String customMessage;
    private Object object;
    private AuthUser authUser;


    public BasicResponseModel(int errorCode, String errorName) {
        this.errorCode = errorCode;
        this.errorName = errorName;
    }
    public BasicResponseModel(Object object) {
        this.object = object;
    }
    public BasicResponseModel(Object object, AuthUser authUser) {
        this.object = object;
        this.authUser = authUser;
    }

    public BasicResponseModel(String customMessage) {
        this.customMessage = customMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorName() {
        return errorName;
    }

    public void setErrorName(String errorName) {
        this.errorName = errorName;
    }

    public String getCustomMessage() {
        return customMessage;
    }

    public void setCustomMessage(String customMessage) {
        this.customMessage = customMessage;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public AuthUser getAuthUser() {
        return authUser;
    }

    public void setAuthUser(AuthUser authUser) {
        this.authUser = authUser;
    }
}

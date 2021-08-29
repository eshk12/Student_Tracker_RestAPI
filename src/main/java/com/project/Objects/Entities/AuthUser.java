package com.project.Objects.Entities;

public class AuthUser {
    private int authUserpermission;
    private int authUserInstituteId;

    public AuthUser(int permissionGranted) {
        this.authUserpermission = permissionGranted;
    }
    public AuthUser(int permissionGranted, int instituteId) {
        this.authUserpermission = permissionGranted;
        this.authUserInstituteId = instituteId;
    }

    public int getAuthUserpermission() {
        return authUserpermission;
    }

    public void setAuthUserpermission(int authUserpermission) {
        this.authUserpermission = authUserpermission;
    }

    public int getAuthUserInstituteId() {
        return authUserInstituteId;
    }

    public void setAuthUserInstituteId(int authUserInstituteId) {
        this.authUserInstituteId = authUserInstituteId;
    }
}

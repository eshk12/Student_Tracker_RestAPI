package com.project.Objects.Entities;

public class AuthUser {
    private int authUserpermission;
    private int authUserInstituteId;
    private int authUserDepartmentId;
    private String authUser;

    public AuthUser(int authUserpermission) {
        this.authUserpermission = authUserpermission;
    }


    public AuthUser(int authUserpermission, int authUserInstituteId) {
        this.authUserpermission = authUserpermission;
        this.authUserInstituteId = authUserInstituteId;
    }

    public AuthUser(int authUserpermission, int authUserInstituteId, int authUserDepartmentId, String authUser) {
        this.authUserpermission = authUserpermission;
        this.authUserInstituteId = authUserInstituteId;
        this.authUserDepartmentId = authUserDepartmentId;
        this.authUser = authUser;
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

    public int getAuthUserDepartmentId() {
        return authUserDepartmentId;
    }

    public void setAuthUserDepartmentId(int authUserDepartmentId) {
        this.authUserDepartmentId = authUserDepartmentId;
    }

    public String getAuthUser() {
        return authUser;
    }

    public void setAuthUser(String authUser) {
        this.authUser = authUser;
    }
}

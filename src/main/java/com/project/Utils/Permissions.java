package com.project.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Permissions {
    @Autowired private Definitions definitions;

    public boolean validPermission(int userPermission, int requiredPermission) {
        return userPermission <= requiredPermission;
    }

    public boolean validPermissionRange(int permission){
        return definitions.HIGHEST_LOGGED_IN_PERMISSION <= permission && permission <= definitions.LOWEST_LOGGED_IN_PERMISSION;
    }
}

package com.project.Utils;

import org.springframework.stereotype.Component;

@Component
public class Permissions {
    public boolean validPermission(int userPermission, int requiredPermission) {
        return userPermission <= requiredPermission;
    }


}

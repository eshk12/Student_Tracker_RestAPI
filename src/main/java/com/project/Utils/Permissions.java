package com.project.Utils;

public class Permissions {
    public static boolean validPermission(int userPermission, int requiredPermission) {
        System.out.println(requiredPermission);
        return userPermission <= requiredPermission;
    }
}

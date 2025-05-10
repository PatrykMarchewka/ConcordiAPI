package com.patrykmarchewka.concordiapi;

import org.springframework.stereotype.Service;

@Service
public class PermissionService {
    public static boolean adminGroup(PublicVariables.UserRole role){
        return switch (role){
            case OWNER,ADMIN,MANAGER -> true;
            default -> false;
        };
    }




}

package com.patrykmarchewka.concordiapi;

public enum UserRole{
    OWNER,
    ADMIN,
    MANAGER,
    MEMBER,
    BANNED;

    /**
     * Converts String to UserRole value ignoring case
     * @param name String of UserRole value
     * @return UserRole from the String if found, otherwise throws
     * @throws IllegalArgumentException Thrown when it can't find UserRole from given String
     */
    public static UserRole fromString(String name){
        for (UserRole role : UserRole.values() ){
            if (role.name().equalsIgnoreCase(name)){
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + name);
    }

    public boolean isOwner(){ return this == OWNER; }
    public boolean isAdmin() { return this == ADMIN; }
    public boolean isManager(){ return this == MANAGER; }
    public boolean isMember(){ return this == MEMBER; }
    public boolean isBanned(){ return this == BANNED; }


    /**
     * Checks whether user role is in admin group
     * @return True if User Role is set to OWNER, ADMIN or MANAGER, otherwise false
     */
    public boolean isAdminGroup(){ return this == OWNER || this == ADMIN || this == MANAGER; }
    public boolean isOwnerOrAdmin(){ return this == OWNER || this == ADMIN; }

    /**
     * Checks whether user can perform basic operations
     * @return True if User Role is set to OWNER, ADMIN, MANAGER or MEMBER, otherwise false
     */
    public boolean isAllowedBasic() { return this == OWNER || this == ADMIN || this == MANAGER || this == MEMBER; }
}
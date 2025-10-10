package com.patrykmarchewka.concordiapi;

public enum UserRole{
    OWNER,
    ADMIN,
    MANAGER,
    MEMBER,
    BANNED;

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
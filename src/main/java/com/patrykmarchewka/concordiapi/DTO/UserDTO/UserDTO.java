package com.patrykmarchewka.concordiapi.DTO.UserDTO;

import com.patrykmarchewka.concordiapi.HydrationContracts.User.UserIdentity;

public interface UserDTO extends UserIdentity {
    void setID(Long ID);
    void setName(String name);
    void setLastName(String lastName);
}

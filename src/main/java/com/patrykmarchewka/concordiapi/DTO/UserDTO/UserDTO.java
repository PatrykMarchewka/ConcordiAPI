package com.patrykmarchewka.concordiapi.DTO.UserDTO;

import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.HydrationContracts.User.UserIdentity;

import java.util.Objects;

public interface UserDTO extends UserIdentity {
    void setID(Long ID);
    void setName(String name);
    void setLastName(String lastName);

    default boolean equalsUser(User user){
        return Objects.equals(getID(), user.getID()) &&
                Objects.equals(getName(), user.getName()) &&
                Objects.equals(getLastName(), user.getLastName());
    }
}

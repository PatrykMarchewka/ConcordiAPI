package com.patrykmarchewka.concordiapi.DTO.UserDTO;

import com.patrykmarchewka.concordiapi.DatabaseModel.User;

import java.util.Objects;

public interface UserDTO {
    long getID();
    String getName();
    String getLastName();

    default boolean equalsUser(User user){
        return Objects.equals(getID(), user.getID()) &&
                Objects.equals(getName(), user.getName()) &&
                Objects.equals(getLastName(), user.getLastName());
    }
}

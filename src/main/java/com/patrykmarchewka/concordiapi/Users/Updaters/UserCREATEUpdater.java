package com.patrykmarchewka.concordiapi.Users.Updaters;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;

public interface UserCREATEUpdater extends UserUpdater {
    void CREATEUpdate(User user, UserRequestBody body);
}

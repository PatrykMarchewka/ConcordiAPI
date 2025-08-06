package com.patrykmarchewka.concordiapi.Users.Updaters;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;

public interface UserPATCHUpdater extends UserUpdater{
    void PATCHUpdate(User user, UserRequestBody body);
}

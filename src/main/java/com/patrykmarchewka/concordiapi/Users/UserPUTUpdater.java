package com.patrykmarchewka.concordiapi.Users;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;

public interface UserPUTUpdater extends UserUpdater{
    void PUTUpdate(User user, UserRequestBody body);
}

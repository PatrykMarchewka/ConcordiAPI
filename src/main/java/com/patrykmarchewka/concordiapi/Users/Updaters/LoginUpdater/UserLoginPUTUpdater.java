package com.patrykmarchewka.concordiapi.Users.Updaters.LoginUpdater;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Exceptions.ConflictException;
import com.patrykmarchewka.concordiapi.Users.Updaters.UserPUTUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserLoginPUTUpdater implements UserPUTUpdater {

    private final UserLoginUpdaterHelper userLoginUpdaterHelper;

    @Autowired
    public UserLoginPUTUpdater(UserLoginUpdaterHelper userLoginUpdaterHelper) {
        this.userLoginUpdaterHelper = userLoginUpdaterHelper;
    }

    /**
     * Sets the user's login to new one
     * @param user User to edit
     * @param body UserRequestBody with data containing login
     * @throws ConflictException Thrown when login is already in use
     */
    @Override
    public void PUTUpdate(User user, UserRequestBody body) {
        userLoginUpdaterHelper.sharedUpdate(user, body);
    }
}

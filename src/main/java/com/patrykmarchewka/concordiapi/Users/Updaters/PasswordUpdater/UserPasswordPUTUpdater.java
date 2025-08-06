package com.patrykmarchewka.concordiapi.Users.Updaters.PasswordUpdater;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Users.Updaters.UserPUTUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserPasswordPUTUpdater implements UserPUTUpdater {

    private final UserPasswordUpdaterHelper userPasswordUpdaterHelper;

    @Autowired
    public UserPasswordPUTUpdater(UserPasswordUpdaterHelper userPasswordUpdaterHelper) {
        this.userPasswordUpdaterHelper = userPasswordUpdaterHelper;
    }

    /**
     * Sets the user password with new value from body
     * @param user User to edit
     * @param body UserRequestBody containing data with new value
     */
    @Override
    public void PUTUpdate(User user, UserRequestBody body) {
        userPasswordUpdaterHelper.sharedUpdate(user, body);
    }
}

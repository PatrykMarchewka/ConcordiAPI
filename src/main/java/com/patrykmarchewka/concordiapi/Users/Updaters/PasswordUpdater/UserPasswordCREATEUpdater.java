package com.patrykmarchewka.concordiapi.Users.Updaters.PasswordUpdater;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Users.Updaters.UserCREATEUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserPasswordCREATEUpdater implements UserCREATEUpdater {

    private final UserPasswordUpdaterHelper userPasswordUpdaterHelper;

    @Autowired
    public UserPasswordCREATEUpdater(UserPasswordUpdaterHelper userPasswordUpdaterHelper) {
        this.userPasswordUpdaterHelper = userPasswordUpdaterHelper;
    }


    /**
     * Sets the user password during user creation
     * @param user User being created
     * @param body UserRequestBody containing data with password
     */
    @Override
    public void CREATEUpdate(User user, UserRequestBody body) {
        userPasswordUpdaterHelper.sharedUpdate(user, body);
    }
}

package com.patrykmarchewka.concordiapi.Users.Updaters.LoginUpdater;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Exceptions.ConflictException;
import com.patrykmarchewka.concordiapi.Users.Updaters.UserCREATEUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserLoginCREATEUpdater implements UserCREATEUpdater {

    private final UserLoginUpdaterHelper userLoginUpdaterHelper;

    @Autowired
    public UserLoginCREATEUpdater(UserLoginUpdaterHelper userLoginUpdaterHelper) {
        this.userLoginUpdaterHelper = userLoginUpdaterHelper;
    }

    /**
     * Sets the user's login during user creation
     * @param user User being created
     * @param body UserRequestBody with data containing login
     * @throws ConflictException Thrown when login is already in use
     */
    @Override
    public void CREATEUpdate(User user, UserRequestBody body) {
        userLoginUpdaterHelper.sharedUpdate(user, body);
    }
}

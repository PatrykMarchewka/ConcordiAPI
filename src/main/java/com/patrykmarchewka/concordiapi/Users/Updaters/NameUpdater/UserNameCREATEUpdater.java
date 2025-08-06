package com.patrykmarchewka.concordiapi.Users.Updaters.NameUpdater;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Users.Updaters.UserCREATEUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserNameCREATEUpdater implements UserCREATEUpdater {

    private final UserNameUpdaterHelper userNameUpdaterHelper;

    @Autowired
    public UserNameCREATEUpdater(UserNameUpdaterHelper userNameUpdaterHelper) {
        this.userNameUpdaterHelper = userNameUpdaterHelper;
    }

    /**
     * Sets the user's name during user creation
     * @param user User being created
     * @param body UserRequestBody with data containing name
     */
    @Override
    public void CREATEUpdate(User user, UserRequestBody body) {
        userNameUpdaterHelper.sharedUpdate(user, body);
    }
}

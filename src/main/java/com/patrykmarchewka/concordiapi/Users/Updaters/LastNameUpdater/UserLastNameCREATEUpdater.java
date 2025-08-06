package com.patrykmarchewka.concordiapi.Users.Updaters.LastNameUpdater;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Users.Updaters.UserCREATEUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserLastNameCREATEUpdater implements UserCREATEUpdater {

    private final UserLastNameUpdaterHelper userLastNameUpdaterHelper;

    @Autowired
    public UserLastNameCREATEUpdater(UserLastNameUpdaterHelper userLastNameUpdaterHelper) {
        this.userLastNameUpdaterHelper = userLastNameUpdaterHelper;
    }

    /**
     * Sets the user's lastName during user creation
     * @param user User being created
     * @param body UserRequestBody with data containing lastName
     */
    @Override
    public void CREATEUpdate(User user, UserRequestBody body) {
        userLastNameUpdaterHelper.sharedUpdate(user, body);
    }
}

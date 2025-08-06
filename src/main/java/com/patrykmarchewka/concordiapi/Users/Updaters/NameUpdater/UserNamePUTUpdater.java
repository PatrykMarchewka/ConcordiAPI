package com.patrykmarchewka.concordiapi.Users.Updaters.NameUpdater;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Users.Updaters.UserPUTUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserNamePUTUpdater implements UserPUTUpdater {

    private final UserNameUpdaterHelper userNameUpdaterHelper;

    @Autowired
    public UserNamePUTUpdater(UserNameUpdaterHelper userNameUpdaterHelper) {
        this.userNameUpdaterHelper = userNameUpdaterHelper;
    }

    /**
     * Sets the user's name with new value
     * @param user User to edit
     * @param body UserRequestBody with data containing new value
     */
    @Override
    public void PUTUpdate(User user, UserRequestBody body) {
        userNameUpdaterHelper.sharedUpdate(user, body);
    }
}

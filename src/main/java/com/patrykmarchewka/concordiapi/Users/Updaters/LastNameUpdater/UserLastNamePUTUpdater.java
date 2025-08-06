package com.patrykmarchewka.concordiapi.Users.Updaters.LastNameUpdater;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Users.Updaters.UserPUTUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserLastNamePUTUpdater implements UserPUTUpdater {

    private final UserLastNameUpdaterHelper userLastNameUpdaterHelper;

    @Autowired
    public UserLastNamePUTUpdater(UserLastNameUpdaterHelper userLastNameUpdaterHelper) {
        this.userLastNameUpdaterHelper = userLastNameUpdaterHelper;
    }

    /**
     * Sets the user's lastName with new value
     * @param user User to edit
     * @param body UserRequestBody with data containing new value
     */
    @Override
    public void PUTUpdate(User user, UserRequestBody body) {
        userLastNameUpdaterHelper.sharedUpdate(user, body);
    }
}

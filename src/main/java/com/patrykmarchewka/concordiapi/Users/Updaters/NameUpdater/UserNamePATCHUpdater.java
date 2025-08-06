package com.patrykmarchewka.concordiapi.Users.Updaters.NameUpdater;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Users.Updaters.UserPATCHUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserNamePATCHUpdater implements UserPATCHUpdater {

    private final UserNameUpdaterHelper userNameUpdaterHelper;

    @Autowired
    public UserNamePATCHUpdater(UserNameUpdaterHelper userNameUpdaterHelper) {
        this.userNameUpdaterHelper = userNameUpdaterHelper;
    }

    /**
     * Sets the user's name if new value is present in body
     * @param user User to edit
     * @param body UserRequestBody with data
     */
    @Override
    public void PATCHUpdate(User user, UserRequestBody body) {
        if (body.getName() != null){
            userNameUpdaterHelper.sharedUpdate(user, body);
        }
    }
}

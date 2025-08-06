package com.patrykmarchewka.concordiapi.Users.Updaters.LastNameUpdater;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Users.Updaters.UserPATCHUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserLastNamePATCHUpdater implements UserPATCHUpdater {

    private final UserLastNameUpdaterHelper userLastNameUpdaterHelper;

    @Autowired
    public UserLastNamePATCHUpdater(UserLastNameUpdaterHelper userLastNameUpdaterHelper) {
        this.userLastNameUpdaterHelper = userLastNameUpdaterHelper;
    }

    /**
     * Sets the user's lastName if new value is present in body
     * @param user User to edit
     * @param body UserRequestBody with data
     */
    @Override
    public void PATCHUpdate(User user, UserRequestBody body) {
        if (body.getLastName() != null){
            userLastNameUpdaterHelper.sharedUpdate(user, body);
        }
    }
}

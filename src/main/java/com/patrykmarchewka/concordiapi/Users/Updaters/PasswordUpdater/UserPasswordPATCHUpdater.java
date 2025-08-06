package com.patrykmarchewka.concordiapi.Users.Updaters.PasswordUpdater;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Users.Updaters.UserPATCHUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserPasswordPATCHUpdater implements UserPATCHUpdater {

    private final UserPasswordUpdaterHelper userPasswordUpdaterHelper;

    @Autowired
    public UserPasswordPATCHUpdater(UserPasswordUpdaterHelper userPasswordUpdaterHelper) {
        this.userPasswordUpdaterHelper = userPasswordUpdaterHelper;
    }

    /**
     * Sets the user password if new value is present in body
     * @param user User to edit
     * @param body UserRequestBody containing data
     */
    @Override
    public void PATCHUpdate(User user, UserRequestBody body) {
        if (body.getPassword() != null){
            userPasswordUpdaterHelper.sharedUpdate(user, body);
        }
    }
}

package com.patrykmarchewka.concordiapi.Users.Updaters.LoginUpdater;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Exceptions.ConflictException;
import com.patrykmarchewka.concordiapi.Users.Updaters.UserPATCHUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserLoginPATCHUpdater implements UserPATCHUpdater {

    private final UserLoginUpdaterHelper userLoginUpdaterHelper;

    @Autowired
    public UserLoginPATCHUpdater(UserLoginUpdaterHelper userLoginUpdaterHelper) {
        this.userLoginUpdaterHelper = userLoginUpdaterHelper;
    }

    /**
     * Sets the user's login to new one, if it is present in body
     * @param user User to edit
     * @param body UserRequestBody with data
     * @throws ConflictException Thrown when login is already in use
     */
    @Override
    public void PATCHUpdate(User user, UserRequestBody body) {
        if (body.getLogin() != null){
            userLoginUpdaterHelper.sharedUpdate(user, body);
        }
    }
}

package com.patrykmarchewka.concordiapi.Users.Updaters;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserUpdatersPATCH {

    private final List<UserPATCHUpdater> updaters;

    @Autowired
    public UserUpdatersPATCH(List<UserPATCHUpdater> updaters) {
        this.updaters = updaters;
    }

    /**
     * Applies PATCH updates for the User given the UserRequestBody details, should be only called from {@link UserUpdatersService#patchUpdate(User, UserRequestBody)}
     * @param user User to modify
     * @param body UserRequestBody with information to update
     */
    void applyPatchUpdates(User user, UserRequestBody body){
        for (UserPATCHUpdater updater : updaters){
            updater.PATCHUpdate(user, body);
        }
    }
}

package com.patrykmarchewka.concordiapi.Users.Updaters;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserUpdatersCREATE {

    private final List<UserCREATEUpdater> updaters;

    @Autowired
    public UserUpdatersCREATE(List<UserCREATEUpdater> updaters) {
        this.updaters = updaters;
    }

    /**
     * Applies CREATE updates for the User given the UserRequestBody details, should be only called from {@link com.patrykmarchewka.concordiapi.Users.UserService#createUser(UserRequestBody)}
     * @param user User to modify
     * @param body UserRequestBody with information to update
     */
    void applyCreateUpdates(User user, UserRequestBody body){
        for (UserCREATEUpdater updater : updaters){
            updater.CREATEUpdate(user, body);
        }
    }
}

package com.patrykmarchewka.concordiapi.Users.Updaters;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserUpdatersPUT {

    private final List<UserPUTUpdater> updaters;

    @Autowired
    public UserUpdatersPUT(List<UserPUTUpdater> updaters) {
        this.updaters = updaters;
    }

    /**
     * Applies PUT updates for the User given the UserRequestBody details, should be only called from {@link com.patrykmarchewka.concordiapi.Users.UserService#putUser(User, UserRequestBody)}
     * @param user User to modify
     * @param body UserRequestBody with information to update
     */
    void applyPutUpdates(User user, UserRequestBody body){
        for (UserPUTUpdater updater : updaters){
            updater.PUTUpdate(user, body);
        }
    }
}

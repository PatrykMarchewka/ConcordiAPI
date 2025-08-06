package com.patrykmarchewka.concordiapi.Users.Updaters.NameUpdater;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import org.springframework.stereotype.Service;

@Service
public class UserNameUpdaterHelper {
    /**
     * Shared logic for updating name field
     * @param user User to edit
     * @param body UserRequestBody with data containing new value
     */
    void sharedUpdate(User user, UserRequestBody body){
        user.setName(body.getName());
    }
}

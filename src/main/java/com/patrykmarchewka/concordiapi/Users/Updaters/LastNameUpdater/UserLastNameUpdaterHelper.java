package com.patrykmarchewka.concordiapi.Users.Updaters.LastNameUpdater;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import org.springframework.stereotype.Service;

@Service
public class UserLastNameUpdaterHelper {
    /**
     * Shared logic for updating lastName field
     * @param user User to edit
     * @param body UserRequestBody with data containing new value
     */
    void sharedUpdate(User user, UserRequestBody body){
        user.setLastName(body.getLastName());
    }
}

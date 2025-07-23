package com.patrykmarchewka.concordiapi.Users;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;

/**
 * Handles updating the {@code name} field of {@link User} entities
 */
public class UserNameUpdater implements UserCREATEUpdater,UserPUTUpdater,UserPATCHUpdater{
    /**
     * Sets the user's name during user creation
     * @param user User being created
     * @param body UserRequestBody with data containing name
     */
    @Override
    public void CREATEUpdate(User user, UserRequestBody body) {
        sharedUpdate(user, body);
    }

    /**
     * Sets the user's name if new value is present in body
     * @param user User to edit
     * @param body UserRequestBody with data
     */
    @Override
    public void PATCHUpdate(User user, UserRequestBody body) {
        if (body.getName() != null){
            sharedUpdate(user, body);
        }
    }

    /**
     * Sets the user's name with new value
     * @param user User to edit
     * @param body UserRequestBody with data containing new value
     */
    @Override
    public void PUTUpdate(User user, UserRequestBody body) {
        sharedUpdate(user, body);
    }

    /**
     * Shared logic for updating name field
     * @param user User to edit
     * @param body UserRequestBody with data containing new value
     */
    void sharedUpdate(User user, UserRequestBody body){
        user.setName(body.getName());
    }
}

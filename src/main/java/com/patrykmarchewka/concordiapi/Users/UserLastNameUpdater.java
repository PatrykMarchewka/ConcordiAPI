package com.patrykmarchewka.concordiapi.Users;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;

/**
 * Handles updating the {@code lastName} field of {@link User} entities
 */
public class UserLastNameUpdater implements UserCREATEUpdater,UserPUTUpdater,UserPATCHUpdater{

    /**
     * Sets the user's lastName during user creation
     * @param user User being created
     * @param body UserRequestBody with data containing lastName
     */
    @Override
    public void CREATEUpdate(User user, UserRequestBody body) {
        sharedUpdate(user, body);
    }

    /**
     * Sets the user's lastName if new value is present in body
     * @param user User to edit
     * @param body UserRequestBody with data
     */
    @Override
    public void PATCHUpdate(User user, UserRequestBody body) {
        if (body.getLastName() != null){
            sharedUpdate(user, body);
        }
    }

    /**
     * Sets the user's lastName with new value
     * @param user User to edit
     * @param body UserRequestBody with data containing new value
     */
    @Override
    public void PUTUpdate(User user, UserRequestBody body) {
        sharedUpdate(user, body);
    }

    /**
     * Shared logic for updating lastName field
     * @param user User to edit
     * @param body UserRequestBody with data containing new value
     */
    void sharedUpdate(User user, UserRequestBody body){
        user.setLastName(body.getLastName());
    }
}

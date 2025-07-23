package com.patrykmarchewka.concordiapi.Users;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Passwords;

/**
 * Handles updating the {@code password} field of {@link User} entities
 */
public class UserPasswordUpdater implements UserCREATEUpdater,UserPUTUpdater,UserPATCHUpdater{
    /**
     * Sets the user password during user creation
     * @param user User being created
     * @param body UserRequestBody containing data with password
     */
    @Override
    public void CREATEUpdate(User user, UserRequestBody body) {
        sharedUpdate(user, body);
    }

    /**
     * Sets the user password if new value is present in body
     * @param user User to edit
     * @param body UserRequestBody containing data
     */
    @Override
    public void PATCHUpdate(User user, UserRequestBody body) {
        if (body.getPassword() != null){
            sharedUpdate(user, body);
        }
    }

    /**
     * Sets the user password with new value from body
     * @param user User to edit
     * @param body UserRequestBody containing data with new value
     */
    @Override
    public void PUTUpdate(User user, UserRequestBody body) {
        sharedUpdate(user, body);
    }

    /**
     * Shared logic for updating user's password, for security instead of storing plain passwords they are hashed using BCrypt
     * @param user User to edit
     * @param body UserRequestBody containing data with password
     */
    void sharedUpdate(User user, UserRequestBody body){
        user.setPassword(Passwords.HashPasswordBCrypt(body.getPassword()));
    }
}

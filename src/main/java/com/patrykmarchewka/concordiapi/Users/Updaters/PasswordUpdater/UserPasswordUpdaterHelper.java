package com.patrykmarchewka.concordiapi.Users.Updaters.PasswordUpdater;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Passwords;
import org.springframework.stereotype.Service;

@Service
public class UserPasswordUpdaterHelper {

    /**
     * Shared logic for updating user's password, for security instead of storing plain passwords they are hashed using BCrypt
     * @param user User to edit
     * @param body UserRequestBody containing data with password
     */
    void sharedUpdate(User user, UserRequestBody body){
        user.setPassword(Passwords.HashPasswordBCrypt(body.getPassword()));
    }
}

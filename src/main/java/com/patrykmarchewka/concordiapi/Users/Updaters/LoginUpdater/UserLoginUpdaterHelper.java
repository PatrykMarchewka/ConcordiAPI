package com.patrykmarchewka.concordiapi.Users.Updaters.LoginUpdater;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Exceptions.ConflictException;
import com.patrykmarchewka.concordiapi.Users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class UserLoginUpdaterHelper {

    private final UserService userService;

    @Autowired
    public UserLoginUpdaterHelper(@Lazy UserService userService) {
        this.userService = userService;
    }

    /**
     * Performs the shared update to user's login ensuring new login is not already in use
     * @param user User to edit
     * @param body UserRequestBody with data
     * @throws ConflictException Thrown when Login is already in use
     */
    void sharedUpdate(User user, UserRequestBody body){
        if (userService.checkIfUserExistsByLogin(body.getLogin())){
            throw new ConflictException("Login currently in use");
        }
        user.setLogin(body.getLogin());
    }
}

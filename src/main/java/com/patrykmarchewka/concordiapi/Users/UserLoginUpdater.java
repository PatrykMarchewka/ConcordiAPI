package com.patrykmarchewka.concordiapi.Users;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Exceptions.ConflictException;

/**
 * Handles updating the {@code login} field of {@link User} entities ensuring that each login is unique
 */
public class UserLoginUpdater implements UserCREATEUpdater,UserPUTUpdater,UserPATCHUpdater{

    private final UserService userService;

    public UserLoginUpdater(UserService userService){
        this.userService = userService;
    }


    /**
     * Sets the user's login during user creation
     * @param user User being created
     * @param body UserRequestBody with data containing login
     * @throws ConflictException Thrown when login is already in use
     */
    @Override
    public void CREATEUpdate(User user, UserRequestBody body) {
        sharedUpdate(user, body);
    }

    /**
     * Sets the user's login to new one, if it is present in body
     * @param user User to edit
     * @param body UserRequestBody with data
     * @throws ConflictException Thrown when login is already in use
     */
    @Override
    public void PATCHUpdate(User user, UserRequestBody body) {
        if (body.getLogin() != null){
            sharedUpdate(user, body);
        }
    }

    /**
     * Sets the user's login to new one
     * @param user User to edit
     * @param body UserRequestBody with data containing login
     * @throws ConflictException Thrown when login is already in use
     */
    @Override
    public void PUTUpdate(User user, UserRequestBody body) {
        sharedUpdate(user, body);
    }

    /**
     * Performs the shared update to user's login ensuring new login is not already in use
     * @param user User to edit
     * @param body UserRequestBody with data
     * @throws ConflictException Thrown when Login is already in use
     */
    void sharedUpdate(User user,UserRequestBody body){
        if (userService.checkIfUserExistsByLogin(body.getLogin())){
            throw new ConflictException("Login currently in use");
        }
        user.setLogin(body.getLogin());
    }
}

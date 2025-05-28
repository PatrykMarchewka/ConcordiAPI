package com.patrykmarchewka.concordiapi.Users;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Exceptions.ConflictException;

public class UserLoginUpdater implements UserCREATEUpdater,UserPUTUpdater,UserPATCHUpdater{

    private final UserService userService;

    public UserLoginUpdater(UserService userService){
        this.userService = userService;
    }


    @Override
    public void CREATEUpdate(User user, UserRequestBody body) {
        sharedUpdate(user, body);
    }

    @Override
    public void PATCHUpdate(User user, UserRequestBody body) {
        if (body.getLogin() != null){
            sharedUpdate(user, body);
        }
    }

    @Override
    public void PUTUpdate(User user, UserRequestBody body) {
        sharedUpdate(user, body);
    }

    void sharedUpdate(User user,UserRequestBody body){
        if (userService.checkIfUserExistsByLogin(body.getLogin())){
            throw new ConflictException("Login currently in use");
        }
        user.setLogin(body.getLogin());
    }
}

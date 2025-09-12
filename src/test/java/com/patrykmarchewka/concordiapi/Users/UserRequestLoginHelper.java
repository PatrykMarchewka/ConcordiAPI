package com.patrykmarchewka.concordiapi.Users;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestLogin;

public interface UserRequestLoginHelper {
    default UserRequestLogin createUserRequestLogin(String login, String password){
        UserRequestLogin userRequestLogin = new UserRequestLogin();
        userRequestLogin.setLogin(login);
        userRequestLogin.setPassword(password);
        return userRequestLogin;
    }
}

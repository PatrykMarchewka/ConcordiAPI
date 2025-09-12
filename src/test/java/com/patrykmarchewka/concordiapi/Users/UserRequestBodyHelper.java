package com.patrykmarchewka.concordiapi.Users;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;

public interface UserRequestBodyHelper {
    default UserRequestBody createUserRequestBody(String login){
        UserRequestBody userRequestBody = new UserRequestBody();
        userRequestBody.setName("Jane");
        userRequestBody.setLastName("Doe");
        userRequestBody.setLogin(login);
        userRequestBody.setPassword("d");
        return userRequestBody;
    }

    default UserRequestBody createUserRequestBody(String name, String lastName, String login, String password){
        UserRequestBody userRequestBody = new UserRequestBody();
        userRequestBody.setName(name);
        userRequestBody.setLastName(lastName);
        userRequestBody.setLogin(login);
        userRequestBody.setPassword(password);
        return userRequestBody;
    }
}

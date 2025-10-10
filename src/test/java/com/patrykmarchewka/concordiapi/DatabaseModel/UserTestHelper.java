package com.patrykmarchewka.concordiapi.DatabaseModel;

import com.patrykmarchewka.concordiapi.Passwords;

public interface UserTestHelper {

    default User createUser(String login, UserRepository userRepository){
        User user = new User();
        user.setLogin(login);
        user.setPassword(Passwords.HashPasswordBCrypt("d"));
        user.setName("John");
        user.setLastName("Doe");

        return userRepository.save(user);
    }
}

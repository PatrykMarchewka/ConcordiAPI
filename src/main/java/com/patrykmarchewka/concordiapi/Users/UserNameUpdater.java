package com.patrykmarchewka.concordiapi.Users;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;

public class UserNameUpdater implements UserCREATEUpdater,UserPUTUpdater,UserPATCHUpdater{
    @Override
    public void CREATEUpdate(User user, UserRequestBody body) {
        sharedUpdate(user, body);
    }

    @Override
    public void PATCHUpdate(User user, UserRequestBody body) {
        if (body.getName() != null){
            sharedUpdate(user, body);
        }
    }

    @Override
    public void PUTUpdate(User user, UserRequestBody body) {
        sharedUpdate(user, body);
    }

    void sharedUpdate(User user, UserRequestBody body){
        user.setName(body.getName());
    }
}

package com.patrykmarchewka.concordiapi.Users.Updaters;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import org.springframework.stereotype.Service;

@Service
public class UserUpdatersService {

    private final UserUpdatersCREATE userUpdatersCREATE;
    private final UserUpdatersPUT userUpdatersPUT;
    private final UserUpdatersPATCH userUpdatersPATCH;

    public UserUpdatersService(UserUpdatersCREATE userUpdatersCREATE, UserUpdatersPUT userUpdatersPUT, UserUpdatersPATCH userUpdatersPATCH) {
        this.userUpdatersCREATE = userUpdatersCREATE;
        this.userUpdatersPUT = userUpdatersPUT;
        this.userUpdatersPATCH = userUpdatersPATCH;
    }

    public void createUpdate(User user, UserRequestBody body){
        userUpdatersCREATE.applyCreateUpdates(user, body);
    }

    public void putUpdate(User user, UserRequestBody body){
        userUpdatersPUT.applyPutUpdates(user, body);
    }

    public void patchUpdate(User user, UserRequestBody body){
        userUpdatersPATCH.applyPatchUpdates(user, body);
    }
}

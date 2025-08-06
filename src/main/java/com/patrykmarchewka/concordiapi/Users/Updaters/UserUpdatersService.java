package com.patrykmarchewka.concordiapi.Users.Updaters;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Exceptions.BadRequestException;
import com.patrykmarchewka.concordiapi.UpdateType;
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


    public void update(User user, UserRequestBody body, UpdateType type){
        switch (type){
            case CREATE -> userUpdatersCREATE.applyCreateUpdates(user, body);
            case PUT -> userUpdatersPUT.applyPutUpdates(user, body);
            case PATCH -> userUpdatersPATCH.applyPatchUpdates(user, body);
            case null, default -> throw new BadRequestException("Called update type that isn't CREATE/PUT/PATCH");
        }

    }
}

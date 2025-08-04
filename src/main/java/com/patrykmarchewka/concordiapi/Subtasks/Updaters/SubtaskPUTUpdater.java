package com.patrykmarchewka.concordiapi.Subtasks.Updaters;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.UpdateType;

public interface SubtaskPUTUpdater extends SubtaskUpdater{
    default boolean supports(UpdateType updateType){
        return updateType == UpdateType.PUT;
    }
    void PUTUpdate(Subtask subtask, SubtaskRequestBody body);
}

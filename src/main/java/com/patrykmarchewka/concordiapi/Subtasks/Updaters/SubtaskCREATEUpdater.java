package com.patrykmarchewka.concordiapi.Subtasks.Updaters;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.UpdateType;

public interface SubtaskCREATEUpdater extends SubtaskUpdater{
    default boolean supports(UpdateType updateType){
        return updateType == UpdateType.CREATE;
    }
    void CREATEUpdate(Subtask subtask, SubtaskRequestBody body);
}

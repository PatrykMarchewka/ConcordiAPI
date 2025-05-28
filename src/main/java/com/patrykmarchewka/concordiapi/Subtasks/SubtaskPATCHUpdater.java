package com.patrykmarchewka.concordiapi.Subtasks;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;

public interface SubtaskPATCHUpdater extends SubtaskUpdater {
    void PATCHUpdate(Subtask subtask, SubtaskRequestBody body);
    //Name,desc,task status
}

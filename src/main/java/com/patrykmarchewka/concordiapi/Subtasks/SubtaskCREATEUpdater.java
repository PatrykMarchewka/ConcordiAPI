package com.patrykmarchewka.concordiapi.Subtasks;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;

public interface SubtaskCREATEUpdater extends SubtaskUpdater{
    void CREATEUpdate(Subtask subtask, SubtaskRequestBody body);
}

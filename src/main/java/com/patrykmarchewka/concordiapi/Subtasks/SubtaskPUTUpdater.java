package com.patrykmarchewka.concordiapi.Subtasks;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;

public interface SubtaskPUTUpdater extends SubtaskUpdater{
    void PUTUpdate(Subtask subtask, SubtaskRequestBody body);
}

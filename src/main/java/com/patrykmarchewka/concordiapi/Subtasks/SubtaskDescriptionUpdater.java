package com.patrykmarchewka.concordiapi.Subtasks;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;

public class SubtaskDescriptionUpdater implements SubtaskCREATEUpdater,SubtaskPUTUpdater,SubtaskPATCHUpdater{
    @Override
    public void CREATEUpdate(Subtask subtask, SubtaskRequestBody body) {
        sharedUpdate(subtask, body);
    }

    @Override
    public void PATCHUpdate(Subtask subtask, SubtaskRequestBody body) {
        sharedUpdate(subtask, body);
    }

    @Override
    public void PUTUpdate(Subtask subtask, SubtaskRequestBody body) {
        if (body.getDescription() != null){
            sharedUpdate(subtask, body);
        }
    }

    private void sharedUpdate(Subtask subtask, SubtaskRequestBody body){
        subtask.setDescription(body.getDescription());
    }
}

package com.patrykmarchewka.concordiapi.Subtasks.Updaters;

import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;

public interface SubtaskCREATEUpdaterBasic extends SubtaskUpdater{
    void CREATEUpdate(Subtask subtask);
}

package com.patrykmarchewka.concordiapi.Subtasks.Updaters;

import com.patrykmarchewka.concordiapi.DatabaseModel.Task;

public interface SubtaskCREATEUpdaterBasicWithTask extends SubtaskCREATEUpdaterBasic{
    void setTask(Task task);
}

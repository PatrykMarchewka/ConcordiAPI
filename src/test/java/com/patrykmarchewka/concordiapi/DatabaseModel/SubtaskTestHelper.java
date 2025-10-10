package com.patrykmarchewka.concordiapi.DatabaseModel;

import com.patrykmarchewka.concordiapi.TaskStatus;

public interface SubtaskTestHelper {
    default Subtask createSubtask(Task task, SubtaskRepository subtaskRepository){
        Subtask subtask = new Subtask();
        subtask.setName("TESTSub");
        subtask.setDescription("Test subtask");
        subtask.setTask(task);
        subtask.setTaskStatus(TaskStatus.NEW);
        return subtaskRepository.save(subtask);
    }
}

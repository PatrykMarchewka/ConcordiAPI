package com.patrykmarchewka.concordiapi;

import com.patrykmarchewka.concordiapi.DatabaseModel.*;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.DatabaseModel.SubtaskRepository;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubtaskService {

    @Autowired
    private SubtaskRepository subtaskRepository;
    @Autowired
    @Lazy
    private TaskService taskService;

    public Subtask getSubtaskByID(long taskID, long subtaskID){
        return subtaskRepository.findSubtaskByIdAndTaskId(subtaskID,taskID).orElseThrow(() -> new RuntimeException("Subtask not found"));
    }

    @Transactional
    public Subtask createSubtask(Team team, long taskID, String name, String description){
        Task task = taskService.getTaskByID(taskID,team);
        Subtask subtask = new Subtask();
        subtask.setTask(task);
        subtask.setName(name);
        subtask.setDescription(description);
        subtask.setTaskStatus(PublicVariables.TaskStatus.NEW);
        subtaskRepository.save(subtask);
        task.getSubtasks().add(subtask);
        taskService.saveTask(task);
        return subtask;
    }

    @Transactional
    public void deleteSubtask(long taskID, long subtaskID){
        Subtask subtask = getSubtaskByID(taskID,subtaskID);
        Task task = subtask.getTask();
        task.getSubtasks().remove(subtask);
        taskService.saveTask(task);
        subtaskRepository.delete(subtask);
    }

    public Subtask saveSubtask(Subtask subtask){
        return subtaskRepository.save(subtask);
    }



}

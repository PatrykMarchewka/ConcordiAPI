package com.patrykmarchewka.concordiapi;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.*;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.DatabaseModel.SubtaskRepository;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class SubtaskService {

    @Autowired
    private SubtaskRepository subtaskRepository;
    @Autowired
    @Lazy
    private TaskService taskService;

    public Subtask getSubtaskByID(long taskID, long subtaskID){
        return subtaskRepository.findSubtaskByIdAndTaskId(subtaskID,taskID).orElseThrow(() -> new NotFoundException());
    }

    @Transactional
    public Subtask createSubtask(Team team, long taskID, String name, String description){
        Task task = taskService.getTaskbyIDAndTeam(taskID,team);
        Subtask subtask = new Subtask();
        subtask.setTask(task);
        subtask.setName(name);
        subtask.setDescription(description);
        subtask.setTaskStatus(PublicVariables.TaskStatus.NEW);
        saveSubtask(subtask);
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

    public Set<SubtaskMemberDTO> getSubtasksDTO(Task task){
        Set<SubtaskMemberDTO> subtasks = new HashSet<>();
        for (Subtask sub : task.getSubtasks()){
            subtasks.add(new SubtaskMemberDTO(sub));
        }
        return subtasks;
    }

    @Transactional
    public Subtask putUpdate(Subtask subtask, SubtaskRequestBody body){
        subtask.setName(body.getName());
        subtask.setDescription(body.getDescription());
        subtask.setTaskStatus(body.getTaskStatus());
        return saveSubtask(subtask);
    }

    public boolean checkIfSubtaskExistsByIDAndTask(long ID,Task task){
        return subtaskRepository.existsByIdAndTask(ID, task);
    }

    public boolean checkIfSubtaskExistsByID(long ID){
        return subtaskRepository.existsById(ID);
    }


    @Transactional
    public Subtask partialUpdate(Subtask subtask, SubtaskRequestBody body){
        if (body.getName() != null){
            subtask.setName(body.getName());
        }
        if (body.getDescription() != null){
            subtask.setDescription(body.getDescription());
        }
        if (body.getTaskStatus() != null){
            subtask.setTaskStatus(body.getTaskStatus());
        }
        return saveSubtask(subtask);
    }



}

package com.patrykmarchewka.concordiapi.Subtasks;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.DatabaseModel.SubtaskRepository;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.Exceptions.BadRequestException;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
import com.patrykmarchewka.concordiapi.Tasks.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SubtaskService {


    private final SubtaskRepository subtaskRepository;
    private final TaskService taskService;

    @Autowired
    public SubtaskService(SubtaskRepository subtaskRepository,@Lazy TaskService taskService){
        this.subtaskRepository = subtaskRepository;
        this.taskService = taskService;
    }



    final List<SubtaskUpdater> updaters(){
        return List.of(new SubtaskNameUpdater(),
                new SubtaskDescriptionUpdater(),
                new SubtaskStatusUpdater());
    }

    private void applyCreateUpdates(Subtask subtask, SubtaskRequestBody body){
        for (SubtaskUpdater updater : updaters()){
            if (updater instanceof SubtaskCREATEUpdater createUpdater){
                createUpdater.CREATEUpdate(subtask,body);
            }
        }
    }

    private void applyPutUpdates(Subtask subtask, SubtaskRequestBody body){
        for (SubtaskUpdater updater : updaters()){
            if (updater instanceof SubtaskPUTUpdater putUpdater){
                putUpdater.PUTUpdate(subtask,body);
            }
        }
    }

    private void applyPatchUpdates(Subtask subtask, SubtaskRequestBody body){
        for (SubtaskUpdater updater : updaters()){
            if (updater instanceof SubtaskPATCHUpdater patchUpdater){
                patchUpdater.PATCHUpdate(subtask,body);
            }
        }
    }

    public Subtask getSubtaskByID(long taskID, long subtaskID){
        return subtaskRepository.findSubtaskByIdAndTaskId(subtaskID,taskID).orElseThrow(() -> new NotFoundException());
    }

    @Transactional
    public Subtask createSubtask(Task task, SubtaskRequestBody body){
        Subtask subtask = new Subtask();
        applyCreateUpdates(subtask,body);
        saveSubtask(subtask);
        taskService.addSubtaskToTask(task,subtask);
        return subtask;
    }

    /**
     * Unused, TODO: delete
     * @param taskID
     * @param subtaskID
     */
    @Transactional
    public void deleteSubtask(long taskID, long subtaskID){
        Subtask subtask = getSubtaskByID(taskID,subtaskID);
        Task task = subtask.getTask();
        task.getSubtasks().remove(subtask);
        taskService.saveTask(task);
        subtaskRepository.delete(subtask);
    }

    @Transactional
    public void deleteSubtask(Subtask subtask){
        Task task = subtask.getTask();
        taskService.removeSubtaskFromTask(task,subtask);
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
        applyPutUpdates(subtask,body);
        return saveSubtask(subtask);
    }

    public boolean checkIfSubtaskExistsByIDAndTask(long ID,Task task){
        return subtaskRepository.existsByIdAndTask(ID, task);
    }

    public boolean checkIfSubtaskExistsByID(long ID){
        return subtaskRepository.existsById(ID);
    }


    @Transactional
    public Subtask patchUpdate(Subtask subtask, SubtaskRequestBody body){
        applyPatchUpdates(subtask,body);
        return saveSubtask(subtask);
    }


    public boolean validateSubtasks(Set<Integer> subtaskIDs){
        if (subtaskIDs == null) return false;
        for (int id : subtaskIDs){
            if (!checkIfSubtaskExistsByID(id)){
                throw new BadRequestException("Tried to add subtask that doesn't exist");
            }
        }
        return true;
    }

    /**
     * Should only be called from TaskService.addSubtaskToTask method
     * @param subtask
     * @param task
     */
    public void setTaskToSubtask(Subtask subtask, Task task){
        subtask.setTask(task);
        saveSubtask(subtask);
    }


}

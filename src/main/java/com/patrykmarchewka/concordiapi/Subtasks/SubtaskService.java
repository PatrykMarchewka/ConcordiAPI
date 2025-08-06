package com.patrykmarchewka.concordiapi.Subtasks;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.DatabaseModel.SubtaskRepository;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.Exceptions.BadRequestException;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
import com.patrykmarchewka.concordiapi.Subtasks.Updaters.SubtaskUpdatersService;
import com.patrykmarchewka.concordiapi.Tasks.TaskService;
import com.patrykmarchewka.concordiapi.Teams.TeamService;
import com.patrykmarchewka.concordiapi.UpdateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

@Service
public class SubtaskService {


    private final SubtaskRepository subtaskRepository;
    private final TeamService teamService;
    private final SubtaskUpdatersService subtaskUpdatersService;

    @Autowired
    public SubtaskService(SubtaskRepository subtaskRepository, @Lazy TeamService teamService, SubtaskUpdatersService subtaskUpdatersService){
        this.subtaskRepository = subtaskRepository;
        this.teamService = teamService;
        this.subtaskUpdatersService = subtaskUpdatersService;
    }

    /**
     * Returns subtask based on task ID and subtask ID
     * @param taskID ID of task to check for
     * @param subtaskID ID of subtask to check for
     * @return Subtask with provided ID in provided task
     * @throws NotFoundException Thrown when subtask with provided ID and task ID doesn't exist
     */
    public Subtask getSubtaskByID(long taskID, long subtaskID){
        return subtaskRepository.findSubtaskByIdAndTaskId(subtaskID,taskID).orElseThrow(() -> new NotFoundException());
    }

    /**
     * Creates subtask with given body details
     * @param body SubtaskRequestBody with details of subtask to create
     * @param teamID ID of team in which to create the subtask
     * @return Created subtask
     */
    @Transactional
    public Subtask createSubtask(SubtaskRequestBody body, Long teamID){
        Subtask subtask = new Subtask();
        Supplier<Team> team = () -> teamService.getTeamByID(teamID);
        subtaskUpdatersService.update(subtask,body,team, UpdateType.CREATE);
        saveSubtask(subtask);
        return subtask;
    }

    /**
     * Should only be called from {@link TaskService#removeSubtaskFromTaskAndDelete(Task, Subtask)}
     * @param subtask Subtask to delete
     */
    @Transactional
    public void deleteSubtask(Subtask subtask){
        subtaskRepository.delete(subtask);
    }

    /**
     * Saves pending changes to subtask
     * @param subtask Subtask to save
     * @return Subtask after saved changes
     */
    public Subtask saveSubtask(Subtask subtask){
        return subtaskRepository.save(subtask);
    }

    /**
     * Returns SubtaskDTOs in given task
     * @param task Task to check in
     * @return Set of SubtaskDTO in provided task
     */
    public Set<SubtaskMemberDTO> getSubtasksDTO(Task task){
        Set<SubtaskMemberDTO> subtasks = new HashSet<>();
        for (Subtask sub : task.getSubtasks()){
            subtasks.add(new SubtaskMemberDTO(sub));
        }
        return subtasks;
    }

    /**
     * Edit subtask completely with new values
     * @param subtask Subtask to edit
     * @param body SubtaskRequestBody with new values
     * @param teamID ID of the team containing the subtask
     * @return Subtask after changes
     */
    @Transactional
    public Subtask putUpdate(Subtask subtask, SubtaskRequestBody body, Long teamID){
        Supplier<Team> team = () -> teamService.getTeamByID(teamID);
        subtaskUpdatersService.update(subtask,body,team,UpdateType.PUT);
        return saveSubtask(subtask);
    }

    /**
     * Unused, checks whether subtask exists provided ID and task ID
     * @param ID ID of the subtask to check for
     * @param task ID of the team to check in
     * @return True if subtask with given ID in task with specified ID exists, otherwise false
     */
    public boolean checkIfSubtaskExistsByIDAndTask(long ID,Task task){
        return subtaskRepository.existsByIdAndTask(ID, task);
    }

    /**
     * Checks whether subtask exists by provided ID
     * @param ID ID of the subtask to check for
     * @return True if subtask with given ID exists, otherwise false
     */
    public boolean checkIfSubtaskExistsByID(long ID){
        return subtaskRepository.existsById(ID);
    }

    /**
     * Edits subtask with new values
     * @param subtask Subtask to edit
     * @param body SubtaskRequestBody with new values
     * @param teamID ID of the team containing the subtask
     * @return Subtask after changes
     */
    @Transactional
    public Subtask patchUpdate(Subtask subtask, SubtaskRequestBody body, Long teamID){
        Supplier<Team> team = () -> teamService.getTeamByID(teamID);
        subtaskUpdatersService.update(subtask,body,team,UpdateType.PATCH);
        return saveSubtask(subtask);
    }

    /**
     * Checks whether subtasks with provided IDs exist
     * @param subtaskIDs Set of IDs to check
     * @return True if all subtasks with provided IDs exist
     * @throws  BadRequestException Thrown when one or more subtasks with provided ID don't exist
     */
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
     * Sets subtask to specified task ,should only be called from {@link TaskService#addSubtaskToTask(Task, Subtask)}
     * @param subtask Subtask to change task of
     * @param task Task to attach
     */
    public void setTaskToSubtask(Subtask subtask, Task task){
        subtask.setTask(task);
        saveSubtask(subtask);
    }


}

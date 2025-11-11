package com.patrykmarchewka.concordiapi.Subtasks;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.DatabaseModel.SubtaskRepository;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
import com.patrykmarchewka.concordiapi.HydrationContracts.Subtask.SubtaskIdentity;
import com.patrykmarchewka.concordiapi.Subtasks.Updaters.SubtaskUpdatersService;
import com.patrykmarchewka.concordiapi.Tasks.TaskService;
import com.patrykmarchewka.concordiapi.Teams.TeamService;
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
    private final TaskService taskService;

    @Autowired
    public SubtaskService(SubtaskRepository subtaskRepository, @Lazy TeamService teamService, SubtaskUpdatersService subtaskUpdatersService, @Lazy TaskService taskService){
        this.subtaskRepository = subtaskRepository;
        this.teamService = teamService;
        this.subtaskUpdatersService = subtaskUpdatersService;
        this.taskService = taskService;
    }

    /**
     * Returns subtask based on task ID and subtask ID
     * @param taskID ID of task to check for
     * @param subtaskID ID of subtask to check for
     * @return Subtask with provided ID in provided task
     * @throws NotFoundException Thrown when subtask with provided ID and task ID doesn't exist
     */
    public Subtask getSubtaskEntityByID(long taskID, long subtaskID){
        return subtaskRepository.findSubtaskEntityByIDAndTaskID(subtaskID,taskID).orElseThrow(() -> new NotFoundException());
    }

    /**
     * Returns subtask based on task ID and subtask ID
     * @param taskID ID of task to check for
     * @param subtaskID ID of subtask to check for
     * @return Subtask with provided ID in provided task
     * @throws NotFoundException Thrown when subtask with provided ID and task ID doesn't exist
     */
    public SubtaskIdentity getSubtaskByID(final long taskID, final long subtaskID){
        return subtaskRepository.findSubtaskIdentityByIDAndTaskID(subtaskID, taskID).orElseThrow(NotFoundException::new);
    }

    /**
     * Creates subtask with given body details
     * @param body SubtaskRequestBody with details of subtask to create
     * @param teamID ID of team in which to create the subtask
     * @param taskID ID of the Task in which to add subtask to
     * @return Created subtask
     */
    @Transactional
    public Subtask createSubtask(SubtaskRequestBody body, long teamID, long taskID){
        Subtask subtask = new Subtask();
        Team team = teamService.getTeamEntityByID(teamID);
        Supplier<Task> task = () -> (Task) taskService.getTaskWithSubtasksByIDAndTeamID(taskID,team.getID());
        subtaskUpdatersService.createUpdate(subtask, body, task);
        return saveSubtask(subtask);
    }

    /**
     * Edit subtask completely with new values
     * @param subtask Subtask to edit
     * @param body SubtaskRequestBody with new values
     * @return Subtask after changes
     */
    @Transactional
    public Subtask putUpdate(Subtask subtask, SubtaskRequestBody body){
        subtaskUpdatersService.putUpdate(subtask, body);
        return saveSubtask(subtask);
    }

    /**
     * Edits subtask with new values
     * @param subtask Subtask to edit
     * @param body SubtaskRequestBody with new values
     * @return Subtask after changes
     */
    @Transactional
    public Subtask patchUpdate(Subtask subtask, SubtaskRequestBody body){
        subtaskUpdatersService.patchUpdate(subtask, body);
        return saveSubtask(subtask);
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
    public Set<SubtaskMemberDTO> getSubtasksDTO(final Task task){
        final Set<SubtaskMemberDTO> subtasks = new HashSet<>();
        for (final Subtask sub : task.getSubtasks()){
            subtasks.add(new SubtaskMemberDTO(sub));
        }
        return subtasks;
    }

    /**
     * Deletes everything and flushes
     */
    public void deleteAll(){
        subtaskRepository.deleteAll();
        subtaskRepository.flush();
    }
}

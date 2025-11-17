package com.patrykmarchewka.concordiapi.Subtasks;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.DatabaseModel.SubtaskRepository;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
import com.patrykmarchewka.concordiapi.HydrationContracts.Subtask.SubtaskIdentity;
import com.patrykmarchewka.concordiapi.HydrationContracts.Task.TaskWithSubtasks;
import com.patrykmarchewka.concordiapi.Subtasks.Updaters.SubtaskUpdatersService;
import com.patrykmarchewka.concordiapi.Tasks.TaskService;
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
    private final SubtaskUpdatersService subtaskUpdatersService;
    private final TaskService taskService;

    @Autowired
    public SubtaskService(SubtaskRepository subtaskRepository, SubtaskUpdatersService subtaskUpdatersService, @Lazy TaskService taskService){
        this.subtaskRepository = subtaskRepository;
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
        Supplier<Task> task = () -> (Task) taskService.getTaskWithSubtasksByIDAndTeamID(taskID,teamID);
        subtaskUpdatersService.createUpdate(subtask, body, task);
        return saveSubtask(subtask);
    }

    /**
     * Edit subtask completely with new values
     * @param taskID ID of Task in which subtask is
     * @param subtaskID ID of Subtask to edit
     * @param body SubtaskRequestBody with new values
     * @return Subtask after changes
     */
    @Transactional
    public SubtaskIdentity putUpdate(long taskID, long subtaskID, SubtaskRequestBody body){
        Subtask subtask = (Subtask) getSubtaskByID(taskID, subtaskID);
        subtaskUpdatersService.putUpdate(subtask, body);
        return saveSubtask(subtask);
    }

    /**
     * Edits subtask with new values
     * @param taskID ID of Task in which subtask is
     * @param subtaskID ID of Subtask to edit
     * @param body SubtaskRequestBody with new values
     * @return Subtask after changes
     */
    @Transactional
    public SubtaskIdentity patchUpdate(long taskID, long subtaskID, SubtaskRequestBody body){
        Subtask subtask = (Subtask) getSubtaskByID(taskID, subtaskID);
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
     * Deletes Subtask completely
     * @param taskID ID of Task in which subtask exists
     * @param subtaskID ID of Subtask to delete
     */
    @Transactional
    public void deleteSubtask(long taskID, long subtaskID){
        Subtask subtask = (Subtask) getSubtaskByID(taskID, subtaskID);
        subtaskRepository.delete(subtask);
    }

    /**
     * Returns SubtaskDTOs in given task
     * @param task Task to check in
     * @return Set of SubtaskDTO in provided task
     */
    public Set<SubtaskMemberDTO> getSubtasksDTO(final TaskWithSubtasks task){
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

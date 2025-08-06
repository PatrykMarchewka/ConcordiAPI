package com.patrykmarchewka.concordiapi.Subtasks;

import com.patrykmarchewka.concordiapi.APIResponse;
import com.patrykmarchewka.concordiapi.ControllerContext;
import com.patrykmarchewka.concordiapi.DTO.OnCreate;
import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.Exceptions.NoPrivilegesException;
import com.patrykmarchewka.concordiapi.Tasks.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/teams/{teamID}/tasks/{taskID}")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Subtasks", description = "Managing subtasks assigned to task")
public class SubtaskController {
    private final SubtaskService subtaskService;
    private final TaskService taskService;
    private ControllerContext context;

    @Autowired
    public SubtaskController(SubtaskService subtaskService,TaskService taskService, ControllerContext context){
        this.subtaskService = subtaskService;
        this.taskService = taskService;
        this.context = context;
    }

    /**
     * Returns subtasks attached to the given task
     * @param teamID ID of the team to check in
     * @param taskID ID of the task to check in
     * @param authentication User credentials to authenticate
     * @return SubtaskDTO with all the subtasks in the task
     * @throws NoPrivilegesException Thrown when the user role is neither Owner, Admin, nor Manager, and they are not attached to the task.
     */
    @Operation(summary = "Check subtasks",description = "Check all subtasks for the given team and task")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @GetMapping("/subtasks")
    public ResponseEntity<APIResponse<Set<SubtaskMemberDTO>>> getSubtasks(@PathVariable long teamID, @PathVariable long taskID, Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withRole().withTask(taskID);
        if (!context.getUserRole().isAdminGroup() && !context.getTask().hasUser(context.getUser())){
            throw new NoPrivilegesException();
        }
        return ResponseEntity.ok(new APIResponse<>("Subtasks attached to this task",subtaskService.getSubtasksDTO(context.getTask())));
    }

    /**
     * Creates new subtask
     * @param teamID ID of the team to check in
     * @param taskID ID of the task to check for
     * @param body SubtaskRequestBody with subtask information
     * @param authentication User credentials to authenticate
     * @return SubtaskDTO of the created subtask
     * @throws NoPrivilegesException Thrown when the user role is neither Owner, Admin, nor Manager, and they are not attached to the task.
     */
    @Operation(summary = "Create new subtask", description = "Create new subtask for the given team and task")
    @ApiResponse(responseCode = "201", ref = "201")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @PostMapping("/subtasks")
    public ResponseEntity<APIResponse<SubtaskMemberDTO>> createSubtask(@PathVariable long teamID, @PathVariable long taskID, @RequestBody @Validated(OnCreate.class) SubtaskRequestBody body, Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withTask(taskID).withRole();

        if (!context.getUserRole().isAdminGroup() && !context.getTask().hasUser(context.getUser())){
            throw new NoPrivilegesException();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new APIResponse<>("Subtask created", new SubtaskMemberDTO(subtaskService.createSubtask(body,() -> teamID))));

    }

    /**
     * Returns information abotu specific subtask
     * @param teamID ID of the team to check in
     * @param taskID ID of the task to check in
     * @param ID ID of the subtask to check for
     * @param authentication User credentials to authenticate
     * @return SubtaskDTO of the specified subtask
     * @throws NoPrivilegesException Thrown when the user role is neither Owner, Admin, nor Manager, and they are not attached to the task.
     */
    @Operation(summary = "Check specific subtask", description = "Check information about specific subtask for the given team and task")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @GetMapping("/subtasks/{ID}")
    public ResponseEntity<APIResponse<SubtaskMemberDTO>> getSubtaskByID(@PathVariable long teamID,@PathVariable long taskID, @PathVariable long ID, Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withRole().withTask(taskID);

        if (!context.getUserRole().isAdminGroup() && !context.getTask().hasUser(context.getUser())){
            throw new NoPrivilegesException();
        }
        return ResponseEntity.ok(new APIResponse<>("Subtask details",new SubtaskMemberDTO(subtaskService.getSubtaskByID(taskID,ID))));
    }

    /**
     * Replaces all subtask information with new values
     * @param teamID ID of the team to check in
     * @param taskID ID of the task to check in
     * @param ID ID of the subtask to edit
     * @param body SubtaskRequestBody with new values
     * @param authentication User credentials to authenticate
     * @return SubtaskDTO after changes
     * @throws NoPrivilegesException Thrown when the user role is neither Owner, Admin, nor Manager, and they are not attached to the task.
     */
    @Operation(summary = "Edit entire subtask", description = "Edits entire subtask with all required fields")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @PutMapping("/subtasks/{ID}")
    public ResponseEntity<APIResponse<SubtaskMemberDTO>> putSubtask(@PathVariable long teamID, @PathVariable long taskID, @PathVariable long ID, @RequestBody @Validated(OnCreate.class) SubtaskRequestBody body, Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withRole().withTask(taskID);
        if (!context.getUserRole().isAdminGroup() && !context.getTask().hasUser(context.getUser())){
            throw new NoPrivilegesException();
        }
        Subtask subtask = subtaskService.getSubtaskByID(taskID,ID);
        return ResponseEntity.ok(new APIResponse<>("Subtask changed", new SubtaskMemberDTO(subtaskService.putUpdate(subtask,body,() -> teamID))));
    }

    /**
     * Patches subtask information
     * @param teamID ID of the team to check in
     * @param taskID ID of the task to check in
     * @param ID ID of the subtask to edit
     * @param body SubtaskRequestBody with new values
     * @param authentication User credentials to authenticate
     * @return SubtaskDTO after changes
     * @throws NoPrivilegesException Thrown when the user role is neither Owner, Admin, nor Manager, and they are not attached to the task.
     */
    @Operation(summary = "Edit subtask", description = "Edit subtask fields for the given team and task")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @PatchMapping("/subtasks/{ID}")
    public ResponseEntity<APIResponse<SubtaskMemberDTO>> patchSubtask(@PathVariable long teamID,@PathVariable long taskID, @PathVariable long ID, @RequestBody SubtaskRequestBody body, Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withRole().withTask(taskID);
        if (!context.getUserRole().isAdminGroup() && !context.getTask().hasUser(context.getUser())){
            throw new NoPrivilegesException();
        }
        Subtask subtask = subtaskService.getSubtaskByID(taskID,ID);
        return ResponseEntity.ok(new APIResponse<>("Subtask updated",new SubtaskMemberDTO(subtaskService.patchUpdate(subtask,body))));
    }

    /**
     * Deletes subtask
     * @param teamID ID of the team to check in
     * @param taskID ID of the task to check in
     * @param ID ID of the subtask to delete
     * @param authentication User credentials to authenticate
     * @return Message that subtask has been deleted
     * @throws NoPrivilegesException Thrown when user is not Owner,Admin or Manager in the team
     */
    @Operation(summary = "Delete the subtask",description = "Delete the subtask entirely")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @DeleteMapping("/subtasks/{ID}")
    public ResponseEntity<APIResponse<String>> deleteSubtask(@PathVariable long teamID,@PathVariable long taskID, @PathVariable long ID, Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withRole().withTask(taskID);
        if (!context.getUserRole().isOwnerOrAdmin()){
            throw new NoPrivilegesException();
        }
        taskService.removeSubtaskFromTaskAndDelete(context.getTask(), subtaskService.getSubtaskByID(taskID,ID));
        return ResponseEntity.ok(new APIResponse<>("Subtask deleted", null));
    }

}

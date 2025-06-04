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
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

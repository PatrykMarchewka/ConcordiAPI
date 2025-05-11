package com.patrykmarchewka.concordiapi;

import com.patrykmarchewka.concordiapi.DTO.OnCreate;
import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.Exceptions.NoPrivilegesException;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
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

    @Autowired
    private SubtaskService subtaskService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private TeamUserRoleService teamUserRoleService;

    @Operation(summary = "Check subtasks",description = "Check all subtasks for the given team and task")
    @GetMapping("/subtasks")
    public ResponseEntity<?> getSubtasks(@PathVariable long teamID,@PathVariable long taskID, Authentication authentication){
        ControllerContext context = ControllerContext.forSubtasks(authentication,teamID,taskID,teamService,taskService,teamUserRoleService);

        if (!context.getUserRole().isAdminGroup() && !context.getTask().hasUser(context.getUser())){
            throw new NoPrivilegesException();
        }
        return ResponseEntity.ok(new APIResponse<>("Subtasks attached to this task",subtaskService.getSubtasksDTO(context.getTask())));
    }
    @Operation(summary = "Create new subtask", description = "Create new subtask for the given team and task")
    @PostMapping("/subtasks")
    public ResponseEntity<?> createSubtask(@PathVariable long teamID, @PathVariable long taskID, @RequestBody @Validated(OnCreate.class) SubtaskRequestBody body, Authentication authentication){
        ControllerContext context = ControllerContext.forSubtasks(authentication,teamID,taskID,teamService,taskService,teamUserRoleService);

        if (!context.getUserRole().isAdminGroup() && !context.getTask().hasUser(context.getUser())){
            throw new NoPrivilegesException();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new APIResponse<>("Subtask created", new SubtaskMemberDTO(subtaskService.createSubtask(context.getTeam(), taskID, body.getName(), body.getDescription()))));

    }

    @Operation(summary = "Check specific subtask", description = "Check information about specific subtask for the given team and task")
    @GetMapping("/subtasks/{ID}")
    public ResponseEntity<?> getSubtaskByID(@PathVariable long teamID,@PathVariable long taskID, @PathVariable long ID, Authentication authentication){
        ControllerContext context = ControllerContext.forSubtasks(authentication,teamID,taskID,teamService,taskService,teamUserRoleService);

        if (!context.getUserRole().isAdminGroup() && !context.getTask().hasUser(context.getUser())){
            throw new NoPrivilegesException();
        }
        return ResponseEntity.ok(new APIResponse<>("Subtask details",new SubtaskMemberDTO(subtaskService.getSubtaskByID(taskID,ID))));
    }

    @Operation(summary = "Edit entire subtask", description = "Edits entire subtask with all required fields")
    @PutMapping("/subtasks/{ID}")
    public ResponseEntity<?> putSubtask(@PathVariable long teamID, @PathVariable long taskID, @PathVariable long ID, @RequestBody @Validated(OnCreate.class) SubtaskRequestBody body, Authentication authentication){
        ControllerContext context = ControllerContext.forSubtasks(authentication,teamID,taskID,teamService,taskService,teamUserRoleService);
        if (!context.getUserRole().isAdminGroup() && !context.getTask().hasUser(context.getUser())){
            throw new NoPrivilegesException();
        }
        Subtask subtask = subtaskService.getSubtaskByID(taskID,ID);
        if (subtask == null){
            throw new NotFoundException();
        }
        return ResponseEntity.ok(new APIResponse<>("Subtask changed", new SubtaskMemberDTO(subtaskService.putUpdate(subtask,body))));
    }

    @Operation(summary = "Edit subtask", description = "Edit subtask fields for the given team and task")
    @PatchMapping("/subtasks/{ID}")
    public ResponseEntity<?> patchSubtask(@PathVariable long teamID,@PathVariable long taskID, @PathVariable long ID, @RequestBody SubtaskRequestBody body, Authentication authentication){
        ControllerContext context = ControllerContext.forSubtasks(authentication,teamID,taskID,teamService,taskService,teamUserRoleService);
        if (!context.getUserRole().isAdminGroup() && !context.getTask().hasUser(context.getUser())){
            throw new NoPrivilegesException();
        }
        Subtask subtask = subtaskService.getSubtaskByID(taskID,ID);
        if (subtask == null){
            throw new NotFoundException();
        }
        return ResponseEntity.ok(new APIResponse<>("Subtask updated",new SubtaskMemberDTO(subtaskService.partialUpdate(subtask,body))));
    }

    @Operation(summary = "Delete the subtask",description = "Delete the subtask entirely")
    @DeleteMapping("/subtasks/{ID}")
    public ResponseEntity<?> deleteSubtask(@PathVariable long teamID,@PathVariable long taskID, @PathVariable long ID, Authentication authentication){
        ControllerContext context = ControllerContext.forSubtasks(authentication,teamID,taskID,teamService,taskService,teamUserRoleService);
        if (!context.getUserRole().isOwnerOrAdmin()){
            throw new NoPrivilegesException();
        }
        Subtask subtask = subtaskService.getSubtaskByID(taskID,ID);
        if (subtask == null){
            throw new NotFoundException();
        }
        subtaskService.deleteSubtask(taskID,ID);
        return ResponseEntity.ok(new APIResponse<>("Subtask deleted", null));
    }

}

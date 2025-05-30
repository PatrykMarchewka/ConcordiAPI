package com.patrykmarchewka.concordiapi.Tasks;

import com.patrykmarchewka.concordiapi.*;
import com.patrykmarchewka.concordiapi.DTO.OnCreate;
import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.Exceptions.NoPrivilegesException;
import com.patrykmarchewka.concordiapi.Users.UserService;
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
@RequestMapping("/api/teams/{teamID}")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Tasks", description = "Managing tasks assigned to team")
public class TaskController {


    private final TaskService taskService;
    private final UserService userService;
    private final TeamUserRoleService teamUserRoleService;
    private ControllerContext context;

    @Autowired
    public TaskController(TaskService taskService, UserService userService, TeamUserRoleService teamUserRoleService, ControllerContext context){
        this.taskService = taskService;
        this.userService = userService;
        this.teamUserRoleService = teamUserRoleService;
        this.context = context;
    }

    @Operation(summary = "Get all tasks",description = "Get all tasks if Owner/Admin/Manager or just tasks assigned to me if Member")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @GetMapping("/tasks")
    public ResponseEntity<APIResponse<Set<?>>> getAllTasks(@PathVariable long teamID, Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withRole();
        if (!context.getUserRole().isAllowedBasic()){
            throw new NoPrivilegesException();
        }

        return ResponseEntity.ok(new APIResponse<>("All tasks available", taskService.getAllTasksRole(context.getUserRole(), context.getTeam(), context.getUser())));
    }

    @Operation(summary = "Create new task", description = "Creates a new task with specified information")
    @ApiResponse(responseCode = "201", ref = "201")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @PostMapping("/tasks")
    public ResponseEntity<APIResponse<TaskMemberDTO>> createTask(@PathVariable long teamID, @RequestBody @Validated(OnCreate.class) TaskRequestBody body, Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withRole();
        if (!context.getUserRole().isAllowedBasic()){
            throw new NoPrivilegesException();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new APIResponse<>("Created new task",new TaskMemberDTO(taskService.createTask(body, context.getTeam()))));
    }

    @Operation(summary = "Get all tasks assigned to me", description = "Gets all tasks assigned to me even if user is Owner/Admin/Manager")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @GetMapping("/tasks/me")
    public ResponseEntity<APIResponse<Set<?>>> getAllTasksAssignedToMe(@PathVariable long teamID,Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withRole();
        if (!context.getUserRole().isAllowedBasic()){
            throw new NoPrivilegesException();
        }
        return ResponseEntity.ok(new APIResponse<>("Tasks assigned to me",taskService.getMyTasksRole(context.getUserRole(), context.getUser())));

    }

    @Operation(summary = "Get information about task",description = "Get information about specific task by its ID")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @GetMapping("/tasks/{ID}")
    public ResponseEntity<APIResponse<Object>> getTaskByID(@PathVariable long teamID,@PathVariable long ID, Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withRole().withTask(ID);
        return ResponseEntity.ok(new APIResponse<>("Task details", taskService.getInformationAboutTaskRole(context.getUserRole(), context.getTask(), context.getUser())));
        
    }

    @Operation(summary = "Edit completely task",description = "Edits the entire task with all required fields")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @PutMapping("/tasks/{ID}")
    public ResponseEntity<APIResponse<TaskMemberDTO>> putTask(@PathVariable long teamID, @PathVariable long ID, @RequestBody @Validated(OnCreate.class) TaskRequestBody body, Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withRole().withTask(ID);
        if (!taskService.putTaskRole(context.getUserRole(), context.getTask(), context.getUser())){
            throw new NoPrivilegesException();
        }
        return ResponseEntity.ok(new APIResponse<>("Task fully changed",new TaskMemberDTO(taskService.putTask(body, context.getTeam(), context.getTask()))));
    }

    @Operation(summary = "Edit task",description = "Edit task fields")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @PatchMapping("/tasks/{ID}")
    public ResponseEntity<APIResponse<TaskMemberDTO>> patchTask(@PathVariable long teamID,@PathVariable long ID, @RequestBody TaskRequestBody body, Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withRole().withTask(ID);
        if (!context.getUserRole().isAdminGroup()){
            throw new NoPrivilegesException();
        }
        return ResponseEntity.ok(new APIResponse<>("Task updated",new TaskMemberDTO(taskService.patchTask(context.getTask(), body, context.getTeam()))));
    }

    @Operation(summary = "Delete the task", description = "Delete the entire task completely")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @DeleteMapping("/tasks/{ID}")
    public ResponseEntity<APIResponse<Void>> deleteTask(@PathVariable long teamID,@PathVariable long ID, Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withRole();
        if (!context.getUserRole().isOwnerOrAdmin()){
            throw new NoPrivilegesException();
        }
        return ResponseEntity.ok().body(new APIResponse<>("Task has been deleted",null));
    }

    @Operation(summary = "Attach user to task",description = "Assigns the task to given user")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @PostMapping("/tasks/{ID}/users/{userID}")
    public ResponseEntity<?> addOneUserToTask(@PathVariable long teamID, @PathVariable long ID,@PathVariable long userID, Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withRole().withOtherRole(userService.getUserByID(userID));

        if (!teamUserRoleService.checkRoles.test(context.getUserRole(),context.getOtherRole())){
            throw new NoPrivilegesException();
        }

        taskService.addUserToTask(context.getTask(), userService.getUserByID(userID));
        return ResponseEntity.ok(new APIResponse<>("User added to task",new TaskMemberDTO(taskService.getTaskByIDAndTeam(ID, context.getTeam()))));

    }

    @Operation(summary = "Remove user from task", description = "Removes user from the task")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @DeleteMapping("/tasks/{ID}/users/{userID}")
    public ResponseEntity<?> deleteOneUserFromTask(@PathVariable long teamID, @PathVariable long ID,@PathVariable long userID, Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withRole().withOtherRole(userService.getUserByID(userID));

        if (!teamUserRoleService.checkRoles.test(context.getUserRole(),context.getOtherRole())){
            throw new NoPrivilegesException();
        }

        taskService.removeUserFromTask(context.getTask(), userService.getUserByID(userID));
        return ResponseEntity.ok(new APIResponse<>("User removed from task",new TaskMemberDTO(taskService.getTaskByIDAndTeam(ID, context.getTeam()))));
    }

}

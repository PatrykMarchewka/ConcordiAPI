package com.patrykmarchewka.concordiapi.Tasks;


import com.patrykmarchewka.concordiapi.APIResponse;
import com.patrykmarchewka.concordiapi.ControllerContext;
import com.patrykmarchewka.concordiapi.DTO.OnCreate;
import com.patrykmarchewka.concordiapi.DTO.OnPut;
import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DTO.ValidateGroup;
import com.patrykmarchewka.concordiapi.Exceptions.NoPrivilegesException;
import com.patrykmarchewka.concordiapi.Teams.TeamUserRoleService;
import com.patrykmarchewka.concordiapi.Users.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

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


    /**
     * Returns all tasks, optionally filtered to inactive ones
     * @param teamID ID of the team to check
     * @param authentication User credentials to authenticate
     * @param inactivedays Optional parameter to filter, number of days for task to be considered inactive
     * @return TaskMemberDTO of all tasks avaible or all inactive tasks
     * @throws NoPrivilegesException Thrown when user is not Owner,Admin,Manager or Member in the team
     */
    //param, ?inactivedays=5
    @Operation(summary = "Get all tasks or filter by inactive using parameter",description = "Get all tasks if Owner/Admin/Manager or just tasks assigned to me if Member, can be filtered to inactive only")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @GetMapping("/tasks")
    public ResponseEntity<APIResponse<Set<TaskMemberDTO>>> getAllTasks(@PathVariable long teamID, Authentication authentication, @RequestParam(required = false) Integer inactivedays){
        context = context.withUser(authentication).withTeam(teamID).withRole();
        if (!context.getUserRole().isAllowedBasic()){
            throw new NoPrivilegesException();
        }

        if (inactivedays != null){
            return ResponseEntity.ok(new APIResponse<>("All inactive tasks available",taskService.getInactiveTasks(inactivedays, teamID)));
        }

        return ResponseEntity.ok(new APIResponse<>("All tasks available", taskService.getAllTasksWithRoleCheck(context.getUser().getID(), teamID, context.getUserRole())));
    }

    /**
     * Creates new task
     * @param teamID ID of the team in which task has to be created
     * @param body TaskRequestBody of the task to create
     * @param authentication User credentials to authenticate
     * @return TaskMemberDTO of the created task
     * @throws NoPrivilegesException Thrown when user is not Owner,Admin,Manager or Member in the team
     */
    @Operation(summary = "Create new task", description = "Creates a new task with specified information")
    @ApiResponse(responseCode = "201", ref = "201")
    @ApiResponse(responseCode = "400", ref = "400")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @PostMapping("/tasks")
    public ResponseEntity<APIResponse<TaskMemberDTO>> createTask(@PathVariable long teamID, @RequestBody @ValidateGroup(OnCreate.class) TaskRequestBody body, Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withRole();
        if (!context.getUserRole().isAllowedBasic()){
            throw new NoPrivilegesException();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new APIResponse<>("Created new task",new TaskMemberDTO(taskService.createTask(body, context.getTeam()))));
    }

    /**
     * Returns all tasks assigned to user
     * @param teamID ID of the team to check in
     * @param authentication User credentials to authenticate
     * @return TaskMemberDTO of all tasks assigned to user
     * @throws NoPrivilegesException Thrown when user is not Owner,Admin,Manager or Member in the team
     */
    @Operation(summary = "Get all tasks assigned to me", description = "Gets all tasks assigned to me even if user is Owner/Admin/Manager")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @GetMapping("/tasks/me")
    public ResponseEntity<APIResponse<Set<TaskMemberDTO>>> getAllTasksAssignedToMe(@PathVariable long teamID, Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withRole();
        if (!context.getUserRole().isAllowedBasic()){
            throw new NoPrivilegesException();
        }
        return ResponseEntity.ok(new APIResponse<>("Tasks assigned to me", taskService.getAllTasksAssignedToMe(teamID, context.getUser().getID())));

    }

    /**
     * Returns information about specified task
     * @param teamID ID of the team to check in
     * @param ID ID of the task to check for
     * @param authentication User credentials to authenticate
     * @return TaskMemberDTO of the specified task
     */
    @Operation(summary = "Get information about task",description = "Get information about specific task by its ID")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "404", ref = "404")
    @GetMapping("/tasks/{ID}")
    public ResponseEntity<APIResponse<TaskMemberDTO>> getTaskByID(@PathVariable long teamID,@PathVariable long ID, Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withRole().withTask(ID);
        return ResponseEntity.ok(new APIResponse<>("Task details", new TaskMemberDTO(taskService.getTaskFullByIDAndTeamID(ID, teamID))));
        
    }

    /**
     * Replaces all task information with new values
     * @param teamID ID of the team to check in
     * @param ID ID of the task to replace information of
     * @param body TaskRequestBody with new values
     * @param authentication User credentials to authenticate
     * @return TaskMemberDTO after changes
     * @throws NoPrivilegesException Thrown when user doesn't have enough privileges to execute PUT on task
     */
    @Operation(summary = "Edit completely task",description = "Edits the entire task with all required fields")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "400", ref = "400")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @PutMapping("/tasks/{ID}")
    public ResponseEntity<APIResponse<TaskMemberDTO>> putTask(@PathVariable long teamID, @PathVariable long ID, @RequestBody @ValidateGroup(OnPut.class) TaskRequestBody body, Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withTask(ID);
        return ResponseEntity.ok(new APIResponse<>("Task fully changed",new TaskMemberDTO(taskService.putTask(body, context.getUser().getID(), context.getTeam(), context.getTask()))));
    }

    /**
     * Patches task information
     * @param teamID ID of the team to check in
     * @param ID ID of the task to edit
     * @param body TaskRequestBody with new values
     * @param authentication User credentials to authenticate
     * @return TaskMemberDTO after changes
     * @throws NoPrivilegesException Thrown when user is not Owner,Admin or Manager in the team
     */
    @Operation(summary = "Edit task",description = "Edit task fields")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "400", ref = "400")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @PatchMapping("/tasks/{ID}")
    public ResponseEntity<APIResponse<TaskMemberDTO>> patchTask(@PathVariable long teamID,@PathVariable long ID, @RequestBody @ValidateGroup TaskRequestBody body, Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withTask(ID);
        return ResponseEntity.ok(new APIResponse<>("Task updated",new TaskMemberDTO(taskService.patchTask(body, context.getUser().getID(), context.getTeam(), context.getTask()))));
    }

    /**
     * Deletes task with all subtasks associated with it
     * @param teamID ID of the team to check in
     * @param ID ID of the task to delete
     * @param authentication User credentials to authenticate
     * @return Message that task has been deleted
     * @throws NoPrivilegesException Thrown when user is not Owner,Admin or Manager in the team
     */
    @Operation(summary = "Delete the task", description = "Delete the entire task completely")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @DeleteMapping("/tasks/{ID}")
    public ResponseEntity<APIResponse<String>> deleteTask(@PathVariable long teamID,@PathVariable long ID, Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withRole();
        if (!context.getUserRole().isOwnerOrAdmin()){
            throw new NoPrivilegesException();
        }
        taskService.deleteTaskByID(ID, context.getTeam());
        return ResponseEntity.ok().body(new APIResponse<>("Task has been deleted",null));
    }

    /**
     * Adds given user to the task
     * @param teamID ID of the team to check in
     * @param ID ID of the task to edit
     * @param userID ID of the user to attach to task
     * @param authentication User credentials to authenticate
     * @return TaskDTO after changes
     * @throws NoPrivilegesException Thrown when user tries to add another user that has higher role
     */
    @Operation(summary = "Attach user to task",description = "Assigns the task to given user")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @PostMapping("/tasks/{ID}/users/{userID}")
    public ResponseEntity<APIResponse<TaskMemberDTO>> addOneUserToTask(@PathVariable long teamID, @PathVariable long ID,@PathVariable long userID, Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withTask(ID).withRole().withOtherRole(userID);

        teamUserRoleService.forceCheckRoles(context.getUserRole(), context.getOtherRole());

        taskService.addUserToTask(context.getTask(), userService.getUserByID(userID));
        return ResponseEntity.ok(new APIResponse<>("User added to task",new TaskMemberDTO(taskService.getTaskByIDAndTeam(ID, context.getTeam()))));

    }

    /**
     * Removes given user from the task
     * @param teamID ID of the team to check in
     * @param ID ID of the task to edit
     * @param userID ID of the user to remove from task
     * @param authentication User credentials to authenticate
     * @return TaskDTO after changes
     * @throws NoPrivilegesException Thrown when user tries to remove another user that has higher role
     */
    @Operation(summary = "Remove user from task", description = "Removes user from the task")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @DeleteMapping("/tasks/{ID}/users/{userID}")
    public ResponseEntity<APIResponse<TaskMemberDTO>> deleteOneUserFromTask(@PathVariable long teamID, @PathVariable long ID,@PathVariable long userID, Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withTask(ID).withRole().withOtherRole(userID);

        teamUserRoleService.forceCheckRoles(context.getUserRole(), context.getOtherRole());

        taskService.removeUserFromTask(context.getTask(), userID);
        return ResponseEntity.ok(new APIResponse<>("User removed from task",new TaskMemberDTO(taskService.getTaskFullByIDAndTeamID(ID, teamID))));
    }

}

package com.patrykmarchewka.concordiapi.Tasks;


import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.DatabaseModel.TaskRepository;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.DatabaseModel.UserTask;
import com.patrykmarchewka.concordiapi.DatabaseModel.UserTaskRepository;
import com.patrykmarchewka.concordiapi.Exceptions.BadRequestException;
import com.patrykmarchewka.concordiapi.Exceptions.ImpossibleStateException;
import com.patrykmarchewka.concordiapi.Exceptions.NoPrivilegesException;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
import com.patrykmarchewka.concordiapi.HydrationContracts.Task.TaskFull;
import com.patrykmarchewka.concordiapi.HydrationContracts.Task.TaskIdentity;
import com.patrykmarchewka.concordiapi.HydrationContracts.Task.TaskWithSubtasks;
import com.patrykmarchewka.concordiapi.HydrationContracts.Task.TaskWithUserTasks;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamWithUserRoles;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamWithUserRolesAndTasks;
import com.patrykmarchewka.concordiapi.OffsetDateTimeConverter;
import com.patrykmarchewka.concordiapi.TaskStatus;
import com.patrykmarchewka.concordiapi.Tasks.Updaters.TaskUpdatersService;
import com.patrykmarchewka.concordiapi.Teams.TeamUserRoleService;
import com.patrykmarchewka.concordiapi.UserRole;
import com.patrykmarchewka.concordiapi.Users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final TaskUpdatersService taskUpdatersService;
    private final UserTaskRepository userTaskRepository;
    private final TeamUserRoleService teamUserRoleService;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserService userService, TaskUpdatersService taskUpdatersService, UserTaskRepository userTaskRepository, TeamUserRoleService teamUserRoleService){
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.taskUpdatersService = taskUpdatersService;
        this.userTaskRepository = userTaskRepository;
        this.teamUserRoleService = teamUserRoleService;
    }

    private Supplier<Set<TaskFull>> getAllowedTasks(final long userID, final long teamID){
        UserRole role = teamUserRoleService.getRole(userID, teamID);
        return switch (role){
            case OWNER -> () -> getAllTaskFullByTeamID(teamID);
            case ADMIN -> () -> getAllTaskFullByTeamID(teamID);
            case MANAGER -> () -> getAllTaskFullByTeamID(teamID);
            case MEMBER -> () -> getAllTaskFullByTeamIDAndUserID(teamID, userID);
            case BANNED -> throw new NoPrivilegesException();
            case null, default -> throw new ImpossibleStateException("Called TaskService.getAllowedTaskIDs with unknown role: " + role);
        };
    }

    /**
     * Map of UserRoles and whether they can edit task
     * @param userID ID of User asking
     * @return True if user can edit task, otherwise false
     */
    private Map<UserRole, Predicate<TaskWithUserTasks>> editTaskRoleMap(final long userID) {
        return Map.of(
                UserRole.OWNER, t -> true,
                UserRole.ADMIN, t -> true,
                UserRole.MANAGER, t -> true,
                UserRole.MEMBER, t -> t.hasUser(userID)
        );
    }

    /**
     * Unused, returns tasks that don't have any users assigned
     * @param teamID ID of Team in which to search
     * @return Set of tasks allowed to see that have no users assigned to them
     */
    @Transactional(readOnly = true)
    public Set<TaskFull> getTasksWithoutUsers(final long teamID, final long userID){
        return getAllowedTasks(userID, teamID).get().stream().filter(taskFull -> taskFull.getUserTasks().isEmpty()).collect(Collectors.toUnmodifiableSet());
    }
    /**
     * Unused, returns tasks with given task status in a team
     * @param status TaskStatus to check for
     * @param teamID ID of Team in which to search
     * @return Set of tasks allowed to see that have given TaskStatus
     */
    @Transactional(readOnly = true)
    public Set<TaskFull> getTasksByStatus(@NonNull final TaskStatus status, final long teamID, final long userID){
        return getAllowedTasks(userID, teamID).get().stream().filter(task -> task.getTaskStatus().equals(status)).collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Returns tasks that didn't had any update in given number of days, should only be called from {@link #getInactiveTasksDTO(int, long, long)}
     * @param days Minimum number of days to search for
     * @param teamID ID of Team in which to search for
     * @return Set of tasks that weren't updated in days
     * @throws BadRequestException thrown when number is set to zero or less
     */
    private Set<TaskFull> getTasksNoUpdatesIn(final int days, final long teamID, final long userID){
        if(days <= 0){
            throw new BadRequestException("Number of days cannot be zero or negative!");
        }
        return getAllowedTasks(userID, teamID).get().stream().filter(taskFull -> ChronoUnit.DAYS.between(taskFull.getUpdateDate(), OffsetDateTimeConverter.nowConverted()) >= days).collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Creates task with given body details
     * @param body TaskRequestBody with details of task to create
     * @param team Team in which to make the task
     * @return Created task
     */
    @Transactional
    public Task createTask(@NonNull final TaskRequestBody body, @NonNull final TeamWithUserRolesAndTasks team){
        if (body.getUsers() != null){
            validateUsersForTasksByID(body.getUsers(),team);
        }
        Task task = new Task();
        taskUpdatersService.createUpdate(task, body, () -> (Team) team);
        return saveTask(task);
    }

    /**
     * Edits task completely with all values
     * @param body TaskRequestBody with new values
     * @param userID ID of User asking for edit
     * @param team Team in which task exists
     * @param taskID ID of Task to edit
     * @return Edited task
     */
    @Transactional
    public TaskFull putTask(@NonNull final TaskRequestBody body, final long userID, @NonNull final TeamWithUserRoles team, final long taskID) {
        Task task = (Task) getTaskFullByIDAndTeamID(taskID, team.getID());
        verifyTaskEditPrivilege(userID, team.getID(), taskID);
        validateUsersForTasksByID(body.getUsers(),team);
        taskUpdatersService.putUpdate(task, body);
        return saveTask(task);
    }

    /**
     * Edits partially task with new values from body
     * @param body TeamRequestBody with new updated values
     * @param userID ID of User asking for edit
     * @param team Team in which task exists
     * @param taskID ID of Task to edit
     * @return Edited task
     */
    @Transactional
    public TaskFull patchTask(@NonNull final TaskRequestBody body, final long userID, @NonNull final TeamWithUserRoles team, final long taskID){
        Task task = (Task) getTaskFullByIDAndTeamID(taskID, team.getID());
        verifyTaskEditPrivilege(userID, team.getID(), task.getID());
        if (body.getUsers() != null){
            validateUsersForTasksByID(body.getUsers(),team);
        }
        taskUpdatersService.patchUpdate(task, body);
        return saveTask(task);
    }

    /**
     * Saves pending changes to the task
     * @param task Task to save
     * @return Saved task
     */
    @Transactional
    public Task saveTask(@NonNull final Task task){
        task.setUpdateDate(OffsetDateTimeConverter.nowConverted());
        return taskRepository.save(task);
    }

    /**
     * Deletes Task completely
     * @param taskID ID of Task to delete
     * @param teamID ID of Team in which task is
     */
    @Transactional
    public void deleteTask(final long taskID, final long teamID){
        Task task = (Task) getTaskByIDAndTeamID(taskID, teamID);
        taskRepository.delete(task);
    }

    /**
     * Returns TaskMemberDTO of either all tasks in team or all tasks in team assigned to user based on UserRole
     * @param userID ID of User performing the check
     * @param teamID ID of Team in which to check for
     * @return Set of TaskDTO with information about the tasks
     */
    public Set<TaskMemberDTO> getAllTasksWithRoleCheck(final long userID, final long teamID){
        return getAllowedTasks(userID, teamID).get().stream().map(TaskMemberDTO::new).collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Unused, returns TaskMemberDTO of all tasks in the team
     * @param teamID ID of Team in which to search for
     * @return Set of TaskMemberDTO with all tasks in a team
     */
    public Set<TaskMemberDTO> getAllTasksDTO(final long teamID){
        return getAllTaskFullByTeamID(teamID).stream().map(TaskMemberDTO::new).collect(Collectors.toUnmodifiableSet());
    }

    public Set<TaskMemberDTO> getAllTasksAssignedToMe(final long teamID, final long userID){
        return getAllTaskFullByTeamIDAndUserID(teamID, userID).stream().map(TaskMemberDTO::new).collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Returns TaskMemberDTO of all tasks that didn't had an update in at least x days
     * @param days Minimum amount of days to be labeled as inactive
     * @param teamID ID of Team to search in for
     * @return Set of TaskMemberDTO with information about the tasks
     * @throws RuntimeException Thrown when Task couldn't get converted into proper DTO object
     */
    public Set<TaskMemberDTO> getInactiveTasksDTO(final int days, final long teamID, final long userID){
        return getTasksNoUpdatesIn(days, teamID, userID).stream().map(TaskMemberDTO::new).collect(Collectors.toUnmodifiableSet());
    }

    public Set<TaskMemberDTO> getTaskMemberDTOFromIDs(final Set<Long> taskIDs, final long teamID){
        final Set<TaskMemberDTO> dtoSet = new HashSet<>();
        for (final long id : taskIDs){
            dtoSet.add(new TaskMemberDTO(getTaskFullByIDAndTeamID(id, teamID)));
        }
        return dtoSet;
    }

    /**
     * Checks whether user can modify task
     * @param userID ID of User asking for permission
     * @param teamID ID of Team in which task is in
     * @param taskID ID of Task to edit
     * @throws NoPrivilegesException Thrown when user cannot modify task
     */
    private void verifyTaskEditPrivilege(final long userID, final long teamID, final long taskID){
        TaskWithUserTasks taskWithUserTasks = getTaskWithUserTasksByIDAndTeamID(taskID, teamID);
        UserRole role = teamUserRoleService.getRole(userID, teamID);
        if (!editTaskRoleMap(userID).getOrDefault(role, t -> false).test(taskWithUserTasks)){
            throw new NoPrivilegesException();
        }
    }

    /**
     * Adds User to specified Task
     * @param team Team in which user and task is
     * @param taskID ID of Task to attach user to
     * @param userID ID of User to add to task
     */
    @Transactional
    public void addUserToTask(@NonNull final TeamWithUserRoles team, final long taskID, final long userID){
        validateUsersForTasksByID(Set.of((int)userID), team);
        Task task = (Task) getTaskWithUserTasksByIDAndTeamID(taskID, team.getID());
        User user = (User) userService.getUserWithUserTasks(userID);
        task.addUserTask(user);
        saveTask(task);
    }

    /**
     * Removes user from Task
     * @param taskID ID of Task to edit
     * @param teamID ID of Team in which task exists
     * @param userID ID of User to remove from Task
     */
    @Transactional
    public void removeUserFromTask(final long taskID, final long teamID, final long userID){
        Task task = (Task) getTaskWithUserTasksByIDAndTeamID(taskID, teamID);
        UserTask userTask = userTaskRepository.findUserTaskByAssignedUserIDAndAssignedTaskID(userID, taskID).orElseThrow(NotFoundException::new);
        task.removeUserTask(userTask);
        saveTask(task);
    }

    /**
     * Checks if users belong in a given team
     * @param userIDs Set of IDs of Users to check
     * @param team    Team in which to search
     * @throws BadRequestException Thrown when one or more users are not part of the team
     */
    private void validateUsersForTasksByID(@NonNull final Set<Integer> userIDs, @NonNull final TeamWithUserRoles team){
        for (int id : userIDs){
            if (!team.checkUser(id)){
                throw new BadRequestException("Cannot add user to this task that is not part of the team: UserID - " + id);
            }
        }
    }

    public TaskIdentity getTaskByIDAndTeamID(final long id, final long teamID){
        return taskRepository.findTaskByIDAndAssignedTeamID(id, teamID).orElseThrow(NotFoundException::new);
    }

    public TaskWithUserTasks getTaskWithUserTasksByIDAndTeamID(final long id, final long teamID){
        return taskRepository.findTaskWithUserTasksByIDAndAssignedTeamID(id, teamID).orElseThrow(NotFoundException::new);
    }

    public TaskWithSubtasks getTaskWithSubtasksByIDAndTeamID(final long id, final long teamID){
        return taskRepository.findTaskWithSubtasksByIDAndAssignedTeamID(id, teamID).orElseThrow(NotFoundException::new);
    }

    public TaskFull getTaskFullByIDAndTeamID(final long id, final long teamID){
        return taskRepository.findTaskFullByIDAndAssignedTeamID(id, teamID).orElseThrow(NotFoundException::new);
    }

    /**
     * Returns all tasks in given team
     * @param teamID ID of Team to get tasks from
     * @return Set of all TaskFull in given Team
     */
    public Set<TaskFull> getAllTaskFullByTeamID(final long teamID){
        Set<TaskFull> result = taskRepository.findAllTaskFullByAssignedTeamID(teamID);
        if (result.isEmpty()){
            throw new NotFoundException(String.format("Couldnt find any tasks for team with ID of %d ", teamID));
        }
        return result;
    }

    public Set<TaskFull> getAllTaskFullByTeamIDAndUserID(final long teamID, final long userID){
        Set<TaskFull> result = taskRepository.findAllTaskFullByAssignedTeamIDAndAssignedUserID(teamID, userID);
        if (result.isEmpty()){
            throw new NotFoundException(String.format("Couldnt find any tasks for team with ID of %d and user ID of %d ", teamID, userID));
        }
        return result;
    }

    /**
     * Deletes everything and flushes
     */
    public void deleteAll(){
       taskRepository.deleteAll();
       taskRepository.flush();
    }

}

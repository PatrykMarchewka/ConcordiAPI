package com.patrykmarchewka.concordiapi.Tasks;


import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.DatabaseModel.TaskRepository;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
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
import com.patrykmarchewka.concordiapi.OffsetDateTimeConverter;
import com.patrykmarchewka.concordiapi.TaskStatus;
import com.patrykmarchewka.concordiapi.Tasks.Updaters.TaskUpdatersService;
import com.patrykmarchewka.concordiapi.Teams.TeamService;
import com.patrykmarchewka.concordiapi.Teams.TeamUserRoleService;
import com.patrykmarchewka.concordiapi.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
    private final TeamService teamService;
    private final TaskUpdatersService taskUpdatersService;
    private final UserTaskRepository userTaskRepository;
    private final TeamUserRoleService teamUserRoleService;

    @Autowired
    public TaskService(TaskRepository taskRepository, @Lazy TeamService teamService, TaskUpdatersService taskUpdatersService, UserTaskRepository userTaskRepository, TeamUserRoleService teamUserRoleService){
        this.taskRepository = taskRepository;
        this.teamService = teamService;
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
        return getAllowedTasks(userID, teamID).get().stream().filter(taskFull -> taskFull.getUsers().isEmpty()).collect(Collectors.toUnmodifiableSet());
    }
    /**
     * Unused, returns tasks with given task status in a team
     * @param status TaskStatus to check for
     * @param teamID ID of Team in which to search
     * @return Set of tasks allowed to see that have given TaskStatus
     */
    @Transactional(readOnly = true)
    public Set<TaskFull> getTasksByStatus(final TaskStatus status, final long teamID, final long userID){
        return getAllowedTasks(userID, teamID).get().stream().filter(task -> task.getTaskStatus().equals(status)).collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Returns tasks that didn't had any update in given number of days, should only be called from {@link #getInactiveTasksDTO(int, long, long)}
     * @param days Minimum number of days to search for
     * @param teamID ID of Team in which to search for
     * @return Set of tasks that weren't updated in days
     * @throws IllegalArgumentException thrown when number is set to zero or less
     */
    public Set<TaskFull> getTasksNoUpdatesIn(final int days, final long teamID, final long userID){
        if(days <= 0){
            throw new IllegalArgumentException("Number of days cannot be zero or negative!");
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
    public Task createTask(TaskRequestBody body, Team team){
        validateUsersForTasksByID(body.getUsers(),team);
        Task task = new Task();
        taskUpdatersService.createUpdate(task, body, () -> team);
        return saveTask(task);
    }

    /**
     * Edits task completely with all values
     * @param body TaskRequestBody with new values
     * @param userID ID of User asking for edit
     * @param team Team in which task exists
     * @param task Task to edit
     * @return Edited task
     */
    @Transactional
    public Task putTask(TaskRequestBody body, long userID, Team team, Task task) {
        verifyTaskEditPrivilege(userID, team.getID(), task.getID());
        validateUsersForTasksByID(body.getUsers(),team);
        taskUpdatersService.putUpdate(task, body);
        return saveTask(task);
    }

    /**
     * Edits partially task with new values from body
     * @param body TeamRequestBody with new updated values
     * @param userID ID of User asking for edit
     * @param team Team in which task exists
     * @param task Task to edit
     * @return Edited task
     */
    @Transactional
    public Task patchTask(TaskRequestBody body, long userID, Team team, Task task){
        verifyTaskEditPrivilege(userID, team.getID(), task.getID());
        validateUsersForTasksByID(body.getUsers(),team);
        taskUpdatersService.patchUpdate(task, body);
        return saveTask(task);
    }


    /**
     * Deletes task with the specified ID
     * @param ID ID of the task to delete
     * @param team Team in which to delete
     */
    @Transactional
    public void deleteTaskByID(long ID, Team team){
        Task task = (Task) getTaskByIDAndTeamID(ID,team.getID());
        team.removeTask(task);
        teamService.saveTeam(team);
    }

    /**
     * Saves pending changes to the task
     * @param task Task to save
     * @return Saved task
     */
    @Transactional
    public Task saveTask(Task task){
        task.setUpdateDate(OffsetDateTimeConverter.nowConverted());
        return taskRepository.save(task);
    }

    /**
     * Saves all tasks
     * @param tasks Set of tasks to save
     */
    @Transactional
    public void saveAllTasks(Set<Task> tasks){
        for (Task task : tasks){
            task.setUpdateDate(OffsetDateTimeConverter.nowConverted());
        }
        taskRepository.saveAll(tasks);
    }

    /**
     * Removes subtask from Task and deletes the subtask
     * @param task Task to edit
     * @param subtask Subtask to remove and delete
     */
    @Transactional
    public void removeSubtaskFromTaskAndDelete(Task task, Subtask subtask){
        task.removeSubtask(subtask);
        saveTask(task);
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
     * Returns TaskMemberDTO of all tasks in the team
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
     * @param user User to get added to task
     * @param task Task to attach to user
     */
    @Transactional
    public void addUserToTask(Task task, User user) {
        TeamWithUserRoles team = teamService.getTeamWithUserRolesAndTasksByID(task.getAssignedTeam().getID());
        validateUsersForTasksByID(Set.of((int)user.getID()), team);

        task.addUserTask(user);
        saveTask(task);
    }

    /**
     * Adds Users to Task
     * @param task Task to add users to
     * @param users Set of Users to add to task
     */
    @Transactional
    public void addUsersToTask(Task task, Set<User> users){
        TeamWithUserRoles team = teamService.getTeamWithUserRolesAndTasksByID(task.getAssignedTeam().getID());
        validateUsersForTasks(users, team);

        for (User user : users){
            task.addUserTask(user);
        }
        saveTask(task);
    }

    /**
     * @deprecated Will be replaced by {@link #removeUserFromTask(Task, long)}
     * Removes user from Task
     * @param task Task to edit
     * @param user User to remove from Task
     */
    @Deprecated
    @Transactional
    public void removeUserFromTask(Task task, User user){
        task.removeUserTask(userTaskRepository.findByAssignedUserAndAssignedTask(user, task).orElseThrow(NotFoundException::new));
        saveTask(task);
    }

    /**
     * Removes user from Task
     * @param task Task to edit
     * @param userID ID of User to remove from Task
     */
    @Transactional
    public void removeUserFromTask(Task task, long userID){
        task.removeUserTask(userTaskRepository.findByAssignedUserIDAndAssignedTaskID(userID, task.getID()).orElseThrow(NotFoundException::new));
        saveTask(task);
    }

    /**
     * Removes all users from Task
     * @param task Task to remove users from
     */
    @Transactional
    public void removeUsersFromTask(Task task){
        task.getUserTasks().clear();
        saveTask(task);
    }

    /**
     * Checks if users belong in a given team <br>
     * Prefer {@link #validateUsersForTasksByID(Set, TeamWithUserRoles)}
     * Checks if users belong in a given team
     * @param userIDs Set of IDs of Users to check
     * @param team Team in which to search
     * @throws BadRequestException Thrown when one or more users are not part of the team
     */
    @Deprecated
    public void validateUsersForTasksByID(Set<Integer> userIDs, Team team){
        for (int id : userIDs) {
            if (!team.checkUser(id)) {
                throw new BadRequestException("Cannot add user to this task that is not part of the team: UserID - " + id);
            }
        }
    }


    /**
     * Checks if users belong in a given team
     * @param userIDs Set of IDs of Users to check
     * @param team    Team in which to search
     * @throws BadRequestException Thrown when one or more users are not part of the team
     */
    public void validateUsersForTasksByID(Set<Integer> userIDs, TeamWithUserRoles team){
        for (int id : userIDs){
            if (!team.checkUser(id)){
                throw new BadRequestException("Cannot add user to this task that is not part of the team: UserID - " + id);
            }
        }
    }

    /**
     * Checks if users belong in a given team, calls {@link #validateUsersForTasksByID(Set, Team)}
     * @param users Set of Users to check
     * @param team Team in which to search
     * @throws BadRequestException Thrown when one or more users are not part of the team
     */
    public void validateUsersForTasks(Set<User> users, TeamWithUserRoles team){
        validateUsersForTasksByID(users.stream().map(u -> (int)u.getID()).collect(Collectors.toUnmodifiableSet()),team);
    }

    @Deprecated
    public Task getTaskWithUserTasks(Task task){
        return taskRepository.findTaskWithUserTasksByIDAndAssignedTeam(task.getID(), task.getAssignedTeam()).orElseThrow(() -> new ImpossibleStateException("Task not found with provided ID"));
    }

    @Deprecated
    public Task getTaskWithSubtasks(Task task){
        return taskRepository.findTaskWithSubtasksByIDAndAssignedTeam(task.getID(), task.getAssignedTeam()).orElseThrow(() -> new ImpossibleStateException("Task not found with provided ID"));
    }

    @Deprecated
    public Task getTaskFull(Task task){
        return taskRepository.findTaskFullByID(task.getID(), task.getAssignedTeam()).orElseThrow(() -> new ImpossibleStateException("Task not found with provided ID"));
    }

    public TaskIdentity getTaskByIDAndTeamID(final long id, final long teamID){
        return taskRepository.findTaskByIDAndAssignedTeamID(id, teamID).orElseThrow(NotFoundException::new);
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
     * Deletes everything and flushes
     */
    public void deleteAll(){
       taskRepository.deleteAll();
       taskRepository.flush();
    }

}

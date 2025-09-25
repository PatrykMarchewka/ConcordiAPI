package com.patrykmarchewka.concordiapi.Tasks;


import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskDTO;
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
import com.patrykmarchewka.concordiapi.OffsetDateTimeConverter;
import com.patrykmarchewka.concordiapi.RoleRegistry;
import com.patrykmarchewka.concordiapi.TaskStatus;
import com.patrykmarchewka.concordiapi.Tasks.Updaters.TaskUpdatersService;
import com.patrykmarchewka.concordiapi.Teams.TeamService;
import com.patrykmarchewka.concordiapi.UpdateType;
import com.patrykmarchewka.concordiapi.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TeamService teamService;
    private final RoleRegistry roleRegistry;
    private final TaskUpdatersService taskUpdatersService;
    private final UserTaskRepository userTaskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, @Lazy TeamService teamService, RoleRegistry roleRegistry, TaskUpdatersService taskUpdatersService, UserTaskRepository userTaskRepository){
        this.taskRepository = taskRepository;
        this.teamService = teamService;
        this.roleRegistry = roleRegistry;
        this.taskUpdatersService = taskUpdatersService;
        this.userTaskRepository = userTaskRepository;
    }



    /**
     * Returns all tasks in given team
     * @param team Team to chose to get tasks from
     * @return Set of tasks in given Team
     */
    public Set<Task> getAllTasks(Team team){
        return taskRepository.getByAssignedTeam(team);
    }

    /**
     * Unused, returns all tasks that don't have any users assigned
     * @param team Team in which to search
     * @return Set of tasks that have no users assigned to them
     */
    @Transactional(readOnly = true)
    public Set<Task> getAllTasksWithoutUsers(Team team){
        return getAllTasks(team).stream().filter(task -> task.getUsers().isEmpty()).collect(Collectors.toSet());
    }

    /**
     * Unused, returns all tasks that are assigned to user in given team
     * @param user User to search for
     * @param team Team in which to search
     * @return Set of tasks that user is assigned to
     */
    @Transactional(readOnly = true)
    public Set<Task> getAllTasksForUser(User user, Team team){
        return getAllTasks(team).stream().filter(task -> task.hasUser(user)).collect(Collectors.toSet());
    }

    /**
     * Unused, returns all tasks with given task status in a team
     * @param status TaskStatus to check for
     * @param team Team in which to search
     * @return Set of tasks that have given TaskStatus
     */
    @Transactional(readOnly = true)
    public Set<Task> getAllTasksByStatus(TaskStatus status, Team team){
        return getAllTasks(team).stream().filter(task -> task.getTaskStatus().equals(status)).collect(Collectors.toSet());
    }

    /**
     * Returns all tasks that didn't had any update in given number of days, should only be called from {@link #getInactiveTasks(Team, UserRole, Integer)}
     * @param days Minimum number of days to search for
     * @param team Team in which to search for
     * @return Set of tasks that weren't updated in days
     * @throws IllegalArgumentException thrown when number is set to zero or less
     */
    @Transactional(readOnly = true)
    public Set<Task> getAllTasksNoUpdatesIn(int days, Team team){
        if(days <= 0){
            throw new IllegalArgumentException("Number of days cannot be zero or negative!");
        }
        return getAllTasks(team).stream().filter( task -> ChronoUnit.DAYS.between(task.getUpdateDate(), OffsetDateTimeConverter.nowConverted()) >= days).collect(Collectors.toSet());
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
        taskUpdatersService.update(task,body, UpdateType.CREATE, () -> team);

        return saveTask(task);
    }

    /**
     * Edits task completely with all values
     * @param body TaskRequestBody with new values
     * @param team Team in which task exists
     * @param task Task to edit
     * @return Edited task
     */
    @Transactional
    public Task putTask(TaskRequestBody body, Team team, Task task) {
        validateUsersForTasksByID(body.getUsers(),team);
        taskUpdatersService.update(task,body,UpdateType.PUT, () -> team);
        return saveTask(task);
    }

    /**
     * Edits partially task with new values from body
     * @param task Task to edit
     * @param body TeamRequestBody with new updated values
     * @param team Team in which task exists
     * @return Edited task
     */
    @Transactional
    public Task patchTask(TaskRequestBody body, Team team, Task task){
        validateUsersForTasksByID(body.getUsers(),team);
        taskUpdatersService.update(task,body,UpdateType.PATCH, () -> team);
        return saveTask(task);
    }


    /**
     * Deletes task with the specified ID
     * @param ID ID of the task to delete
     * @param team Team in which to delete
     */
    @Transactional
    public void deleteTaskByID(long ID, Team team){
        Task task = getTaskByIDAndTeam(ID,team);
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
     * Unused, saves all tasks
     * @param tasks Set of tasks to save
     */
    @Transactional
    public void saveAllTasks(Set<Task> tasks){
        taskRepository.saveAll(tasks);
    }

    /**
     * Returns task with given ID and team
     * @param id ID of the task to search for
     * @param team Team in which task is located
     * @return Task with given ID
     * @throws NotFoundException Thrown when can't find task with specified ID and Team
     */
    public Task getTaskByIDAndTeam(long id, Team team){
        return taskRepository.findByIdAndAssignedTeam(id,team).orElseThrow(() -> new NotFoundException());
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
     * Returns TaskDTO of either all tasks in team or all tasks in team assigned to user based on UserRole
     * @param user User performing the check
     * @param team Team in which to check for
     * @param role Role of the user in a team
     * @return Set of TaskDTO with information about the tasks
     */
    @Transactional(readOnly = true)
    public Set<TaskDTO> getAllTasks(User user, Team team, UserRole role){
        return roleRegistry.getAllTasksMap(user,team).get(role).stream().map(roleRegistry.getTaskDTOMap().get(role)).collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Returns TaskDTO of all tasks in team assigned to user regardless of UserRole. <br>
     * UserRole is used to identify what kind of information to return (which DTO)
     * @param user User performing the check
     * @param team Team in which to check for
     * @param role Role of the user in a team
     * @return Set of TaskDTO with information about the tasks
     * @throws RuntimeException Thrown when Task couldn't get converted into proper DTO object
     */
    @Transactional(readOnly = true)
    public Set<TaskDTO> getMyTasks(User user,Team team, UserRole role){
        return getAllTasksForUser(user, team).stream().map(roleRegistry.getTaskDTOMap().get(role)).collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Returns TaskDTO of all tasks that didn't had an update in at least x days
     * @param team Team in which to search the tasks
     * @param role Role of the user asking to return correct DTOs
     * @param days Minimum amount of days to be labeled as inactive
     * @return Set of TaskDTO with information about the tasks
     * @throws RuntimeException Thrown when Task couldn't get converted into proper DTO object
     */
    @Transactional(readOnly = true)
    public Set<TaskDTO> getInactiveTasks(Team team, UserRole role, Integer days){
        return getAllTasksNoUpdatesIn(days,team).stream().map(roleRegistry.getTaskDTOMap().get(role)).collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Returns TaskDTO about given Task based on role
     * @param role UserRole of user asking
     * @param task Task to return DTO of
     * @param user User asking for DTO
     * @return Task DTO with information about the task
     * @throws NoPrivilegesException Thrown when user cannot get the task details
     */
    public TaskDTO getInformationAboutTaskRole(UserRole role, Task task, User user) {
        return roleRegistry.getInformationAboutTaskRoleMap(task,user).entrySet().stream().filter(entry -> entry.getKey().test(role)).map(entry -> entry.getValue().apply(task)).findFirst().orElseThrow(() -> new NoPrivilegesException());
    }

    /**
     * Checks whether user can modify task using PUT command
     * @param role Role of the User asking for permission
     * @param task Task to edit
     * @param user User asking for permission
     * @return True if user can use PUT to edit task, otherwise false
     */
    public boolean putTaskRole(UserRole role, Task task, User user){
        return roleRegistry.putTaskRoleMap(user).getOrDefault(role, t-> false).test(task);
    }


    /**
     * Unused, removes all Subtasks from Task
     * @param task Task to remove subtasks from
     */
    private void removeSubtasksFromTask(Task task){
        task.getSubtasks().clear();
        saveTask(task);
    }

    /**
     * Adds User to specified Task
     * @param user User to get added to task
     * @param task Task to attach to user
     */
    @Transactional
    public void addUserToTask(Task task, User user) {
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
        for (User user : users){
            task.addUserTask(user);
        }
        saveTask(task);
    }

    /**
     * Removes user from Task
     * @param task Task to edit
     * @param user User to remove from Task
     */
    @Transactional
    public void removeUserFromTask(Task task, User user){
        task.removeUserTask(userTaskRepository.findByAssignedUserAndAssignedTask(user, task).orElseThrow(NotFoundException::new));
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
     * Checks if users belong in a given team
     * @param userIDs Set of IDs of Users to check
     * @param team Team in which to search
     * @throws BadRequestException Thrown when one or more users are not part of the team
     */
    public void validateUsersForTasksByID(Set<Integer> userIDs, Team team){
        for (int id : userIDs) {
            if (!team.checkUser(id)) {
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
    public void validateUsersForTasks(Set<User> users, Team team){
        validateUsersForTasksByID(users.stream().map(u -> (int)u.getID()).collect(Collectors.toUnmodifiableSet()),team);
    }

    public Task getTaskWithUserTasks(Task task){
        return taskRepository.findTaskWithUserTasksByIDAndAssignedTeam(task.getID(), task.getAssignedTeam()).orElseThrow(() -> new ImpossibleStateException("Task not found with provided ID"));
    }

    public Task getTaskWithSubtasks(Task task){
        return taskRepository.findTaskWithSubtasksByIDAndAssignedTeam(task.getID(), task.getAssignedTeam()).orElseThrow(() -> new ImpossibleStateException("Task not found with provided ID"));
    }

    public Task getTaskFull(Task task){
        return taskRepository.findTaskFullByID(task.getID(), task.getAssignedTeam()).orElseThrow(() -> new ImpossibleStateException("Task not found with provided ID"));
    }

    /**
     * Deletes everything and flushes
     */
    public void deleteAll(){
       taskRepository.deleteAll();
       taskRepository.flush();
    }

}

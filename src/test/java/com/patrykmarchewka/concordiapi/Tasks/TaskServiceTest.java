package com.patrykmarchewka.concordiapi.Tasks;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamRequestBody;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.DatabaseModel.TaskRepository;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.TeamRepository;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.DatabaseModel.UserRepository;
import com.patrykmarchewka.concordiapi.TaskStatus;
import com.patrykmarchewka.concordiapi.Teams.TeamRequestBodyHelper;
import com.patrykmarchewka.concordiapi.Teams.TeamService;
import com.patrykmarchewka.concordiapi.UserRole;
import com.patrykmarchewka.concordiapi.Users.UserRequestBodyHelper;
import com.patrykmarchewka.concordiapi.Users.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class TaskServiceTest implements TaskRequestBodyHelper, TeamRequestBodyHelper, UserRequestBodyHelper {

    private final TaskService taskService;
    private final TeamService teamService;
    private final UserService userService;
    private final TaskRepository taskRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public TaskServiceTest(TaskService taskService, TeamService teamService, UserService userService, TaskRepository taskRepository, TeamRepository teamRepository, UserRepository userRepository) {
        this.taskService = taskService;
        this.teamService = teamService;
        this.userService = userService;
        this.taskRepository = taskRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    @AfterEach
    void cleanUp(){
        taskService.deleteAll();
        teamService.deleteAll();
        userService.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveTaskCorrectlyBasic(){
        TaskRequestBody body = createTaskRequestBody();
        TeamRequestBody teamRequestBody = createTeamRequestBody("TEST");
        UserRequestBody userRequestBody = createUserRequestBody("NotJohnD");
        User user = userService.createUser(userRequestBody);
        Team team = teamService.createTeam(teamRequestBody, user);
        long id = taskService.createTask(body, team).getID();


        Task found = taskService.getTaskByIDAndTeam(id, team);

        assertEquals(id, found.getID());
        assertEquals("Test task", found.getName());
        assertEquals("Test description", found.getDescription());
        assertEquals(TaskStatus.NEW, found.getTaskStatus());
        assertEquals(team, found.getAssignedTeam());
    }

    @Test
    void shouldReturnTaskWithUserTasks(){
        TaskRequestBody body = createTaskRequestBody();
        TeamRequestBody teamRequestBody = createTeamRequestBody("TEST");
        UserRequestBody userRequestBody = createUserRequestBody("NotJohnD");
        User user = userService.createUser(userRequestBody);
        Team team = teamService.createTeam(teamRequestBody, user);
        long id = taskService.createTask(body, team).getID();

        Task found = taskService.getTaskByIDAndTeam(id, team);
        found = taskService.getTaskWithUserTasks(found);

        assertTrue(found.getUserTasks().isEmpty());
        assertTrue(found.getUsers().isEmpty());
    }

    @Test
    void shouldReturnTaskWithSubtasks(){
        TaskRequestBody body = createTaskRequestBody();
        TeamRequestBody teamRequestBody = createTeamRequestBody("TEST");
        UserRequestBody userRequestBody = createUserRequestBody("NotJohnD");
        User user = userService.createUser(userRequestBody);
        Team team = teamService.createTeam(teamRequestBody, user);
        long id = taskService.createTask(body, team).getID();

        Task found = taskService.getTaskByIDAndTeam(id, team);
        found = taskService.getTaskWithSubtasks(found);

        assertTrue(found.getSubtasks().isEmpty());
    }

    @Test
    void shouldSaveAndRetrieveTaskCorrectlyFull(){
        TeamRequestBody teamRequestBody = createTeamRequestBody("TEST");
        UserRequestBody userRequestBody = createUserRequestBody("NotJohnD");
        User user = userService.createUser(userRequestBody);
        Team team = teamService.createTeam(teamRequestBody, user);
        TaskRequestBody body = createTaskRequestBody("Task name", "Task desc", TaskStatus.INPROGRESS, Set.of((int)user.getID()));
        long id = taskService.createTask(body, team).getID();

        Task found = taskService.getTaskByIDAndTeam(id, team);

        found = taskService.getTaskFull(found);

        assertEquals(id, found.getID());
        assertEquals("Task name", found.getName());
        assertEquals("Task desc", found.getDescription());
        assertEquals(TaskStatus.INPROGRESS, found.getTaskStatus());
        assertEquals(team, found.getAssignedTeam());
        assertEquals(1, found.getUserTasks().size());
        assertTrue(found.getUsers().contains(user));
        assertTrue(found.getSubtasks().isEmpty());
    }

    @Test
    void shouldPutTask(){
        TaskRequestBody body = createTaskRequestBody();
        TeamRequestBody teamRequestBody = createTeamRequestBody("TEST");
        UserRequestBody userRequestBody = createUserRequestBody("NotJohnD");
        User user = userService.createUser(userRequestBody);
        Team team = teamService.createTeam(teamRequestBody, user);
        Task task = taskService.createTask(body, team);
        TaskRequestBody body1 = createTaskRequestBody("Task name", "Task desc", TaskStatus.INPROGRESS, Set.of((int)user.getID()));

        taskService.putTask(body1, team, task);
        Task found = taskService.getTaskFull(task);

        assertEquals("Task name", found.getName());
        assertEquals("Task desc", found.getDescription());
        assertEquals(TaskStatus.INPROGRESS, found.getTaskStatus());
        assertEquals(team, found.getAssignedTeam());
        assertEquals(1, found.getUserTasks().size());
        assertTrue(found.getUsers().contains(user));
    }

    @Test
    void shouldPatchTask(){
        TeamRequestBody teamRequestBody = createTeamRequestBody("TEST");
        UserRequestBody userRequestBody = createUserRequestBody("NotJohnD");
        User user = userService.createUser(userRequestBody);
        Team team = teamService.createTeam(teamRequestBody, user);
        TaskRequestBody body = createTaskRequestBody("Task name", "Task desc", TaskStatus.NEW, Set.of());
        TaskRequestBody body1 = createTaskRequestBody("Task name", "Task desc", TaskStatus.INPROGRESS, Set.of((int)user.getID()));
        Task task = taskService.createTask(body, team);

        taskService.patchTask(body1, team, task);
        Task found = taskService.getTaskFull(task);

        assertEquals("Task name", found.getName());
        assertEquals("Task desc", found.getDescription());
        assertEquals(TaskStatus.INPROGRESS, found.getTaskStatus());
        assertEquals(team, found.getAssignedTeam());
        assertEquals(1, found.getUserTasks().size());
        assertTrue(found.getUsers().contains(user));
    }

    @Test
    void shouldDeleteTask(){
        TeamRequestBody teamRequestBody = createTeamRequestBody("TEST");
        UserRequestBody userRequestBody = createUserRequestBody("NotJohnD");
        User user = userService.createUser(userRequestBody);
        Team team = teamService.createTeam(teamRequestBody, user);
        TaskRequestBody body = createTaskRequestBody("Task name", "Task desc", TaskStatus.NEW, Set.of());
        TaskRequestBody body1 = createTaskRequestBody("Task name", "Task desc", TaskStatus.INPROGRESS, Set.of((int)user.getID()));
        Task task = taskService.createTask(body, team);
        Task task1 = taskService.createTask(body1, team);

        taskService.deleteTaskByID(task.getID(), team);

        Set<Task> found = taskService.getAllTasks(team);

        assertEquals(1, found.size());
        assertFalse(found.contains(task));
        assertTrue(found.contains(task1));
    }

    @Test
    void shouldRetrieveAllTasks(){
        TaskRequestBody body = createTaskRequestBody();
        TeamRequestBody teamRequestBody = createTeamRequestBody("TEST");
        UserRequestBody userRequestBody = createUserRequestBody("NotJohnD");
        User user = userService.createUser(userRequestBody);
        Team team = teamService.createTeam(teamRequestBody, user);
        Task task = taskService.createTask(body, team);
        Task task1 = taskService.createTask(body, team);

        Set<Task> found = taskService.getAllTasks(team);

        assertEquals(2, found.size());
        assertTrue(found.contains(task));
        assertTrue(found.contains(task1));
    }

    @Test
    void shouldRetrieveTasksWithoutUsers(){
        TeamRequestBody teamRequestBody = createTeamRequestBody("TEST");
        UserRequestBody userRequestBody = createUserRequestBody("NotJohnD");
        User user = userService.createUser(userRequestBody);
        Team team = teamService.createTeam(teamRequestBody, user);
        TaskRequestBody body = createTaskRequestBody();
        TaskRequestBody body1 = createTaskRequestBody("Task name", "Task desc", TaskStatus.INPROGRESS, Set.of((int)user.getID()));
        Task task = taskService.createTask(body, team);
        Task task1 = taskService.createTask(body1, team);

        Set<Task> found = taskService.getAllTasksWithoutUsers(team);

        assertEquals(1, found.size());
        assertTrue(found.contains(task));
        assertFalse(found.contains(task1));
    }

    @Test
    void shouldRetrieveTasksForUser(){
        TeamRequestBody teamRequestBody = createTeamRequestBody("TEST");
        UserRequestBody userRequestBody = createUserRequestBody("NotJohnD");
        User user = userService.createUser(userRequestBody);
        Team team = teamService.createTeam(teamRequestBody, user);
        TaskRequestBody body = createTaskRequestBody("First Task name", "First Task desc", TaskStatus.NEW, Set.of((int)user.getID()));
        TaskRequestBody body1 = createTaskRequestBody("Second Task name", "Second Task desc", TaskStatus.INPROGRESS, Set.of((int)user.getID()));
        TaskRequestBody body2 = createTaskRequestBody();
        Task task = taskService.createTask(body, team);
        Task task1 = taskService.createTask(body1, team);
        Task task2 = taskService.createTask(body2, team);

        Set<Task> found = taskService.getAllTasksForUser(user, team);

        assertEquals(2, found.size());
        assertTrue(found.contains(task));
        assertTrue(found.contains(task1));
        assertFalse(found.contains(task2));
    }

    @Test
    void shouldRetrieveTasksByStatus(){
        TeamRequestBody teamRequestBody = createTeamRequestBody("TEST");
        UserRequestBody userRequestBody = createUserRequestBody("NotJohnD");
        User user = userService.createUser(userRequestBody);
        Team team = teamService.createTeam(teamRequestBody, user);
        TaskRequestBody body = createTaskRequestBody("First Task name", "First Task desc", TaskStatus.NEW, Set.of((int)user.getID()));
        TaskRequestBody body1 = createTaskRequestBody("Second Task name", "Second Task desc", TaskStatus.INPROGRESS, Set.of((int)user.getID()));
        TaskRequestBody body2 = createTaskRequestBody();
        Task task = taskService.createTask(body, team);
        Task task1 = taskService.createTask(body1, team);
        Task task2 = taskService.createTask(body2, team);

        Set<Task> found = taskService.getAllTasksByStatus(TaskStatus.NEW, team);

        assertEquals(2, found.size());
        assertTrue(found.contains(task));
        assertFalse(found.contains(task1));
        assertTrue(found.contains(task2));
    }

    @Test
    void shouldRetrieveTasksWithoutUpdatesInDays(){
        TeamRequestBody teamRequestBody = createTeamRequestBody("TEST");
        UserRequestBody userRequestBody = createUserRequestBody("NotJohnD");
        User user = userService.createUser(userRequestBody);
        Team team = teamService.createTeam(teamRequestBody, user);
        TaskRequestBody body = createTaskRequestBody("First Task name", "First Task desc", TaskStatus.NEW, Set.of((int)user.getID()));
        TaskRequestBody body1 = createTaskRequestBody("Second Task name", "Second Task desc", TaskStatus.INPROGRESS, Set.of((int)user.getID()));
        TaskRequestBody body2 = createTaskRequestBody();
        Task task = taskService.createTask(body, team);
        Task task1 = taskService.createTask(body1, team);
        Task task2 = taskService.createTask(body2, team);

        task.setUpdateDate(task.getUpdateDate().minusDays(1));
        task1.setUpdateDate(task1.getUpdateDate().minusDays(2));
        task2.setUpdateDate(task2.getUpdateDate().minusDays(3));
        taskService.saveAllTasks(Set.of(task,task1,task2));
        Set<Task> found = taskService.getAllTasksNoUpdatesIn(2, team);

        assertEquals(2, found.size());
        assertFalse(found.contains(task));
        assertTrue(found.contains(task1));
        assertTrue(found.contains(task2));
    }

    @Test
    void shouldCheckPutTaskRole(){
        TeamRequestBody teamRequestBody = createTeamRequestBody("TEST");
        UserRequestBody userRequestBody = createUserRequestBody("NotJohnD");
        UserRequestBody userRequestBody1 = createUserRequestBody("NotJaneD");
        User user = userService.createUser(userRequestBody);
        User user1 = userService.createUser(userRequestBody1);
        Team team = teamService.createTeam(teamRequestBody, user);
        TaskRequestBody body1 = createTaskRequestBody("Task name", "Task desc", TaskStatus.INPROGRESS, Set.of((int)user.getID()));
        Task task = taskService.createTask(body1, team);

        assertTrue(taskService.putTaskRole(UserRole.OWNER,task, user1));
        assertTrue(taskService.putTaskRole(UserRole.ADMIN, task, user1));
        assertTrue(taskService.putTaskRole(UserRole.MANAGER, task ,user1));
        assertFalse(taskService.putTaskRole(UserRole.MEMBER, task, user1));
        assertTrue(taskService.putTaskRole(UserRole.MEMBER, task, user));
    }

    @Test
    void shouldAddUserToTask(){
        TaskRequestBody body = createTaskRequestBody();
        TeamRequestBody teamRequestBody = createTeamRequestBody("TEST");
        UserRequestBody userRequestBody = createUserRequestBody("NotJohnD");
        User user = userService.createUser(userRequestBody);
        Team team = teamService.createTeam(teamRequestBody, user);
        Task task = taskService.createTask(body, team);

        Task found = taskService.getTaskWithUserTasks(task);

        assertTrue(found.getUsers().isEmpty());
        assertFalse(found.getUsers().contains(user));

        taskService.addUserToTask(found, user);

        found = taskService.getTaskWithUserTasks(found);

        assertEquals(1, found.getUsers().size());
        assertTrue(found.getUsers().contains(user));
    }

    @Test
    void shouldAddUsersToTask(){
        TaskRequestBody body = createTaskRequestBody();
        TeamRequestBody teamRequestBody = createTeamRequestBody("TEST");
        UserRequestBody userRequestBody = createUserRequestBody("NotJohnD");
        UserRequestBody userRequestBody1 = createUserRequestBody("NotJaneD");
        User user = userService.createUser(userRequestBody);
        User user1 = userService.createUser(userRequestBody1);
        Team team = teamService.createTeam(teamRequestBody, user);
        Task task = taskService.createTask(body, team);

        Task found = taskService.getTaskWithUserTasks(task);

        assertTrue(found.getUsers().isEmpty());
        assertFalse(found.getUsers().contains(user));
        assertFalse(found.getUsers().contains(user1));

        taskService.addUsersToTask(found, Set.of(user, user1));

        found = taskService.getTaskWithUserTasks(found);

        assertEquals(2, found.getUsers().size());
        assertTrue(found.getUsers().contains(user));
        assertTrue(found.getUsers().contains(user1));
    }

    @Test
    void shouldRemoveUserFromTask(){
        TeamRequestBody teamRequestBody = createTeamRequestBody("TEST");
        UserRequestBody userRequestBody = createUserRequestBody("NotJohnD");
        UserRequestBody userRequestBody1 = createUserRequestBody("NotJaneD");
        User user = userService.createUser(userRequestBody);
        User user1 = userService.createUser(userRequestBody1);
        Team team = teamService.createTeam(teamRequestBody, user);
        teamService.addUser(team, user1, UserRole.ADMIN);
        TaskRequestBody body1 = createTaskRequestBody("Task name", "Task desc", TaskStatus.INPROGRESS, Set.of((int)user.getID(), (int)user1.getID()));
        Task task = taskService.createTask(body1, team);

        taskService.removeUserFromTask(task, user);
        Task found = taskService.getTaskWithUserTasks(task);

        assertEquals(1, found.getUsers().size());
        assertFalse(found.getUsers().contains(user));
        assertTrue(found.getUsers().contains(user1));
    }

    @Test
    void shouldRemoveUsersFromTask(){
        TeamRequestBody teamRequestBody = createTeamRequestBody("TEST");
        UserRequestBody userRequestBody = createUserRequestBody("NotJohnD");
        UserRequestBody userRequestBody1 = createUserRequestBody("NotJaneD");
        User user = userService.createUser(userRequestBody);
        User user1 = userService.createUser(userRequestBody1);
        Team team = teamService.createTeam(teamRequestBody, user);
        teamService.addUser(team, user1, UserRole.ADMIN);
        TaskRequestBody body1 = createTaskRequestBody("Task name", "Task desc", TaskStatus.INPROGRESS, Set.of((int)user.getID(), (int)user1.getID()));
        Task task = taskService.createTask(body1, team);

        taskService.removeUsersFromTask(task);
        Task found = taskService.getTaskWithUserTasks(task);

        assertTrue(found.getUsers().isEmpty());
    }

}

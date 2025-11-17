package com.patrykmarchewka.concordiapi.Tasks;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamRequestBody;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Exceptions.BadRequestException;
import com.patrykmarchewka.concordiapi.HydrationContracts.Task.TaskFull;
import com.patrykmarchewka.concordiapi.HydrationContracts.Task.TaskIdentity;
import com.patrykmarchewka.concordiapi.HydrationContracts.Task.TaskWithSubtasks;
import com.patrykmarchewka.concordiapi.HydrationContracts.Task.TaskWithUserTasks;
import com.patrykmarchewka.concordiapi.TaskStatus;
import com.patrykmarchewka.concordiapi.Teams.TeamRequestBodyHelper;
import com.patrykmarchewka.concordiapi.Teams.TeamService;
import com.patrykmarchewka.concordiapi.UserRole;
import com.patrykmarchewka.concordiapi.Users.UserRequestBodyHelper;
import com.patrykmarchewka.concordiapi.Users.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

    private Task task;
    private Team team;
    private User user;

    public TaskServiceTest(TaskService taskService, TeamService teamService, UserService userService) {
        this.taskService = taskService;
        this.teamService = teamService;
        this.userService = userService;
    }

    @BeforeEach
    void initialize(){
        TaskRequestBody body = createTaskRequestBody();
        TeamRequestBody teamRequestBody = createTeamRequestBody("TEST");
        UserRequestBody userRequestBody = createUserRequestBody("NotJohnD");
        user = userService.createUser(userRequestBody);
        team = teamService.createTeam(teamRequestBody, user);
        task = taskService.createTask(body, team);
    }

    @AfterEach
    void cleanUp(){
        taskService.deleteAll();
        teamService.deleteAll();
        userService.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveTaskCorrectlyBasic(){
        TaskIdentity found = taskService.getTaskByIDAndTeamID(task.getID(), team.getID());

        assertEquals(task.getID(), found.getID());
        assertEquals("Test task", found.getName());
        assertEquals("Test description", found.getDescription());
        assertEquals(TaskStatus.NEW, found.getTaskStatus());
        assertEquals(team, found.getAssignedTeam());
    }

    @Test
    void shouldReturnTaskWithUserTasks(){
        TaskWithUserTasks found = taskService.getTaskWithUserTasksByIDAndTeamID(task.getID(), team.getID());

        assertTrue(found.getUserTasks().isEmpty());
        assertTrue(found.getUsers().isEmpty());
    }

    @Test
    void shouldReturnTaskWithSubtasks(){
        TaskWithSubtasks found = taskService.getTaskWithSubtasksByIDAndTeamID(task.getID(), team.getID());

        assertTrue(found.getSubtasks().isEmpty());
    }

    @Test
    void shouldSaveAndRetrieveTaskCorrectlyFull(){
        TaskRequestBody body = createTaskRequestBody("Task name", "Task desc", TaskStatus.INPROGRESS, Set.of((int)user.getID()));
        Task task = taskService.createTask(body, team);

        TaskFull found = taskService.getTaskFullByIDAndTeamID(task.getID(), team.getID());

        assertEquals(task.getID(), found.getID());
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
        TaskRequestBody body1 = createTaskRequestBody("Task name", "Task desc", TaskStatus.INPROGRESS, Set.of((int)user.getID()));

        taskService.putTask(body1, user.getID(), team, task.getID());
        TaskFull found = taskService.getTaskFullByIDAndTeamID(task.getID(), team.getID());

        assertEquals("Task name", found.getName());
        assertEquals("Task desc", found.getDescription());
        assertEquals(TaskStatus.INPROGRESS, found.getTaskStatus());
        assertEquals(team, found.getAssignedTeam());
        assertEquals(1, found.getUserTasks().size());
        assertTrue(found.getUsers().contains(user));
    }

    @Test
    void shouldPatchTask(){
        TaskRequestBody body1 = createTaskRequestBodyPATCH(TaskStatus.INPROGRESS, Set.of((int)user.getID()));

        taskService.patchTask(body1, user.getID(), team, task.getID());
        TaskFull found = taskService.getTaskFullByIDAndTeamID(task.getID(), team.getID());

        assertEquals("Test task", found.getName());
        assertEquals("Test description", found.getDescription());
        assertEquals(TaskStatus.INPROGRESS, found.getTaskStatus());
        assertEquals(team, found.getAssignedTeam());
        assertEquals(1, found.getUserTasks().size());
        assertTrue(found.getUsers().contains(user));
    }

    @Test
    void shouldPatchTaskFull(){
        TaskRequestBody body1 = createTaskRequestBody("Task name", "Task desc", TaskStatus.INPROGRESS, Set.of((int)user.getID()));

        taskService.patchTask(body1, user.getID(), team, task.getID());
        TaskFull found = taskService.getTaskFullByIDAndTeamID(task.getID(), team.getID());

        assertEquals("Task name", found.getName());
        assertEquals("Task desc", found.getDescription());
        assertEquals(TaskStatus.INPROGRESS, found.getTaskStatus());
        assertEquals(team, found.getAssignedTeam());
        assertEquals(1, found.getUserTasks().size());
        assertTrue(found.getUsers().contains(user));
    }

    @Test
    void shouldDeleteTask(){
        TaskRequestBody body1 = createTaskRequestBody("Task name", "Task desc", TaskStatus.INPROGRESS, Set.of((int)user.getID()));
        Task task1 = taskService.createTask(body1, team);

        taskService.deleteTask(task.getID(), team.getID());

        Set<TaskMemberDTO> found = taskService.getAllTasksDTO(team.getID());

        assertEquals(1, found.size());
        assertFalse(found.contains(new TaskMemberDTO(task)));
        assertTrue(found.contains(new TaskMemberDTO(task1)));
    }

    @Test
    void shouldRetrieveAllTasks(){
        TaskRequestBody body = createTaskRequestBody();
        Task task1 = taskService.createTask(body, team);

        Set<TaskMemberDTO> found = taskService.getAllTasksDTO(team.getID());

        assertEquals(2, found.size());
        assertTrue(found.contains(new TaskMemberDTO(task)));
        assertTrue(found.contains(new TaskMemberDTO(task1)));
    }

    @Test
    void shouldRetrieveTasksWithoutUsers(){
        TaskRequestBody body1 = createTaskRequestBody("Task name", "Task desc", TaskStatus.INPROGRESS, Set.of((int)user.getID()));
        Task task1 = taskService.createTask(body1, team);

        Set<TaskFull> found = taskService.getTasksWithoutUsers(team.getID(), user.getID());

        assertEquals(1, found.size());
        assertTrue(found.contains(task));
        assertFalse(found.contains(task1));
    }

    @Test
    void shouldRetrieveTasksForUser(){
        TaskRequestBody body1 = createTaskRequestBody("First Task name", "First Task desc", TaskStatus.NEW, Set.of((int)user.getID()));
        TaskRequestBody body2 = createTaskRequestBody("Second Task name", "Second Task desc", TaskStatus.INPROGRESS, Set.of((int)user.getID()));
        Task task1 = taskService.createTask(body1, team);
        Task task2 = taskService.createTask(body2, team);

        Set<TaskMemberDTO> found = taskService.getAllTasksAssignedToMe(team.getID(), user.getID());

        assertEquals(2, found.size());
        assertFalse(found.contains(new TaskMemberDTO(task)));
        assertTrue(found.contains(new TaskMemberDTO(task1)));
        assertTrue(found.contains(new TaskMemberDTO(task2)));
    }

    @Test
    void shouldRetrieveTasksByStatus(){
        TaskRequestBody body1 = createTaskRequestBody("First Task name", "First Task desc", TaskStatus.NEW, Set.of((int)user.getID()));
        TaskRequestBody body2 = createTaskRequestBody("Second Task name", "Second Task desc", TaskStatus.INPROGRESS, Set.of((int)user.getID()));
        Task task1 = taskService.createTask(body1, team);
        Task task2 = taskService.createTask(body2, team);

        Set<TaskFull> found = taskService.getTasksByStatus(TaskStatus.NEW, team.getID(), user.getID());

        assertEquals(2, found.size());
        assertTrue(found.contains(task));
        assertTrue(found.contains(task1));
        assertFalse(found.contains(task2));
    }

    @Test
    void shouldGetAllTasksDTO(){
        TaskRequestBody body1 = createTaskRequestBody("Task name", "Task desc", TaskStatus.INPROGRESS, Set.of());
        Task task1 = taskService.createTask(body1, team);

        Set<TaskMemberDTO> found = taskService.getAllTasksWithRoleCheck(user.getID(), team.getID());

        assertEquals(2, found.size());
        assertTrue(found.contains(new TaskMemberDTO(task)));
        assertTrue(found.contains(new TaskMemberDTO(task1)));
    }

    @Test
    void shouldGetMyTasksDTO(){
        UserRequestBody userRequestBody1 = createUserRequestBody("NotJaneD");
        User user1 = userService.createUser(userRequestBody1);
        teamService.addUser(team.getID(), user1, UserRole.ADMIN);
        TaskRequestBody body1 = createTaskRequestBody("Task name", "Task desc", TaskStatus.INPROGRESS, Set.of((int)user1.getID()));
        this.team = (Team) teamService.getTeamWithUserRolesAndTasksByID(team.getID());
        Task task1 = taskService.createTask(body1, team);

        Set<TaskMemberDTO> found = taskService.getAllTasksAssignedToMe(team.getID(), user1.getID());

        assertEquals(1, found.size());
        assertTrue(found.contains(new TaskMemberDTO(task1)));
    }

    @Test
    void shouldAddUserToTask(){
        TaskWithUserTasks found = taskService.getTaskWithUserTasksByIDAndTeamID(task.getID(), team.getID());

        assertTrue(found.getUsers().isEmpty());
        assertFalse(found.getUsers().contains(user));

        taskService.addUserToTask(team, task.getID(), user.getID());

        found = taskService.getTaskWithUserTasksByIDAndTeamID(found.getID(), team.getID());

        assertEquals(1, found.getUsers().size());
        assertTrue(found.getUsers().contains(user));
    }

    @Test
    void shouldThrowForUserOutsideTeamBeingAddedToTask(){
        UserRequestBody userRequestBody1 = createUserRequestBody("NotJaneD");
        User user1 = userService.createUser(userRequestBody1);

        assertThrows(BadRequestException.class,() -> taskService.addUserToTask(team, task.getID(), user1.getID()));
    }

    @Test
    void shouldRemoveUserFromTask(){
        UserRequestBody userRequestBody1 = createUserRequestBody("NotJaneD");
        User user1 = userService.createUser(userRequestBody1);
        teamService.addUser(team.getID(), user1, UserRole.ADMIN);
        TaskRequestBody body1 = createTaskRequestBody("Task name", "Task desc", TaskStatus.INPROGRESS, Set.of((int)user.getID(), (int)user1.getID()));
        this.team = (Team) teamService.getTeamWithUserRolesAndTasksByID(team.getID());
        Task task1 = taskService.createTask(body1, team);

        taskService.removeUserFromTask(task1.getID(), team.getID(), user.getID());
        TaskWithUserTasks found = taskService.getTaskWithUserTasksByIDAndTeamID(task1.getID(), team.getID());

        assertEquals(1, found.getUsers().size());
        assertFalse(found.getUsers().contains(user));
        assertTrue(found.getUsers().contains(user1));
    }

    @Test
    void shouldValidateUsersForTasksByID(){
        assertDoesNotThrow(() -> taskService.validateUsersForTasksByID(Set.of((int)user.getID()), team));
    }

    @Test
    void shouldThrowForUserOutsideTeamBeingValidated(){
        UserRequestBody userRequestBody1 = createUserRequestBody("NotJaneD");
        User user1 = userService.createUser(userRequestBody1);

        assertThrows(BadRequestException.class,() -> taskService.validateUsersForTasksByID(Set.of((int)user1.getID()), team));
    }

}

package com.patrykmarchewka.concordiapi.Tasks;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamRequestBody;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Exceptions.BadRequestException;
import com.patrykmarchewka.concordiapi.HydrationContracts.Task.TaskIdentity;
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
        Task found = taskService.getTaskByIDAndTeam(task.getID(), team);

        assertEquals(task.getID(), found.getID());
        assertEquals("Test task", found.getName());
        assertEquals("Test description", found.getDescription());
        assertEquals(TaskStatus.NEW, found.getTaskStatus());
        assertEquals(team, found.getAssignedTeam());
    }

    @Test
    void shouldReturnTaskWithUserTasks(){
        Task found = taskService.getTaskByIDAndTeam(task.getID(), team);
        found = taskService.getTaskWithUserTasks(found);

        assertTrue(found.getUserTasks().isEmpty());
        assertTrue(found.getUsers().isEmpty());
    }

    @Test
    void shouldReturnTaskWithSubtasks(){
        Task found = taskService.getTaskByIDAndTeam(task.getID(), team);
        found = taskService.getTaskWithSubtasks(found);

        assertTrue(found.getSubtasks().isEmpty());
    }

    @Test
    void shouldSaveAndRetrieveTaskCorrectlyFull(){
        TaskRequestBody body = createTaskRequestBody("Task name", "Task desc", TaskStatus.INPROGRESS, Set.of((int)user.getID()));
        Task task = taskService.createTask(body, team);

        Task found = taskService.getTaskByIDAndTeam(task.getID(), team);

        found = taskService.getTaskFull(found);

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

        taskService.putTask(body1, team, task, user, UserRole.OWNER);
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
        TaskRequestBody body1 = createTaskRequestBodyPATCH(TaskStatus.INPROGRESS, Set.of((int)user.getID()));

        taskService.patchTask(body1, team, task, user, UserRole.OWNER);
        Task found = taskService.getTaskFull(task);

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

        taskService.patchTask(body1, team, task, user, UserRole.OWNER);
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
        TaskRequestBody body1 = createTaskRequestBody("Task name", "Task desc", TaskStatus.INPROGRESS, Set.of((int)user.getID()));
        Task task1 = taskService.createTask(body1, team);

        taskService.deleteTaskByID(task.getID(), team);

        Set<TaskMemberDTO> found = taskService.getAllTasks(team.getID());

        assertEquals(1, found.size());
        assertFalse(found.contains(new TaskMemberDTO(task)));
        assertTrue(found.contains(new TaskMemberDTO(task1)));
    }

    @Test
    void shouldRetrieveAllTasks(){
        TaskRequestBody body = createTaskRequestBody();
        Task task1 = taskService.createTask(body, team);

        Set<TaskMemberDTO> found = taskService.getAllTasks(team.getID());

        assertEquals(2, found.size());
        assertTrue(found.contains(new TaskMemberDTO(task)));
        assertTrue(found.contains(new TaskMemberDTO(task1)));
    }

    @Test
    void shouldRetrieveTasksWithoutUsers(){
        TaskRequestBody body1 = createTaskRequestBody("Task name", "Task desc", TaskStatus.INPROGRESS, Set.of((int)user.getID()));
        Task task1 = taskService.createTask(body1, team);

        Set<TaskMemberDTO> found = taskService.getAllTasksWithoutUsers(team.getID());

        assertEquals(1, found.size());
        assertTrue(found.contains(new TaskMemberDTO(task)));
        assertFalse(found.contains(new TaskMemberDTO(task1)));
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

        Set<TaskMemberDTO> found = taskService.getAllTasksByStatus(TaskStatus.NEW, team.getID());

        assertEquals(2, found.size());
        assertTrue(found.contains(new TaskMemberDTO(task)));
        assertTrue(found.contains(new TaskMemberDTO(task1)));
        assertFalse(found.contains(new TaskMemberDTO(task2)));
    }

    @Test
    void shouldRetrieveTasksWithoutUpdatesInDays(){
        TaskRequestBody body1 = createTaskRequestBody("First Task name", "First Task desc", TaskStatus.NEW, Set.of((int)user.getID()));
        TaskRequestBody body2 = createTaskRequestBody("Second Task name", "Second Task desc", TaskStatus.INPROGRESS, Set.of((int)user.getID()));
        Task task1 = taskService.createTask(body1, team);
        Task task2 = taskService.createTask(body2, team);

        task.setUpdateDate(task.getUpdateDate().minusDays(1));
        task1.setUpdateDate(task1.getUpdateDate().minusDays(2));
        task2.setUpdateDate(task2.getUpdateDate().minusDays(3));
        taskService.saveAllTasks(Set.of(task,task1,task2));
        Set<TaskIdentity> found = taskService.getAllTasksNoUpdatesIn(2, team.getID());

        assertEquals(2, found.size());
        assertFalse(found.stream().anyMatch(taskIdentity -> taskIdentity.getID() == task.getID()));
        assertTrue(found.stream().anyMatch(taskIdentity -> taskIdentity.getID() == task1.getID()));
        assertTrue(found.stream().anyMatch(taskIdentity -> taskIdentity.getID() == task2.getID()));
    }

    @Test
    void shouldGetAllTasksDTO(){
        TaskRequestBody body1 = createTaskRequestBody("Task name", "Task desc", TaskStatus.INPROGRESS, Set.of());
        Task task1 = taskService.createTask(body1, team);

        Set<TaskMemberDTO> found = taskService.getAllTasksWithRoleCheck(user.getID(), team.getID(), UserRole.OWNER);

        assertEquals(2, found.size());
        assertTrue(found.contains(new TaskMemberDTO(task)));
        assertTrue(found.contains(new TaskMemberDTO(task1)));
    }

    @Test
    void shouldGetMyTasksDTO(){
        UserRequestBody userRequestBody1 = createUserRequestBody("NotJaneD");
        User user1 = userService.createUser(userRequestBody1);
        teamService.addUser(team, user1, UserRole.ADMIN);
        TaskRequestBody body1 = createTaskRequestBody("Task name", "Task desc", TaskStatus.INPROGRESS, Set.of((int)user1.getID()));
        Task task1 = taskService.createTask(body1, team);

        Set<TaskMemberDTO> found = taskService.getAllTasksAssignedToMe(team.getID(), user1.getID());

        assertEquals(1, found.size());
        assertTrue(found.contains(new TaskMemberDTO(task1)));
    }

    @Test
    void shouldAddUserToTask(){
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
        UserRequestBody userRequestBody1 = createUserRequestBody("NotJaneD");
        User user1 = userService.createUser(userRequestBody1);
        teamService.addUser(team, user1, UserRole.ADMIN);

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
    void shouldThrowForUserOutsideTeamBeingAddedToTask(){
        UserRequestBody userRequestBody1 = createUserRequestBody("NotJaneD");
        User user1 = userService.createUser(userRequestBody1);

        assertThrows(BadRequestException.class,() -> taskService.addUserToTask(task, user1));
    }

    @Test
    void shouldRemoveUserFromTask(){
        UserRequestBody userRequestBody1 = createUserRequestBody("NotJaneD");
        User user1 = userService.createUser(userRequestBody1);
        teamService.addUser(team, user1, UserRole.ADMIN);
        TaskRequestBody body1 = createTaskRequestBody("Task name", "Task desc", TaskStatus.INPROGRESS, Set.of((int)user.getID(), (int)user1.getID()));
        Task task1 = taskService.createTask(body1, team);

        taskService.removeUserFromTask(task1, user);
        Task found = taskService.getTaskWithUserTasks(task1);

        assertEquals(1, found.getUsers().size());
        assertFalse(found.getUsers().contains(user));
        assertTrue(found.getUsers().contains(user1));
    }

    @Test
    void shouldRemoveUsersFromTask(){
        UserRequestBody userRequestBody1 = createUserRequestBody("NotJaneD");
        User user1 = userService.createUser(userRequestBody1);
        teamService.addUser(team, user1, UserRole.ADMIN);
        TaskRequestBody body1 = createTaskRequestBody("Task name", "Task desc", TaskStatus.INPROGRESS, Set.of((int)user.getID(), (int)user1.getID()));
        Task task1 = taskService.createTask(body1, team);

        taskService.removeUsersFromTask(task1);
        Task found = taskService.getTaskWithUserTasks(task1);

        assertTrue(found.getUsers().isEmpty());
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

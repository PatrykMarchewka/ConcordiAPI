package com.patrykmarchewka.concordiapi.Subtasks;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamRequestBody;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.HydrationContracts.Subtask.SubtaskIdentity;
import com.patrykmarchewka.concordiapi.TaskStatus;
import com.patrykmarchewka.concordiapi.Tasks.TaskRequestBodyHelper;
import com.patrykmarchewka.concordiapi.Tasks.TaskService;
import com.patrykmarchewka.concordiapi.Teams.TeamRequestBodyHelper;
import com.patrykmarchewka.concordiapi.Teams.TeamService;
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
public class SubtaskServiceTest implements SubtaskRequestBodyHelper, TaskRequestBodyHelper, TeamRequestBodyHelper, UserRequestBodyHelper {

    private final SubtaskService subtaskService;
    private final TaskService taskService;
    private final TeamService teamService;
    private final UserService userService;

    private Subtask subtask;
    private Task task;
    private Team team;


    public SubtaskServiceTest(SubtaskService subtaskService, TaskService taskService, TeamService teamService, UserService userService) {
        this.subtaskService = subtaskService;
        this.taskService = taskService;
        this.teamService = teamService;
        this.userService = userService;
    }

    @BeforeEach
    void initialize(){
        SubtaskRequestBody body = createSubtaskRequestBody();
        TaskRequestBody taskRequestBody = createTaskRequestBody();
        TeamRequestBody teamRequestBody = createTeamRequestBody("TEST TEAM");
        UserRequestBody userRequestBody = createUserRequestBody("NotJaneD");
        User user = userService.createUser(userRequestBody);
        team = teamService.createTeam(teamRequestBody, user);
        task = taskService.createTask(taskRequestBody, team);
        subtask = subtaskService.createSubtask(body, team.getID(), task.getID());
    }

    @AfterEach
    void cleanUp(){
        subtaskService.deleteAll();
        taskService.deleteAll();
        teamService.deleteAll();
        userService.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveSubtaskCorrectly(){
        SubtaskIdentity found = subtaskService.getSubtaskByID(task.getID(), subtask.getID());

        assertEquals(subtask.getID(), found.getID());
        assertEquals("Test Subtask", found.getName());
        assertEquals("Test Subtask Description", found.getDescription());
        assertEquals(TaskStatus.NEW, found.getTaskStatus());
        assertEquals(task, found.getTask());
    }

    @Test
    void shouldPutSubtask(){
        SubtaskRequestBody body1 = createSubtaskRequestBody("New name", "New description", TaskStatus.INPROGRESS);

        Subtask found = subtaskService.putUpdate(task.getID(), subtask.getID(),body1);

        assertEquals(subtask.getID(), found.getID());
        assertEquals("New name", found.getName());
        assertEquals("New description", found.getDescription());
        assertEquals(TaskStatus.INPROGRESS, found.getTaskStatus());
        assertEquals(task, found.getTask());
    }

    @Test
    void shouldPatchSubtask(){
        SubtaskRequestBody body1 = createSubtaskRequestBodyPATCH("New name");

        Subtask found = subtaskService.patchUpdate(task.getID(), subtask.getID(),body1);

        assertEquals(subtask.getID(), found.getID());
        assertEquals("New name", found.getName());
        assertEquals("Test Subtask Description", found.getDescription());
        assertEquals(TaskStatus.NEW, found.getTaskStatus());
        assertEquals(task, found.getTask());
    }

    @Test
    void shouldPatchSubtaskFull(){
        SubtaskRequestBody body1 = createSubtaskRequestBody("New name", "New description", TaskStatus.INPROGRESS);

        Subtask found = subtaskService.patchUpdate(task.getID(), subtask.getID(),body1);

        assertEquals(subtask.getID(), found.getID());
        assertEquals("New name", found.getName());
        assertEquals("New description", found.getDescription());
        assertEquals(TaskStatus.INPROGRESS, found.getTaskStatus());
        assertEquals(task, found.getTask());
    }

    @Test
    void shouldRetrieveSubtasksDTO(){
        SubtaskRequestBody body1 = createSubtaskRequestBody("New name", "New description", TaskStatus.INPROGRESS);
        Subtask subtask1 = subtaskService.createSubtask(body1, team.getID(), task.getID());
        task = taskService.getTaskWithSubtasks(task);

        Set<SubtaskMemberDTO> found = subtaskService.getSubtasksDTO(task);

        assertEquals(2, found.size());
        assertTrue(found.stream().anyMatch(d -> d.equalsSubtask(subtask)));
        assertTrue(found.stream().anyMatch(d -> d.equalsSubtask(subtask1)));
    }
}

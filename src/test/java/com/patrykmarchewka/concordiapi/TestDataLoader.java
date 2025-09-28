package com.patrykmarchewka.concordiapi;

import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationRequestBody;
import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamRequestBody;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Invitations.InvitationService;
import com.patrykmarchewka.concordiapi.Subtasks.SubtaskService;
import com.patrykmarchewka.concordiapi.Tasks.TaskService;
import com.patrykmarchewka.concordiapi.Teams.TeamService;
import com.patrykmarchewka.concordiapi.Users.UserService;
import org.springframework.stereotype.Component;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

@Component
public class TestDataLoader {

    private final UserService userService;
    private final TeamService teamService;
    private final TaskService taskService;
    private final SubtaskService subtaskService;
    private final InvitationService invitationService;

    public User user1;
    public User user2;
    public Team team1;
    public Team team2;
    public Task task1;
    public Task task2;
    public Subtask subtask1;
    public Subtask subtask2;
    public Invitation invitation;
    public String jwt1;
    public String jwt2;

    public TestDataLoader(UserService userService, TeamService teamService, TaskService taskService, SubtaskService subtaskService, InvitationService invitationService) {
        this.userService = userService;
        this.teamService = teamService;
        this.taskService = taskService;
        this.subtaskService = subtaskService;
        this.invitationService = invitationService;
    }

    public void loadDataForTests(){
        this.user1 = userService.createUser(new UserRequestBody("string", "string", "string", "string"));
        this.user2 = userService.createUser(new UserRequestBody("login", "password", "name", "lastName"));
        this.team1 = teamService.createTeam(new TeamRequestBody("team"), user1);
        this.team2 = teamService.createTeam(new TeamRequestBody("team2"), user1);
        this.task1 = taskService.createTask(new TaskRequestBody("task", "taskdesc", Set.of((int)user1.getID()), TaskStatus.NEW), team1);
        this.task2 = taskService.createTask(new TaskRequestBody("task2", "taskdesc2", Set.of(), TaskStatus.INPROGRESS), team1);
        this.subtask1 = subtaskService.createSubtask(new SubtaskRequestBody("subtask", "subtaskdesc", TaskStatus.HALTED), team1.getID(), task1.getID());
        this.subtask2 = subtaskService.createSubtask(new SubtaskRequestBody("subtask2", "subtaskdesc2", TaskStatus.CANCELLED), team1.getID(), task2.getID());
        this.invitation = invitationService.createInvitation(new InvitationRequestBody((short) 101, UserRole.ADMIN, null), team1.getID());
        try {
            this.jwt1 = JSONWebToken.GenerateJWToken(user1.getID());
            this.jwt2 = JSONWebToken.GenerateJWToken(user2.getID());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        this.user1 = userService.getUserFull(user1);
        this.team1 = teamService.getTeamFull(team1);
        this.task1 = taskService.getTaskFull(task1);
        this.task2 = taskService.getTaskFull(task2);

    }
}

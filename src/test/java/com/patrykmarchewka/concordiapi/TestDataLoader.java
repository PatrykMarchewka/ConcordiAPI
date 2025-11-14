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

    public User userReadOwner;
    public User userWriteOwner;
    public User userDeleteOwner;
    public User userAdmin;
    public User userManager;
    public User userMember;
    public User userNoTeam;

    public Team teamRead;
    public Team teamWrite;
    public Team teamDelete;

    public Task taskRead;
    public Task taskWrite;
    public Task taskDelete;

    public Subtask subtaskRead;
    public Subtask subtaskWrite;
    public Subtask subtaskDelete;

    public Invitation invitationRead;
    public Invitation invitationNoUses;
    public Invitation invitationExpired;
    public Invitation invitationWrite;
    public Invitation invitationDelete;

    public String jwtRead;
    public String jwtWrite;
    public String jwtDelete;
    public String jwtAdmin;
    public String jwtManager;
    public String jwtMember;
    public String jwtNoTeam;


    public TestDataLoader(UserService userService, TeamService teamService, TaskService taskService, SubtaskService subtaskService, InvitationService invitationService) {
        this.userService = userService;
        this.teamService = teamService;
        this.taskService = taskService;
        this.subtaskService = subtaskService;
        this.invitationService = invitationService;
    }

    public void loadDataForTests(){
        createUsers();
        createTeams();
        addUsersToTeams();
        createTasks();
        createSubtasks();
        createInvitations();
        setJWTs();
        getFullEntities();
    }

    public void clearDB(){
        invitationService.deleteAll();
        subtaskService.deleteAll();
        taskService.deleteAll();
        teamService.deleteAll();
        userService.deleteAll();
    }

    private void createUsers(){
        this.userReadOwner = userService.createUser(new UserRequestBody("READ", "READ", "READ", "READ"));
        this.userWriteOwner = userService.createUser(new UserRequestBody("WRITE", "WRITE", "WRITE", "WRITE"));
        this.userDeleteOwner = userService.createUser(new UserRequestBody("DELETE", "DELETE", "DELETE", "DELETE"));
        this.userAdmin = userService.createUser(new UserRequestBody("ADMIN", "ADMIN", "ADMIN", "ADMIN"));
        this.userManager = userService.createUser(new UserRequestBody("MANAGER", "MANAGER", "MANAGER", "MANAGER"));
        this.userMember = userService.createUser(new UserRequestBody("MEMBER", "MEMBER", "MEMBER", "MEMBER"));
        this.userNoTeam = userService.createUser(new UserRequestBody("NOTEAM", "NOTEAM", "NOTEAM", "NOTEAM"));
    }

    private void createTeams(){
        this.teamRead = teamService.createTeam(new TeamRequestBody("teamRead"), userReadOwner);
        this.teamWrite = teamService.createTeam(new TeamRequestBody("teamWrite"), userWriteOwner);
        this.teamDelete = teamService.createTeam(new TeamRequestBody("teamDelete"), userDeleteOwner);
    }

    private void addUsersToTeams(){
        this.teamRead = teamService.addUser(teamRead, userAdmin, UserRole.ADMIN);
        this.teamRead = teamService.addUser(teamRead, userManager, UserRole.MANAGER);
        this.teamRead = teamService.addUser(teamRead, userMember, UserRole.MEMBER);

        this.teamWrite = teamService.addUser(teamWrite, userAdmin, UserRole.ADMIN);
        this.teamWrite = teamService.addUser(teamWrite, userManager, UserRole.MANAGER);
        this.teamWrite = teamService.addUser(teamWrite, userMember, UserRole.MEMBER);

        this.teamDelete = teamService.addUser(teamDelete, userAdmin, UserRole.ADMIN);
        this.teamDelete = teamService.addUser(teamDelete, userManager, UserRole.MANAGER);
        this.teamDelete = teamService.addUser(teamDelete, userMember, UserRole.MEMBER);
    }

    private void createTasks(){
        this.taskRead = taskService.createTask(new TaskRequestBody("taskRead", "taskReadDesc", Set.of((int) userReadOwner.getID(), (int)userMember.getID()), TaskStatus.NEW), teamRead);
        this.taskWrite = taskService.createTask(new TaskRequestBody("taskWrite", "taskWriteDesc", Set.of((int) userWriteOwner.getID()), TaskStatus.INPROGRESS), teamWrite);
        this.taskDelete = taskService.createTask(new TaskRequestBody("taskDelete", "taskDeleteDesc", Set.of(), TaskStatus.CANCELLED), teamDelete);
    }

    private void createSubtasks(){
        this.subtaskRead = subtaskService.createSubtask(new SubtaskRequestBody("subtaskRead", "subtaskReadDesc", TaskStatus.NEW), teamRead.getID(), taskRead.getID());
        this.subtaskWrite = subtaskService.createSubtask(new SubtaskRequestBody("subtaskWrite", "subtaskWriteDesc", TaskStatus.HALTED), teamWrite.getID(), taskWrite.getID());
        this.subtaskDelete = subtaskService.createSubtask(new SubtaskRequestBody("subtaskDelete", "subtaskDeleteDesc", TaskStatus.CANCELLED), teamDelete.getID(), taskDelete.getID());
    }

    private void createInvitations(){
        this.invitationRead = invitationService.createInvitation(UserRole.OWNER,new InvitationRequestBody((short) 101, UserRole.ADMIN, null), teamRead.getID());
        this.invitationNoUses = invitationService.createInvitation(UserRole.OWNER,new InvitationRequestBody((short)0, UserRole.MANAGER, null), teamRead.getID());
        this.invitationExpired = invitationService.createInvitation(UserRole.OWNER,new InvitationRequestBody((short)1, UserRole.MEMBER, OffsetDateTimeConverter.nowConverted().minusDays(1)), teamRead.getID());
        this.invitationWrite = invitationService.createInvitation(UserRole.OWNER,new InvitationRequestBody(null, null, null), teamWrite.getID());
        this.invitationDelete = invitationService.createInvitation(UserRole.OWNER,new InvitationRequestBody(null,null,null), teamDelete.getID());
    }

    private void setJWTs(){
        try {
            this.jwtRead = JSONWebToken.GenerateJWToken(userReadOwner.getID());
            this.jwtWrite = JSONWebToken.GenerateJWToken(userWriteOwner.getID());
            this.jwtDelete = JSONWebToken.GenerateJWToken(userDeleteOwner.getID());
            this.jwtAdmin = JSONWebToken.GenerateJWToken(userAdmin.getID());
            this.jwtManager = JSONWebToken.GenerateJWToken(userManager.getID());
            this.jwtMember = JSONWebToken.GenerateJWToken(userMember.getID());
            this.jwtNoTeam = JSONWebToken.GenerateJWToken(userNoTeam.getID());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    private void getFullEntities(){
        this.userReadOwner = userService.getUserEntityFull(userReadOwner);
        this.userWriteOwner = userService.getUserEntityFull(userWriteOwner);
        this.userDeleteOwner = userService.getUserEntityFull(userDeleteOwner);
        this.userMember = userService.getUserEntityFull(userMember);

        this.teamRead = teamService.getTeamEntityFull(teamRead.getID());
        this.teamWrite = teamService.getTeamEntityFull(teamWrite.getID());
        this.teamDelete = teamService.getTeamEntityFull(teamDelete.getID());

        this.taskRead = (Task) taskService.getTaskFullByIDAndTeamID(taskRead.getID(), teamRead.getID());
        this.taskWrite = (Task) taskService.getTaskFullByIDAndTeamID(taskWrite.getID(), teamWrite.getID());
        this.taskDelete = (Task) taskService.getTaskFullByIDAndTeamID(taskDelete.getID(), teamDelete.getID());
    }

    public User refreshUser(User user){ return userService.getUserEntityFull(user); }
    public Team refreshTeam(Team team){ return teamService.getTeamEntityFull(team.getID()); }
    public Task refreshTask(Task task){ return (Task) taskService.getTaskFullByIDAndTeamID(task.getID(), task.getAssignedTeam().getID()); }
    public Subtask refreshSubtask(Subtask subtask){ return (Subtask) subtaskService.getSubtaskByID(subtask.getTask().getID(), subtask.getID()); }
    public Invitation refreshInvitation(Invitation invitation){ return  (Invitation) invitationService.getInvitationFullByUUID(invitation.getUUID()); }

}

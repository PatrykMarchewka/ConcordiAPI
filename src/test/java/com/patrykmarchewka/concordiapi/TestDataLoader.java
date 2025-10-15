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

    //Old
    public User user1;
    public User user2;
    public User userAdminA;
    public User userAdminA2;
    public User userManagerA;
    public User userMemberA;
    public User userAdminB;
    public User userAdminB2;
    public User userManagerB;
    public User userMemberB;
    public Team team1;
    public Team team2;
    public Team teamToDelete;
    public Task task1;
    public Task task1New;
    public Task task2;
    public Task taskToDelete;
    public Subtask subtask1;
    public Subtask subtask2;
    public Subtask subtask1New;
    public Subtask subtaskToDelete;
    public Invitation invitation;
    public Invitation invitation1New;
    public Invitation invitationToDelete;
    public String jwt1;
    public String jwt2;
    public String jwtAdminA;
    public String jwtAdminB;
    public String jwtMemberA;
    public String jwtMemberB;
    public User refreshedUser;
    public Team refreshedTeam;
    public Task refreshedTask;
    public Subtask refreshedSubtask;
    public Invitation refreshedInvitation;
    //

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
        //Old
        this.user1 = userService.createUser(new UserRequestBody("string", "string", "string", "string"));
        this.user2 = userService.createUser(new UserRequestBody("login", "password", "name", "lastName"));
        this.userAdminA = userService.createUser(new UserRequestBody("adminA", "adminA", "adminA", "adminA"));
        this.userAdminA2 = userService.createUser(new UserRequestBody("adminA2", "adminA2", "adminA2", "adminA2"));
        this.userManagerA = userService.createUser(new UserRequestBody("managerA", "managerA", "managerA", "managerA"));
        this.userMemberA = userService.createUser(new UserRequestBody("memberA", "memberA", "memberA", "memberA"));
        this.userAdminB = userService.createUser(new UserRequestBody("adminB", "adminB", "adminB", "adminB"));
        this.userAdminB2 = userService.createUser(new UserRequestBody("adminB2", "adminB2", "adminB2", "adminB2"));
        this.userManagerB = userService.createUser(new UserRequestBody("managerB", "managerB", "managerB", "managerB"));
        this.userMemberB = userService.createUser(new UserRequestBody("memberB", "memberB", "memberB", "memberB"));
        //

        this.userReadOwner = userService.createUser(new UserRequestBody("READ", "READ", "READ", "READ"));
        this.userWriteOwner = userService.createUser(new UserRequestBody("WRITE", "WRITE", "WRITE", "WRITE"));
        this.userDeleteOwner = userService.createUser(new UserRequestBody("DELETE", "DELETE", "DELETE", "DELETE"));
        this.userAdmin = userService.createUser(new UserRequestBody("ADMIN", "ADMIN", "ADMIN", "ADMIN"));
        this.userManager = userService.createUser(new UserRequestBody("MANAGER", "MANAGER", "MANAGER", "MANAGER"));
        this.userMember = userService.createUser(new UserRequestBody("MEMBER", "MEMBER", "MEMBER", "MEMBER"));
        this.userNoTeam = userService.createUser(new UserRequestBody("NOTEAM", "NOTEAM", "NOTEAM", "NOTEAM"));
    }

    private void createTeams(){
        //Old
        this.team1 = teamService.createTeam(new TeamRequestBody("team"), user1);
        this.team2 = teamService.createTeam(new TeamRequestBody("team2"), user1);
        this.teamToDelete = teamService.createTeam(new TeamRequestBody("toDelete"), user2);
        //

        this.teamRead = teamService.createTeam(new TeamRequestBody("teamRead"), userReadOwner);
        this.teamWrite = teamService.createTeam(new TeamRequestBody("teamWrite"), userWriteOwner);
        this.teamDelete = teamService.createTeam(new TeamRequestBody("teamDelete"), userDeleteOwner);
    }

    private void addUsersToTeams(){
        //Old
        this.team1 = teamService.addUser(team1, userAdminA, UserRole.ADMIN);
        this.team1 = teamService.addUser(team1, userAdminA2, UserRole.ADMIN);
        this.team1 = teamService.addUser(team1, userManagerA, UserRole.MANAGER);
        this.team1 = teamService.addUser(team1, userMemberA, UserRole.MEMBER);
        this.team2 = teamService.addUser(team2, userAdminB, UserRole.ADMIN);
        this.team2 = teamService.addUser(team2, userAdminB2, UserRole.ADMIN);
        this.team2 = teamService.addUser(team2, userManagerB, UserRole.MANAGER);
        this.team2 = teamService.addUser(team2, userMemberB, UserRole.MEMBER);
        //

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
        //Old
        this.task1 = taskService.createTask(new TaskRequestBody("task", "taskdesc", Set.of((int)user1.getID(), (int)userAdminA.getID(), (int)userAdminA2.getID(), (int)userMemberA.getID()), TaskStatus.NEW), team1);
        this.task1New = taskService.createTask(new TaskRequestBody("taskNew", "taskdescNew", Set.of(), TaskStatus.NEW), team1);
        this.task2 = taskService.createTask(new TaskRequestBody("task2", "taskdesc2", Set.of(), TaskStatus.INPROGRESS), team1);
        this.taskToDelete = taskService.createTask(new TaskRequestBody("toDelete", "toDelete", Set.of(), TaskStatus.CANCELLED), teamToDelete);
        //

        this.taskRead = taskService.createTask(new TaskRequestBody("taskRead", "taskReadDesc", Set.of((int) userReadOwner.getID(), (int)userMember.getID()), TaskStatus.NEW), teamRead);
        this.taskWrite = taskService.createTask(new TaskRequestBody("taskWrite", "taskWriteDesc", Set.of((int) userWriteOwner.getID()), TaskStatus.INPROGRESS), teamWrite);
        this.taskDelete = taskService.createTask(new TaskRequestBody("taskDelete", "taskDeleteDesc", Set.of(), TaskStatus.CANCELLED), teamDelete);
    }

    private void createSubtasks(){
        //Old
        this.subtask1 = subtaskService.createSubtask(new SubtaskRequestBody("subtask", "subtaskdesc", TaskStatus.HALTED), team1.getID(), task1.getID());
        this.subtask1New = subtaskService.createSubtask(new SubtaskRequestBody("newSubtask", "newSubtaskdesc", TaskStatus.NEW), team1.getID(), task1.getID());
        this.subtask2 = subtaskService.createSubtask(new SubtaskRequestBody("subtask2", "subtaskdesc2", TaskStatus.CANCELLED), team1.getID(), task2.getID());
        this.subtaskToDelete = subtaskService.createSubtask(new SubtaskRequestBody("toDelete", "toDelete", TaskStatus.CANCELLED), teamToDelete.getID(), taskToDelete.getID());
        //

        this.subtaskRead = subtaskService.createSubtask(new SubtaskRequestBody("subtaskRead", "subtaskReadDesc", TaskStatus.NEW), teamRead.getID(), taskRead.getID());
        this.subtaskWrite = subtaskService.createSubtask(new SubtaskRequestBody("subtaskWrite", "subtaskWriteDesc", TaskStatus.HALTED), teamWrite.getID(), taskWrite.getID());
        this.subtaskDelete = subtaskService.createSubtask(new SubtaskRequestBody("subtaskDelete", "subtaskDeleteDesc", TaskStatus.CANCELLED), teamDelete.getID(), taskDelete.getID());
    }

    private void createInvitations(){
        //Old
        this.invitation = invitationService.createInvitation(new InvitationRequestBody((short) 101, UserRole.ADMIN, null), team1.getID());
        this.invitationNoUses = invitationService.createInvitation(new InvitationRequestBody((short)0, UserRole.MANAGER, null), team1.getID());
        this.invitationExpired = invitationService.createInvitation(new InvitationRequestBody((short)1, UserRole.MEMBER, OffsetDateTimeConverter.nowConverted().minusDays(1)), team1.getID());
        this.invitation1New = invitationService.createInvitation(new InvitationRequestBody((short)10, UserRole.OWNER, null), team2.getID());
        this.invitationToDelete = invitationService.createInvitation(new InvitationRequestBody((short)1, UserRole.OWNER, null), teamToDelete.getID());
        //

        this.invitationRead = invitationService.createInvitation(new InvitationRequestBody((short) 101, UserRole.ADMIN, null), teamRead.getID());
        this.invitationNoUses = invitationService.createInvitation(new InvitationRequestBody((short)0, UserRole.MANAGER, null), teamRead.getID());
        this.invitationExpired = invitationService.createInvitation(new InvitationRequestBody((short)1, UserRole.MEMBER, OffsetDateTimeConverter.nowConverted().minusDays(1)), teamRead.getID());
        this.invitationWrite = invitationService.createInvitation(new InvitationRequestBody(null, null, null), teamWrite.getID());
        this.invitationDelete = invitationService.createInvitation(new InvitationRequestBody(null,null,null), teamDelete.getID());
    }

    private void setJWTs(){
        try {
            //Old
            this.jwt1 = JSONWebToken.GenerateJWToken(user1.getID());
            this.jwt2 = JSONWebToken.GenerateJWToken(user2.getID());
            this.jwtAdminA = JSONWebToken.GenerateJWToken(userAdminA.getID());
            this.jwtAdminB = JSONWebToken.GenerateJWToken(userAdminB.getID());
            this.jwtMemberA = JSONWebToken.GenerateJWToken(userMemberA.getID());
            this.jwtMemberB = JSONWebToken.GenerateJWToken(userMemberB.getID());
            //

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
        //Old
        this.user1 = userService.getUserFull(user1);
        this.userAdminA = userService.getUserFull(userAdminA);
        this.userAdminA2 = userService.getUserFull(userAdminA2);
        this.userMemberA = userService.getUserFull(userMemberA);
        this.team1 = teamService.getTeamFull(team1);
        this.team2 = teamService.getTeamFull(team2);
        this.task1 = taskService.getTaskFull(task1);
        this.task1New = taskService.getTaskFull(task1New);
        this.task2 = taskService.getTaskFull(task2);
        //

        this.userReadOwner = userService.getUserFull(userReadOwner);
        this.userWriteOwner = userService.getUserFull(userWriteOwner);
        this.userDeleteOwner = userService.getUserFull(userDeleteOwner);
        this.userMember = userService.getUserFull(userMember);

        this.teamRead = teamService.getTeamFull(teamRead);
        this.teamWrite = teamService.getTeamFull(teamWrite);
        this.teamDelete = teamService.getTeamFull(teamDelete);

        this.taskRead = taskService.getTaskFull(taskRead);
        this.taskWrite = taskService.getTaskFull(taskWrite);
        this.taskDelete = taskService.getTaskFull(taskDelete);
    }

    //Old
    public void refreshTask(Task task){ this.refreshedTask = taskService.getTaskFull(task); }
    public void refreshSubtask(Subtask subtask){ this.refreshedSubtask = subtaskService.getSubtaskByID(subtask.getTask().getID(), subtask.getID()); }
    public void refreshInvitation(Invitation invitation){ this.refreshedInvitation = invitationService.getInvitationByUUID(invitation.getUUID()); }
    //

    public User refreshUser(User user){ return userService.getUserFull(user); }
    public Team refreshTeam(Team team){ return teamService.getTeamFull(team); }
    public Task refreshTaskNew(Task task){ return taskService.getTaskFull(task); }
    public Subtask refreshSubtaskNew(Subtask subtask){ return subtaskService.getSubtaskByID(subtask.getTask().getID(), subtask.getID()); }
    public Invitation refreshInvitationNew(Invitation invitation){ return  invitationService.getInvitationByUUID(invitation.getUUID()); }

}

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
import java.util.HashSet;
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
    public User userSecondOwner;
    public User userAdmin;
    public User userManager;
    public User userMember;
    public User userBanned;
    public User userNoTeam;

    public Team teamRead;
    public Team teamWrite;
    public Team teamDelete;

    public Task taskMultiUserRead;
    public Task taskOwnerUserRead;
    public Task taskBannedUserRead;
    public Task taskMultiUserWrite;
    public Task taskOwnerUserWrite;
    public Task taskBannedUserWrite;
    public Task taskMultiUserDelete;


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
    public String jwtOwner;
    public String jwtAdmin;
    public String jwtManager;
    public String jwtMember;
    public String jwtBanned;
    public String jwtNoTeam;

    public Set<Task> allTasks = new HashSet<>(8,1f);


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
        this.userSecondOwner = userService.createUser(new UserRequestBody("OWNER","OWNER","OWNER","OWNER"));
        this.userAdmin = userService.createUser(new UserRequestBody("ADMIN", "ADMIN", "ADMIN", "ADMIN"));
        this.userManager = userService.createUser(new UserRequestBody("MANAGER", "MANAGER", "MANAGER", "MANAGER"));
        this.userMember = userService.createUser(new UserRequestBody("MEMBER", "MEMBER", "MEMBER", "MEMBER"));
        this.userBanned = userService.createUser(new UserRequestBody("BANNED", "BANNED", "BANNED", "BANNED"));
        this.userNoTeam = userService.createUser(new UserRequestBody("NOTEAM", "NOTEAM", "NOTEAM", "NOTEAM"));
    }

    private void createTeams(){
        this.teamRead = teamService.createTeam(new TeamRequestBody("teamRead"), userReadOwner);
        this.teamWrite = teamService.createTeam(new TeamRequestBody("teamWrite"), userWriteOwner);
        this.teamDelete = teamService.createTeam(new TeamRequestBody("teamDelete"), userDeleteOwner);
    }

    private void addUsersToTeams(){
        teamService.addUser(teamRead.getID(), userSecondOwner, UserRole.OWNER);
        teamService.addUser(teamRead.getID(), userAdmin, UserRole.ADMIN);
        teamService.addUser(teamRead.getID(), userManager, UserRole.MANAGER);
        teamService.addUser(teamRead.getID(), userMember, UserRole.MEMBER);
        teamService.addUser(teamRead.getID(), userBanned, UserRole.BANNED);

        teamService.addUser(teamWrite.getID(), userSecondOwner, UserRole.OWNER);
        teamService.addUser(teamWrite.getID(), userAdmin, UserRole.ADMIN);
        teamService.addUser(teamWrite.getID(), userManager, UserRole.MANAGER);
        teamService.addUser(teamWrite.getID(), userMember, UserRole.MEMBER);
        teamService.addUser(teamWrite.getID(), userBanned, UserRole.BANNED);

        teamService.addUser(teamDelete.getID(), userSecondOwner, UserRole.OWNER);
        teamService.addUser(teamDelete.getID(), userAdmin, UserRole.ADMIN);
        teamService.addUser(teamDelete.getID(), userManager, UserRole.MANAGER);
        teamService.addUser(teamDelete.getID(), userMember, UserRole.MEMBER);
        teamService.addUser(teamDelete.getID(), userBanned, UserRole.BANNED);

        //Refreshing teams because addUser returns TeamWithUserRoles
        this.teamRead = refreshTeam(teamRead);
        this.teamWrite = refreshTeam(teamWrite);
        this.teamDelete = refreshTeam(teamDelete);
    }

    private void createTasks(){
        this.taskMultiUserRead = taskService.createTask(new TaskRequestBody("MultiUser", "MultiUserDesc", Set.of((int) userReadOwner.getID(), (int)userMember.getID()), TaskStatus.NEW), teamRead);
        this.taskOwnerUserRead = taskService.createTask(new TaskRequestBody("OwnerUser", "OwnerUserDesc", Set.of((int) userReadOwner.getID()), TaskStatus.INPROGRESS), teamRead);
        this.taskBannedUserRead = taskService.createTask(new TaskRequestBody("BannedUser", "BannedUserDesc", Set.of((int) userBanned.getID()), TaskStatus.CANCELLED), teamRead);

        this.taskMultiUserWrite = taskService.createTask(new TaskRequestBody("MultiUser", "MultiUserDesc", Set.of((int) userWriteOwner.getID(), (int)userMember.getID()), TaskStatus.NEW), teamWrite);
        this.taskOwnerUserWrite = taskService.createTask(new TaskRequestBody("OwnerUser", "OwnerUserDesc", Set.of((int) userWriteOwner.getID()), TaskStatus.INPROGRESS), teamWrite);
        this.taskBannedUserWrite = taskService.createTask(new TaskRequestBody("BannedUser", "BannedUserDesc", Set.of((int) userBanned.getID()), TaskStatus.CANCELLED), teamWrite);

        //TeamDelete has only one task on purpose to test single task Team
        this.taskMultiUserDelete = taskService.createTask(new TaskRequestBody("MultiUser", "MultiUserDesc", Set.of((int) userDeleteOwner.getID(), (int)userMember.getID()), TaskStatus.NEW), teamDelete);

        this.allTasks.addAll(Set.of(taskMultiUserRead, taskMultiUserWrite, taskMultiUserDelete, taskOwnerUserRead, taskOwnerUserWrite, taskBannedUserRead, taskBannedUserWrite));
    }

    private void createSubtasks(){
        this.subtaskRead = subtaskService.createSubtask(new SubtaskRequestBody("subtaskRead", "subtaskReadDesc", TaskStatus.NEW), teamRead.getID(), taskMultiUserRead.getID());
        this.subtaskWrite = subtaskService.createSubtask(new SubtaskRequestBody("subtaskWrite", "subtaskWriteDesc", TaskStatus.HALTED), teamWrite.getID(), taskMultiUserWrite.getID());
        this.subtaskDelete = subtaskService.createSubtask(new SubtaskRequestBody("subtaskDelete", "subtaskDeleteDesc", TaskStatus.CANCELLED), teamDelete.getID(), taskMultiUserDelete.getID());
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
            this.jwtOwner = JSONWebToken.GenerateJWToken(userSecondOwner.getID());
            this.jwtAdmin = JSONWebToken.GenerateJWToken(userAdmin.getID());
            this.jwtManager = JSONWebToken.GenerateJWToken(userManager.getID());
            this.jwtMember = JSONWebToken.GenerateJWToken(userMember.getID());
            this.jwtBanned = JSONWebToken.GenerateJWToken(userBanned.getID());
            this.jwtNoTeam = JSONWebToken.GenerateJWToken(userNoTeam.getID());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    private void getFullEntities(){
        this.userReadOwner = (User) userService.getUserFull(userReadOwner.getID());
        this.userWriteOwner = (User) userService.getUserFull(userWriteOwner.getID());
        this.userDeleteOwner = (User) userService.getUserFull(userDeleteOwner.getID());
        this.userMember = (User) userService.getUserFull(userMember.getID());

        this.teamRead =  (Team) teamService.getTeamFull(teamRead.getID());
        this.teamWrite = (Team) teamService.getTeamFull(teamWrite.getID());
        this.teamDelete = (Team) teamService.getTeamFull(teamDelete.getID());

        this.taskMultiUserRead = (Task) taskService.getTaskFullByIDAndTeamID(taskMultiUserRead.getID(), teamRead.getID());
        this.taskOwnerUserRead = (Task) taskService.getTaskFullByIDAndTeamID(taskOwnerUserRead.getID(), teamRead.getID());
        this.taskBannedUserRead = (Task) taskService.getTaskFullByIDAndTeamID(taskBannedUserRead.getID(), teamRead.getID());
        this.taskMultiUserWrite = (Task) taskService.getTaskFullByIDAndTeamID(taskMultiUserWrite.getID(), teamWrite.getID());
        this.taskOwnerUserWrite = (Task) taskService.getTaskFullByIDAndTeamID(taskOwnerUserWrite.getID(), teamWrite.getID());
        this.taskBannedUserWrite = (Task) taskService.getTaskFullByIDAndTeamID(taskBannedUserWrite.getID(), teamWrite.getID());
        this.taskMultiUserDelete = (Task) taskService.getTaskFullByIDAndTeamID(taskMultiUserDelete.getID(), teamDelete.getID());
    }

    public User refreshUser(User user){ return (User) userService.getUserFull(user.getID()); }
    public Team refreshTeam(Team team){ return (Team) teamService.getTeamFull(team.getID()); }
    public Task refreshTask(Task task){ return (Task) taskService.getTaskFullByIDAndTeamID(task.getID(), task.getAssignedTeam().getID()); }
    public Subtask refreshSubtask(Subtask subtask){ return (Subtask) subtaskService.getSubtaskByID(subtask.getTask().getID(), subtask.getID()); }
    public Invitation refreshInvitation(Invitation invitation){ return  (Invitation) invitationService.getInvitationFullByUUID(invitation.getUUID()); }

}

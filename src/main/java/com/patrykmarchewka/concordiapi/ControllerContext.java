package com.patrykmarchewka.concordiapi;

import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Exceptions.ImpossibleStateException;
import com.patrykmarchewka.concordiapi.Invitations.InvitationService;
import com.patrykmarchewka.concordiapi.Tasks.TaskService;
import com.patrykmarchewka.concordiapi.Teams.TeamUserRoleService;
import com.patrykmarchewka.concordiapi.Teams.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


/**
 * Order: (withUser, withTeam, withInvitation) -> (withTask, withRole, withOtherRole) <br>
 */
@Component
public class ControllerContext {
    private User user;
    private Team team;
    private Task task;
    private UserRole userRole;
    private UserRole otherRole;
    private Invitation invitation;



    private final TeamService teamService;
    private final TaskService taskService;
    private final TeamUserRoleService teamUserRoleService;
    private final InvitationService invitationService;


    @Autowired
    public ControllerContext(TeamService teamService, TaskService taskService, TeamUserRoleService teamUserRoleService, InvitationService invitationService) {
        this.teamService = teamService;
        this.taskService = taskService;
        this.teamUserRoleService = teamUserRoleService;
        this.invitationService = invitationService;
    }

    /**
     * @param authentication Authentication from logged user
     * @return User
     */
    public ControllerContext withUser(Authentication authentication){
        this.user = (User)authentication.getPrincipal();
        return this;
    }

    /**
     * @param teamID long to convert into Team object
     * @return Team
     */
    public ControllerContext withTeam(long teamID){
        this.team = teamService.getTeamByID(teamID);
        return this;
    }

    /**
     * Requires withTeam to be called before
     * @param taskID long to convert into Task object
     * @return Task
     * @throws ImpossibleStateException Thrown when called before {@link #withTeam(long)}
     */
    public ControllerContext withTask(long taskID){
        if (team == null){
            throw new ImpossibleStateException("Cannot call withTask before specifying team!");
        }
        this.task = taskService.getTaskByIDAndTeam(taskID,team);
        return this;
    }

    /**
     * Requires withUser and withTeam to be called before
     * @return UserRole
     * @throws ImpossibleStateException Thrown when called before {@link #withTeam(long)} and {@link #withUser(Authentication)}
     */
    public ControllerContext withRole(){
        if (user == null || team == null){
            throw new ImpossibleStateException("Cannot call withRole before specifying user and team!");
        }
        this.userRole = teamUserRoleService.getRole(user,team);
        return this;
    }

    /**
     * Requires withTeam to be called before
     * @param otherUser User to get UserRole
     * @return UserRole
     * @throws ImpossibleStateException Thrown when called before {@link #withTeam(long)}
     */
    public ControllerContext withOtherRole(User otherUser){
        if (team == null){
            throw new ImpossibleStateException("Cannot call withOtherRole before specifying team!");
        }
        this.otherRole = teamUserRoleService.getRole(otherUser,team);
        return this;
    }

    /**
     * @param UUID String to get Invitation
     * @return Invitation
     */
    public ControllerContext withInvitation(String UUID){
        this.invitation = invitationService.getInvitationByUUID(UUID);
        return this;
    }


    public User getUser() {return user;}
    public Team getTeam() {return team;}
    public Task getTask() {return task;}
    public UserRole getUserRole() {return userRole;}
    public UserRole getOtherRole() {return otherRole;}
    public Invitation getInvitation(){return invitation;}

}

package com.patrykmarchewka.concordiapi;

import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Exceptions.BadRequestException;
import com.patrykmarchewka.concordiapi.Invitations.InvitationService;
import com.patrykmarchewka.concordiapi.Tasks.TaskService;
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

    public ControllerContext withUser(Authentication authentication){
        this.user = (User)authentication.getPrincipal();
        return this;
    }

    public ControllerContext withTeam(long teamID){
        this.team = teamService.getTeamByID(teamID);
        return this;
    }

    public ControllerContext withTask(long taskID){
        if (team == null){
            throw new BadRequestException("Cannot call withTask before specifying team!");
        }
        this.task = taskService.getTaskByIDAndTeam(taskID,team);
        return this;
    }

    public ControllerContext withRole(){
        if (user == null || team == null){
            throw new BadRequestException("Cannot call withRole before specifying user and team!");
        }
        this.userRole = teamUserRoleService.getRole(user,team);
        return this;
    }

    public ControllerContext withOtherRole(User otherUser){
        if (team == null){
            throw new BadRequestException("Cannot call withOtherRole before specifying team!");
        }
        this.otherRole = teamUserRoleService.getRole(otherUser,team);
        return this;
    }

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

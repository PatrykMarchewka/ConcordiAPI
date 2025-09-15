package com.patrykmarchewka.concordiapi.Teams;

import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamDTO;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.TeamRepository;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Exceptions.ImpossibleStateException;
import com.patrykmarchewka.concordiapi.Exceptions.NoPrivilegesException;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
import com.patrykmarchewka.concordiapi.RoleRegistry;
import com.patrykmarchewka.concordiapi.Tasks.TaskService;
import com.patrykmarchewka.concordiapi.Teams.Updaters.TeamUpdatersService;
import com.patrykmarchewka.concordiapi.UpdateType;
import com.patrykmarchewka.concordiapi.UserRole;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamUserRoleService teamUserRoleService;
    private final TaskService taskService;
    private final RoleRegistry roleRegistry;
    private final TeamUpdatersService teamUpdatersService;

    @Autowired
    public TeamService(TeamRepository teamRepository, TeamUserRoleService teamUserRoleService, TaskService taskService, RoleRegistry roleRegistry, TeamUpdatersService teamUpdatersService){
        this.teamRepository = teamRepository;
        this.teamUserRoleService = teamUserRoleService;
        this.taskService = taskService;
        this.roleRegistry = roleRegistry;
        this.teamUpdatersService = teamUpdatersService;
    }


    /**
     * Creates team with specified body details and sets given User as team owner
     * @param body TeamRequestBody with information
     * @param user User creating team that becomes team owner
     * @return Team that has been created
     */
    @Transactional
    public Team createTeam(TeamRequestBody body, User user){
        Team team = new Team();
        teamUpdatersService.update(team,body, UpdateType.CREATE);
        team.addUserRole(user,UserRole.OWNER);
        return saveTeam(team);
    }

    /**
     * Edits entire team with values specified in the body
     * @param team Team to edit
     * @param body TeamRequestBody with new values
     * @return Team after changes
     */
    @Transactional
    public Team putTeam(Team team, TeamRequestBody body){
        teamUpdatersService.update(team,body,UpdateType.PUT);
        return saveTeam(team);
    }

    /**
     * Edits team values with those specified in the body
     * @param team Team to edit
     * @param body TeamRequestBody with new values
     * @return Team after changes
     */
    @Transactional
    public Team patchTeam(Team team,TeamRequestBody body){
        teamUpdatersService.update(team,body,UpdateType.PATCH);
        return saveTeam(team);
    }

    /**
     * Gives the Team based on provided ID
     * @param id ID of the team to get of
     * @return Team that has the specified ID
     */
    public Team getTeamByID(long id){
        return teamRepository.findTeamById(id).orElseThrow(NotFoundException::new);
    }

    /**
     * Saves pending changes to the Team
     * @param team Team to save
     * @return Team after changes
     */
    public Team saveTeam(Team team){return teamRepository.save(team);}

    /**
     * Deletes the team completely
     * @param team Team to delete
     */
    public void deleteTeam(Team team){
        teamRepository.delete(team);
    }

    /**
     * Removes specified user from the team, removes the user from tasks attached to the team and removes role mention for that user. <br>
     * If the team would end up empty without any pending invitations the team gets deleted too
     * @param team Team that contains the user that we want to remove
     * @param user User to be removed
     */
    @Transactional
    public Team removeUser(Team team, User user){
        Team newteam = getTeamFull(team);
        removeTeamUserRoleForUser(newteam, user);
        removeTeamTasksForUser(newteam, user);
        return deleteTeamIfEmpty(newteam);
    }

    public void removeTeamUserRoleForUser(Team team, User user){
        if (!Hibernate.isInitialized(team.getUserRoles())){
            throw new ImpossibleStateException("Cannot access userRoles for given team");
        }
        team.removeUserRole(teamUserRoleService.getByUserAndTeam(user, team));
        saveTeam(team);
    }

    public void removeTeamTasksForUser(Team team, User user){
        if (!Hibernate.isInitialized(team.getTeamTasks())){
            throw new ImpossibleStateException("Cannot access teamTasks for given team");
        }
        for (Task task : team.getTeamTasks()){
            if (task.getUsers().contains(user)){
                taskService.removeUserFromTask(task, user);
            }
        }
        saveTeam(team);
    }

    public Team deleteTeamIfEmpty(Team team){
        if (!Hibernate.isInitialized(team.getTeammates()) || !Hibernate.isInitialized(team.getInvitations())){
            throw new ImpossibleStateException("Cannot access userRoles and/or invitations for given team");
        }
        if (team.getTeammates().isEmpty() && team.getInvitations().isEmpty()){
            deleteTeam(team);
            return null;
        }
        return team;
    }

    /**
     * Removes all users from team using {@link #removeUser(Team, User)}
     * @param team Team to remove everyone from
     */
    @Transactional
    public void removeAllUsers(Team team){
        for (User user : team.getTeammates()){
            removeUser(team,user);
        }
    }


    /**
     * Returns Set of TeamDTO
     * @param user User calling the action
     * @return Set of TeamDTO based on User Role
     */
    public Set<TeamDTO> getTeamsDTO(User user){
        return user.getTeams().stream().map(team -> createTeamDTO(user,team)).collect(Collectors.toSet());
    }


    /**
     * Returns one DTO of a team
     * @param user User calling the action
     * @param team Team to return DTO of
     * @return TeamDTO based on User Role
     * @throws NoPrivilegesException Thrown if user doesn't have enough privileges to generate DTO
     */
    public TeamDTO createTeamDTO(User user, Team team){
        UserRole role = teamUserRoleService.getRole(user,team);
        return roleRegistry.createTeamDTOMap().getOrDefault(role, (t, u) -> { throw new NoPrivilegesException(); }).apply(team, user);
    }

    public Team getTeamWithUserRoles(Team team){
        return teamRepository.findTeamWithUserRolesAndUsersByID(team.getId()).orElseThrow(() -> new ImpossibleStateException("Team not found with provided ID"));
    }

    public Team getTeamWithTeamTasks(Team team){
        return teamRepository.findTeamWithTeamTasksByID(team.getId()).orElseThrow(() -> new ImpossibleStateException("Team not found with provided ID"));
    }

    public Team getTeamWithInvitations(Team team){
        return teamRepository.findTeamWithInvitationsByID(team.getId()).orElseThrow(() -> new ImpossibleStateException("Team not found with provided ID"));
    }

    public Team getTeamFull(Team team){
        return teamRepository.findTeamFullByID(team.getId()).orElseThrow(() -> new ImpossibleStateException("Team not found with provided ID"));
    }




}

package com.patrykmarchewka.concordiapi.Teams;

import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamDTO;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.*;
import com.patrykmarchewka.concordiapi.Exceptions.NoPrivilegesException;
import com.patrykmarchewka.concordiapi.PublicVariables;
import com.patrykmarchewka.concordiapi.RoleRegistry;
import com.patrykmarchewka.concordiapi.Tasks.TaskService;
import com.patrykmarchewka.concordiapi.TeamUserRoleService;
import com.patrykmarchewka.concordiapi.Users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserService userService;
    private final TeamUserRoleService teamUserRoleService;
    private final TaskService taskService;

    private final RoleRegistry roleRegistry;

    @Autowired
    public TeamService(TeamRepository teamRepository, UserService userService, TeamUserRoleService teamUserRoleService, TaskService taskService, RoleRegistry roleRegistry){
        this.teamRepository = teamRepository;
        this.userService = userService;
        this.teamUserRoleService = teamUserRoleService;
        this.taskService = taskService;
        this.roleRegistry = roleRegistry;
    }

    List<TeamUpdater> updaters(){
        return List.of(new TeamNameUpdater()
        );
    }

    private void applyCreateUpdates(Team team, TeamRequestBody body){
        for (TeamUpdater updater : updaters()){
            if (updater instanceof TeamCREATEUpdater createUpdater){
                createUpdater.CREATEUpdate(team,body);
            }
        }
    }

    private void applyPutUpdates(Team team, TeamRequestBody body){
        for (TeamUpdater updater : updaters()){
            if (updater instanceof TeamPUTUpdater putUpdater){
                putUpdater.PUTUpdate(team,body);
            }
        }
    }

    private void applyPatchUpdates(Team team, TeamRequestBody body){
        for (TeamUpdater updater : updaters()){
            if (updater instanceof TeamPATCHUpdater patchUpdater){
                patchUpdater.PATCHUpdate(team, body);
            }
        }
    }







    @Transactional
    public Team createTeam(TeamRequestBody body, User user){
        Team team = new Team();
        applyCreateUpdates(team,body);
        addUser(team,user, PublicVariables.UserRole.OWNER);
        return team;
    }

    @Transactional
    public Team patchTeam(Team team,TeamRequestBody body){
        applyPatchUpdates(team, body);
        return team;
    }

    public long getID(Team team){
        return team.getId();
    }

    public Team getTeamByID(long id){
        return teamRepository.getTeamById(id);
    }

    public Team saveTeam(Team team){return teamRepository.save(team);}

    public void deleteTeam(Team team){
        teamRepository.delete(team);
    }

    @Transactional
    public void removeUser(Team team, User user){
        team.removeTeammate(user);
        saveTeam(team);
        user.removeTeam(team);
        userService.saveUser(user);
        for (Task task : team.getTasks()){
            if (task.getUsers().contains(user)){
                taskService.removeUserFromTask(task, user);
            }
        }
        teamUserRoleService.deleteTMR(teamUserRoleService.getByUserAndTeam(user,team));
        if (team.getTeammates().isEmpty() && team.getInvitations().isEmpty()){
            deleteTeam(team);
        }

    }

    @Transactional
    public void removeAllUsers(Team team){
        for (User user : team.getTeammates()){
            removeUser(team,user);
        }
    }


    @Transactional
    public void addUser(Team team, User user, PublicVariables.UserRole role){
        team.addTeammate(user);
        saveTeam(team);
        user.addTeam(team);
        userService.saveUser(user);
        teamUserRoleService.createTMR(user,team,role);

    }

    public void addTask(Team team, Task task){
        team.getTasks().add(task);
        saveTeam(team);
    }




    public Set<TeamDTO> getTeamsDTO(User user){
        return userService.getTeams(user).stream().map(team -> createTeamDTO(user,team)).collect(Collectors.toSet());
    }


    public TeamDTO createTeamDTO(User user, Team team){
        PublicVariables.UserRole role = teamUserRoleService.getRole(user,team);
        return roleRegistry.createTeamDTOMap().getOrDefault(role, (t, u) -> { throw new NoPrivilegesException(); }).apply(team, user);
    }

    public void removeTaskFromTeam(Team team, Task task){
        team.getTasks().remove(task);
        saveTeam(team);
    }







}

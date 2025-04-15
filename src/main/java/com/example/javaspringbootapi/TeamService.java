package com.example.javaspringbootapi;

import com.example.javaspringbootapi.DatabaseModel.*;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private TeamUserRoleService teamUserRoleService;
    @Autowired
    private TaskService taskService;

    @Transactional
    public Team createTeam(String name, User user){
        Team team = new Team();
        team.setName(name);
        team = teamRepository.save(team);
        addUser(team,user, PublicVariables.UserRole.ADMIN);
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
        team.getTeammates().remove(user);
        teamRepository.save(team);
        user.getTeams().remove(team);
        userService.saveUser(user);
        for (Task task : team.getTasks()){
            if (task.getUsers().contains(user)){
                taskService.removeUserFromTask(team, task.getID(), user);
            }
        }
        teamUserRoleService.deleteTMR(teamUserRoleService.getByUserAndTeam(user,team));
        if (team.getTeammates().isEmpty() && team.getInvitations().isEmpty()){
            teamRepository.delete(team);
        }

    }

    @Transactional
    public void addUser(Team team, User user, PublicVariables.UserRole role){
        team.getTeammates().add(user);
        teamRepository.save(team);
        user.getTeams().add(team);
        userService.saveUser(user);
        TeamUserRole tmr = new TeamUserRole();
        tmr.setTeam(team);
        tmr.setUser(user);
        tmr.setUserRole(role);
        teamUserRoleService.saveTMR(tmr);

    }
}

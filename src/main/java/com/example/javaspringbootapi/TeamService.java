package com.example.javaspringbootapi;

import com.example.javaspringbootapi.DatabaseModel.*;
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
    private TeamUserRoleRepository teamUserRoleRepository;

    @Transactional
    public Team createTeam(String name, User user){
        Team team = new Team();
        team.setName(name);
        team = teamRepository.save(team);
        TeamUserRole tmr = new TeamUserRole();
        tmr.setTeam(team);
        tmr.setUser(user);
        tmr.setUserRole(PublicVariables.UserRole.ADMIN);
        teamUserRoleRepository.save(tmr);
        user.addToTeam(team);
        userService.saveUser(user);
        return team;
    }

    public long getID(Team team){
        return team.getId();
    }

    public Team getTeamByID(long id){
        return teamRepository.getTeamById(id);
    }

    public Team saveTeam(Team team){
        return teamRepository.save(team);
    }

    public void deleteTeam(Team team){
        teamRepository.delete(team);
    }

    @Transactional
    public void removeUser(Team team, User user){
        team.getTeammates().remove(user);
        teamRepository.save(team);
        user.getTeams().remove(team);
        userService.saveUser(user);
        if (team.getTeammates().isEmpty() && team.getInvitations().isEmpty()){
            teamRepository.delete(team);
        }
    }
}
